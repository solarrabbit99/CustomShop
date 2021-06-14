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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import com.paratopiamc.customshop.plugin.CustomShop;
import com.paratopiamc.customshop.plugin.CustomShopLogger;
import com.paratopiamc.customshop.plugin.CustomShopLogger.Level;
import com.paratopiamc.customshop.utils.LanguageUtils;
import com.paratopiamc.customshop.utils.UIUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/** GUI for players to create a new custom shop. */
public class CreationGUI {
    public static int noOfItems;
    public static LinkedList<String> names;
    public static LinkedList<Integer> modelData;
    private static int noOfPages;
    private static List<Integer> defaults;

    private List<Integer> unlockedShops;
    private Inventory[] pages;
    private int currentPage;
    private boolean isAdmin;
    private Player player;

    /**
     * Set up a GUI for the player. Called when static method
     * {@link #openFirstPage(Player)} is called.
     */
    public CreationGUI(Player player, boolean isAdmin) {
        this.isAdmin = isAdmin;
        this.currentPage = 0;
        if (!isAdmin && !CustomShop.getPlugin().getConfig().getBoolean("unlock-all")) {
            unlockedShops = CustomShop.getPlugin().getDatabase().getUnlockedShops(player);
        }
        this.setUpGUI(player);
        this.player = player;
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

        int defaultVM = CustomShop.getPlugin().getConfig().getInt("defaults.vending-machine");
        Set<String> vm = CustomShop.getPlugin().getConfig().getConfigurationSection("vending-machine").getKeys(false);
        for (String e : vm) {
            String customModelName = CustomShop.getPlugin().getConfig().getString("vending-machine." + e + ".name");
            if (customModelName == null) {
                CustomShopLogger.sendMessage(
                        "Name not set for at least one of the vending machines! Disabling plugin...", Level.FAIL);
                Bukkit.getPluginManager().disablePlugin(CustomShop.getPlugin());
            }
            names.add(customModelName);
            Integer customModelData = CustomShop.getPlugin().getConfig().getInt("vending-machine." + e + ".model-data");
            if (customModelData == 0) {
                CustomShopLogger.sendMessage(
                        "Missing Custom Model Data or set to 0 for at least one of the vending machines! Disabling plugin...",
                        Level.FAIL);
                Bukkit.getPluginManager().disablePlugin(CustomShop.getPlugin());
            }
            modelData.add(customModelData);
            defaults.add(defaultVM);
        }

        int defaultBriefcase = CustomShop.getPlugin().getConfig().getInt("defaults.briefcase");
        Set<String> bc = CustomShop.getPlugin().getConfig().getConfigurationSection("briefcase").getKeys(false);
        for (String e : bc) {
            String customModelName = CustomShop.getPlugin().getConfig().getString("briefcase." + e + ".name");
            if (customModelName == null) {
                CustomShopLogger.sendMessage("Name not set for at least one of the briefcases! Disabling plugin...",
                        Level.FAIL);
                Bukkit.getPluginManager().disablePlugin(CustomShop.getPlugin());
            }
            names.add(customModelName);
            Integer customModelData = CustomShop.getPlugin().getConfig().getInt("briefcase." + e + ".model-data");
            if (customModelData == 0) {
                CustomShopLogger.sendMessage(
                        "Missing Custom Model Data or set to 0 for at least one of the briefcases! Disabling plugin...",
                        Level.FAIL);
                Bukkit.getPluginManager().disablePlugin(CustomShop.getPlugin());
            }
            modelData.add(customModelData);
            defaults.add(defaultBriefcase);
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
            pages[i] = Bukkit.createInventory(null, 9 * 4, isAdmin ? LanguageUtils.getString("admin-shop-creation")
                    : LanguageUtils.getString("shop-creation"));

            // Setting up UI elemenets on the last row.
            int[] blackSlots = new int[] { 0, 1, 2, 6, 7, 8 };
            for (int j : blackSlots) {
                UIUtils.createItem(pages[i], 3, j, Material.BLACK_STAINED_GLASS_PANE, 1, " ");
            }
            UIUtils.createItem(pages[i], 3, 3, Material.ARROW, 1, "§e" + LanguageUtils.getString("icons.previous"));
            UIUtils.createItem(pages[i], 3, 4, Material.BARRIER, 1, "§c" + LanguageUtils.getString("icons.close"));
            UIUtils.createItem(pages[i], 3, 5, Material.ARROW, 1, "§e" + LanguageUtils.getString("icons.next"));

            for (int j = 0; j < 27; j++) {
                if (i == noOfPages - 1 && item == noOfItems)
                    break;
                UIUtils.createItem(pages[i], j, Material.PAPER, 1, iterModelData.poll(), iterNames.poll());
                item++;
            }
        }
    }

    public Inventory currentInventory() {
        return this.pages[this.currentPage];
    }

    public boolean isAdmin() {
        return this.isAdmin;
    }

    /**
     * Opens the first page for its viewer.
     * 
     * @throws NullPointerException if GUI is not yet initialised
     * @see #setUpGUI()
     */
    public void openFirstPage() {
        currentPage = 0;
        Bukkit.getScheduler().runTask(CustomShop.getPlugin(), () -> player.openInventory(pages[currentPage]));
    }

    /**
     * Navigate to the previous page for its viewer.
     *
     * @throws NullPointerException if player has yet to open the first page
     */
    public void nextPage() {
        if (currentPage != pages.length - 1) {
            currentPage++;
            Bukkit.getScheduler().runTask(CustomShop.getPlugin(), () -> player.openInventory(pages[currentPage]));
        }
    }

    /**
     * Navigate to the previous page for its viewer.
     *
     * @throws NullPointerException if player has yet to open the first page
     */
    public void previousPage() {
        if (currentPage != 0) {
            currentPage--;
            Bukkit.getScheduler().runTask(CustomShop.getPlugin(), () -> player.openInventory(pages[currentPage]));
        }
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
