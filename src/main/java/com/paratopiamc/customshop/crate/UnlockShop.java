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

package com.paratopiamc.customshop.crate;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import com.paratopiamc.customshop.gui.CreationGUI;
import com.paratopiamc.customshop.player.PlayerState;
import com.paratopiamc.customshop.plugin.CustomShop;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class UnlockShop implements Listener {
    @EventHandler
    public void onPlace(BlockPlaceEvent evt) {
        Player player = evt.getPlayer();
        ItemStack item = evt.getItemInHand();
        ItemMeta meta = item.getItemMeta();

        if ((item.getType() != Material.PLAYER_HEAD && item.getType() != Material.PLAYER_WALL_HEAD)
                || !meta.hasDisplayName() || !meta.hasCustomModelData())
            return;

        String displayName = meta.getDisplayName();
        int modelData = meta.getCustomModelData();

        for (int i = 0; i < CreationGUI.noOfItems; i++) {
            if (CreationGUI.modelData.get(i).equals(modelData) && CreationGUI.names.get(i).equals(displayName)) {
                evt.setCancelled(true);
                PlayerState state = PlayerState.getPlayerState(player);
                if (state.getUnlockingShopItem() == null || !state.getUnlockingShopItem().isSimilar(item)) {
                    player.sendMessage("§9Place again to confirm!");
                    state.setUnlockingShop(item);
                } else {
                    CompletableFuture<List<Integer>> cf = CompletableFuture
                            .supplyAsync(() -> CustomShop.getPlugin().getDatabase().getUnlockedShops(player));
                    cf.thenAccept(list -> {
                        BukkitRunnable runnable = new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (list.contains(modelData)) {
                                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.5F, 1.0F);
                                    player.sendMessage("§6You already have " + displayName + "§6 unlocked!");
                                } else {
                                    list.add(modelData);
                                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.5F, 1.0F);
                                    CompletableFuture.runAsync(() -> {
                                        CustomShop.getPlugin().getDatabase().setUnlockedShops(player, list);
                                        player.sendMessage("§aYou have unlocked a new custom shop: " + displayName);
                                    });
                                    item.setAmount(item.getAmount() - 1);
                                }
                            }
                        };
                        runnable.runTask(CustomShop.getPlugin());
                    });
                }
                break;
            }
        }
    }
}
