package customshop.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import customshop.plugin.CustomShop;
import net.milkbowl.vault.economy.Economy;

public class PlayerJoin implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent evt) {
        Economy economy = CustomShop.getEconomy();
        // TODO: Send message for all shop interactions
    }
}
