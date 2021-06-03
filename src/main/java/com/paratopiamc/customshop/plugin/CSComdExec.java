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

import com.paratopiamc.customshop.crate.GetTotal;
import com.paratopiamc.customshop.crate.GiveKey;
import com.paratopiamc.customshop.crate.LockAll;
import com.paratopiamc.customshop.crate.SetCrate;
import com.paratopiamc.customshop.shop.SetShopCount;
import com.paratopiamc.customshop.shop.ShopCreation;
import com.paratopiamc.customshop.shop.ShopRemoval;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * CommandExecutor for plugin commands, more specifically {@code /customshop}
 * commands.
 */
public class CSComdExec implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return false;
        } else {
            CSComd comd;
            switch (args[0]) {
            case "gettotal":
                comd = new GetTotal(sender);
                break;
            case "givekey":
                comd = new GiveKey(sender, args);
                break;
            case "lockall":
                comd = new LockAll(sender, args);
                break;
            case "setcrate":
                comd = new SetCrate(sender);
                break;
            case "removeshop":
                comd = new ShopRemoval(sender);
                break;
            case "newshop":
                comd = new ShopCreation(sender, false);
                break;
            case "newadminshop":
                comd = new ShopCreation(sender, true);
                break;
            case "setcount":
                comd = new SetShopCount(sender, args);
                break;
            case "reload":
                comd = new Reload(sender);
                break;
            default:
                comd = null;
                break;
            }
            return comd == null ? false : comd.exec();
        }
    }
}
