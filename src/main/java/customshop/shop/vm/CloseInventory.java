package customshop.shop.vm;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerKickEvent;
import customshop.gui.VMGUI;

/**
 * Listener for {@link InventoryCloseEvent} and saves player's inventory, this
 * also includes events of plugin disabling and player leaving/kicking.
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
            VMGUI.saveInventory((Player) evt.getPlayer());
        }
    }

    /**
     * Handler in event player gets kicked with GUI opened.
     *
     * @param evt event of player kicking
     */
    @EventHandler
    public void playerLeave(PlayerKickEvent evt) {
        VMGUI.saveInventory(evt.getPlayer());
    }
}
