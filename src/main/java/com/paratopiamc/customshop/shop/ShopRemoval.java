/*
 *  This file is part of CustomShop. Copyright (c) 2021 Paratopia.
 *
 *  CustomShop is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  CustomShop is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CustomShop. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package com.paratopiamc.customshop.shop;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import com.paratopiamc.customshop.player.PlayerState;
import com.paratopiamc.customshop.plugin.CSComd;
import com.paratopiamc.customshop.plugin.CustomShop;
import com.paratopiamc.customshop.shop.briefcase.BriefcaseRemover;
import com.paratopiamc.customshop.shop.vm.VMRemover;
import com.paratopiamc.customshop.utils.LanguageUtils;
import com.paratopiamc.customshop.utils.ShopUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

/** Player removing shop, either by breaking or by command. */
public class ShopRemoval extends CSComd implements Listener {
    public ShopRemoval() {
        super(null, null);
    }

    public ShopRemoval(CommandSender sender) {
        super(sender, null);
    }

    /**
     * Event of player breaking the shop, specifically the barrier blocks in which
     * the armor stands are held within.
     *
     * @param evt player damaging block (particularly barrier blocks) event
     */
    @EventHandler
    public void onBarrierBreak(BlockDamageEvent evt) {
        Player player = evt.getPlayer();
        Block targetBlock = evt.getBlock();
        ShopRemover remover = getShopRemover(targetBlock, player);
        createPipeline(evt);
        if (remover != null) {
            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!evt.isCancelled()) {
                        PlayerState.getPlayerState(player).clearShopInteractions();
                        UUID ownerID = remover.removeShop(true);
                        if (ownerID != null) {
                            targetBlock.getWorld().playSound(targetBlock.getLocation(), Sound.BLOCK_STONE_BREAK, 1.5F,
                                    1.0F);
                            CompletableFuture.runAsync(
                                    () -> CustomShop.getPlugin().getDatabase().decrementTotalShopsOwned(ownerID));
                        }
                    }
                }
            };
            runnable.runTaskLater(CustomShop.getPlugin(), 36);
        }
    }

    /**
     * Event of player breaking the shop in creative, specifically the barrier
     * blocks in which the armor stands are held within.
     *
     * @param evt player breaking block (particularly barrier blocks) event
     */
    @EventHandler
    public void onBarrierBreak(BlockBreakEvent evt) {
        Player player = evt.getPlayer();
        Block targetBlock = evt.getBlock();
        ShopRemover remover = getShopRemover(targetBlock, player);
        if (remover != null) {
            PlayerState.getPlayerState(player).clearShopInteractions();
            UUID ownerID = remover.removeShop(true);
            if (ownerID != null) {
                CompletableFuture
                        .runAsync(() -> CustomShop.getPlugin().getDatabase().decrementTotalShopsOwned(ownerID));
            } else {
                evt.setCancelled(true);
            }
        } else if (ShopUtils.getArmorStand(targetBlock) != null) {
            evt.setCancelled(true);
        }
    }

    @Override
    public boolean exec() {
        if (!(sender instanceof Player)) {
            return true;
        } else if (!sender.hasPermission("customshop.removeshop")) {
            sender.sendMessage(LanguageUtils.getString("command-no-perms"));
            return false;
        }
        Player player = (Player) sender;
        Block targetBlock = player.getTargetBlockExact(5);
        if (targetBlock == null) {
            player.sendMessage(LanguageUtils.getString("remove.invalid-block"));
            return true;
        }
        ShopRemover remover = getShopRemover(targetBlock, player);
        if (remover != null) {
            UUID ownerID = remover.removeShop(false);
            if (ownerID != null) {
                CompletableFuture
                        .runAsync(() -> CustomShop.getPlugin().getDatabase().decrementTotalShopsOwned(ownerID));
            }
        } else {
            player.sendMessage(LanguageUtils.getString("remove.invalid-target"));
        }
        return true;
    }

    /**
     * Checker for which subtype of {@link ShopRemover} to be used. Returns
     * {@code null} if:
     * <ul>
     * <li>no armorstand is detected within the target block
     * <li>player does not have permissions to the shop (not an owner/admin)
     * <li>shop is in use by any player (including the player attempting to break
     * it)
     * <li>no ShopRemover can be used.
     * </ul>
     * It is assumed that a custom shop, if there is any, is the only entity within
     * a particular barrier block. Conversely, each custom shop has an armor stand
     * embedded in at least a barrier block.
     * 
     * @param targetBlock block targeted by player
     * @return correspond remover for the type of shop
     */
    private static ShopRemover getShopRemover(Block targetBlock, Player player) {
        ArmorStand armorStand = ShopUtils.getArmorStand(targetBlock);
        if (armorStand == null) {
            return null;
        } else if (PlayerState.getInteractingPlayer(armorStand) != null) {
            player.sendMessage(LanguageUtils.getString("shop-currently-in-use.shop"));
            return null;
        } else if (!ShopUtils.hasShopPermission(armorStand, player)
                || !CustomShop.getPlugin().support().hasRemovePerms(armorStand.getLocation(), player))
            return null;

        String customName = armorStand.getCustomName();
        ShopRemover result;
        switch (customName) {
        case "§5§lVending Machine":
            result = new VMRemover(targetBlock, armorStand);
            break;
        case "§5§lNewt's Briefcase":
            result = new BriefcaseRemover(targetBlock, armorStand);
            break;
        default:
            result = null;
            break;
        }
        return result;
    }

    /**
     * Creates a {@link ChannelPipeline} that listens in to packets where player
     * aborts damaging barrier blocks. If player pre-maturely aborts damaging, the
     * event is set as cancelled.
     * 
     * @param evt event of player damaging the block
     */
    private void createPipeline(BlockDamageEvent evt) {
        Class<?> PacketPlayInBlockDig, EnumPlayerDigType, CraftPlayer;
        try {
            PacketPlayInBlockDig = getNMSClass("PacketPlayInBlockDig");
            EnumPlayerDigType = PacketPlayInBlockDig.getDeclaredClasses()[0];
            CraftPlayer = getCraftBukkitClass("entity.CraftPlayer");

            Player player = evt.getPlayer();
            Object handle = CraftPlayer.getMethod("getHandle").invoke(player);
            Object channel;
            if (Bukkit.getVersion().contains("1.17")) {
                Object playerConnection = handle.getClass().getField("b").get(handle);
                Object networkManager = playerConnection.getClass().getField("a").get(playerConnection);
                channel = networkManager.getClass().getField("k").get(networkManager);
            } else {
                Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
                Object networkManager = playerConnection.getClass().getField("networkManager").get(playerConnection);
                channel = networkManager.getClass().getField("channel").get(networkManager);
            }
            Object pipeline = channel.getClass().getMethod("pipeline").invoke(channel);

            ChannelDuplexHandler handler = new ChannelDuplexHandler() {
                @Override
                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                    if (PacketPlayInBlockDig.isInstance(msg)) {
                        Object detectedEnum = PacketPlayInBlockDig.getMethod("d").invoke(msg);
                        Object digType = Enum.class.getMethod("valueOf", Class.class, String.class).invoke(null,
                                EnumPlayerDigType, "ABORT_DESTROY_BLOCK");
                        if (detectedEnum.equals(digType)) {
                            evt.setCancelled(true);
                            pipeline.getClass().getMethod("remove", ChannelHandler.class).invoke(pipeline, this);
                        }
                    }
                    super.channelRead(ctx, msg);
                }
            };

            if (pipeline.getClass().getMethod("get", String.class).invoke(pipeline, player.getName()) == null) {
                pipeline.getClass().getMethod("addBefore", String.class, String.class, ChannelHandler.class)
                        .invoke(pipeline, "packet_handler", player.getName(), handler);
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get class by name under {@code net.minecraft.server.*}.
     *
     * @param className name of the class
     * @return the {@code Class} object of the class with given name
     * @throws ClassNotFoundException if class with the given name cannot be found
     */
    private Class<?> getNMSClass(String className) throws ClassNotFoundException {
        if (Bukkit.getVersion().contains("1.17")) {
            return Class.forName("net.minecraft.network.protocol.game." + className);
        } else {
            return Class.forName("net.minecraft.server."
                    + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + className);
        }
    }

    /**
     * Get class by name under {@code org.bukkit.craftbukkit.*}.
     *
     * @param className name of the class
     * @return the {@code Class} object of the class with given name
     * @throws ClassNotFoundException if class with the given name cannot be found
     */
    private Class<?> getCraftBukkitClass(String className) throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit."
                + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + className);
    }
}
