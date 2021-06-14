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

import java.util.ArrayList;
import java.util.List;
import com.paratopiamc.customshop.plugin.CustomShop;
import com.paratopiamc.customshop.plugin.CustomShopLogger;
import com.paratopiamc.customshop.plugin.CustomShopLogger.Level;
import com.paratopiamc.customshop.utils.LanguageUtils;
import com.paratopiamc.customshop.utils.MessageUtils;
import com.paratopiamc.customshop.utils.UIUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * GUI for Newt's Briefcase.
 */
public class BriefcaseGUI extends ShopGUI {
    /**
     * Inventory viewed by normal players, consisting of UI elements such as exit
     * buttons, next page etc. Item for selling/buying is also labelled with its
     * price.
     */
    private Inventory normalView;
    /**
     * Inventory viewed by owners. This is where the items are placed in or
     * retrieved.
     */
    private Inventory ownerView;
    /**
     * Price of item.
     */
    private double price;
    /**
     * Whether the shop is selling.
     */
    private boolean selling;
    /**
     * Quantity of item in the shop.
     */
    private int quantity;

    /**
     * Constructor for briefcase's GUI, which can be called regardless whether the
     * briefcase has been initialized with any items.
     *
     * @param armorStand verified to be a briefcase, cannot be {@code null}
     * @param player     viewer of the GUI
     * @throws NullPointerException if {@code armorStand}'s chestplate item does not
     *                              have a display name
     */
    public BriefcaseGUI(ArmorStand armorStand, Player player) {
        super(player, armorStand, armorStand.getEquipment().getChestplate().getItemMeta().getDisplayName());
        EntityEquipment armorStandContent = armorStand.getEquipment();
        ItemStack item = armorStandContent.getLeggings();
        if (item != null && item.getType() != Material.AIR) {
            normalView = Bukkit.createInventory(null, 9 * 4, LanguageUtils.getString("newt-briefcase-customer"));
            ownerView = Bukkit.createInventory(null, 9 * 4, LanguageUtils.getString("newt-briefcase-owner"));

            ItemStack placeHolder = armorStandContent.getChestplate();
            List<String> info = placeHolder.getItemMeta().getLore();
            this.price = Double.parseDouble(info.get(0));
            this.quantity = Integer.parseInt(info.get(1));
            this.selling = Boolean.parseBoolean(info.get(2));

            // Setting up UI elements on the last row.
            int[] blackSlots = new int[] { 0, 1, 2, 3, 5, 6, 7, 8 };
            for (int i : blackSlots) {
                UIUtils.createItem(normalView, 3, i, Material.BLACK_STAINED_GLASS_PANE, 1, " ");
                UIUtils.createItem(ownerView, 3, i, Material.BLACK_STAINED_GLASS_PANE, 1, " ");
            }
            UIUtils.createItem(normalView, 3, 4, Material.BARRIER, 1, "§c" + LanguageUtils.getString("icons.close"));

            UIUtils.createItem(ownerView, 3, 2, Material.OAK_SIGN, 1,
                    "§6" + (this.selling ? LanguageUtils.getString("price-tag.selling")
                            : LanguageUtils.getString("price-tag.buying")),
                    "§2" + LanguageUtils.getString("icons.selling-status.lore"));
            UIUtils.createItem(ownerView, 3, 3, Material.NAME_TAG, 1,
                    "§6" + LanguageUtils.getString("icons.change-price.title"),
                    "§2" + LanguageUtils.getString("icons.change-price.lore"));
            UIUtils.createItem(ownerView, 3, 4, Material.HOPPER_MINECART, 1,
                    "§6" + LanguageUtils.getString("icons.add-items.title"),
                    "§2" + LanguageUtils.getString("icons.add-items.lore"));
            UIUtils.createItem(ownerView, 3, 5, Material.CHEST_MINECART, 1,
                    "§6" + LanguageUtils.getString("icons.retrieve-items.title"),
                    "§2" + LanguageUtils.getString("icons.retrieve-items.lore"));
            UIUtils.createItem(ownerView, 3, 6, Material.BARRIER, 1, "§c" + LanguageUtils.getString("icons.close"));

            normalView.setItem(13, UIUtils.setPriceTag(item, this.price, this.selling, this.isAdmin, this.quantity));
            ownerView.setItem(13, UIUtils.setPriceTag(item, this.price, this.selling, this.isAdmin, this.quantity));
        }
    }

    /**
     * Returns a copy of item that the shop is selling/buying. {@code null} or
     * {@link ItemStack} with type {@link Material#AIR} if no such item exists.
     *
     * @return item that shop is selling/buying
     */
    public ItemStack getItem() {
        return this.armorStand.getEquipment().getLeggings().clone();
    }

    /**
     * Return if shop has an {@link ItemStack} set for sale/buying.
     * 
     * @return {@code true} if the shop has an item set
     */
    public boolean hasItem() {
        ItemStack item = this.armorStand.getEquipment().getLeggings();
        return item != null && item.getType() != Material.AIR;
    }

    /**
     * Initializes the item that the shop is buying/selling and its price.
     * {@inheritDoc}
     */
    public String listPrice(ItemStack item, double price) {
        if (item == null || item.getType() == Material.AIR) {
            return LanguageUtils.getString("price-convo-failed-no-item");
        } else {
            EntityEquipment armorStandContent = this.armorStand.getEquipment();
            item.setAmount(1);
            armorStandContent.setLeggings(item);

            this.updatePlaceHolderLore(1, price);
            return String.format(LanguageUtils.getString("price-convo-success"),
                    MessageUtils.getReadablePriceTag(price));
        }
    }

    /**
     * Viewer sells items to shop owner.
     *
     * @param item   cannot be {@code null}
     * @param amount amount to sell, disregards the amount tagged to {@code item}
     */
    public void sellItem(ItemStack item, int amount) {
        if (item == null) {
            viewer.sendMessage("§cItem is null...");
            return;
        }
        ItemStack clone = item.clone();
        clone.setAmount(amount);
        Inventory pInventory = viewer.getInventory();

        if (!pInventory.containsAtLeast(item, amount)) {
            viewer.sendMessage(MessageUtils.convertMessage(LanguageUtils.getString("customer-sell-fail-item"), ownerID,
                    viewer, 0, item, amount));
            return;
        }

        int remainingSpace = Integer.MAX_VALUE - this.quantity;
        double totalCost = amount * price;
        if (remainingSpace < amount && !this.isAdmin) {
            viewer.sendMessage(String.format(LanguageUtils.getString("sell-convo-failed-limit"), remainingSpace));
        } else if (super.ownerBuy(amount, totalCost, item)) { // Valid transaction
            if (!this.isAdmin) {
                this.updatePlaceHolderLore(2, this.quantity + amount);
            }
            pInventory.removeItem(clone);
        }
    }

    /**
     * Getter for whether the shop is selling items, the alternative being buying.
     *
     * @return {@code true} if the shop is selling
     */
    public boolean isSelling() {
        return this.selling;
    }

    /**
     * Setter for whether the shop is selling items, the alternative being buying.
     *
     * @param selling whether the shop is selling
     */
    public void setSelling(boolean selling) {
        this.selling = selling;
        this.updatePlaceHolderLore(3, selling);

        UIUtils.createItem(ownerView, 3, 2, Material.OAK_SIGN, 1,
                "§6" + (this.selling ? LanguageUtils.getString("price-tag.selling")
                        : LanguageUtils.getString("price-tag.buying")),
                "§2" + LanguageUtils.getString("icons.selling-status.lore"));

        ItemStack item = ownerView.getItem(13);
        ItemMeta itemMeta = item.getItemMeta();
        List<String> itemLore = itemMeta.getLore();
        itemLore.set(itemLore.size() - 2, "§5" + (this.selling ? LanguageUtils.getString("price-tag.selling")
                : LanguageUtils.getString("price-tag.buying")));
        itemMeta.setLore(itemLore);
        item.setItemMeta(itemMeta);
    }

    /**
     * Viewer (inferred to be the owner) retrieves items from the shop.
     *
     * @param amount amount of item to retrieve from shop
     */
    public void retrieveItem(int amount) {
        if (!this.hasItem()) {
            viewer.sendMessage("§cItem doesn't exist...");
            return;
        }
        ItemStack item = this.getItem();
        if (this.quantity < amount) {
            viewer.sendMessage(MessageUtils.convertMessage(LanguageUtils.getString("customer-buy-fail-item"), ownerID,
                    viewer, 0, item, amount));
            return;
        }
        Inventory pInventory = viewer.getInventory();

        if (!UIUtils.hasSpace(pInventory, item, amount)) {
            viewer.sendMessage(MessageUtils.convertMessage(LanguageUtils.getString("customer-buy-fail-space"), ownerID,
                    viewer, 0, item, amount));
        } else { // Valid operation
            List<ItemStack> stacks = new ArrayList<>();
            int stackNumber = 0;
            int currentAmount = amount;
            while (currentAmount > item.getMaxStackSize()) {
                ItemStack clone = item.clone();
                clone.setAmount(item.getMaxStackSize());
                stacks.add(clone);
                currentAmount -= item.getMaxStackSize();
                stackNumber++;
            }
            if (currentAmount != 0) {
                item.setAmount(currentAmount);
                stacks.add(item);
                stackNumber++;
            }
            if (this.updatePlaceHolderLore(2, this.quantity - amount)
                    && pInventory.addItem(stacks.toArray(new ItemStack[stackNumber])).isEmpty())
                viewer.sendMessage(String.format(LanguageUtils.getString("retrieve-convo-success"), amount));
        }
    }

    /**
     * Viewer (inferred to be the owner) adds items to the shop.
     *
     * @param amount amount of item to add to shop
     */
    public void addItem(int amount) {
        if (!this.hasItem()) {
            viewer.sendMessage("§cItem doesn't exist...");
            return;
        }
        ItemStack item = this.getItem();
        item.setAmount(amount);
        Inventory pInventory = viewer.getInventory();

        if (!pInventory.containsAtLeast(item, amount)) {
            viewer.sendMessage(MessageUtils.convertMessage(LanguageUtils.getString("customer-sell-fail-item"), ownerID,
                    viewer, 0, item, amount));
            return;
        }

        int remainingSpace = Integer.MAX_VALUE - this.quantity;
        if (remainingSpace < amount) {
            viewer.sendMessage(String.format(LanguageUtils.getString("add-convo-failed-limit"), remainingSpace));
        } else { // Valid operation
            if (this.updatePlaceHolderLore(2, this.quantity + amount) && pInventory.removeItem(item).isEmpty())
                viewer.sendMessage(String.format(LanguageUtils.getString("add-convo-success"), amount));
        }
    }

    /**
     * Updates placeholder's lore with the following properties at index:
     * <ol>
     * <li>price of the item
     * <li>current stock of the item
     * <li>boolean indicating whether shop is selling
     * </ol>
     * Sends console messages if update fails.
     * 
     * @param index    the index to update, refer above
     * @param property the updated property
     * @return {@code true} if updated succesfully
     */
    private boolean updatePlaceHolderLore(int index, Object property) {
        EntityEquipment armorStandContent = this.armorStand.getEquipment();
        ItemStack placeHolder = armorStandContent.getChestplate();
        if (placeHolder == null || placeHolder.getType() == Material.AIR) {
            CustomShopLogger.sendMessage("Briefcase without placeHolder detected at " + this.armorStand.getLocation()
                    + ", unable to update shop info. Report this error!", Level.FAIL);
            return false;
        }
        ItemMeta meta = placeHolder.getItemMeta();
        if (!meta.hasLore()) {
            CustomShopLogger.sendMessage("Briefcase's placeHolder without lore detected at "
                    + this.armorStand.getLocation() + ", unable to update shop info. Report this error!", Level.FAIL);
            return false;
        }
        List<String> lore = meta.getLore();
        if (lore.size() < 3) {
            CustomShopLogger.sendMessage("Briefcase's placeHolder with incomplete lore size detected at "
                    + this.armorStand.getLocation() + ", unable to update shop info. Report this error!", Level.FAIL);
            return false;
        } else {
            lore.set(index - 1, property.toString());
            meta.setLore(lore);
            placeHolder.setItemMeta(meta);
            armorStandContent.setChestplate(placeHolder);
            return true;
        }
    }

    @Override
    public void purchaseItem(ItemStack item, int amount) {
        if (item == null) {
            viewer.sendMessage("§cItem is null...");
            return;
        }
        if (this.quantity < amount && !this.isAdmin) {
            viewer.sendMessage(MessageUtils.convertMessage(LanguageUtils.getString("customer-buy-fail-item"), ownerID,
                    viewer, 0, item, amount));
            return;
        }
        Inventory pInventory = viewer.getInventory();
        double totalCost = amount * price;

        if (!UIUtils.hasSpace(pInventory, item, amount)) {
            viewer.sendMessage(MessageUtils.convertMessage(LanguageUtils.getString("customer-buy-fail-space"), ownerID,
                    viewer, totalCost, item, amount));
        } else if (super.ownerSell(amount, totalCost, item)) { // Valid transaction
            List<ItemStack> stacks = new ArrayList<>();
            int stackNumber = 0;
            int currentAmount = amount;
            while (currentAmount > item.getMaxStackSize()) {
                ItemStack clone = item.clone();
                clone.setAmount(item.getMaxStackSize());
                stacks.add(clone);
                currentAmount -= item.getMaxStackSize();
                stackNumber++;
            }
            if (currentAmount != 0) {
                item.setAmount(currentAmount);
                stacks.add(item);
                stackNumber++;
            }
            pInventory.addItem(stacks.toArray(new ItemStack[stackNumber]));
            if (!this.isAdmin) {
                this.updatePlaceHolderLore(2, this.quantity - amount);
            }
        }
    }

    @Override
    public void openUI() {
        if (normalView == null) {
            viewer.sendMessage(LanguageUtils.getString("briefcase-not-initialized"));
        } else {
            viewer.playSound(armorStand.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 0.5F, 1.0F);
            Bukkit.getScheduler().runTaskLater(CustomShop.getPlugin(), () -> this.viewer.openInventory(normalView), 2);
            this.interactingInventory = normalView;
            this.isOwnerView = false;
        }
    }

    @Override
    public void openOwnerUI() {
        if (ownerView == null) {
            viewer.sendMessage(LanguageUtils.getString("briefcase-not-initialized"));
        } else {
            viewer.playSound(armorStand.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 0.5F, 1.0F);
            this.viewer.openInventory(ownerView);
            this.interactingInventory = ownerView;
            this.isOwnerView = true;
        }
    }

    @Override
    public void saveInventories() {
        // Nothing to do for briefcases.
    }
}
