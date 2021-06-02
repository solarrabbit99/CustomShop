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

package com.paratopiamc.customshop.shop.vm;

import com.paratopiamc.customshop.gui.VMGUI;
import com.paratopiamc.customshop.player.PlayerState;
import com.paratopiamc.customshop.plugin.CustomShop;
import com.paratopiamc.customshop.shop.conversation.SetPriceConversationFactory;
import com.paratopiamc.customshop.utils.ShopUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * Listener for players interacting with custom shops, containing handlers for
 * which the owner left clicks on shops to list items.
 */
public class VMListItem implements Listener {
    @EventHandler
    public void listItem(PlayerInteractEvent evt) {
        EquipmentSlot hand = evt.getHand();
        Player player = evt.getPlayer();
        if (!hand.equals(EquipmentSlot.HAND) || !evt.getAction().equals(Action.LEFT_CLICK_BLOCK)
                || !player.isSneaking()) {
            return;
        }
        Block targetBlock = evt.getClickedBlock();
        ArmorStand armorStand = ShopUtils.getArmorStand(targetBlock);
        if (armorStand == null) {
            return;
        }
        if (!armorStand.getCustomName().equals("§5§lVending Machine")) {
            return;
        } else {
            // For creative mode
            evt.setCancelled(true);
            PlayerState state = PlayerState.getPlayerState(player);
            state.clearShopInteractions();
            if (!ShopUtils.hasShopPermission(armorStand, player)) {
                player.sendMessage("§cYou do not own the vending machine!");
                return;
            }
            if (PlayerState.getInteractingPlayer(armorStand) != null) {
                player.sendMessage("§cVending machine current in use, please wait...");
                return;
            }
            VMGUI ui = new VMGUI(armorStand, player);
            // Note that on top of what getItemInMainHand()'s documentation suggests, the
            // method most possibly returns a shallow copy.
            ItemStack itemInHand = player.getEquipment().getItemInMainHand();
            if (itemInHand.getType().equals(Material.AIR)) {
                ui.openOwnerUI();
            } else {
                // New conversation must begin in a different tick that cancelled conversation
                Bukkit.getScheduler().runTask(CustomShop.getPlugin(),
                        () -> state.startConversation(new SetPriceConversationFactory(itemInHand)));
            }
            state.setShopGUI(ui);
        }
    }
}
