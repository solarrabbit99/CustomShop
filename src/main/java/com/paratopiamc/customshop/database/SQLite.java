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
    private static String dbname = "player_data";
    // Player is 36 characters as we are using UUID to represent a player.
    private String SQLiteCreateTokensTable = "CREATE TABLE IF NOT EXISTS " + dbname + " ("
            + "`player` varchar(36) NOT NULL," + "`shops_unlocked` varchar(1024) NOT NULL,"
            + "`total_shops_owned` int(2) NOT NULL," + "PRIMARY KEY (`player`));";

    public SQLite(CustomShop instance) {
        super(instance, dbname);
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
            s.executeUpdate(SQLiteCreateTokensTable);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initialize();
    }
}