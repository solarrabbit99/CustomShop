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

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerDigType;
import com.palmergames.bukkit.towny.object.TownyPermission.ActionType;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import com.palmergames.bukkit.towny.utils.ShopPlotUtil;
import com.paratopiamc.customshop.player.PlayerState;
import com.paratopiamc.customshop.plugin.CSComd;
import com.paratopiamc.customshop.plugin.CustomShop;
import com.paratopiamc.customshop.shop.briefcase.BriefcaseRemover;
import com.paratopiamc.customshop.shop.vm.VMRemover;
import com.paratopiamc.customshop.utils.ShopUtils;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.Material;
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
    }

    public ShopRemoval(CommandSender sender) {
        this.sender = sender;
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
        if (remover != null) {
            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!evt.isCancelled()) {
                        PlayerState.getPlayerState(player).clearShopInteractions();
                        UUID ownerID = remover.removeShop();
                        targetBlock.getWorld().playSound(targetBlock.getLocation(), Sound.BLOCK_STONE_BREAK, 1.5F,
                                1.0F);
                        CompletableFuture
                                .runAsync(() -> CustomShop.getPlugin().getDatabase().decrementTotalShopsOwned(ownerID));
                    }
                }
            };
            runnable.runTaskLater(CustomShop.getPlugin(), 45);
        }

        CustomShop.getPlugin().getProtocolManager()
                .addPacketListener(new PacketAdapter(CustomShop.getPlugin(), Client.BLOCK_DIG) {
                    @Override
                    public void onPacketReceiving(PacketEvent e) {
                        PacketContainer packet = e.getPacket();
                        PlayerDigType digType = packet.getPlayerDigTypes().getValues().get(0);
                        if (digType.name().equals("ABORT_DESTROY_BLOCK")) {
                            evt.setCancelled(true);
                        }
                    }
                });
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
            UUID ownerID = remover.removeShop();
            CompletableFuture.runAsync(() -> CustomShop.getPlugin().getDatabase().decrementTotalShopsOwned(ownerID));
        } else if (ShopUtils.getArmorStand(targetBlock) != null) {
            evt.setCancelled(true);
        }
    }

    @Override
    public boolean exec() {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        Block targetBlock = player.getTargetBlockExact(5);
        if (targetBlock == null) {
            player.sendMessage("§cYou are not targeting any block...");
            return true;
        }
        ShopRemover remover = getShopRemover(targetBlock, player);
        if (remover != null) {
            UUID ownerID = remover.removeShop();
            CompletableFuture.runAsync(() -> CustomShop.getPlugin().getDatabase().decrementTotalShopsOwned(ownerID));
        } else {
            player.sendMessage("§cInvalid target...");
        }
        return true;
    }

    /**
     * Checker for which subtype of {@link ShopRemover} to be used. It is assumed
     * that a custom shop, if there is any, is the only entity within a particular
     * barrier block. Conversely, each custom shop has an armor stand embedded in at
     * least a barrier block. Return {@code null} if player is not owner of the shop
     * or no ShopRemover can be used.
     *
     * @param targetBlock block targeted by player
     * @return correspond remover for the type of shop
     */
    private static ShopRemover getShopRemover(Block targetBlock, Player player) {
        ArmorStand armorStand = ShopUtils.getArmorStand(targetBlock);
        if (armorStand == null || !ShopUtils.hasShopPermission(armorStand, player)
                || !hasTownyPerms(armorStand.getLocation(), player)
                || !hasWorldGuardPerms(armorStand.getLocation(), player))
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
     * Checks if towny will not restrict player from placing custom shop, which is
     * vacuously true if Towny is not installed or is disabled for the plugin.
     *
     * @param location location of the shop
     * @param player   player removing the shop
     * @return {@code true} if player is not restricted to build by towny
     */
    private static boolean hasTownyPerms(Location location, Player player) {
        if (!CustomShop.getPlugin().hasTowny())
            return true;
        if (!CustomShop.getPlugin().getConfig().getBoolean("towny-enabled"))
            return true;

        boolean onlyShopPlots = CustomShop.getPlugin().getConfig().getBoolean("only-shop-plots");
        if (onlyShopPlots) {
            return ShopPlotUtil.doesPlayerHaveAbilityToEditShopPlot(player, location);
        } else {
            return PlayerCacheUtil.getCachePermission(player, location, Material.STONE, ActionType.DESTROY);
        }
    }

    /**
     * Checks if worldguard will not restrict player from placing custom shop, which
     * is vacuously true if WorldGuard is not installed or is disabled for the
     * plugin.
     *
     * @param location location of the shop
     * @param player   player removing the shop
     * @return {@code true} if player is not restricted to build by worldguard
     */
    private static boolean hasWorldGuardPerms(Location location, Player player) {
        if (!CustomShop.getPlugin().hasWorldGuard())
            return true;
        if (!CustomShop.getPlugin().getConfig().getBoolean("worldguard-enabled"))
            return true;

        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        if (WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, localPlayer.getWorld())) {
            return true;
        }

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        return query.testState(BukkitAdapter.adapt(location), localPlayer, Flags.BUILD);
    }
}
