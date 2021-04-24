package Listeners.VendingMachine;

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
import CustomUIs.VendingMachineUI;
import UUIDMaps.VendingMachine;
import Utils.UIUtils;

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
            if (VendingMachine.playerToArmorStand.containsValue(armorStandID)) {
                evt.getPlayer().sendMessage("§cVending machine current in use, please wait...");
                return;
            }
            VendingMachineUI ui = new VendingMachineUI(armorStand);
            ui.openUI(evt.getPlayer());

            UUID playerID = evt.getPlayer().getUniqueId();
            VendingMachine.playerToArmorStand.put(playerID, armorStandID);
            VendingMachine.playerToVendingUI.put(playerID, ui);
        }
    }
}
