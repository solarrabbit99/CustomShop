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
        message = message.replaceAll("\\{%total%\\}", "\\$" + getHumanReadablePriceFromNumber(total));
        message = message.replaceAll("\\{%item%\\}", itemName == null ? "" : itemName);
        message = message.replaceAll("\\{%amount%\\}", "" + amount);
        return message;
    }

    public static String getHumanReadablePriceFromNumber(double number) {
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
