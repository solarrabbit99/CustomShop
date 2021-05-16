package customshop.gui;

import org.bukkit.entity.ArmorStand;
import org.bukkit.block.ShulkerBox;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import customshop.player.PlayerState;
import customshop.plugin.CustomShop;
import customshop.utils.UIUtils;
import net.milkbowl.vault.economy.Economy;

/** Custom UI for vending machines. */
public class VMGUI implements ShopGUI {
    /**
     * Inventory that is viewed by the player, possibly consisting of UI elements
     * such as exit buttons, next page etc. Each item for sale is also labelled with
     * their respective prices.
     */
    private Inventory inventoryView;
    /**
     * A copy of the original inventory of the shulker. This is where the true items
     * are retrieve in event of purchases.
     */
    private Inventory inventory;
    /**
     * A copy of the block state from source container (Shulker Box). Any changes to
     * this block state does not translate to that of the original source.
     */
    private ShulkerBox sourceImage;
    /**
     * An array to keep track of prices for each slots. This array is retrieved from
     * and eventually synced with the lure of the shulker box.
     */
    private double[] prices;
    /**
     * String representation of the UUID of the player who owns the shop.
     */
    private final String ownerID;

    /**
     * Constructor method for vending machine. Retrieves the items from the source
     * container that the armor stand holds.
     *
     * @param armorStand armor stand containing source container
     */
    public VMGUI(ArmorStand armorStand) {
        ItemStack block = armorStand.getEquipment().getChestplate();
        BlockStateMeta blockMeta = (BlockStateMeta) block.getItemMeta();
        ownerID = blockMeta.getDisplayName();
        this.sourceImage = (ShulkerBox) blockMeta.getBlockState();

        String title = "§5§lVending Machine";
        inventoryView = Bukkit.createInventory(null, 9 * 4, title);
        inventory = Bukkit.createInventory(null, 9 * 3);

        // Setting up UI elemenets on the last row.
        int[] blackSlots = new int[] { 0, 1, 2, 3, 5, 6, 7, 8 };
        for (int i : blackSlots) {
            UIUtils.createItem(inventoryView, 3, i, Material.BLACK_STAINED_GLASS_PANE, 1, " ");
        }
        UIUtils.createItem(inventoryView, 3, 4, Material.BARRIER, 1, "§cClose", "");

        ItemStack[] items = sourceImage.getInventory().getContents();
        if (blockMeta.hasLore()) {
            prices = UIUtils.stringListToDoubleArray(blockMeta.getLore());
        } else { // Should not come here.
            prices = new double[27];
        }
        for (int i = 0; i < items.length; i++) {
            inventoryView.setItem(i, UIUtils.setPrice(items[i], prices[i]));
            inventory.setItem(i, items[i]);
        }
    }

    /**
     * Save vending machine inventory, and remove the corresponding mapping in the
     * two HashMaps. This method attempts to replace the original shulker in the
     * armor stand's chestplate slot with a duplicate of the same name and updated
     * contents, as the original copy is not retrievable.
     * <p>
     * New items added into empty slots will follow the price of the last instance
     * of the same item listed. As the prices of the items are saved according to
     * their positions in the inventory, it is possible to swap two or more items in
     * established slots to make differing prices among items of the same type.
     *
     * @param player player viewing the inventory
     */
    public static void saveInventory(Player player) {
        PlayerState state = PlayerState.getPlayerState(player);
        ArmorStand armorStand = state.getArmorStand();
        // Does non-null armorStand imply non-null ShopGUI?
        if (armorStand != null) {
            VMGUI ui = (VMGUI) state.getShopGUI();
            HashMap<ItemStack, Double> withPrices = new HashMap<>();
            for (int i = 0; i < 27; i++) {
                ui.sourceImage.getInventory().setItem(i, ui.inventory.getItem(i));
                if (ui.inventory.getItem(i) == null) {
                    ui.prices[i] = 0;
                } else if (ui.prices[i] != 0) {
                    ItemStack sampleItem = ui.inventory.getItem(i).clone();
                    sampleItem.setAmount(1);
                    Double maybePrice = withPrices.get(sampleItem);
                    if (maybePrice == null) {
                        withPrices.put(sampleItem, ui.prices[i]);
                    } else {
                        ui.prices[i] = maybePrice;
                    }
                }
            }
            for (int i = 0; i < 27; i++) {
                if (ui.prices[i] == 0 && ui.inventory.getItem(i) != null) {
                    ItemStack sampleRequest = ui.inventory.getItem(i).clone();
                    sampleRequest.setAmount(1);
                    Double wrapperPrice = withPrices.get(sampleRequest);
                    double price = wrapperPrice == null ? 0 : wrapperPrice;
                    ui.prices[i] = price;
                }
            }
            ItemStack container = armorStand.getEquipment().getChestplate();
            BlockStateMeta shulkerMeta = (BlockStateMeta) container.getItemMeta();
            shulkerMeta.setLore(UIUtils.doubleToStringList(ui.prices));
            shulkerMeta.setBlockState(ui.sourceImage);
            container.setItemMeta(shulkerMeta);
            armorStand.getEquipment().setChestplate(container);
            state.clearShopInteraction();
        }
    }

    /**
     * Player purchases item from shop. This event is cancelled if:
     * <ul>
     * <li>Shop does not have the specified amount of items
     * <li>Player inventory does not have enough space
     * <li>Player does not have enough money to purchase the specified amount of
     * items
     * </ul>
     * Returns the outcome message of this event.
     *
     * @param player player purchasing item
     * @param item   item to be purchased
     * @param amount amount of item the player intended to purchase
     * @return outcome message of the purchase, to be sent to the player involved
     */
    public String purchaseItem(Player player, ItemStack item, int amount) {
        if (item == null) {
            return "§cItem is null...";
        } else if (!inventory.containsAtLeast(item, amount)) {
            return "§cShop does not have the specified amount of the selected item!";
        }
        Inventory pInventory = player.getInventory();
        int totalSpace = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack pItem = pInventory.getItem(i);
            if (pItem == null) {
                totalSpace += item.getMaxStackSize();
            } else if (pItem.isSimilar(item)) {
                totalSpace += pItem.getMaxStackSize() - pItem.getAmount();
            }
        }

        Economy economy = CustomShop.getEconomy();
        double bal = economy.getBalance(player);
        double totalCost = amount * prices[inventory.first(item)];

        if (totalSpace < amount) {
            return "§cYou do not have enough space in your inventory!";
        } else if (bal < totalCost) {
            return "§cYou need at least $" + totalCost + " to make the purchase!";
        } else { // Valid transaction
            item.setAmount(amount);
            final int printAmount = amount;
            pInventory.addItem(item);
            for (int i = 26; i >= 0 && amount > 0; i--) {
                ItemStack sItem = inventory.getItem(i);
                if (sItem != null && sItem.isSimilar(item)) {
                    int currentStackSize = sItem.getAmount();
                    if (currentStackSize - amount > 0) {
                        sItem.setAmount(currentStackSize - amount);
                        amount = 0;
                    } else {
                        sItem.setAmount(0);
                        prices[i] = 0;
                        amount -= currentStackSize;
                    }
                }
            }
            OfflinePlayer owner = Bukkit.getOfflinePlayer(UUID.fromString(ownerID));
            economy.withdrawPlayer(player, totalCost);
            economy.depositPlayer(owner, totalCost);
            String itemName = item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName()
                    : item.getType() + "";
            return "§aSuccessfully purchased " + printAmount + "x" + itemName + "§a for $" + totalCost + "!";
        }
    }

    /**
     * List all similar ItemStack with the specified price. Returns the outcome
     * message of this event.
     *
     * @param player owner of the shop
     * @param item   item to set the price for
     * @param price  new price
     * @return outcome message of the purchase, to be sent to the player involved
     */
    public String listPrice(Player player, ItemStack item, double price) {
        if (item == null) {
            return "§cYou are not holding anything in your main hand!";
        } else {
            for (int i = 0; i < 27; i++) {
                ItemStack sItem = inventory.getItem(i);
                if (sItem != null && sItem.isSimilar(item)) {
                    prices[i] = price;
                }
            }
            ItemMeta meta = item.getItemMeta();
            String name = meta.hasDisplayName() ? meta.getDisplayName() : item.getType().toString();
            return "§aSuccessfully listed " + name + "§a for $" + price + "!";
        }
    }

    /**
     * Gets the original copy of item without price lore.
     *
     * @param index index of the item in the inventory
     * @return the copy of the item
     */
    public ItemStack getItem(int index) {
        return this.inventory.getItem(index).clone();
    }

    /**
     * Open vending machine's UI on specified player.
     *
     * @param player player to view the inventory
     */
    public void openUI(Player player) {
        player.openInventory(inventoryView);
    }

    /**
     * Open vending machine's UI on the owner.
     *
     * @param player player to view the inventory
     */
    public void openOwnerUI(Player player) {
        player.openInventory(inventory);
    }
}
