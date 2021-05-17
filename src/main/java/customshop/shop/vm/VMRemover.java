package customshop.shop.vm;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import customshop.plugin.CustomShop;
import customshop.shop.ShopRemover;

/**
 * Vending machine's shop remover.
 */
public class VMRemover extends ShopRemover {
    ArmorStand armorStand;

    public VMRemover(Block targetBlock, ArmorStand armorStand) {
        super(targetBlock);
        this.armorStand = armorStand;
    }

    @Override
    public void removeShop() {
        Location bottom = armorStand.getLocation();
        Location top = armorStand.getLocation();
        top.setY(top.getY() + 1);
        bottom.getBlock().setType(Material.AIR);
        top.getBlock().setType(Material.AIR);
        ItemStack chestItem = armorStand.getEquipment().getChestplate();
        if (chestItem == null || !(chestItem.getItemMeta() instanceof BlockStateMeta)
                || !(((BlockStateMeta) chestItem.getItemMeta()).getBlockState() instanceof ShulkerBox)) {
            CustomShop.getPlugin().getServer().getConsoleSender()
                    .sendMessage("§c§l[CustomShop] Attempting to remove vending machine at " + bottom
                            + " with missing shulker box!");
        } else {
            ShulkerBox shulker = (ShulkerBox) ((BlockStateMeta) chestItem.getItemMeta()).getBlockState();
            shulker.getInventory().forEach(item -> {
                if (item != null)
                    armorStand.getWorld().dropItemNaturally(armorStand.getLocation(), item);
            });
        }
        armorStand.remove();
    }
}
