package Utils;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * This class stores utility methods for other classes.
 */
public final class UIUtils {
    private UIUtils() {
    }

    /**
     * Truncated method to translate message with ChatColor.
     *
     * @param message string to be translated
     * @return translated string
     * @deprecated use {@code §} directly instead.
     */
    @Deprecated
    public static String chat(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Creates and inserts ItemStack in given inventory. Returns the ItemStack.
     *
     * @param inv         inventory to insert stack
     * @param row         row in inventory to insert stack
     * @param column      column in inventory to insert stack
     * @param material    material of the item
     * @param amount      amount of item in the stack
     * @param displayName display name of the item
     * @param lore        lore of item
     * @return ItemStack of given specifications
     */
    public static ItemStack createItem(Inventory inv, int row, int column, Material material, int amount,
            String displayName, String... lore) {
        ItemStack item = new ItemStack(material, amount);
        List<String> loreStrings = new ArrayList<>();
        for (String s : lore) {
            loreStrings.add(s);
        }
        int invSlot = 9 * row + column;

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(loreStrings);
        item.setItemMeta(meta);
        inv.setItem(invSlot, item);
        return item;
    }

    /**
     * Creates and inserts ItemStack in given inventory. Returns the ItemStack.
     *
     * @param inv         inventory to insert stack
     * @param slot        slot in inventory to insert stack
     * @param material    material of the item
     * @param amount      amount of item in the stack
     * @param displayName display name of the item
     * @param lore        lore of item
     * @return ItemStack of given specifications
     */
    public static ItemStack createItem(Inventory inv, int slot, Material material, int amount, String displayName,
            String... lore) {
        ItemStack item = new ItemStack(material, amount);
        List<String> loreStrings = new ArrayList<>();
        for (String s : lore) {
            loreStrings.add(s);
        }

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(loreStrings);
        item.setItemMeta(meta);
        inv.setItem(slot, item);
        return item;
    }

    /**
     * Creates and inserts ItemStack in given inventory. Returns the ItemStack.
     *
     * @param inv           inventory to insert stack
     * @param slot          slot in inventory to insert stack
     * @param material      material of the item
     * @param amount        amount of item in the stack
     * @param customModelID custom model data's ID
     * @param displayName   display name of the item
     * @param lore          lore of item
     * @return ItemStack of given specifications
     */
    public static ItemStack createItem(Inventory inv, int slot, Material material, int amount, int customModelID,
            String displayName, String... lore) {
        ItemStack item = new ItemStack(material, amount);
        List<String> loreStrings = new ArrayList<>();
        for (String s : lore) {
            loreStrings.add(s);
        }

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(loreStrings);
        meta.setCustomModelData(customModelID);
        item.setItemMeta(meta);
        inv.setItem(slot, item);
        return item;
    }

    /**
     * Returns a new copy of ItemStack with given lore.
     *
     * @param item ItemStack to clone
     * @param lore array of string
     * @return new instance of ItemStack
     */
    public static ItemStack loreItem(ItemStack item, String... lore) {
        ItemStack clone = item.clone();
        ItemMeta meta = item.getItemMeta();
        List<String> loreStrings = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        for (String s : lore) {
            loreStrings.add(0, s);
        }
        meta.setLore(loreStrings);
        clone.setItemMeta(meta);
        return clone;
    }

    /**
     * Set price as lore of the item.
     *
     * @param item  ItemStack to label
     * @param price given price in double
     * @return new instance of ItemStack
     */
    public static ItemStack setPrice(ItemStack item, double price) {
        return item == null ? null : loreItem(item, "§9Price: " + Double.toString(price));
    }

    /**
     * Get price from the lore of the container.
     *
     * @param container intended container for the shop (e.g. Shulker Box)
     * @param index     the index of item in the container
     * @return price of selected item
     * @throws NullPointerException  if the string is null
     * @throws NumberFormatException if the string does not contain a parsable
     *                               double
     */
    public static double getPrice(ItemStack container, int index) {
        String label = container.getItemMeta().getLore().get(index);
        return Double.parseDouble(label);
    }

    /**
     * For converting lore list of shop's container to an array of item prices. The
     * index of prices corresponds to index of item in the container. Size of array
     * is in accordance to that of the given list.
     *
     * @param lore lore list obtained from item's meta
     * @return double array
     */
    public static double[] stringListToDoubleArray(List<String> lore) {
        double[] prices = new double[lore.size()];
        for (int i = 0; i < lore.size(); i++) {
            prices[i] = Double.parseDouble(lore.get(i));
        }
        return prices;
    }

    /**
     * For converting an array of item prices to lore list. The index of lore
     * corresponds to index of item in the container. Size of list is in accordance
     * to that of the given array.
     *
     * @param prices double array
     * @return List of string representation of the prices
     */
    public static List<String> doubleToStringList(double[] prices) {
        List<String> result = new ArrayList<>(prices.length);
        for (int i = 0; i < prices.length; i++) {
            result.add(Double.toString(prices[i]));
        }
        return result;
    }

    /**
     * Validate whether an entity is a vending machine.
     *
     * @param entity entity to be validated
     * @return a boolean value
     */
    public static boolean validate(Entity entity) {
        EntityType entityType = entity.getType();
        if (!entityType.equals(EntityType.ARMOR_STAND)) {
            return false;
        } else {
            ArmorStand armorStand = (ArmorStand) entity;
            String standName = armorStand.getCustomName();
            return standName.equals("§5§lVending Machine");
        }
    }
}
