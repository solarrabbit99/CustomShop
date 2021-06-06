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
