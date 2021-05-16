package customshop.player;

import java.util.HashMap;
import java.util.Optional;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import customshop.gui.ShopGUI;
import customshop.gui.VMGUI;

/**
 * Contains all the mappings from player's UUID to corresponding interacting
 * entities/items/GUIs. Any data that is not required of the server to save over
 * reboots should be saved here.
 */
public class PlayerState {
    private static HashMap<Player, PlayerState> playerStates = new HashMap<>();

    private ArmorStand armorStand;
    private ShopGUI shopGUI;
    private ItemStack purchase;
    private Conversation conversation;
    private Player player;

    private PlayerState(Player player) {
        this.player = player;
        playerStates.put(player, this);
    }

    public static PlayerState getPlayerState(Player player) {
        PlayerState result = playerStates.get(player);
        if (result == null) {
            return new PlayerState(player);
        } else {
            return result;
        }
    }

    public void setShopGUI(ShopGUI gui) {
        this.shopGUI = gui;
    }

    public ShopGUI getShopGUI() {
        return this.shopGUI;
    }

    /**
     * Sets the armor stand of a {@link PlayerState} if there are no players have
     * the armor stand set in their state.
     *
     * @param armorStand
     * @return {@code true} if there were no players interacting with the ArmorStand
     */
    public boolean setArmorStand(ArmorStand armorStand) {
        if (getInteractingPlayer(armorStand) != null)
            return false;
        this.armorStand = armorStand;
        return true;
    }

    /**
     * Get the player interacting with it.
     *
     * @param armorStand the target {@link ArmorStand}
     * @return player interacting with the armor stand
     */
    private static Player getInteractingPlayer(ArmorStand armorStand) {
        return playerStates.entrySet().stream()
                .filter(e -> armorStand.getUniqueId().equals(
                        Optional.ofNullable(e.getValue().armorStand).map(stand -> stand.getUniqueId()).orElse(null)))
                .findFirst().map(e -> e.getKey()).orElse(null);
    }

    public ArmorStand getArmorStand() {
        return this.armorStand;
    }

    public void clearShopInteraction() {
        this.armorStand = null;
        this.shopGUI = null;
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

    public static void saveAll() {
        playerStates.forEach((player, state) -> VMGUI.saveInventory(player));
    }
}
