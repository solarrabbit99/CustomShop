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
import com.paratopiamc.customshop.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import net.milkbowl.vault.economy.Economy;

/**
 * Encapsulates a shop GUI, which contains inventory views for interacting with
 * custom shops. Implementing classes do not check for any permissions regarding
 * shop access.
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

    public ShopGUI(Player player, ArmorStand armorStand, String ownerID) {
        this.armorStand = armorStand;
        this.viewer = player;
        this.ownerID = ownerID;
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

    protected boolean ownerSell(int amount, double totalCost, ItemStack item) {
        Economy economy = CustomShop.getPlugin().getEconomy();
        double bal = economy.getBalance(viewer);

        if (bal < totalCost) {
            viewer.sendMessage(
                    MessageUtils.convertMessage(CustomShop.getPlugin().getConfig().getString("customer-buy-fail-money"),
                            ownerID, viewer, totalCost, item, amount));
            return false;
        } else { // Valid transaction
            OfflinePlayer owner = Bukkit.getOfflinePlayer(UUID.fromString(this.ownerID));
            economy.withdrawPlayer(viewer, totalCost);
            economy.depositPlayer(owner, totalCost);
            viewer.sendMessage(MessageUtils.convertMessage(
                    CustomShop.getPlugin().getConfig().getString("customer-buy-success-customer"), ownerID, viewer,
                    totalCost, item, amount));
            String ownerMessage = MessageUtils.convertMessage(
                    CustomShop.getPlugin().getConfig().getString("customer-buy-success-owner"), ownerID, viewer,
                    totalCost, item, amount);
            if (owner.isOnline()) {
                owner.getPlayer().sendMessage(ownerMessage);
            } else {
                CompletableFuture.runAsync(() -> CustomShop.getPlugin().getDatabase().storeMessage(ownerID, viewer,
                        true, item, amount, totalCost));
            }
            return true;
        }
    }

    protected boolean ownerBuy(int amount, double totalCost, ItemStack item) {
        OfflinePlayer owner = Bukkit.getOfflinePlayer(UUID.fromString(this.ownerID));
        Economy economy = CustomShop.getPlugin().getEconomy();
        double bal = economy.getBalance(owner);

        if (bal < totalCost) {
            viewer.sendMessage(MessageUtils.convertMessage(
                    CustomShop.getPlugin().getConfig().getString("customer-sell-fail-money"), ownerID, viewer,
                    totalCost, item, amount));
            return false;
        } else { // Valid transaction
            economy.withdrawPlayer(owner, totalCost);
            economy.depositPlayer(viewer, totalCost);
            viewer.sendMessage(MessageUtils.convertMessage(
                    CustomShop.getPlugin().getConfig().getString("customer-sell-success-customer"), ownerID, viewer,
                    totalCost, item, amount));
            String ownerMessage = MessageUtils.convertMessage(
                    CustomShop.getPlugin().getConfig().getString("customer-sell-success-owner"), ownerID, viewer,
                    totalCost, item, amount);
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
     * Returns the outcome message of this event.
     *
     * @param item  item to set the price for
     * @param price new price
     * @return outcome message of the purchase, to be sent to the player involved
     */
    abstract public String listPrice(ItemStack item, double price);

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
