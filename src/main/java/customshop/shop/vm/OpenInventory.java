package customshop.shop.vm;

import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import customshop.gui.VMGUI;
import customshop.player.PlayerState;
import customshop.utils.UIUtils;

/**
 * Listener for players interacting with custom shops, containing handlers for
 * which the player (owner or not) right clicks on shops to purchase items.
 */
public class OpenInventory implements Listener {
    /**
     * Open vending machine's UI when player right clicks the armor stand.
     *
     * @param evt event of player right clicking entity
     */
    @EventHandler
    public void openShop(PlayerInteractEvent evt) {
        EquipmentSlot hand = evt.getHand();
        if (!hand.equals(EquipmentSlot.HAND) || !evt.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        Block targetBlock = evt.getClickedBlock();
        ArmorStand armorStand = UIUtils.getArmorStand(targetBlock);
        if (armorStand != null) {
            evt.setCancelled(true);
            PlayerState state = PlayerState.getPlayerState(evt.getPlayer());
            if (PlayerState.getInteractingPlayer(armorStand) != null) {
                evt.getPlayer().sendMessage("§cVending machine current in use, please wait...");
                return;
            }
            VMGUI ui = new VMGUI(armorStand, evt.getPlayer());
            ui.openUI();
            state.setShopGUI(ui);
        }
    }
}
