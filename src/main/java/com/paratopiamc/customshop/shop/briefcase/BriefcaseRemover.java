package com.paratopiamc.customshop.shop.briefcase;

import java.util.UUID;
import com.paratopiamc.customshop.shop.ShopRemover;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

public class BriefcaseRemover extends ShopRemover {

    private Location location;
    // private BlockStateMeta meta;

    public BriefcaseRemover(Block targetBlock, ArmorStand armorStand) {
        this.targetBlock = targetBlock;
        this.armorStand = armorStand;
        this.location = armorStand.getLocation();

        ItemStack chestItem = armorStand.getEquipment().getChestplate();
        // if (chestItem == null || !(chestItem.getItemMeta() instanceof BlockStateMeta)
        // || !(((BlockStateMeta) chestItem.getItemMeta()).getBlockState() instanceof
        // ShulkerBox)) {
        // CustomShop.getPlugin().getServer().getConsoleSender()
        // .sendMessage("§c§l[CustomShop] Attempting to remove vending machine at " +
        // bottom
        // + " with missing shulker box! Report this error!");
        // } else {
        // meta = (BlockStateMeta) chestItem.getItemMeta();
        // if (meta.hasDisplayName()) {
        // this.ownerUUID = UUID.fromString(meta.getDisplayName());
        // } else {
        // CustomShop.getPlugin().getServer().getConsoleSender()
        // .sendMessage("§c§l[CustomShop] Attempting to remove vending machine at " +
        // bottom
        // + " with shulker box with missing display name! Report this error!");
        // }
        // }
    }

    @Override
    public UUID removeShop() {
        location.getBlock().setType(Material.AIR);

        // ShulkerBox shulker = (ShulkerBox) this.meta.getBlockState();
        // shulker.getInventory().forEach(item -> {
        // if (item != null)
        // armorStand.getWorld().dropItem(armorStand.getLocation(), item);
        // });

        armorStand.remove();
        return this.ownerUUID;
    }

}
