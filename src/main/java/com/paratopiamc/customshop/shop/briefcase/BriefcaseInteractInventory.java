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
import com.paratopiamc.customshop.shop.conversation.AddConversationFactory;
import com.paratopiamc.customshop.shop.conversation.PurchaseConversationFactory;
import com.paratopiamc.customshop.shop.conversation.RetrieveConversationFactory;
import com.paratopiamc.customshop.shop.conversation.SellConversationFactory;
import com.paratopiamc.customshop.shop.conversation.SetPriceConversationFactory;
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
                    if (displayName.equals("§cClose"))
                        Bukkit.getScheduler().runTask(CustomShop.getPlugin(), () -> player.closeInventory());
                } else if (evt.getSlot() < 27) {
                    ItemStack item = ui.getItem();
                    if (ui.isSelling()) {
                        state.startTransaction(item, new PurchaseConversationFactory());
                    } else {
                        state.startTransaction(item, new SellConversationFactory());
                    }
                    Bukkit.getScheduler().runTask(CustomShop.getPlugin(), () -> player.closeInventory());
                }
            }
            evt.setCancelled(true);
        } else if (title.equalsIgnoreCase("§5§lNewt's Briefcase Settings")) {
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
                        ui.setSelling(false);
                        break;
                    case "§6Buying":
                        ui.setSelling(true);
                        break;
                    case "§6Change Price":
                        // Don't have to start conversation in separate tick as player in GUI implies
                        // player not conversing.
                        state.startConversation(new SetPriceConversationFactory(ui.getItem()));
                        Bukkit.getScheduler().runTask(CustomShop.getPlugin(), () -> player.closeInventory());
                        break;
                    case "§6Add Items":
                        state.startConversation(new AddConversationFactory());
                        Bukkit.getScheduler().runTask(CustomShop.getPlugin(), () -> player.closeInventory());
                        break;
                    case "§6Retrieve Items":
                        state.startConversation(new RetrieveConversationFactory());
                        Bukkit.getScheduler().runTask(CustomShop.getPlugin(), () -> player.closeInventory());
                        break;
                    default:
                        break;
                    }
                }
            }
            evt.setCancelled(true);
        }
    }
}
