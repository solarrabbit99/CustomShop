package plugin;

import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import commands.GetTotal;
import commands.GiveKey;
import commands.LockAll;
import commands.SetCrate;
import database.Database;
import database.SQLite;
import events.OpenCrate;
import events.ShopCreation;
import events.vm.CloseInventory;
import events.vm.InteractInventory;
import events.vm.ListItem;
import events.vm.OpenInventory;
import events.vm.ShopRemoval;

/**
 * A custom chestshop plugin that implements custom shop designs.
 *
 * @author SolarRabbit
 */
public final class CustomShop extends JavaPlugin {
    private static Economy economy;
    private static CustomShop pluginInstance;
    private Database database;

    @Override
    public void onEnable() {
        pluginInstance = this;
        Logger log = this.getLogger();
        if (!setUpEconomy()) {
            log.info("[CustomShops] - No Vault dependencies found! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (!this.getDataFolder().exists()) {
            try {
                this.getDataFolder().mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new OpenInventory(), this);
        pluginManager.registerEvents(new CloseInventory(), this);
        pluginManager.registerEvents(new InteractInventory(), this);
        pluginManager.registerEvents(new ListItem(), this);
        pluginManager.registerEvents(new ShopCreation(), this);
        pluginManager.registerEvents(new OpenCrate(), this);
        getCommand("newshop").setExecutor(new ShopCreation());
        getCommand("removeshop").setExecutor(new ShopRemoval());
        getCommand("setcrate").setExecutor(new SetCrate(this));
        getCommand("gettotal").setExecutor(new GetTotal());
        getCommand("givekey").setExecutor(new GiveKey());
        getCommand("lockall").setExecutor(new LockAll());
        InteractInventory.initConversationFactory(this);
        ListItem.initConversationFactory(this);

        this.database = new SQLite(this);
        this.database.load();

        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        CloseInventory.saveAll();
        super.onDisable();
    }

    /**
     * Get economy provider of the server.
     *
     * @return {@code true} if the detected provider is valid
     */
    private boolean setUpEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        } else {
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                return false;
            } else {
                economy = rsp.getProvider();
                return economy != null;
            }
        }
    }

    /**
     * Get the current economy that the plugin is using.
     *
     * @return economy instance
     */
    public static Economy getEconomy() {
        return economy;
    }

    /**
     * Return instance of plugin that is running.
     *
     * @return plugin instance
     */
    public static CustomShop getPlugin() {
        return pluginInstance;
    }

    /**
     * Return database used by the plugin.
     *
     * @return database
     */
    public Database getDatabase() {
        return this.database;
    }
}
