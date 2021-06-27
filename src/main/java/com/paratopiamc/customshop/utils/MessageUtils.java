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
import java.util.regex.Matcher;
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

    public static class Message {
        private String message;
        private String itemName;
        private boolean hasDisplayName;

        public Message(String message, String itemName, boolean hasDisplayName) {
            this.message = message;
            this.itemName = itemName;
            this.hasDisplayName = hasDisplayName;
        }

        public String getMessage() {
            return this.message;
        }

        public String getItemName() {
            return this.itemName;
        }

        public boolean hasDisplayName() {
            return this.hasDisplayName;
        }
    }

    public static Message getMessage(String message, String ownerID, OfflinePlayer viewer, double total,
            String itemName, boolean hasDisplayName, int amount) {
        String rawMessage = convertMessage(message, ownerID, viewer, total, amount);
        return new Message(rawMessage, itemName, hasDisplayName);
    }

    public static Message getMessage(String message, String ownerID, OfflinePlayer viewer, double total, ItemStack item,
            int amount) {
        String rawMessage = convertMessage(message, ownerID, viewer, total, amount);
        if (item == null) {
            return new Message(rawMessage, null, false);
        } else {
            ItemMeta meta = item.getItemMeta();
            String itemName = meta.hasDisplayName() ? meta.getDisplayName() : item.getType().toString();
            return new Message(rawMessage, itemName, meta.hasDisplayName());
        }
    }

    /**
     * Formats messages to replace place holders with their respective texts.
     * 
     * @param message the entire message containing placeholders
     * @param ownerID String representation of shop owner's UUID
     * @param viewer  customer of the transaction, who can be offline when
     *                transaction messages are sent to the shop owner
     * @param total   total amount of moeny involved in transaction
     * @param amount  amount of items involved in transaction
     * @return formatted message
     */
    private static String convertMessage(String message, String ownerID, OfflinePlayer viewer, double total,
            int amount) {
        message = message.replaceAll("\\{%customer%\\}", viewer == null ? "" : viewer.getName());
        message = message.replaceAll("\\{%owner%\\}", Bukkit.getOfflinePlayer(UUID.fromString(ownerID)).getName());
        message = message.replaceAll("\\{%total%\\}", Matcher.quoteReplacement(getReadablePriceTag(total)));
        message = message.replaceAll("\\{%amount%\\}", "" + amount);
        return message;
    }

    public static String getReadablePriceTag(double number) {
        return CustomShop.getPlugin().getEconomy().format(number);
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
     * @deprecated use {@link #getMessage} instead
     */
    @Deprecated
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
     * @deprecated use {@link #getMessage} instead
     */
    @Deprecated
    public static String convertMessage(String message, String ownerID, OfflinePlayer viewer, double total,
            String itemName, int amount) {
        message = message.replaceAll("\\{%customer%\\}", viewer == null ? "" : viewer.getName());
        message = message.replaceAll("\\{%owner%\\}", Bukkit.getOfflinePlayer(UUID.fromString(ownerID)).getName());
        message = message.replaceAll("\\{%total%\\}", Matcher.quoteReplacement(getReadablePriceTag(total)));
        message = message.replaceAll("\\{%item%\\}", itemName == null ? "" : itemName);
        message = message.replaceAll("\\{%amount%\\}", "" + amount);
        return message;
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
