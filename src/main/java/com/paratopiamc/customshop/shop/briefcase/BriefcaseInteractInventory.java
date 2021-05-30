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

package com.paratopiamc.customshop.shop.briefcase;

import com.paratopiamc.customshop.gui.BriefcaseGUI;
import com.paratopiamc.customshop.player.PlayerState;
import com.paratopiamc.customshop.plugin.CustomShop;
import com.paratopiamc.customshop.shop.conversation.PurchaseConversationFactory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BriefcaseInteractInventory implements Listener {

    /**
     * Event handler for interactions with shop's GUI.
     *
     * @param evt event of inventory clicking
     */
    @EventHandler
    public void clickShop(InventoryClickEvent evt) {
        if (evt.getCurrentItem() == null) {
            return;
        }
        InventoryHolder holder = evt.getClickedInventory().getHolder();
        Player player = (Player) evt.getWhoClicked();
        String title = evt.getView().getTitle();
        if (title.equalsIgnoreCase("§5§lNewt's Briefcase")) {
            if (holder == null) {
                ItemMeta itemMeta = evt.getCurrentItem().getItemMeta();
                PlayerState state = PlayerState.getPlayerState(player);
                BriefcaseGUI ui = (BriefcaseGUI) state.getShopGUI();
                if (evt.getSlot() >= 27 && itemMeta.hasDisplayName()) {
                    String displayName = itemMeta.getDisplayName();
                    switch (displayName) {
                    case "§cClose":
                        Bukkit.getScheduler().runTask(CustomShop.getPlugin(), () -> player.closeInventory());
                        break;
                    case "§6Selling":
                    case "§6Buying":
                    case "§6Change Price":
                    case "§6Add Items":
                    case "§6Retrieve Items":
                    default:
                        break;
                    }
                } else if (evt.getSlot() < 27) {
                    ItemStack item = ui.getItem();
                    state.startPurchase(item, new PurchaseConversationFactory());
                    Bukkit.getScheduler().runTask(CustomShop.getPlugin(), () -> player.closeInventory());
                }
            }
            evt.setCancelled(true);
        }
    }
}
