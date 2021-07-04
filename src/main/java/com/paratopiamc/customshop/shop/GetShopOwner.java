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

package com.paratopiamc.customshop.shop;

import com.paratopiamc.customshop.plugin.CSComd;
import com.paratopiamc.customshop.utils.LanguageUtils;
import com.paratopiamc.customshop.utils.ShopUtils;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

public class GetShopOwner extends CSComd {
    public GetShopOwner(CommandSender sender) {
        super(sender, null);
    }

    @Override
    public boolean exec() {
        if (this.sender instanceof Player) {
            Player player = (Player) this.sender;
            Block targetBlock = player.getTargetBlockExact(5);
            ArmorStand armorStand = ShopUtils.getArmorStand(targetBlock);
            if (armorStand != null) {
                player.sendMessage(
                        String.format(LanguageUtils.getString("shop-owner"), ShopUtils.getOwner(armorStand).getName()));
            } else {
                player.sendMessage(LanguageUtils.getString("invalid-target"));

            }
        }
        return true;
    }
}
