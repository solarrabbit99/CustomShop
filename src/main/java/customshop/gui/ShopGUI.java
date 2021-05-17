package customshop.gui;

import org.bukkit.entity.ArmorStand;

public interface ShopGUI {
    void openUI();

    void openOwnerUI();

    ArmorStand getArmorStand();

    boolean isOwner();

    void saveInventories();
}
