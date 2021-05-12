package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.entity.Player;
import Plugin.CustomShops;

public abstract class Database {
    CustomShops plugin;
    Connection connection;
    public String table;
    public int tokens = 0;

    public Database(CustomShops instance, String dbname) {
        plugin = instance;
        this.table = dbname;
    }

    public abstract Connection getSQLConnection();

    public abstract void load();

    public void initialize() {
        connection = getSQLConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + table + " WHERE player = ?");
            ResultSet rs = ps.executeQuery();
            close(ps, rs);

        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Unable to retrieve connection", ex);
        }
    }

    /**
     * Returns a string representation of the list of custom shops unlocked by the
     * player.
     *
     * @param player player of interest
     * @return a list of shops unlocked by the player in string form
     */
    private String getUnlockedShopsString(Player player) {
        String string = player.getUniqueId().toString();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE player = '" + string + "';");

            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("player").equalsIgnoreCase(string.toLowerCase())) {
                    return rs.getString("shops_unlocked");
                }
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
        return "[]";
    }

    /**
     * Returns an array of custom shops unlocked by the player.
     *
     * @param player player of interest
     * @return an array of shops unlocked by the player
     */
    public List<Integer> getUnlockedShops(Player player) {
        String arrayString = this.getUnlockedShopsString(player);
        if (arrayString.equalsIgnoreCase("[]") || arrayString.equalsIgnoreCase("")) {
            return new ArrayList<>();
        }
        arrayString = arrayString.substring(1, arrayString.length() - 1);
        String[] strArr = arrayString.split(", ");
        List<Integer> intList = new ArrayList<>();
        for (String e : strArr) {
            intList.add(Integer.parseInt(e));
        }
        return intList;
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
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE player = '" + string + "';");

            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("player").equalsIgnoreCase(string.toLowerCase())) {
                    return rs.getInt("total_shops_owned");
                }
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
        return 0;
    }

    /**
     * Updates the all data of the player with the given inputs.
     *
     * @param player          player of interest
     * @param unlockedShops   string representation of the list of shops unlocked by
     *                        the player
     * @param totalShopsOwned total shops owned by the player
     */
    public void setData(Player player, List<Integer> unlockedShops, Integer totalShopsOwned) {
        Connection conn = null;
        PreparedStatement ps = null;
        String unlockedShopsString = Arrays.toString(unlockedShops.toArray());
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement(
                    "REPLACE INTO " + table + " (player,shops_unlocked,total_shops_owned) VALUES(?,?,?)");
            ps.setString(1, player.getUniqueId().toString());
            ps.setString(2, unlockedShopsString);
            ps.setInt(3, totalShopsOwned);
            ps.executeUpdate();
            return;
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
        return;
    }

    /**
     * Decrements the total number of custom shops owned by the player.
     *
     * @param player player of interest
     */
    public void decrementTotalShopsOwned(Player player) {
        Integer previousTotal = getTotalShopOwned(player);
        List<Integer> unlockedShops = getUnlockedShops(player);
        setData(player, unlockedShops, previousTotal - 1);
    }

    /**
     * Increments the total number of custom shops owned by the player.
     *
     * @param player player of interest
     */
    public void incrementTotalShopsOwned(Player player) {
        Integer previousTotal = getTotalShopOwned(player);
        List<Integer> unlockedShops = getUnlockedShops(player);
        setData(player, unlockedShops, previousTotal + 1);
    }

    /**
     * Updates the list of shops owned by the player.
     *
     * @param player player of interest
     * @param lst    list of shops unlocked by the player
     */
    public void setUnlockedShops(Player player, List<Integer> unlockedShops) {
        Integer totalShopsOwned = getTotalShopOwned(player);
        setData(player, unlockedShops, totalShopsOwned);
    }

    public void close(PreparedStatement ps, ResultSet rs) {
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            Error.close(plugin, ex);
        }
    }
}