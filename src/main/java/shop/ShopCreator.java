package shop;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Encapsulates a creator of a shop. Each type of custom shops should have a
 * creator of its own type that implements this interface.
 */
@FunctionalInterface
public interface ShopCreator {
    /**
     * The main method where the creator creates the shop with its own requirements
     * and specifications. A message is returned as a feedback to player on whether
     * the shop is created successfully.
     *
     * @param location location in which the shop will attempt on top of
     * @param owner    owner of the shop
     * @param item     design of the shop
     * @return feedback message to be sent to the player involved
     */
    public String createShop(Location location, Player owner, ItemStack item);
}
