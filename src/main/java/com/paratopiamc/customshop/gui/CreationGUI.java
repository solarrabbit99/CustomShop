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

package com.paratopiamc.customshop.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import com.paratopiamc.customshop.plugin.CustomShop;
import com.paratopiamc.customshop.utils.UIUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/** GUI for players to create a new custom shop. */
public class CreationGUI {
    public static int noOfItems;
    public static LinkedList<String> names;
    public static LinkedList<Integer> modelData;

    private static int noOfPages;
    private static List<Integer> defaults;
    private static HashMap<UUID, CreationGUI> playerToCreationGUI;

    private List<Integer> unlockedShops;
    private Inventory[] pages;
    private int currentPage;

    /**
     * Set up a GUI for the player. Called when static method
     * {@link #openFirstPage(Player)} is called.
     */
    private CreationGUI(Player player) {
        this.currentPage = 0;
        if (!CustomShop.getPlugin().getConfig().getBoolean("unlock-all")) {
            unlockedShops = CustomShop.getPlugin().getDatabase().getUnlockedShops(player);
        }
        this.setUpGUI(player);
    }

    /**
     * Read from loaded configuration of {@code config.yml} the model data of custom
     * shops. Initializes class's static variables. Must be run, preferably in
     * plugin's {@code onEnable()} method, before any creation of
     * {@link CreationGUI}s.
     */
    public static void initialize() {
        names = new LinkedList<>();
        modelData = new LinkedList<>();
        defaults = new ArrayList<>();
        playerToCreationGUI = new HashMap<>();
        ConsoleCommandSender logger = CustomShop.getPlugin().getServer().getConsoleSender();

        int defaultVM = CustomShop.getPlugin().getConfig().getInt("defaults.vending-machine");
        Set<String> vm = CustomShop.getPlugin().getConfig().getConfigurationSection("vending-machine").getKeys(false);
        for (String e : vm) {
            String customModelName = CustomShop.getPlugin().getConfig().getString("vending-machine." + e + ".name");
            if (customModelName == null) {
                logger.sendMessage(
                        "§c§l[CustomShop] Name not set for at least one of the vending machines! Disabling plugin...");
                Bukkit.getPluginManager().disablePlugin(CustomShop.getPlugin());
            }
            names.add(customModelName);
            Integer customModelData = CustomShop.getPlugin().getConfig().getInt("vending-machine." + e + ".model-data");
            if (customModelData == 0) {
                logger.sendMessage(
                        "§c§l[CustomShop] Missing Custom Model Data or set to 0 for at least one of the vending machines! Disabling plugin...");
                Bukkit.getPluginManager().disablePlugin(CustomShop.getPlugin());
            }
            modelData.add(customModelData);
            defaults.add(defaultVM);
        }

        noOfItems = names.size();
        noOfPages = ((Double) Math.ceil(noOfItems / 27.0)).intValue();
    }

    /**
     * Set up all player's unlocked custom shops variables.
     *
     * @param player player opening the GUI
     */
    public void setUpGUI(Player player) {
        @SuppressWarnings("unchecked")
        LinkedList<String> iterNames = (LinkedList<String>) names.clone();
        @SuppressWarnings("unchecked")
        LinkedList<Integer> iterModelData = (LinkedList<Integer>) modelData.clone();
        if (unlockedShops != null) {
            iterModelData.replaceAll(e -> unlockedShops.contains(e) ? e : getDefault(e));
        }
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
                UIUtils.createItem(pages[i], j, Material.PAPER, 1, iterModelData.poll(), iterNames.poll());
                item++;
            }
        }
    }

    /**
     * Opens the first page for its viewer.
     *
     * @throws NullPointerException if GUI is not yet initialised
     * @see #setUpGUI()
     */
    public static void openFirstPage(Player player) {
        CreationGUI gui = new CreationGUI(player);
        playerToCreationGUI.put(player.getUniqueId(), gui);
        Bukkit.getScheduler().runTask(CustomShop.getPlugin(), () -> player.openInventory(gui.pages[gui.currentPage]));
    }

    /**
     * Navigate to the previous page for its viewer.
     *
     * @param player viewer of the GUI
     * @throws NullPointerException if player has yet to open the first page
     */
    public static void nextPage(Player player) {
        CreationGUI gui = playerToCreationGUI.get(player.getUniqueId());
        if (gui.currentPage != gui.pages.length - 1) {
            gui.currentPage++;
            Bukkit.getScheduler().runTask(CustomShop.getPlugin(),
                    () -> player.openInventory(gui.pages[gui.currentPage]));
        }
    }

    /**
     * Navigate to the previous page for its viewer.
     *
     * @param player viewer of the GUI
     * @throws NullPointerException if player has yet to open the first page
     */
    public static void previousPage(Player player) {
        CreationGUI gui = playerToCreationGUI.get(player.getUniqueId());
        if (gui.currentPage != 0) {
            gui.currentPage--;
            Bukkit.getScheduler().runTask(CustomShop.getPlugin(),
                    () -> player.openInventory(gui.pages[gui.currentPage]));
        }
    }

    /**
     * Removes any mapping of a player if he/she closed the GUI.
     *
     * @param player player that closed the GUI
     * @return {@code true} if the specified player had the GUI open
     */
    public static boolean closeGUI(Player player) {
        Bukkit.getScheduler().runTask(CustomShop.getPlugin(), () -> player.closeInventory());
        CreationGUI gui = playerToCreationGUI.remove(player.getUniqueId());
        return gui != null;
    }

    /**
     * Return the default custom model data of a shop given its variant's model
     * data.
     *
     * @param model variant's model data
     * @return default model data
     */
    private static Integer getDefault(Integer model) {
        int index = modelData.indexOf(model);
        return defaults.get(index);
    }
}
