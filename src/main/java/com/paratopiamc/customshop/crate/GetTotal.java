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

import java.util.concurrent.CompletableFuture;
import com.paratopiamc.customshop.plugin.CSComd;
import com.paratopiamc.customshop.plugin.CustomShop;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A command executor that returns the number of custom shops owned by the
 * sender.
 */
public class GetTotal extends CSComd {
    public GetTotal(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public boolean exec() {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            CompletableFuture<Integer> cf = CompletableFuture
                    .supplyAsync(() -> CustomShop.getPlugin().getDatabase().getTotalShopOwned(player.getUniqueId()));
            cf.thenAccept(total -> player.sendMessage("§9Total custom shops owned: " + total));
        }
        return false;
    }
}
