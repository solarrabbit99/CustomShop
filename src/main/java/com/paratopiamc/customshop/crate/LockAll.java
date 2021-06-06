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

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import com.paratopiamc.customshop.plugin.CSComd;
import com.paratopiamc.customshop.plugin.CustomShop;
import com.paratopiamc.customshop.utils.LanguageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/** Locks and resets all unlocked custom shops of the specified player. */
public class LockAll extends CSComd {

    public LockAll(CommandSender sender, String[] args) {
        this.sender = sender;
        this.args = args;
    }

    @Override
    public boolean exec() {
        if (!sender.hasPermission("customshop.lockall")) {
            sender.sendMessage(LanguageUtils.getString("command-no-perms"));
            return false;
        }
        if (args.length < 2) {
            sender.sendMessage("§cInvalid number of arguments!");
            return false;
        }
        Player player = Bukkit.getPlayerExact(args[1]);
        if (player == null) {
            sender.sendMessage("§cCannot find specified player!");
            return false;
        }
        CompletableFuture.runAsync(() -> {
            CustomShop.getPlugin().getDatabase().setUnlockedShops(player, new ArrayList<>());
            sender.sendMessage("§aSuccessfully locked all custom shops of the specified player!");
        });
        return true;
    }
}
