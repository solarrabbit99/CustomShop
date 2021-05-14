package utils;

import java.util.HashMap;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;

import gui.CreationGUI;
import gui.VendingMachineUI;

/**
 * Contains all the mappings from player's UUID to corresponding interacting
 * entities/items/GUIs. Any data that is not required of the server to save over
 * reboots should be saved here.
 */
public class UUIDMaps {
    /** Maps player's UUID to a {@link CreationGUI}. */
    public static HashMap<UUID, CreationGUI> playerToCreationGUI = new HashMap<>();
    /** Maps player's UUID to an armor stand's UUID. */
    public static HashMap<UUID, UUID> playerToArmorStand = new HashMap<>();
    /** Maps player's UUID to a {@link VendingMachineUI}. */
    public static HashMap<UUID, VendingMachineUI> playerToVendingUI = new HashMap<>();
    /** Maps player's UUID to the item the player is attempting to purchase. */
    public static HashMap<UUID, ItemStack> purchasing = new HashMap<>();
}
