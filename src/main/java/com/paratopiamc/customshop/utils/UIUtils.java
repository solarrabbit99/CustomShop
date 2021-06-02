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
 * This class stores utility methods for other classes.
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
     * @param item  ItemStack to label
     * @param price given price in double
     * @return new instance of ItemStack
     */
    public static ItemStack setPrice(ItemStack item, double price, boolean withSpace) {
        return item == null ? null
                : withSpace
                        ? loreItem(item, "§7--------------------",
                                "§5Price: §e$" + MessageUtils.getHumanReadablePriceFromNumber(price))
                        : loreItem(item, "§5Price: §e$" + MessageUtils.getHumanReadablePriceFromNumber(price));
    }

    /**
     * For converting lore list of shop's container to an array of item prices. The
     * index of prices corresponds to index of item in the container. Size of array
     * is in accordance to that of the given list.
     *
     * @param lore lore list obtained from item's meta
     * @return double array
     * @throws NullPointerException  if any of the String is null
     * @throws NumberFormatException if any of the String is not of double format
     */
    public static double[] stringListToDoubleArray(List<String> lore) {
        double[] prices = new double[lore.size()];
        for (int i = 0; i < lore.size(); i++) {
            prices[i] = Double.parseDouble(lore.get(i));
        }
        return prices;
    }

    /**
     * For converting an array of item prices to lore list. The index of lore
     * corresponds to index of item in the container. Size of list is in accordance
     * to that of the given array.
     *
     * @param prices double array
     * @return List of string representation of the prices
     */
    public static List<String> doubleToStringList(double[] prices) {
        List<String> result = new ArrayList<>(prices.length);
        for (int i = 0; i < prices.length; i++) {
            result.add(Double.toString(prices[i]));
        }
        return result;
    }
}
