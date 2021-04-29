package Plugin;

import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import CustomUIs.CreationGUI;
import Listeners.VendingMachine.ShopRemoval;
import Listeners.ShopCreation;
import Listeners.VendingMachine.CloseInventory;
import Listeners.VendingMachine.InteractInventory;
import Listeners.VendingMachine.ListItem;
import Listeners.VendingMachine.OpenInventory;

/**
 * A custom chestshop plugin that implements custom shop designs.
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

    @Override
    public void onEnable() {
        pluginInstance = this;
        Logger log = this.getLogger();
        if (!setUpEconomy()) {
            log.info("[CustomShops] - No Vault dependencies found! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(openInventory, this);
        pluginManager.registerEvents(closeInventory, this);
        pluginManager.registerEvents(interactInventory, this);
        pluginManager.registerEvents(listItem, this);
        pluginManager.registerEvents(shopCreation, this);
        getCommand("newshop").setExecutor(shopCreation);
        getCommand("removeshop").setExecutor(shopRemoval);
        CreationGUI.setUpGUI();
        InteractInventory.initConversationFactory(this);
        ListItem.initConversationFactory(this);
    }

    @Override
    public void onDisable() {
        closeInventory.saveAll();
        super.onDisable();
    }

    /**
     * Get economy provider of the server.
     *
     * @return a boolean value of whether the detected provider is valid
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
}
