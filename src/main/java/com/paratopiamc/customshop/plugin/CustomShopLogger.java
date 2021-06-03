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

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

/**
 * Standardized logger for the plugin.
 */
public class CustomShopLogger {
    /**
     * Level of log message.
     */
    public enum Level {
        FAIL(ChatColor.RED), INFO(ChatColor.WHITE), WARN(ChatColor.YELLOW), SUCCESS(ChatColor.GREEN);

        private ChatColor color;

        Level(ChatColor color) {
            this.color = color;
        }

        @Override
        public String toString() {
            return this.color.toString();
        }
    }

    private static ConsoleCommandSender getLogger() {
        return CustomShop.getPlugin().getServer().getConsoleSender();
    }

    /**
     * Uses {@link ConsoleCommandSender} to log color-coded messages.
     * 
     * @param message message to send to console
     * @param type    {@link CustomShopLogger.Level} associated with the message
     */
    public static void sendMessage(String message, Level type) {
        getLogger().sendMessage(type + "§l[CustomShop] " + message);
    }
}
