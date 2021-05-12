package database;

import java.util.logging.Level;
import plugin.CustomShop;

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
