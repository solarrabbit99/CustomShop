package database;

import java.util.logging.Level;

import plugin.CustomShop;

public class Error {

    public static void execute(CustomShop plugin, Exception ex) {
        plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
    }

    public static void close(CustomShop plugin, Exception ex) {
        plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
    }

}
