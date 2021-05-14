package events.vm;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import plugin.CustomShop;
import utils.UUIDMaps;
import gui.VMGUI;

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
        UUID playerID = player.getUniqueId();
        String title = evt.getView().getTitle();
        if (title.equalsIgnoreCase("§5§lVending Machine")) {
            if (holder == null) {
                ItemMeta itemMeta = evt.getCurrentItem().getItemMeta();
                if (itemMeta.hasDisplayName() && itemMeta.getDisplayName().equals(CLOSE)) {
                    Bukkit.getScheduler().runTask(CustomShop.getPlugin(), () -> player.closeInventory());
                } else if (evt.getSlot() < 27) {
                    VMGUI ui = UUIDMaps.playerToVendingUI.get(playerID);
                    ItemStack item = ui.getItem(evt.getSlot());
                    UUIDMaps.purchasing.put(playerID, item);
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
    private static class AmountPrompt extends NumericPrompt {
        @Override
        public String getPromptText(ConversationContext context) {
            return "§aEnter the amount that you want to purchase...";
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, Number input) {
            if (context.getForWhom() instanceof Player) {
                Player player = (Player) context.getForWhom();
                ItemStack purchasingItem = UUIDMaps.purchasing.remove(player.getUniqueId());
                VMGUI ui = UUIDMaps.playerToVendingUI.get(player.getUniqueId());
                ui.purchaseItem(player, purchasingItem, input.intValue());
            }
            return END_OF_CONVERSATION;
        }

        @Override
        protected boolean isNumberValid(ConversationContext context, Number input) {
            return input.intValue() == input.doubleValue() && input.doubleValue() > 0;
        }

        @Override
        protected String getFailedValidationText(ConversationContext context, String invalidInput) {
            return "§cInput is not valid!";
        }
    }
}
