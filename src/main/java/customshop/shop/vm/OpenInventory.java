package customshop.shop.vm;

import java.util.Collection;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import customshop.gui.VMGUI;
import customshop.utils.UIUtils;
import customshop.utils.UUIDMaps;

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
        Block clickedBlock = evt.getClickedBlock();
        Location loc = new Location(clickedBlock.getWorld(), clickedBlock.getX() + 0.5, clickedBlock.getY(),
                clickedBlock.getZ() + 0.5);
        Collection<Entity> list = clickedBlock.getWorld().getNearbyEntities(loc, 0.5, 0.5, 0.5);
        if (clickedBlock.getType() != Material.BARRIER || list.size() != 1 || evt.getPlayer().isSneaking()) {
            return;
        }
        if (UIUtils.validate((Entity) list.toArray()[0])) {
            evt.setCancelled(true);
            ArmorStand armorStand = ((ArmorStand) list.toArray()[0]);
            UUID armorStandID = armorStand.getUniqueId();
            if (UUIDMaps.playerToArmorStand.containsValue(armorStandID)) {
                evt.getPlayer().sendMessage("§cVending machine current in use, please wait...");
                return;
            }
            VMGUI ui = new VMGUI(armorStand);
            ui.openUI(evt.getPlayer());

            UUID playerID = evt.getPlayer().getUniqueId();
            UUIDMaps.playerToArmorStand.put(playerID, armorStandID);
            UUIDMaps.playerToVendingUI.put(playerID, ui);
        }
    }
}
