package UUIDMaps;

import java.util.HashMap;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;
import CustomUIs.VendingMachineUI;

/**
 * Contains all the mapping from player to corresponding interacting
 * entities/items.
 */
public class VendingMachine {
    public static HashMap<UUID, UUID> playerToArmorStand = new HashMap<>();
    public static HashMap<UUID, VendingMachineUI> playerToVendingUI = new HashMap<>();
    public static HashMap<UUID, ItemStack> purchasing = new HashMap<>();
}
