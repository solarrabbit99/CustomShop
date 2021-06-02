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

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MessageUtils {
    public static String convertMessage(String message, String ownerID, Player viewer, double total, ItemStack item,
            int amount) {
        ItemMeta meta = item.getItemMeta();
        String itemName = item == null ? "" : meta.hasDisplayName() ? meta.getDisplayName() : item.getType().toString();
        return convertMessage(message, ownerID, viewer, total, itemName, amount);
    }

    public static String convertMessage(String message, String ownerID, OfflinePlayer viewer, double total,
            String itemName, int amount) {
        message = message.replaceAll("\\{%customer%\\}", viewer == null ? "" : viewer.getName());
        message = message.replaceAll("\\{%owner%\\}", Bukkit.getOfflinePlayer(UUID.fromString(ownerID)).getName());
        message = message.replaceAll("\\{%total%\\}", "\\$" + getHumanReadableNumber(total));
        message = message.replaceAll("\\{%item%\\}", itemName == null ? "" : itemName);
        message = message.replaceAll("\\{%amount%\\}", "" + amount);
        return message;
    }

    public static String getHumanReadableNumber(double number) {
        if (number >= 1000000000) {
            return String.format("%.2fB", number / 1000000000.0);
        }
        if (number >= 1000000) {
            return String.format("%.2fM", number / 1000000.0);
        }
        if (number >= 1000) {
            return String.format("%.2fK", number / 1000.0);
        }
        return String.valueOf(number);
    }
}
