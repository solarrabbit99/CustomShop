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
import com.paratopiamc.customshop.plugin.CustomShop;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Utility class for messages/number formatting related methods.
 */
public class MessageUtils {
    private MessageUtils() {
    }

    /**
     * Formats messages to replace place holders with their respective texts.
     * 
     * @param message the entire message containing placeholders
     * @param ownerID String representation of shop owner's UUID
     * @param viewer  customer of the transaction, who can be offline when
     *                transaction messages are sent to the shop owner
     * @param total   total amount of moeny involved in transaction
     * @param item    item involved in transaction
     * @param amount  amount of items involved in transaction
     * @return formatted message
     */
    public static String convertMessage(String message, String ownerID, Player viewer, double total, ItemStack item,
            int amount) {
        ItemMeta meta = item.getItemMeta();
        String itemName = item == null ? "" : meta.hasDisplayName() ? meta.getDisplayName() : item.getType().toString();
        return convertMessage(message, ownerID, viewer, total, itemName, amount);
    }

    /**
     * Formats messages to replace place holders with their respective texts.
     * 
     * @param message  the entire message containing placeholders
     * @param ownerID  String representation of shop owner's UUID
     * @param viewer   customer of the transaction, who can be offline when
     *                 transaction messages are sent to the shop owner
     * @param total    total amount of moeny involved in transaction
     * @param itemName material type of the item or display name of the item, if any
     * @param amount   amount of items involved in transaction
     * @return formatted message
     */
    public static String convertMessage(String message, String ownerID, OfflinePlayer viewer, double total,
            String itemName, int amount) {
        message = message.replaceAll("\\{%customer%\\}", viewer == null ? "" : viewer.getName());
        message = message.replaceAll("\\{%owner%\\}", Bukkit.getOfflinePlayer(UUID.fromString(ownerID)).getName());
        message = message.replaceAll("\\{%total%\\}", getReadablePriceTag(total));
        message = message.replaceAll("\\{%item%\\}", itemName == null ? "" : itemName);
        message = message.replaceAll("\\{%amount%\\}", "" + amount);
        return message;
    }

    public static String getReadablePriceTag(double number) {
        // TODO: include the use of placeholder api to better format currency
        return CustomShop.getPlugin().getEconomy().format(number);
    }

    /**
     * Format number with units (K for thousand, M for million, B for billion) with
     * 2 decimal places.
     *
     * @param number unformatted number
     * @return formatted number in string form
     * @deprecated use vault's/placeholder api's formatting methods
     */
    @Deprecated
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
