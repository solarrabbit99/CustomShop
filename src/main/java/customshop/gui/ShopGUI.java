package customshop.gui;

import org.bukkit.entity.ArmorStand;

/**
 * Encapsulates a shop GUI, which contains inventory views for interacting with
 * custom shops. Implementing classes do not check for any permissions regarding
 * shop access.
 */
public interface ShopGUI {
    /**
     * Opens public InventoryView of the shop that facilitates purchasing or selling
     * of items to/from the shop.
     */
    void openUI();

    /**
     * Opens owner's InventoryView of the shop that allows adding or removing shop
     * items.
     */
    void openOwnerUI();

    /**
     * Get the armor stand entity associated with the shop.
     *
     * @return armor stand reference
     */
    ArmorStand getArmorStand();

    /**
     * Checks if viewer is owner of the shop.
     *
     * @return {@code true} if viewer is owner of the shop
     */
    boolean isOwner();

    /**
     * Save any changes to player and/or the shop.
     */
    void saveInventories();
}
