package CustomUIs;

import java.util.LinkedList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import UUIDMaps.PendingShopCreation;
import Utils.UIUtils;

public class CreationGUI {
    private static int[] vendingMachineID = new int[] { 100000, 100001, 100002 };
    private static String[] vendingMachineNames = new String[] { "Wooden", "Stone", "Nether" };
    private static Inventory[] pages;
    private int currentPage;

    /**
     * Set up a GUI. Ensure that {@code CreationGUI.setUpGUI()} is called before
     * using the constructor.
     */
    private CreationGUI() {
        this.currentPage = 0;
    }

    /**
     * This method must be run to set up all static variables before calling other
     * methods.
     */
    public static void setUpGUI() {
        int noOfItems = vendingMachineID.length;
        LinkedList<String> names = new LinkedList<>();
        for (String e : vendingMachineNames) {
            names.add(e + " Vending Machine");
        }
        LinkedList<Integer> ids = new LinkedList<>();
        for (int e : vendingMachineID) {
            ids.add(e);
        }

        final int noOfPages = ((Double) Math.ceil(noOfItems / 27.0)).intValue();
        pages = new Inventory[noOfPages];

        int item = 0;
        for (int i = 0; i < noOfPages; i++) {
            pages[i] = Bukkit.createInventory(null, 9 * 4, "§e§lCustom Shops");

            // Setting up UI elemenets on the last row.
            int[] blackSlots = new int[] { 0, 1, 2, 6, 7, 8 };
            for (int j : blackSlots) {
                UIUtils.createItem(pages[i], 3, j, Material.BLACK_STAINED_GLASS_PANE, 1, " ");
            }
            UIUtils.createItem(pages[i], 3, 3, Material.ARROW, 1, "§eBack");
            UIUtils.createItem(pages[i], 3, 4, Material.BARRIER, 1, "§cClose");
            UIUtils.createItem(pages[i], 3, 5, Material.ARROW, 1, "§eNext");

            for (int j = 0; j < 27; j++) {
                if (i == noOfPages - 1 && item == noOfItems)
                    break;
                UIUtils.createItem(pages[i], j, Material.PAPER, 1, ids.poll(), names.poll());
                item++;
            }
        }
    }

    /**
     * Opens the first page for its viewer.
     *
     * @throws NullPointerException if GUI is not yet initialised
     * @see {@code CreationGUI.setUpGUI()}
     */
    public static void openFirstPage(Player player) {
        CreationGUI gui = new CreationGUI();
        PendingShopCreation.playerToCreationGUI.put(player.getUniqueId(), gui);
        player.openInventory(pages[gui.currentPage]);
    }

    /**
     * Navigate to the previous page for its viewer.
     *
     * @param player viewer of the GUI
     * @throws NullPointerException if GUI is not yet initialised, or player hasn't
     *                              up the GUI
     * @see {@code CreationGUI.setUpGUI()}
     */
    public static void nextPage(Player player) {
        CreationGUI gui = PendingShopCreation.playerToCreationGUI.get(player.getUniqueId());
        if (gui.currentPage != pages.length - 1) {
            gui.currentPage++;
            player.openInventory(pages[gui.currentPage]);
        }
    }

    /**
     * Navigate to the previous page for its viewer.
     *
     * @param player viewer of the GUI
     * @throws NullPointerException if GUI is not yet initialised, or player hasn't
     *                              open up the GUI
     * @see {@code CreationGUI.setUpGUI()}
     */
    public static void previousPage(Player player) {
        CreationGUI gui = PendingShopCreation.playerToCreationGUI.get(player.getUniqueId());
        if (gui.currentPage != 0) {
            gui.currentPage--;
            player.openInventory(pages[gui.currentPage]);
        }
    }

    /**
     * Removes any mapping of a player if he/she closed the GUI.
     *
     * @param player player that closed the GUI
     * @return a boolean value indicating if the specified player had the GUI open
     */
    public static boolean playerClosedGUI(Player player) {
        CreationGUI gui = PendingShopCreation.playerToCreationGUI.remove(player.getUniqueId());
        return gui != null;
    }
}
