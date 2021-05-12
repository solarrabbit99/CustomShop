package Database;

import java.util.logging.Level;

import Plugin.CustomShops;

public class Error {

    public static void execute(CustomShops plugin, Exception ex) {
        plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
    }

    public static void close(CustomShops plugin, Exception ex) {
        plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
    }

}
