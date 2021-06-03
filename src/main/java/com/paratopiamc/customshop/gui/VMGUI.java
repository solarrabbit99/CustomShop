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
import org.bukkit.block.ShulkerBox;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.paratopiamc.customshop.plugin.CustomShop;
import com.paratopiamc.customshop.utils.MessageUtils;
import com.paratopiamc.customshop.utils.UIUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

/** Custom GUI for vending machines. */
public class VMGUI extends ShopGUI {
    /**
     * Inventory viewed by normal players, consisting of UI elements such as exit
     * buttons, next page etc. Each item for sale is also labelled with their
     * respective prices.
     */
    private Inventory inventoryView;
    /**
     * A copy of the original inventory of the shulker, also inventory viewed by
     * owners. This is where the true items are retrieve in event of purchases.
     */
    private Inventory inventory;
    /**
     * A copy of the block state from source container (Shulker Box), required to
     * save items from {@link #inventory} to the true ShulkerBox. Any changes to
     * this block state does not translate to that of the original source.
     */
    private ShulkerBox sourceImage;
    /**
     * A mapping of items to their prices. This mapping is retrieved from and
     * eventually synced with the lure of the shulker box.
     */
    private HashMap<ItemStack, Double> prices;

    /**
     * Constructor method for vending machine. Retrieves the items from the source
     * container that the armor stand holds.
     *
     * @param armorStand armor stand containing source container
     * @param player     player viewing the GUI
     */
    public VMGUI(ArmorStand armorStand, Player player) {
        super(player, armorStand, armorStand.getEquipment().getChestplate().getItemMeta().getDisplayName());
        ItemStack block = armorStand.getEquipment().getChestplate();
        BlockStateMeta blockMeta = (BlockStateMeta) block.getItemMeta();
        this.sourceImage = (ShulkerBox) blockMeta.getBlockState();

        inventoryView = Bukkit.createInventory(null, 9 * 4, "§5§lVending Machine");
        inventory = Bukkit.createInventory(null, 9 * 3, "§5§lVending Machine Content");

        // Setting up UI elemenets on the last row.
        int[] blackSlots = new int[] { 0, 1, 2, 3, 5, 6, 7, 8 };
        for (int i : blackSlots) {
            UIUtils.createItem(inventoryView, 3, i, Material.BLACK_STAINED_GLASS_PANE, 1, " ");
        }
        UIUtils.createItem(inventoryView, 3, 4, Material.BARRIER, 1, "§cClose", "");

        prices = this.stringListToDoubleArray(blockMeta.getLore(), sourceImage.getInventory());

        Inventory shulkerContent = sourceImage.getInventory();
        for (int i = 0; i < shulkerContent.getSize(); i++) {
            ItemStack item = shulkerContent.getItem(i);
            if (item != null) {
                ItemStack key = item.clone();
                key.setAmount(1);
                inventoryView.setItem(i, UIUtils.setPrice(item, prices.get(key), true));
                inventory.setItem(i, item);
            }
        }
    }

    /**
     * Gets the original copy of item without price lore.
     *
     * @param index index of the item in the inventory
     * @return the copy of the item
     */
    public ItemStack getItem(int index) {
        return this.inventory.getItem(index).clone();
    }

    /**
     * For converting lore list of shop's container to a {@link HashMap} of item
     * prices. Each item is mapped to the first available price stated in lore. The
     * lore is assumed to be of the same size as the inventory. As accordance to
     * {@link ItemStack}'s {@link #equals(Object)} method, the keys stored in the
     * resulting HashMap is of amount set to 1, and is equal to an item if and only
     * if {@link ItemStack#isSimilar(ItemStack)} returns {@code true} and item
     * amount is set to 1.
     *
     * @param lore lore list obtained from shulker's meta, cannot be {@code null}
     * @return mapping of items to their corresponding prices
     * @throws NullPointerException  if any of the String is null
     * @throws NumberFormatException if any of the String is not of double format
     */
    private HashMap<ItemStack, Double> stringListToDoubleArray(List<String> lore, Inventory inventory) {
        HashMap<ItemStack, Double> result = new HashMap<>();
        for (int i = 0; i < lore.size(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null) {
                item = item.clone();
                item.setAmount(1);
                if (!result.containsKey(item)) {
                    result.put(item, Double.parseDouble(lore.get(i)));
                }
            }
        }
        return result;
    }

    /**
     * For converting mapping of prices to lore list. The index of lore corresponds
     * to index of item in {@link #inventory}, similarly for size.
     *
     * @return List of string representation of the prices
     */
    public List<String> doubleToStringList() {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < this.inventory.getSize(); i++) {
            ItemStack item = this.inventory.getItem(i);
            if (item != null) {
                ItemStack key = item.clone();
                key.setAmount(1);
                Double price = this.prices.get(key);
                price = price == null ? 0.0 : price;
                result.add(price.toString());
            } else {
                result.add("0.0");
            }
        }
        return result;
    }

    /**
     * {@inheritDoc} This method attempts to replace the original shulker in the
     * armor stand's chestplate slot with a duplicate of the same name and updated
     * contents, as the original copy is not retrievable.
     */
    @Override
    public void saveInventories() {
        // Does non-null armorStand imply non-null ShopGUI?
        if (armorStand != null) {
            for (int i = 0; i < 27; i++) {
                sourceImage.getInventory().setItem(i, inventory.getItem(i));
            }
            ItemStack container = armorStand.getEquipment().getChestplate();
            BlockStateMeta shulkerMeta = (BlockStateMeta) container.getItemMeta();
            shulkerMeta.setLore(this.doubleToStringList());
            shulkerMeta.setBlockState(sourceImage);
            container.setItemMeta(shulkerMeta);
            armorStand.getEquipment().setChestplate(container);
        }
    }

    /**
     * Player purchases item from shop. This event is cancelled if:
     * <ul>
     * <li>Shop does not have the specified amount of items
     * <li>Player inventory does not have enough space
     * <li>Player does not have enough money to purchase the specified amount of
     * items
     * </ul>
     * Sends the outcome message of this event to viewer.
     *
     * @param item   item to be purchased
     * @param amount amount of item the player intended to purchase
     */
    @Override
    public void purchaseItem(ItemStack item, int amount) {
        if (item == null) {
            viewer.sendMessage("§cItem is null...");
            return;
        } else if (!inventory.containsAtLeast(item, amount)) {
            viewer.sendMessage(
                    MessageUtils.convertMessage(CustomShop.getPlugin().getConfig().getString("customer-buy-fail-item"),
                            ownerID, viewer, 0, item, amount));
            return;
        }

        ItemStack key = item.clone();
        key.setAmount(1);
        double totalCost = amount * prices.get(key);

        Inventory pInventory = viewer.getInventory();
        if (!UIUtils.hasSpace(pInventory, item, amount)) {
            viewer.sendMessage(
                    MessageUtils.convertMessage(CustomShop.getPlugin().getConfig().getString("customer-buy-fail-space"),
                            ownerID, viewer, totalCost, item, amount));
        } else if (super.ownerSell(amount, totalCost, item)) { // Valid transaction
            item.setAmount(amount);
            // addItem mutates item, use temp to clone a copy
            ItemStack temp = item.clone();
            pInventory.addItem(item);
            inventory.removeItem(temp);
        }
    }

    /**
     * List all similar ItemStack with the specified price. {@inheritDoc}
     */
    @Override
    public String listPrice(ItemStack item, double price) {
        if (item == null || item.getType() == Material.AIR) {
            return "§cYou are not holding anything in your main hand!";
        } else {
            ItemStack key = item.clone();
            key.setAmount(1);
            prices.put(key, price);
            ItemMeta meta = item.getItemMeta();
            String name = meta.hasDisplayName() ? meta.getDisplayName() : item.getType().toString();
            return "§aSuccessfully listed " + name + "§r§a for $" + MessageUtils.getHumanReadableNumber(price) + "!";
        }
    }

    @Override
    public void openUI() {
        viewer.playSound(armorStand.getLocation(), Sound.BLOCK_BARREL_OPEN, 0.5F, 1.0F);
        viewer.openInventory(inventoryView);
    }

    @Override
    public void openOwnerUI() {
        viewer.playSound(armorStand.getLocation(), Sound.BLOCK_BARREL_OPEN, 0.5F, 1.0F);
        viewer.openInventory(inventory);
    }
}
