package Listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ShopCreator {
    public String createShop(Location location, Player owner, ItemStack item);
}
