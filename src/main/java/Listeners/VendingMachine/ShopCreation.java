package Listeners.VendingMachine;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.ArmorStand.LockType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import Utils.UIUtils;

/**
 * Listener that deals with shop creation and destruction.
 */
public class ShopCreation implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        Block targetBlock = player.getTargetBlockExact(5);
        if (targetBlock == null) {
            player.sendMessage("§cYou are not targeting any block...");
            return false;
        }
        float yaw = targetBlock.getLocation().getYaw();
        Location loc = new Location(targetBlock.getWorld(), targetBlock.getX() + 0.5, targetBlock.getY() + 1,
                targetBlock.getZ() + 0.5);
        Location locAddOne = new Location(targetBlock.getWorld(), targetBlock.getX() + 0.5, targetBlock.getY() + 2,
                targetBlock.getZ() + 0.5);

        if (loc.getBlock().getType() != Material.AIR || locAddOne.getBlock().getType() != Material.AIR) {
            player.sendMessage("§cTarget location must have at least 2 blocks of air above...");
            return false;
        }

        if (args.length == 0) {
            player.sendMessage("§cInput shop type as first arguement...");
            return false;
        }
        String shopType = args[0];
        if (shopType.equalsIgnoreCase("vending_machine")) {
            loc.getBlock().setType(Material.BARRIER);
            locAddOne.getBlock().setType(Material.BARRIER);

            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.setCustomModelData(100000);
            item.setItemMeta(meta);

            ArmorStand armorStand = (ArmorStand) targetBlock.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
            armorStand.setRotation(yaw, 0);
            armorStand.setCustomName("§5§lVending Machine");
            EntityEquipment armorStandBody = armorStand.getEquipment();
            armorStandBody.setHelmet(item);

            ItemStack container = new ItemStack(Material.SHULKER_BOX);
            BlockStateMeta blockMeta = (BlockStateMeta) container.getItemMeta();
            double[] prices = new double[27];
            List<String> lore = UIUtils.doubleToStringList(prices);
            blockMeta.setDisplayName(player.getUniqueId().toString());
            blockMeta.setLore(lore);

            container.setItemMeta(blockMeta);
            armorStandBody.setChestplate(container);

            lockArmorStand(armorStand);
        } else {
            player.sendMessage("§cUnknown shop type...");
        }

        return false;
    }

    /**
     * Locks armor stand to prevent accesibility of item within its slots.
     *
     * @param armorStand armor stand to lock
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
