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

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.entity.ArmorStand.LockType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * Encapsulates a creator of a shop. Each type of custom shops should have a
 * creator of its own type that implements this interface.
 */
public abstract class ShopCreator {
    /**
     * The main method where the creator creates the shop with its own requirements
     * and specifications. A message is returned as a feedback to player on whether
     * the shop is created successfully.
     *
     * @param location location in which the shop will attempt on top of
     * @param owner    owner of the shop
     * @param item     design of the shop
     * @param isAdmin  whether the shop is an admin shop
     */
    public abstract void createShop(Location location, Player owner, ItemStack item, boolean isAdmin);

    /**
     * Locks armor stand to prevent accessibility of items within its slots.
     *
     * @param armorStand ArmorStand to lock
     */
    protected void lockArmorStand(ArmorStand armorStand) {
        armorStand.setInvulnerable(true);
        armorStand.setGravity(false);
        armorStand.setVisible(false);

        armorStand.addEquipmentLock(EquipmentSlot.HEAD, LockType.ADDING_OR_CHANGING);
        armorStand.addEquipmentLock(EquipmentSlot.CHEST, LockType.ADDING_OR_CHANGING);
        armorStand.addEquipmentLock(EquipmentSlot.LEGS, LockType.ADDING_OR_CHANGING);
        armorStand.addEquipmentLock(EquipmentSlot.FEET, LockType.ADDING_OR_CHANGING);
        armorStand.addEquipmentLock(EquipmentSlot.HAND, LockType.ADDING_OR_CHANGING);
        armorStand.addEquipmentLock(EquipmentSlot.OFF_HAND, LockType.ADDING_OR_CHANGING);
    }
}
