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

package com.paratopiamc.customshop.gui;

import org.bukkit.entity.ArmorStand;

/**
 * Encapsulates a shop GUI, which contains inventory views for interacting with
 * custom shops. Implementing classes do not check for any permissions regarding
 * shop access.
 */
public interface ShopGUI {
    /**
     * Opens public InventoryView of the shop that facilitates purchasing or selling
     * of items to/from the shop.
     */
    void openUI();

    /**
     * Opens owner's InventoryView of the shop that allows adding or removing shop
     * items.
     */
    void openOwnerUI();

    /**
     * Get the armor stand entity associated with the shop.
     *
     * @return armor stand reference
     */
    ArmorStand getArmorStand();

    /**
     * Checks if viewer is owner of the shop.
     *
     * @return {@code true} if viewer is owner of the shop
     */
    boolean isOwner();

    /**
     * Save any changes to player and/or the shop.
     */
    void saveInventories();
}
