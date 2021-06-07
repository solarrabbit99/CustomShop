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

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.UUID;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.paratopiamc.customshop.gui.CreationGUI;
import com.paratopiamc.customshop.plugin.CSComd;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GiveHead extends CSComd {
    public GiveHead(CommandSender sender, String[] args) {
        super(sender, args);
    }

    @Override
    public boolean exec() {
        if (sender instanceof ConsoleCommandSender) {
            ItemStack reward;
            if (args.length < 3) {
                return false;
            } else if (args.length == 3) {
                reward = getSkull(
                        "http://textures.minecraft.net/texture/c6e69b1c7e69bcd49ed974f5ac36ea275efabb8c649cb2b1fe9d6ea6166ec3");
            } else {
                reward = getSkull(args[3]);
            }
            ItemMeta meta = reward.getItemMeta();
            int model = Integer.parseInt(args[2]);
            meta.setCustomModelData(model);
            meta.setDisplayName(getNamefromModelData(model));
            reward.setItemMeta(meta);
            Player player = Bukkit.getPlayer(args[1]);
            if (!player.getInventory().addItem(reward).isEmpty()) {
                player.sendMessage("§6Insufficient inventory space, dropping reward on the ground...");
                player.getLocation().getWorld().dropItem(player.getLocation(), reward);
            }
            return true;
        }
        return false;
    }

    private ItemStack getSkull(String url) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);

        if (url == null || url.isEmpty())
            return null;

        ItemMeta skullMeta = skull.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.getEncoder()
                .encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField = null;

        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }

        profileField.setAccessible(true);

        try {
            profileField.set(skullMeta, profile);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        skull.setItemMeta(skullMeta);
        return skull;
    }

    private String getNamefromModelData(int model) {
        return CreationGUI.names.get(CreationGUI.modelData.indexOf(model));
    }
}
