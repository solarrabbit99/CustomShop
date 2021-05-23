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

package com.paratopiamc.customshop.database;

import java.util.logging.Level;
import com.paratopiamc.customshop.plugin.CustomShop;

/** Error loggers. */
public class Errors {
    public static String sqlConnectionExecute() {
        return "Couldn't execute MySQL statement: ";
    }

    public static String sqlConnectionClose() {
        return "Failed to close MySQL connection: ";
    }

    public static String noSQLConnection() {
        return "Unable to retreive MYSQL connection: ";
    }

    public static String noTableFound() {
        return "Database Error: No Table Found";
    }

    public static void execute(CustomShop plugin, Exception ex) {
        plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
    }

    public static void close(CustomShop plugin, Exception ex) {
        plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
    }
}
