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

package com.paratopiamc.customshop.crate;

import java.util.List;
import com.paratopiamc.customshop.plugin.CustomShop;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Key extends ItemStack {
    private static final String crateKeyName = CustomShop.getPlugin().getConfig().getString("crate-key-name");
    private static final List<String> crateKeyLore = CustomShop.getPlugin().getConfig().getStringList("crate-key-lore");
    private static final Key template = new Key(1);

    public Key(int amount) {
        super(Material.TRIPWIRE_HOOK);
        ItemMeta meta = this.getItemMeta();
        meta.setDisplayName(crateKeyName);
        meta.setLore(crateKeyLore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        this.setItemMeta(meta);
        this.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
        this.setAmount(amount);
    }

    public static boolean isKey(ItemStack item) {
        return template.isSimilar(item);
    }
}
