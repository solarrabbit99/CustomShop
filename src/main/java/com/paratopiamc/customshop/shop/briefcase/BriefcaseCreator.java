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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import com.paratopiamc.customshop.plugin.CustomShop;
import com.paratopiamc.customshop.shop.ShopCreator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Newt's Briefcase's shop creator. Player's target block must have at least 1
 * block of air above for the shop to be spawned successfully.
 */
public class BriefcaseCreator extends ShopCreator {
    @Override
    public void createShop(Location location, Player owner, ItemStack item) {
        if (item.getItemMeta().getCustomModelData() == CustomShop.getPlugin().getConfig()
                .getInt("defaults.briefcase")) {
            owner.sendMessage("§cYou have yet to unlock the selected Newt's Briefcase!");
            return;
        }
        if (location.getBlock().getType() != Material.AIR) {
            owner.sendMessage("§cTarget location must have at least 1 block of air above...");
            return;
        }

        location.getBlock().setType(Material.BARRIER);

        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        armorStand.setSmall(true);
        armorStand.setCustomName("§5§lNewt's Briefcase");
        EntityEquipment armorStandBody = armorStand.getEquipment();
        armorStandBody.setHelmet(item);

        ItemStack placeHolder = new ItemStack(Material.DIRT);
        ItemMeta meta = placeHolder.getItemMeta();

        double price = 0;
        long amount = 0;
        boolean selling = true;

        List<String> lore = Arrays.asList(String.valueOf(price), String.valueOf(amount), String.valueOf(selling));
        meta.setDisplayName(owner.getUniqueId().toString());
        meta.setLore(lore);

        placeHolder.setItemMeta(meta);
        armorStandBody.setChestplate(placeHolder);

        lockArmorStand(armorStand);

        CompletableFuture.runAsync(() -> {
            CustomShop.getPlugin().getDatabase().incrementTotalShopsOwned(owner.getUniqueId());
            owner.sendMessage("§aNewt's Briefcase successfully created!");
        });
    }
}
