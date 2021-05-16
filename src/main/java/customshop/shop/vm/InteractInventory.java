package customshop.shop.vm;

import org.bukkit.Bukkit;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import customshop.plugin.CustomShop;
import customshop.gui.VMGUI;
import customshop.player.PlayerState;

/**
 * Listener for players interacting with custom shops' GUI, containing handlers
 * for which the player (owner or not) purchases items.
 */
public class InteractInventory implements Listener {
    private static ConversationFactory purchasingConversation;
    private static final String CLOSE = "§cClose";

    /**
     * Event handler for interactions with shop's GUI.
     *
     * @param evt event of inventory clicking
     */
    @EventHandler
    public void clickShop(InventoryClickEvent evt) {
        if (evt.getCurrentItem() == null) {
            return;
        }
        InventoryHolder holder = evt.getClickedInventory().getHolder();
        Player player = (Player) evt.getWhoClicked();
        String title = evt.getView().getTitle();
        if (title.equalsIgnoreCase("§5§lVending Machine")) {
            if (holder == null) {
                ItemMeta itemMeta = evt.getCurrentItem().getItemMeta();
                if (itemMeta.hasDisplayName() && itemMeta.getDisplayName().equals(CLOSE)) {
                    Bukkit.getScheduler().runTask(CustomShop.getPlugin(), () -> player.closeInventory());
                } else if (evt.getSlot() < 27) {
                    PlayerState state = PlayerState.getPlayerState(player);
                    VMGUI ui = (VMGUI) state.getShopGUI();
                    ItemStack item = ui.getItem(evt.getSlot());
                    state.startPurchase(item);
                    Conversation conversation = purchasingConversation.buildConversation(player);
                    conversation.begin();
                    Bukkit.getScheduler().runTask(CustomShop.getPlugin(), () -> player.closeInventory());
                }
            }
            evt.setCancelled(true);
        }
    }

    /**
     * Initialises conversation factory for shop purchases.
     *
     * @param plugin instance of plugin that owns the factory
     */
    public static void initConversationFactory(Plugin plugin) {
        purchasingConversation = new ConversationFactory(plugin).withFirstPrompt(new AmountPrompt()).withModality(false)
                .withLocalEcho(false);
    }

    /**
     * Prompt when player attempts to purchase from Vending Machine.
     */
    private static class AmountPrompt extends StringPrompt {
        @Override
        public String getPromptText(ConversationContext context) {
            return "§aEnter the amount that you want to purchase...";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if (context.getForWhom() instanceof Player) {
                Player player = (Player) context.getForWhom();
                PlayerState state = PlayerState.getPlayerState(player);
                ItemStack purchasingItem = state.removePurchase();
                try {
                    int inputInt = Integer.parseInt(input);
                    double inputDouble = Double.parseDouble(input);

                    if (inputInt != inputDouble || inputDouble <= 0) {
                        player.sendMessage("§cInvalid input!");
                    } else if (context.getForWhom() instanceof Player) {
                        VMGUI ui = (VMGUI) state.getShopGUI();
                        player.sendMessage(ui.purchaseItem(player, purchasingItem, inputInt));
                    }
                } catch (NumberFormatException e) {
                    player.sendMessage("§cInvalid input!");
                }
                VMGUI.saveInventory(player);
            } else {
                // Should not get here.
                context.getForWhom().sendRawMessage("This is a player-only command.");
            }
            return END_OF_CONVERSATION;
        }
    }
}
