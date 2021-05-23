package com.paratopiamc.customshop.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import com.paratopiamc.customshop.plugin.CustomShop;
import com.paratopiamc.customshop.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
    static String pendingTransactions = "pending_transaction_messages";

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
     * Initializes SQL connection by attempting to execute select statements from
     * respective tables in the database.
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
            ps = connection.prepareStatement("SELECT * FROM " + pendingTransactions + " WHERE player = ?");
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
            close(ps, conn);
        }
        return result;
    }

    /**
     * Returns the total number of shops owned by player. The number of shops is
     * capped at 99, due to SQL {@code int(2)} declaration.
     *
     * @param playerID UUUID of player of interest
     * @return total custom shops owned by player
     */
    public Integer getTotalShopOwned(UUID playerID) {
        String string = playerID.toString();
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
            close(ps, conn);
        }
        return result;
    }

    /**
     * Decrements the total number of custom shops owned by the player. Lower limit
     * set to 0.
     *
     * @param playerID UUID of player of interest
     */
    public void decrementTotalShopsOwned(UUID playerID) {
        Integer previousTotal = getTotalShopOwned(playerID);
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("REPLACE INTO " + totalShopOwned + " (player,total_shops_owned) VALUES('"
                    + playerID.toString() + "', " + (previousTotal - 1) + ")");
            ps.executeUpdate();
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            close(ps, conn);
        }
    }

    /**
     * Increments the total number of custom shops owned by the player.
     *
     * @param playerID UUID of player of interest
     */
    public void incrementTotalShopsOwned(UUID playerID) {
        Integer previousTotal = getTotalShopOwned(playerID);
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("REPLACE INTO " + totalShopOwned + " (player,total_shops_owned) VALUES('"
                    + playerID.toString() + "', " + (previousTotal + 1) + ")");
            ps.executeUpdate();
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            close(ps, conn);
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
            close(ps, conn);
        }
    }

    public void storeMessage(String ownerID, Player customer, boolean selling, ItemStack item, int amount,
            double totalCost) {
        Connection conn = null;
        PreparedStatement ps = null;
        int sell = selling ? 1 : 0;
        ItemMeta meta = item.getItemMeta();
        String itemName = meta.hasDisplayName() ? meta.getDisplayName() : item.getType().toString();
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("INSERT INTO " + pendingTransactions
                    + " (player,customer,selling,item_name,amount,total_cost) VALUES('" + ownerID + "', '"
                    + customer.getUniqueId().toString() + "'," + sell + ", '" + itemName + "', " + amount + ", "
                    + totalCost + ");");
            ps.executeUpdate();
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            close(ps, conn);
        }
    }

    public List<String> getMessages(String ownerID) {
        List<String> messages = new ArrayList<>();
        String sellMessage = plugin.getConfig().getString("customer-buy-success-owner");
        String buyMessage = plugin.getConfig().getString("customer-sell-success-owner");
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + pendingTransactions + " WHERE player = '" + ownerID + "';");
            rs = ps.executeQuery();
            while (rs.next()) {
                Player customer = Bukkit.getPlayer(UUID.fromString(rs.getString("customer")));
                String itemName = rs.getString("item_name");
                int amount = rs.getInt("amount");
                double totalCost = rs.getDouble("total_cost");
                String message;
                if (rs.getBoolean("selling")) {
                    message = MessageUtils.convertMessage(sellMessage, ownerID, customer, totalCost, itemName, amount);
                } else {
                    message = MessageUtils.convertMessage(buyMessage, ownerID, customer, totalCost, itemName, amount);
                }
                messages.add(message);
            }
            close(ps, rs);
            ps = conn.prepareStatement("DELETE FROM " + pendingTransactions + " WHERE player = '" + ownerID + "';");
            ps.executeUpdate();
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            close(ps, conn);
        }
        return messages;
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

    public void close(PreparedStatement ps, Connection conn) {
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