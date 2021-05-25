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
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import com.paratopiamc.customshop.gui.CreationGUI;
import com.paratopiamc.customshop.plugin.CustomShop;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import co.aikar.taskchain.TaskChain;

/** Encapsulated an event of player attempting to open a custom shop crate. */
public class OpenCrate implements Listener {

    /**
     * Event handler for player attempting to open crates.
     *
     * @param evt event of player interacting with a block
     */
    @EventHandler
    public static void onOpenCrate(PlayerInteractEvent evt) {
        EquipmentSlot hand = evt.getHand();
        if (!hand.equals(EquipmentSlot.HAND) || !evt.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        } else if (!SetCrate.verifyCrateLocation(evt.getClickedBlock().getLocation())) {
            return;
        } else {
            evt.setCancelled(true);
            Player player = evt.getPlayer();
            ItemStack item = player.getEquipment().getItemInMainHand();
            if (!Key.isKey(item)) {
                player.sendMessage("§cYou do not have the required crate key in main hand!");
                return;
            } else {
                ItemStack itemInHand = player.getInventory().getItemInMainHand();
                itemInHand.setAmount(itemInHand.getAmount() - 1);
                player.getInventory().setItemInMainHand(itemInHand);
                Random rng = new Random();
                int index = rng.nextInt(CreationGUI.noOfItems);
                int unlocked = CreationGUI.modelData.get(index);
                String name = CreationGUI.names.get(index);

                TaskChain<?> task = CustomShop.getPlugin().getTaskChainFactory().newSharedChain("OPENCRATE")
                        .<List<Integer>>asyncFirstCallback(
                                next -> next.accept(CustomShop.getPlugin().getDatabase().getUnlockedShops(player)))
                        .syncLast(list -> {
                            player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.5F, 1.0F);
                            if (list.contains(unlocked)) {
                                player.sendMessage("§6You already have " + name + " unlocked :(");
                            } else {
                                list.add(unlocked);
                                CompletableFuture.runAsync(() -> {
                                    CustomShop.getPlugin().getDatabase().setUnlockedShops(player, list);
                                    player.sendMessage("§aNew custom shop unlocked! " + name);
                                });
                            }
                        });
                task.execute();
            }
        }
    }
}
