package Listeners.VendingMachine;

import java.util.UUID;
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
import CustomUIs.VendingMachineUI;
import UUIDMaps.VendingMachine;

public class InteractInventory implements Listener {
    private static ConversationFactory purchasingConversation;
    private static final String CLOSE = "§c§lClose";

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
        if (holder == null && VendingMachine.playerToArmorStand.containsKey(playerID)
                && title.equalsIgnoreCase("§5§lVending Machine")) {
            ItemMeta itemMeta = evt.getCurrentItem().getItemMeta();
            if (itemMeta.hasDisplayName() && itemMeta.getDisplayName().equals(CLOSE)) {
                player.closeInventory();
            } else if (evt.getSlot() < 27) { // For non shop-owners.
                VendingMachineUI ui = VendingMachine.playerToVendingUI.get(playerID);
                ItemStack item = ui.getItem(evt.getSlot());
                VendingMachine.purchasing.put(playerID, item);
                Conversation conversation = purchasingConversation.buildConversation(player);
                conversation.begin();
                player.closeInventory();
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
                ItemStack purchasingItem = VendingMachine.purchasing.remove(player.getUniqueId());
                VendingMachineUI ui = VendingMachine.playerToVendingUI.get(player.getUniqueId());
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
