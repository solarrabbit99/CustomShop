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

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import com.paratopiamc.customshop.plugin.CustomShop;
import com.paratopiamc.customshop.utils.LanguageUtils;
import com.paratopiamc.customshop.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import net.milkbowl.vault.economy.Economy;

/**
 * Encapsulates a shop GUI, which contains inventory views for interacting with
 * custom shops. ShopGUIs contains basic information about the interactive
 * parties, and therefore should also be the implementing class for shop
 * operations.
 * <p>
 * Implementing classes do not check for any permissions regarding shop access.
 */
public abstract class ShopGUI {
    /**
     * String representation of the UUID of the player who owns the shop.
     */
    protected final String ownerID;
    /**
     * Armor stand associated with the shop.
     */
    protected final ArmorStand armorStand;
    /**
     * Player viewing the GUI.
     */
    protected final Player viewer;
    /**
     * Whether this shop is an admin shop.
     */
    protected final boolean isAdmin;
    /**
     * Shop's inventory that the viewer is viewing.
     */
    protected Inventory interactingInventory;

    protected boolean isOwnerView;

    public ShopGUI(Player player, ArmorStand armorStand, String ownerID) {
        this.armorStand = armorStand;
        this.viewer = player;
        this.ownerID = ownerID;

        ItemStack adminItem = armorStand.getEquipment().getBoots();
        this.isAdmin = adminItem != null && adminItem.getType() != Material.AIR;
    }

    /**
     * Get the armor stand entity associated with the shop.
     *
     * @return armor stand reference
     */
    public ArmorStand getArmorStand() {
        return this.armorStand;
    }

    /**
     * Checks if viewer is owner of the shop.
     *
     * @return {@code true} if viewer is owner of the shop
     */
    public boolean isOwner() {
        return this.viewer.getUniqueId().toString().equals(this.ownerID);
    }

    /**
     * Money transaction when owner sells item (recieves money) to customer. Sends
     * feedback message to viewer regarding the outcome of the transaction. If
     * transaction is made successfully, owner will also receive relevant messages
     * if he is online. Otherwise, it will be saved in the database and pushed to
     * the owner on join.
     * 
     * @param amount    amount of items sold, disregards the amount tagged to
     *                  {@code item}
     * @param totalCost total money involved in the transaction
     * @param item      item sold
     * @return {@code true} if transaction is successful
     */
    protected boolean ownerSell(int amount, double totalCost, ItemStack item) {
        Economy economy = CustomShop.getPlugin().getEconomy();
        double bal = economy.getBalance(viewer);

        if (bal < totalCost) {
            viewer.sendMessage(MessageUtils.convertMessage(LanguageUtils.getString("customer-buy-fail-money"), ownerID,
                    viewer, totalCost, item, amount));
            return false;
        } else if (this.isAdmin) { // Valid transaction
            economy.withdrawPlayer(viewer, totalCost);
            viewer.sendMessage(MessageUtils.convertMessage(LanguageUtils.getString("customer-buy-success-customer"),
                    ownerID, viewer, totalCost, item, amount));
            return true;
        } else {
            OfflinePlayer owner = Bukkit.getOfflinePlayer(UUID.fromString(this.ownerID));
            economy.withdrawPlayer(viewer, totalCost);
            economy.depositPlayer(owner, totalCost);
            viewer.sendMessage(MessageUtils.convertMessage(LanguageUtils.getString("customer-buy-success-customer"),
                    ownerID, viewer, totalCost, item, amount));
            String ownerMessage = MessageUtils.convertMessage(LanguageUtils.getString("customer-buy-success-owner"),
                    ownerID, viewer, totalCost, item, amount);
            if (owner.isOnline()) {
                owner.getPlayer().sendMessage(ownerMessage);
            } else {
                CompletableFuture.runAsync(() -> CustomShop.getPlugin().getDatabase().storeMessage(ownerID, viewer,
                        true, item, amount, totalCost));
            }
            return true;
        }
    }

    /**
     * Money transaction when owner buys item (pays money) from customer. Sends
     * feedback message to viewer regarding the outcome of the transaction. If
     * transaction is made successfully, owner will also receive relevant messages
     * if he is online. Otherwise, it will be saved in the database and pushed to
     * the owner on join.
     * 
     * @param amount    amount of items bought, disregards the amount tagged to
     *                  {@code item}
     * @param totalCost total money involved in the transaction
     * @param item      item bought
     * @return {@code true} if transaction is successful
     */
    protected boolean ownerBuy(int amount, double totalCost, ItemStack item) {
        OfflinePlayer owner = Bukkit.getOfflinePlayer(UUID.fromString(this.ownerID));
        Economy economy = CustomShop.getPlugin().getEconomy();
        double bal = economy.getBalance(owner);

        if (bal < totalCost) {
            viewer.sendMessage(MessageUtils.convertMessage(LanguageUtils.getString("customer-sell-fail-money"), ownerID,
                    viewer, totalCost, item, amount));
            return false;
        } else if (this.isAdmin) { // Valid transaction
            economy.depositPlayer(viewer, totalCost);
            viewer.sendMessage(MessageUtils.convertMessage(LanguageUtils.getString("customer-sell-success-customer"),
                    ownerID, viewer, totalCost, item, amount));
            return true;
        } else {
            economy.withdrawPlayer(owner, totalCost);
            economy.depositPlayer(viewer, totalCost);
            viewer.sendMessage(MessageUtils.convertMessage(LanguageUtils.getString("customer-sell-success-customer"),
                    ownerID, viewer, totalCost, item, amount));
            String ownerMessage = MessageUtils.convertMessage(LanguageUtils.getString("customer-sell-success-owner"),
                    ownerID, viewer, totalCost, item, amount);
            if (owner.isOnline()) {
                owner.getPlayer().sendMessage(ownerMessage);
            } else {
                CompletableFuture.runAsync(() -> CustomShop.getPlugin().getDatabase().storeMessage(ownerID, viewer,
                        false, item, amount, totalCost));
            }
            return true;
        }
    }

    /**
     * Get the inventory that the player is interacting with.
     * 
     * @return shop's {@link Inventory} that the player is viewing
     */
    public Inventory getInteractingInventory() {
        return this.interactingInventory;
    };

    /**
     * Whether the inventory that the player is interacting is an owner's view.
     * 
     * @return {@code true} if player is interacting with an owner's view
     */
    public boolean interactingInventoryIsOwnerView() {
        return this.isOwnerView;
    }

    /**
     * Returns the outcome message of this event.
     *
     * @param item  item to set the price for
     * @param price new price
     * @return outcome message of the purchase, to be sent to the player involved
     */
    abstract public String listPrice(ItemStack item, double price);

    /**
     * Viewer purchase item from shop. Non-economy related operations dependent on
     * the type of shop will be handled here.
     *
     * @param item   item purchased by the viewer
     * @param amount amounnt of item to purchase, disregards the amount tagged to
     *               {@code item}
     */
    abstract public void purchaseItem(ItemStack item, int amount);

    /**
     * Opens public InventoryView of the shop that facilitates purchasing or selling
     * of items to/from the shop.
     */
    abstract public void openUI();

    /**
     * Opens owner's InventoryView of the shop that allows adding or removing shop
     * items.
     */
    abstract public void openOwnerUI();

    /**
     * Save any changes to player and/or the shop.
     */
    abstract public void saveInventories();
}
