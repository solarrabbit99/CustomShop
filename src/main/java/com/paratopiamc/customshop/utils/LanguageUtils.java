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
        languageFile = new File(plugin.getDataFolder(), language + ".yml");
        languageFile.getParentFile().mkdirs();
        plugin.saveResource(language + ".yml", true);
        languageConfiguration = new YamlConfiguration();
        try {
            languageConfiguration.load(languageFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static String getString(String node) {
        return languageConfiguration.getString(node);
    }
}
