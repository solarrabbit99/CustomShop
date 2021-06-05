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

package com.paratopiamc.customshop.player;

import java.util.HashMap;
import java.util.Optional;
import com.paratopiamc.customshop.gui.ShopGUI;
import com.paratopiamc.customshop.plugin.CustomShop;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Contains all the references from player to corresponding interacting
 * entities/items/GUIs. Any data that is not required of the server to save over
 * reboots should be saved here.
 */
public class PlayerState {
    /** Mapping of player to player state. */
    private static HashMap<Player, PlayerState> playerStates = new HashMap<>();

    private ShopGUI shopGUI;
    private ItemStack transactionItem;
    private Conversation conversation;
    private Player player;
    private BukkitRunnable unlockingShop;
    private ItemStack unlockingShopItem;

    private PlayerState(Player player) {
        this.player = player;
        playerStates.put(player, this);
    }

    /**
     * Factory method to construct player state and create reference from player if
     * not already existing.
     *
     * @param player player of interest
     * @return player state associated with the player
     */
    public static PlayerState getPlayerState(Player player) {
        PlayerState result = playerStates.get(player);
        if (result == null) {
            return new PlayerState(player);
        } else {
            return result;
        }
    }

    /**
     * Returns the current shop item (player head) used to unlock the shop within 5
     * seconds of its first placement.
     * 
     * @return {@code null} if 5 seconds timer is up
     */
    public ItemStack getUnlockingShopItem() {
        return this.unlockingShopItem;
    }

    /**
     * Sets the shop that the player is attempting to unlock using player head. This
     * method starts a 5 seconds timer for which {@code shopItem} is obtainable
     * through {@link #getUnlockingShopItem()}.
     *
     * @param shopItem the player head associated to the shop
     */
    public void setUnlockingShop(ItemStack shopItem) {
        if (this.unlockingShop != null && !this.unlockingShop.isCancelled())
            this.unlockingShop.cancel();
        this.unlockingShopItem = shopItem;
        this.unlockingShop = new BukkitRunnable() {
            @Override
            public void run() {
                PlayerState.this.unlockingShopItem = null;
                PlayerState.this.unlockingShop = null;
            }
        };
        this.unlockingShop.runTaskLater(CustomShop.getPlugin(), 45);
    }

    /**
     * Setter for ShopGUI if there isn't already one assigned to the player. It is
     * expected for {@link #clearShopInteractions()} to be called before this.
     *
     * @param gui a {@link ShopGUI} object
     */
    public void setShopGUI(ShopGUI gui) {
        this.shopGUI = gui;
    }

    /**
     * ShopGUI getter. Returns the ShopGUI that a player has opened, {@code null} if
     * player has no ShopGUIs opened.
     *
     * @return ShopGUI opened by player
     */
    public ShopGUI getShopGUI() {
        return this.shopGUI;
    }

    /**
     * Get the player interacting with it.
     *
     * @param armorStand the target {@link ArmorStand}
     * @return player interacting with the armor stand
     */
    public static Player getInteractingPlayer(ArmorStand armorStand) {
        return playerStates.entrySet().stream()
                .filter(e -> armorStand.getUniqueId()
                        .equals(Optional.ofNullable(e.getValue().shopGUI).map(gui -> gui.getArmorStand())
                                .map(stand -> stand.getUniqueId()).orElse(null)))
                .findFirst().map(e -> e.getKey()).orElse(null);
    }

    /**
     * Assign item to a player that the player is purchasing and begin purchase
     * conversation if the player is not already conversing.
     *
     * @param item    that the player is purchasing
     * @param factory {@link ConversationFactory} to build conversation for the
     *                player
     * @return {@code true} if player was not conversing
     */
    public boolean startTransaction(ItemStack item, ConversationFactory factory) {
        if (!player.isConversing()) {
            this.transactionItem = item;
            conversation = factory.buildConversation(player);
            conversation.begin();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Begin conversation if the player is not already conversing.
     *
     * @param factory {@link ConversationFactory} to build conversation for the
     *                player
     * @return {@code true} if player was not conversing
     */
    public boolean startConversation(ConversationFactory factory) {
        if (!player.isConversing()) {
            conversation = factory.buildConversation(player);
            conversation.begin();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Abandons conversation if there is one assigned to the player.
     *
     * @return {@code true} if there was a CustomShop conversation assigned to the
     *         player
     */
    private boolean abandonConversation() {
        if (conversation != null) {
            player.abandonConversation(conversation);
            this.conversation = null;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Removes reference from the player of item that the player is purchasing.
     *
     * @return item that the player is purchasing, {@code null} if player is not
     *         purchasing anything
     */
    public ItemStack removeTransactionItem() {
        ItemStack clone = transactionItem.clone();
        this.transactionItem = null;
        return clone;
    }

    /**
     * Clear player's shop interactions, if any.
     */
    public void clearShopInteractions() {
        abandonConversation();
        if (this.shopGUI != null) {
            this.shopGUI.saveInventories();
            this.shopGUI = null;
        }
    }

    /**
     * Clear all players' shop interactions, if any.
     */
    public static void clearAllShopInteractions() {
        playerStates.forEach((player, state) -> state.clearShopInteractions());
    }
}
