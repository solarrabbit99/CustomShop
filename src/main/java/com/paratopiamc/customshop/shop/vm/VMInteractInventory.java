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

import com.paratopiamc.customshop.gui.ShopGUI;
import com.paratopiamc.customshop.gui.VMGUI;
import com.paratopiamc.customshop.player.PlayerState;
import com.paratopiamc.customshop.plugin.CustomShop;
import com.paratopiamc.customshop.shop.conversation.PurchaseConversationFactory;
import com.paratopiamc.customshop.utils.LanguageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Listener for players interacting with custom shops' GUI, containing handlers
 * for which the player (owner or not) purchases items.
 */
public class VMInteractInventory implements Listener {

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
        Player player = (Player) evt.getWhoClicked();
        PlayerState state = PlayerState.getPlayerState(player);
        ShopGUI shopGUI = state.getShopGUI();

        if (shopGUI instanceof VMGUI
                && player.getOpenInventory().getTopInventory().equals(shopGUI.getInteractingInventory())
                && !shopGUI.interactingInventoryIsOwnerView()) {
            evt.setCancelled(true);
            if (!evt.getClickedInventory().equals(shopGUI.getInteractingInventory())) {
                return;
            }
        } else {
            return;
        }

        ItemMeta itemMeta = evt.getCurrentItem().getItemMeta();
        if (itemMeta.hasDisplayName()
                && itemMeta.getDisplayName().equals("§c" + LanguageUtils.getString("icons.close"))) {
            Bukkit.getScheduler().runTask(CustomShop.getPlugin(), () -> player.closeInventory());
        } else if (evt.getSlot() < 27) {
            VMGUI ui = (VMGUI) shopGUI;
            ItemStack item = ui.getItem(evt.getSlot());
            state.startTransaction(item, new PurchaseConversationFactory());
            Bukkit.getScheduler().runTask(CustomShop.getPlugin(), () -> player.closeInventory());
        }

    }
}
