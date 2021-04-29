package Utils;

import java.util.HashMap;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;
import CustomUIs.CreationGUI;
import CustomUIs.VendingMachineUI;

/**
 * Contains all the mappings from player's UUID to corresponding interacting
 * entities/items/GUIs.
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
