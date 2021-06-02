package com.paratopiamc.customshop.utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {
    public static boolean hasSpace(Inventory inventory, ItemStack item, int amount) {
        int totalSpace = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack pItem = inventory.getItem(i);
            if (pItem == null) {
                totalSpace += item.getMaxStackSize();
            } else if (pItem.isSimilar(item)) {
                totalSpace += pItem.getMaxStackSize() - pItem.getAmount();
            }
        }
        return totalSpace >= amount;
    }
}
