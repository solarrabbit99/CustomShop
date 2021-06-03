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

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Utility class for GUI/Inventory related methods.
 */
public final class UIUtils {
    private UIUtils() {
    }

    /**
     * Creates and inserts ItemStack in given inventory. Returns the ItemStack.
     *
     * @param inv         inventory to insert stack
     * @param row         row in inventory to insert stack
     * @param column      column in inventory to insert stack
     * @param material    material of the item
     * @param amount      amount of item in the stack
     * @param displayName display name of the item
     * @param lore        lore of item
     * @return ItemStack of given specifications
     */
    public static ItemStack createItem(Inventory inv, int row, int column, Material material, int amount,
            String displayName, String... lore) {
        ItemStack item = new ItemStack(material, amount);
        List<String> loreStrings = new ArrayList<>();
        for (String s : lore) {
            loreStrings.add(s);
        }
        int invSlot = 9 * row + column;

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(loreStrings);
        item.setItemMeta(meta);
        inv.setItem(invSlot, item);
        return item;
    }

    /**
     * Creates and inserts ItemStack in given inventory. Returns the ItemStack.
     *
     * @param inv         inventory to insert stack
     * @param slot        slot in inventory to insert stack
     * @param material    material of the item
     * @param amount      amount of item in the stack
     * @param displayName display name of the item
     * @param lore        lore of item
     * @return ItemStack of given specifications
     */
    public static ItemStack createItem(Inventory inv, int slot, Material material, int amount, String displayName,
            String... lore) {
        ItemStack item = new ItemStack(material, amount);
        List<String> loreStrings = new ArrayList<>();
        for (String s : lore) {
            loreStrings.add(s);
        }

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(loreStrings);
        item.setItemMeta(meta);
        inv.setItem(slot, item);
        return item;
    }

    /**
     * Creates and inserts ItemStack in given inventory. Returns the ItemStack.
     *
     * @param inv           inventory to insert stack
     * @param slot          slot in inventory to insert stack
     * @param material      material of the item
     * @param amount        amount of item in the stack
     * @param customModelID custom model data's ID
     * @param displayName   display name of the item
     * @param lore          lore of item
     * @return ItemStack of given specifications
     */
    public static ItemStack createItem(Inventory inv, int slot, Material material, int amount, int customModelID,
            String displayName, String... lore) {
        ItemStack item = new ItemStack(material, amount);
        List<String> loreStrings = new ArrayList<>();
        for (String s : lore) {
            loreStrings.add(s);
        }

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(loreStrings);
        meta.setCustomModelData(customModelID);
        item.setItemMeta(meta);
        inv.setItem(slot, item);
        return item;
    }

    /**
     * Returns a new copy of ItemStack with given lore.
     *
     * @param item ItemStack to clone
     * @param lore array of string
     * @return new instance of ItemStack
     */
    public static ItemStack loreItem(ItemStack item, String... lore) {
        ItemStack clone = item.clone();
        ItemMeta meta = item.getItemMeta();
        List<String> loreStrings = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        for (String s : lore) {
            loreStrings.add(s);
        }
        meta.setLore(loreStrings);
        clone.setItemMeta(meta);
        return clone;
    }

    /**
     * Set price as lore of the item.
     *
     * @param item      ItemStack to label
     * @param price     given price in double
     * @param withSpace whether to create partition between item's lore and the
     *                  price tag
     * @return new instance of ItemStack
     */
    public static ItemStack setPrice(ItemStack item, double price, boolean withSpace) {
        return item == null ? null
                : withSpace
                        ? loreItem(item, "§7--------------------",
                                "§5Price: §e$" + MessageUtils.getHumanReadableNumber(price))
                        : loreItem(item, "§5Price: §e$" + MessageUtils.getHumanReadableNumber(price));
    }

    /**
     * Whether the given inventory has sufficient space for adding the amount of
     * specified item. This method can be called before
     * {@link Inventory#addItem(ItemStack...)} if it is intended to not add any of
     * the item into inventory given insufficient space.
     * 
     * @param inventory to add items into
     * @param item      reference item of any positive amount
     * @param amount    amount of item intended to add, disregards the amount tagged
     *                  to {@code item}
     * @return {@code true} if inventory has enough space for the amount of item
     */
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
