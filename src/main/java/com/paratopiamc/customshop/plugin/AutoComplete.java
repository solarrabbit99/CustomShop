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

package com.paratopiamc.customshop.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class AutoComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String[] array = new String[] { "newshop", "removeshop", "gettotal" };
            List<String> subCommands = new ArrayList<>(Arrays.asList(array));
            if (sender.hasPermission("customshop.setcrate")) {
                subCommands.add("setcrate");
            }
            if (sender.hasPermission("customshop.givekey")) {
                subCommands.add("givekey");
            }
            if (sender.hasPermission("customshop.lockall")) {
                subCommands.add("lockall");
            }
            return subCommands;
        } else if (args.length == 2 && ((args[0].equals("givekey") && sender.hasPermission("customshop.givekey"))
                || (args[0].equals("lockall") && sender.hasPermission("customshop.lockall")))) {
            return CustomShop.getPlugin().getServer().getOnlinePlayers().stream().map(p -> p.getName())
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }
}
