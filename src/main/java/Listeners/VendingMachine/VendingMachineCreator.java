package Listeners.VendingMachine;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.ArmorStand.LockType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import Listeners.ShopCreator;
import Utils.UIUtils;

/**
 * Vending machine's shop creator. Player's target block must have at least 2
 * blocks of air above for the shop to be spawned successfully.
 */
public class VendingMachineCreator implements ShopCreator {
    @Override
    public String createShop(Location location, Player owner, ItemStack item) {
        Location locationAddOne = location.clone();
        locationAddOne.setY(location.getY() + 1);
        if (location.getBlock().getType() != Material.AIR || locationAddOne.getBlock().getType() != Material.AIR) {
            return "§cTarget location must have at least 2 blocks of air above...";
        }

        location.getBlock().setType(Material.BARRIER);
        locationAddOne.getBlock().setType(Material.BARRIER);

        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        armorStand.setCustomName("§5§lVending Machine");
        EntityEquipment armorStandBody = armorStand.getEquipment();
        armorStandBody.setHelmet(item);

        ItemStack container = new ItemStack(Material.SHULKER_BOX);
        BlockStateMeta blockMeta = (BlockStateMeta) container.getItemMeta();
        double[] prices = new double[27];
        List<String> lore = UIUtils.doubleToStringList(prices);
        blockMeta.setDisplayName(owner.getUniqueId().toString());
        blockMeta.setLore(lore);

        container.setItemMeta(blockMeta);
        armorStandBody.setChestplate(container);

        lockArmorStand(armorStand);

        return "§aVending machine successfully created!";
    }

    /**
     * Locks armor stand to prevent accessibility of items within its slots.
     *
     * @param armorStand ArmorStand to lock
     */
    private void lockArmorStand(ArmorStand armorStand) {
        armorStand.setInvulnerable(true);
        armorStand.setGravity(false);
        armorStand.setVisible(false);

        armorStand.addEquipmentLock(EquipmentSlot.HEAD, LockType.ADDING_OR_CHANGING);
        armorStand.addEquipmentLock(EquipmentSlot.CHEST, LockType.ADDING_OR_CHANGING);
        armorStand.addEquipmentLock(EquipmentSlot.LEGS, LockType.ADDING_OR_CHANGING);
        armorStand.addEquipmentLock(EquipmentSlot.FEET, LockType.ADDING_OR_CHANGING);
        armorStand.addEquipmentLock(EquipmentSlot.HAND, LockType.ADDING_OR_CHANGING);
        armorStand.addEquipmentLock(EquipmentSlot.OFF_HAND, LockType.ADDING_OR_CHANGING);
    }
}
