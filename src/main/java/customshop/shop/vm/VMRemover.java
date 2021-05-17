/*
 *  This file is part of CustomShop. Copyright (c) 2021 Paratopia.
 *
 *  CustomShop is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  CustomShop is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CustomShop. If not, see <https://www.gnu.org/licenses/>.
 *
 */

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
