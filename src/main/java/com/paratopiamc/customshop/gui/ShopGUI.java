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

import java.util.List;
// import java.util.Set;
// import java.io.File;
// import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import com.paratopiamc.customshop.plugin.CustomShop;
import com.paratopiamc.customshop.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
// import org.bukkit.configuration.InvalidConfigurationException;
// import org.bukkit.configuration.file.FileConfiguration;
// import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
// import org.bukkit.plugin.java.JavaPlugin;
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
    protected static HashMap<String, List<String>> savedMessages;
    // private static File messagesFile;
    // private static FileConfiguration messages;

    public ShopGUI(Player player, ArmorStand armorStand) {
        this.armorStand = armorStand;
        this.viewer = player;
        ItemStack block = armorStand.getEquipment().getChestplate();
        BlockStateMeta blockMeta = (BlockStateMeta) block.getItemMeta();
        this.ownerID = blockMeta.getDisplayName();
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
                // TODO: save owner messages
            }
            return true;
        }
    }

    // public static void initialize(JavaPlugin plugin) {
    // messagesFile = new File(plugin.getDataFolder(), "saved-messages.yml");
    // if (!messagesFile.exists()) {
    // messagesFile.getParentFile().mkdirs();
    // plugin.saveResource("saved-messages.yml", false);
    // }
    // messages = new YamlConfiguration();
    // try {
    // messages.load(messagesFile);
    // } catch (IOException | InvalidConfigurationException e) {
    // e.printStackTrace();
    // }
    // Set<String> listOfPlayers = messages.getDefaultSection().getKeys(false);
    // savedMessages = new HashMap<>();
    // for (String player : listOfPlayers) {
    // List<String> queue = messages.getStringList(player);
    // savedMessages.put(player, queue);
    // }
    // }

    // public static void saveUnsentMessages() {
    // try {
    // messages.save(messagesFile);
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }

    // public HashMap<String, List<String>> getSavedMessages() {
    // return savedMessages;
    // }

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
