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

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import com.paratopiamc.customshop.plugin.CustomShop;

public class SQLite extends Database {
    // Player is 36 characters as we are using UUID to represent a player.
    private String SQLiteCreateTotalShopsOwnedTable = "CREATE TABLE IF NOT EXISTS " + totalShopOwned
            + " (`player` varchar(36) NOT NULL, `total_shops_owned` INTEGER NOT NULL, PRIMARY KEY (`player`));";
    private String SQLiteCreateShopsUnlockedTable = "CREATE TABLE IF NOT EXISTS " + shopsUnlocked
            + " (`player` varchar(36) NOT NULL, `shops_unlocked` INTEGER NOT NULL);";
    private String SQLiteCreatePendingTransactionMessagesTable = "CREATE TABLE IF NOT EXISTS " + pendingTransactions
            + " (`player` varchar(36) NOT NULL, `customer` varchar(36) NOT NULL, `selling` INTEGER NOT NULL, "
            + "`item_name` TEXT NOT NULL, `amount` INTEGER NOT NULL, `total_cost` REAL NOT NULL);";

    public SQLite(CustomShop instance) {
        super(instance);
    }

    @Override
    public Connection getSQLConnection() {
        File dataFolder = new File(plugin.getDataFolder(), dbname + ".db");
        if (!dataFolder.exists()) {
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: " + dbname + ".db");
            }
        }
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
    }

    @Override
    public void load() {
        connection = getSQLConnection();
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(SQLiteCreateTotalShopsOwnedTable);
            s.executeUpdate(SQLiteCreateShopsUnlockedTable);
            s.executeUpdate(SQLiteCreatePendingTransactionMessagesTable);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initialize();
    }
}