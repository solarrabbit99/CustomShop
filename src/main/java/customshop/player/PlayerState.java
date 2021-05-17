package customshop.player;

import java.util.HashMap;
import java.util.Optional;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import customshop.gui.ShopGUI;
import customshop.plugin.CustomShop;

/**
 * Contains all the references from player to corresponding interacting
 * entities/items/GUIs. Any data that is not required of the server to save over
 * reboots should be saved here.
 */
public class PlayerState {
    /** Mapping of player to player state. */
    private static HashMap<Player, PlayerState> playerStates = new HashMap<>();

    private ShopGUI shopGUI;
    private ItemStack purchase;
    private Conversation conversation;
    private Player player;

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
     * Setter for ShopGUI if there isn't already one assigned to the player.
     *
     * @param gui a {@link ShopGUI} object
     */
    public void setShopGUI(ShopGUI gui) {
        if (this.shopGUI != null) {
            CustomShop.getPlugin().getServer().getConsoleSender().sendMessage(
                    "§c§l[CustomShop] Assigning a ShopGUI to player when player has already another ShopGUI assigned!");
        }
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
    public boolean startPurchase(ItemStack item, ConversationFactory factory) {
        if (!player.isConversing()) {
            this.purchase = item;
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
    public boolean abandonConversation() {
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
    public ItemStack removePurchase() {
        ItemStack clone = purchase.clone();
        this.purchase = null;
        return clone;
    }

    /**
     * Clear player's shop interactions, if any.
     */
    public void clearShopInteractions() {
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
