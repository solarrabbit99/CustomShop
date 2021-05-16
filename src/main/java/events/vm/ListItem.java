package events.vm;

import java.util.Collection;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import plugin.CustomShop;
import utils.UIUtils;
import utils.UUIDMaps;
import gui.VMGUI;

/**
 * Listener for players interacting with custom shops, containing handlers for
 * which the owner left clicks on shops to list items.
 */
public class ListItem implements Listener {
    private static ConversationFactory listingConversation;

    @EventHandler
    public void listItem(PlayerInteractEvent evt) {
        EquipmentSlot hand = evt.getHand();
        if (!hand.equals(EquipmentSlot.HAND) || !evt.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            return;
        }
        Block clickedBlock = evt.getClickedBlock();
        Location loc = new Location(clickedBlock.getWorld(), clickedBlock.getX() + 0.5, clickedBlock.getY(),
                clickedBlock.getZ() + 0.5);
        Collection<Entity> list = clickedBlock.getWorld().getNearbyEntities(loc, 0.5, 0.5, 0.5);
        if (clickedBlock.getType() != Material.BARRIER || list.size() != 1 || !evt.getPlayer().isSneaking()) {
            return;
        }
        if (UIUtils.validate((Entity) list.toArray()[0])) {
            evt.setCancelled(true);
            ArmorStand armorStand = ((ArmorStand) list.toArray()[0]);
            UUID armorStandID = armorStand.getUniqueId();
            Player player = evt.getPlayer();
            if (UUIDMaps.playerToArmorStand.containsValue(armorStandID)) {
                player.sendMessage("§cVending machine current in use, please wait...");
                return;
            }
            VMGUI ui = new VMGUI(armorStand);
            if (player.getEquipment().getItemInMainHand().getType().equals(Material.AIR)) {
                ui.openOwnerUI(player);
            } else {
                Conversation conversation = listingConversation.buildConversation(player);
                conversation.begin();
            }
            UUID playerID = player.getUniqueId();
            UUIDMaps.playerToArmorStand.put(playerID, armorStandID);
            UUIDMaps.playerToVendingUI.put(playerID, ui);
        }
    }

    /**
     * Initialises conversation factory for shop purchases.
     *
     * @param plugin instance of plugin that owns the factory
     */
    public static void initConversationFactory(CustomShop plugin) {
        listingConversation = new ConversationFactory(plugin).withFirstPrompt(new PricePrompt()).withModality(false)
                .withLocalEcho(false);
    }

    /**
     * Prompt when player attempts to list a new price to all items in shop similar
     * to the item in hand.
     */
    private static class PricePrompt extends StringPrompt {
        // TODO: Set cancel to true for teleportation/movement/timer/leave.
        @Override
        public String getPromptText(ConversationContext context) {
            return "§aEnter the price of the item that you want to list...";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if (context.getForWhom() instanceof Player) {
                Player player = (Player) context.getForWhom();
                try {
                    double price = Math.round(Double.parseDouble(input) * 100) / 100;
                    if (price <= 0) {
                        player.sendMessage("§cPrice must be more than 0!");
                    } else {
                        PlayerInventory playerInventory = player.getInventory();
                        ItemStack item = playerInventory.getItemInMainHand();
                        VMGUI ui = UUIDMaps.playerToVendingUI.get(player.getUniqueId());
                        player.sendMessage(ui.listPrice(player, item, price));
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
