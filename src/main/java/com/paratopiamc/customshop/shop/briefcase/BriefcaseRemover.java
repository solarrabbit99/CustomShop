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

package com.paratopiamc.customshop.shop.briefcase;

import java.util.List;
import java.util.UUID;
import com.paratopiamc.customshop.plugin.CustomShop;
import com.paratopiamc.customshop.shop.ShopRemover;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BriefcaseRemover extends ShopRemover {

    private Location location;
    private ItemStack item;
    // private BlockStateMeta meta;

    public BriefcaseRemover(Block targetBlock, ArmorStand armorStand) {
        this.targetBlock = targetBlock;
        this.armorStand = armorStand;
        this.location = armorStand.getLocation();

        item = armorStand.getEquipment().getLeggings();
        ItemStack placeHolder = armorStand.getEquipment().getChestplate();

        if (placeHolder == null || placeHolder.getType() == Material.AIR) {
            CustomShop.getPlugin().getServer().getConsoleSender()
                    .sendMessage("§c§l[CustomShop] Attempting to remove briefcase at " + location
                            + " with missing chest item! Report this error!");
        } else {
            ItemMeta meta = placeHolder.getItemMeta();
            if (meta.hasDisplayName()) {
                this.ownerUUID = UUID.fromString(meta.getDisplayName());
            } else {
                CustomShop.getPlugin().getServer().getConsoleSender()
                        .sendMessage("§c§l[CustomShop] Attempting to remove briefcase at " + location
                                + " with chest item with missing display name! Report this error!");
                return;
            }

            List<String> info = meta.getLore();
            int amount = Integer.parseInt(info.get(1));

            if (amount == 0) {
                this.item = null;
            } else if (item != null) {
                this.item.setAmount(amount);
            }
        }
    }

    @Override
    public UUID removeShop() {
        location.getBlock().setType(Material.AIR);

        if (this.item != null && this.item.getType() != Material.AIR) {
            int maxPerStack = item.getMaxStackSize();
            int maxDropAmount = 54 * maxPerStack;
            item.setAmount(Math.min(maxDropAmount, item.getAmount()));

            armorStand.getWorld().dropItem(armorStand.getLocation(), item);
        }

        armorStand.remove();
        return this.ownerUUID;
    }

}