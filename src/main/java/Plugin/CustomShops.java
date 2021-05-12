package Plugin;

import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import Database.Database;
import Database.SQLite;
import Listeners.VendingMachine.ShopRemoval;
import Listeners.GetTotal;
import Listeners.GiveKey;
import Listeners.OpenCrate;
import Listeners.SetCrate;
import Listeners.ShopCreation;
import Listeners.VendingMachine.CloseInventory;
import Listeners.VendingMachine.InteractInventory;
import Listeners.VendingMachine.ListItem;
import Listeners.VendingMachine.OpenInventory;

/**
 * A custom chestshop plugin that implements custom shop designs.
 *
 * @author SolarRabbit
 */
public final class CustomShops extends JavaPlugin {
    private static Economy economy;
    private static CustomShops pluginInstance;
    private static final OpenInventory openInventory = new OpenInventory();
    private static final CloseInventory closeInventory = new CloseInventory();
    private static final InteractInventory interactInventory = new InteractInventory();
    private static final ShopCreation shopCreation = new ShopCreation();
    private static final ShopRemoval shopRemoval = new ShopRemoval();
    private static final ListItem listItem = new ListItem();
    private static final SetCrate setCrate = new SetCrate();
    private static final GetTotal getTotal = new GetTotal();
    private static final GiveKey giveKey = new GiveKey();
    private static final OpenCrate openCrate = new OpenCrate();
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
        pluginManager.registerEvents(openInventory, this);
        pluginManager.registerEvents(closeInventory, this);
        pluginManager.registerEvents(interactInventory, this);
        pluginManager.registerEvents(listItem, this);
        pluginManager.registerEvents(shopCreation, this);
        pluginManager.registerEvents(openCrate, this);
        getCommand("newshop").setExecutor(shopCreation);
        getCommand("removeshop").setExecutor(shopRemoval);
        getCommand("setcrate").setExecutor(setCrate);
        getCommand("gettotal").setExecutor(getTotal);
        getCommand("givekey").setExecutor(giveKey);
        InteractInventory.initConversationFactory(this);
        ListItem.initConversationFactory(this);

        this.database = new SQLite(this);
        this.database.load();
    }

    @Override
    public void onDisable() {
        closeInventory.saveAll();
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
    public static CustomShops getPlugin() {
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
