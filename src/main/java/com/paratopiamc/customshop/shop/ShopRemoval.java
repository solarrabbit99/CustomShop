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
import com.paratopiamc.customshop.player.PlayerState;
import com.paratopiamc.customshop.plugin.CSComd;
import com.paratopiamc.customshop.plugin.CustomShop;
import com.paratopiamc.customshop.shop.briefcase.BriefcaseRemover;
import com.paratopiamc.customshop.shop.vm.VMRemover;
import com.paratopiamc.customshop.utils.LanguageUtils;
import com.paratopiamc.customshop.utils.ShopUtils;

import org.bukkit.GameMode;
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
        CustomShop.getPlugin().support().blockDamagePacketHandler(evt);
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
        // Checking if event is cancelled is an ItemsAdder solution, may also impose
        // land protection for some plugins
        if (evt.isCancelled() || evt.getPlayer().getGameMode() == GameMode.SURVIVAL) {
            return;
        }
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
}
