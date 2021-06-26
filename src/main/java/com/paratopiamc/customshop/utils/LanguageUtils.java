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

package com.paratopiamc.customshop.utils;

import java.io.File;
import java.io.IOException;
import com.paratopiamc.customshop.plugin.CustomShop;
import com.paratopiamc.customshop.plugin.CustomShopLogger;
import com.paratopiamc.customshop.plugin.CustomShopLogger.Level;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class LanguageUtils {
    private static String language;
    private static File languageFile;
    private static FileConfiguration languageConfiguration;

    /**
     * Users should be reminded to create a new file if they intend to edit its
     * content, as it will be overwritten on plugin reload.
     */
    public static void loadLanguageConfig() {
        Plugin plugin = CustomShop.getPlugin();
        language = plugin.getConfig().getString("language");

        File languageFolder = new File(plugin.getDataFolder(), "languages");
        File tempResource = new File(plugin.getDataFolder(), language + ".yml");

        // Create a 'languages' folder if not already exists.
        languageFile = new File(languageFolder, language + ".yml");
        languageFile.getParentFile().mkdirs();

        if (plugin.getResource(language + ".yml") != null) { // Language provided by plugin.
            // After saving resource, tempResource can then be renamed to sub-directory.
            plugin.saveResource(language + ".yml", true);
            tempResource.renameTo(languageFile);
        }

        languageConfiguration = new YamlConfiguration();
        try {
            languageConfiguration.load(languageFile);
            CustomShopLogger.sendMessage("Using language: " + language, Level.INFO);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static String getString(String node) {
        return languageConfiguration.getString(node);
    }
}
