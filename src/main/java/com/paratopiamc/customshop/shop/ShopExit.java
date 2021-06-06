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

import com.paratopiamc.customshop.player.PlayerState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 * Listener for {@link InventoryCloseEvent} and saves player's inventory.
 */
public class ShopExit implements Listener {
    /**
     * Event handler for shop GUI's close event.
     *
     * @param evt event of inventory clicking.
     */
    @EventHandler
    public void closeShop(InventoryCloseEvent evt) {
        if (!((Player) evt.getPlayer()).isConversing()) {
            PlayerState.getPlayerState((Player) evt.getPlayer()).clearShopInteractions();
        }
    }
}
