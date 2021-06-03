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

package com.paratopiamc.customshop.utils;

import java.util.Collection;
import com.paratopiamc.customshop.plugin.CustomShopLogger;
import com.paratopiamc.customshop.plugin.CustomShopLogger.Level;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Utility class for shop related methods.
 */
public class ShopUtils {
    private ShopUtils() {
    }

    /**
     * Checks if the player has permission to access the shop's listing and removal
     * features. Currently only owners, OPs or player with {@code customshop.admin}
     * permission can access the shop.
     *
     * @param armorStand {@link ArmorStand} representing the custom shop
     * @param player     player of interest
     * @return {@code true} if player has permissions
     */
    public static boolean hasShopPermission(ArmorStand armorStand, Player player) {
        if (player.hasPermission("customshop.admin") || player.isOp()) {
            return true;
        }
        String customName = armorStand.getCustomName();
        if (customName == null || !customName.equals("§5§lVending Machine"))
            return false;
        else {
            EntityEquipment equipment = armorStand.getEquipment();
            ItemStack item = equipment.getChestplate();
            if (item != null && item.getType() != Material.AIR) {
                ItemMeta meta = item.getItemMeta();
                if (!meta.hasDisplayName()) {
                    Location standLocation = armorStand.getLocation();
                    CustomShopLogger.sendMessage(
                            "Custom shop without owner's display name detected at " + standLocation + "!", Level.FAIL);
                    return false;
                }
                String ownerUUID = meta.getDisplayName();
                return player.getUniqueId().toString().equals(ownerUUID);
            } else {
                Location standLocation = armorStand.getLocation();
                CustomShopLogger.sendMessage("Custom shop without owner item detected at " + standLocation + "!",
                        Level.FAIL);
                return false;
            }
        }
    }

    /**
     * Checks if a custom shop is in the block targeted by the player. This is done
     * by checking if:
     * <ul>
     * <li>the target block is of type {@link Material#BARRIER}
     * <li>there exists exactly one entity in the barrier block
     * <li>the entity is an instance of {@link ArmorStand}
     * <li>the armor stand has a custom name corresponding to a type of custom shop
     * </ul>
     * Returns {@code null} if any of the above conditions are not satisfied.
     *
     * @param targetBlock block targeted by player, presumably a barrier block
     * @return {@link ArmorStand} entity associated with a custom shop
     */
    public static ArmorStand getArmorStand(Block targetBlock) {
        if (targetBlock == null) {
            return null;
        }
        Location loc = new Location(targetBlock.getWorld(), targetBlock.getX() + 0.5, targetBlock.getY(),
                targetBlock.getZ() + 0.5);
        Collection<Entity> list = targetBlock.getWorld().getNearbyEntities(loc, 0.5, 0.5, 0.5);
        if (targetBlock.getType() != Material.BARRIER || list.size() != 1) {
            return null;
        } else {
            Entity shopEntity = (Entity) list.toArray()[0];
            if (shopEntity instanceof ArmorStand) {
                ArmorStand armorStand = (ArmorStand) shopEntity;
                String name = armorStand.getCustomName();
                boolean valid;
                switch (name == null ? null : name) {
                case "§5§lVending Machine":
                    valid = true;
                    break;
                case "§5§lNewt's Briefcase":
                    valid = true;
                    break;
                default:
                    valid = false;
                    break;
                }
                return valid ? armorStand : null;
            } else {
                return null;
            }
        }
    }
}
