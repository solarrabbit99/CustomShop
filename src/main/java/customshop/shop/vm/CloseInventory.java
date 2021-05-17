package customshop.shop.vm;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import customshop.player.PlayerState;

/**
 * Listener for {@link InventoryCloseEvent} and saves player's inventory.
 */
public class CloseInventory implements Listener {
    /**
     * Event handler for vending machine inventory close event.
     *
     * @param evt event of inventory clicking.
     */
    @EventHandler
    public void closeShop(InventoryCloseEvent evt) {
        if (!((Player) evt.getPlayer()).isConversing()) {
            PlayerState.getPlayerState((Player) evt.getPlayer()).clearShopInteractions();
        }
    }
}
