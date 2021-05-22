package com.paratopiamc.customshop.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import com.paratopiamc.customshop.plugin.CustomShop;
import org.bukkit.entity.Player;

/**
 * Parent class of a database loader. Contains implementation of data retrieval
 * and update methods.
 */
public abstract class Database {
    CustomShop plugin;
    Connection connection;
    /** Name of database table. */
    static String dbname = "player_data";
    static String totalShopOwned = "total_shops_owned";
    static String shopsUnlocked = "shops_unlocked";

    /**
     * Constuctor for database.
     *
     * @param instance plugin instance used for logging
     * @param dbname   name of database table
     */
    public Database(CustomShop instance) {
        plugin = instance;
    }

    /**
     * Returns connection established by the database loader.
     *
     * @return SQL connection
     */
    public abstract Connection getSQLConnection();

    /**
     * Executes create table statement.
     */
    public abstract void load();

    /**
     * Initializes SQL connection.
     */
    public void initialize() {
        connection = getSQLConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + totalShopOwned + " WHERE player = ?");
            ResultSet rs = ps.executeQuery();
            close(ps, rs);
            ps = connection.prepareStatement("SELECT * FROM " + shopsUnlocked + " WHERE player = ?");
            rs = ps.executeQuery();
            close(ps, rs);
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Unable to retrieve connection", ex);
        }
    }

    /**
     * Returns the list of custom shops (represented by its custom model data)
     * unlocked by the player.
     *
     * @param player player of interest
     * @return list of shops unlocked by the player
     */
    public List<Integer> getUnlockedShops(Player player) {
        String string = player.getUniqueId().toString();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Integer> result = new ArrayList<>();
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + shopsUnlocked + " WHERE player = '" + string + "';");
            rs = ps.executeQuery();
            while (rs.next()) {
                result.add(rs.getInt("shops_unlocked"));
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return result;
    }

    /**
     * Returns the total number of shops owned by player. The number of shops is
     * capped at 99, due to SQL {@code int(2)} declaration.
     *
     * @param player player of interest
     * @return total custom shops owned by player
     */
    public Integer getTotalShopOwned(Player player) {
        String string = player.getUniqueId().toString();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Integer result = 0;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + totalShopOwned + " WHERE player = '" + string + "';");
            rs = ps.executeQuery();
            if (rs.next())
                result = rs.getInt("total_shops_owned");
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return result;
    }

    /**
     * Decrements the total number of custom shops owned by the player. Lower limit
     * set to 0.
     *
     * @param player player of interest
     */
    public void decrementTotalShopsOwned(Player player) {
        Integer previousTotal = getTotalShopOwned(player);
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("REPLACE INTO " + totalShopOwned + " (player,total_shops_owned) VALUES('"
                    + player.getUniqueId().toString() + "', " + (previousTotal - 1) + ")");
            ps.executeUpdate();
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
    }

    /**
     * Increments the total number of custom shops owned by the player.
     *
     * @param player player of interest
     */
    public void incrementTotalShopsOwned(Player player) {
        Integer previousTotal = getTotalShopOwned(player);
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("REPLACE INTO " + totalShopOwned + " (player,total_shops_owned) VALUES('"
                    + player.getUniqueId().toString() + "', " + (previousTotal + 1) + ")");
            ps.executeUpdate();
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
    }

    /**
     * Updates the list of shops owned by the player.
     *
     * @param player        player of interest
     * @param unlockedShops list of shops unlocked by the player
     */
    public void setUnlockedShops(Player player, List<Integer> unlockedShops) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement(
                    "DELETE FROM " + shopsUnlocked + " WHERE player = '" + player.getUniqueId().toString() + "';");
            ps.executeUpdate();
            for (Integer e : unlockedShops) {
                ps = conn.prepareStatement("INSERT INTO " + shopsUnlocked + " (player,shops_unlocked) VALUES('"
                        + player.getUniqueId().toString() + "'," + e + ");");
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
    }

    /**
     * Releases both the {@link PreparedStatement} and {@link ResultSet} of the
     * database.
     *
     * @param ps PreparedStatement of the database
     * @param rs ResultSet of the database
     */
    public void close(PreparedStatement ps, ResultSet rs) {
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            Errors.close(plugin, ex);
        }
    }
}