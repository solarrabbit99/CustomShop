package com.paratopiamc.customshop.gui;

import java.util.ArrayList;
import java.util.List;

import com.paratopiamc.customshop.plugin.CustomShop;
import com.paratopiamc.customshop.utils.MessageUtils;
import com.paratopiamc.customshop.utils.UIUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BriefcaseGUI extends ShopGUI {
    /**
     * Inventory viewed by normal players, consisting of UI elements such as exit
     * buttons, next page etc. Item for selling/buying is also labelled with its
     * price.
     */
    private Inventory normalView;
    /**
     * Inventory viewed by owners. This is where the items are placed in or
     * retrieved.
     */
    private Inventory ownerView;
    /**
     * Price of item.
     */
    private double price;
    /**
     * Whether the shop is selling.
     */
    private boolean selling;
    /**
     * Quantity of item in the shop.
     */
    private int quantity;

    public BriefcaseGUI(ArmorStand armorStand, Player player) {
        super(player, armorStand, armorStand.getEquipment().getChestplate().getItemMeta().getDisplayName());
        EntityEquipment armorStandContent = armorStand.getEquipment();
        ItemStack item = armorStandContent.getLeggings();
        if (item != null && item.getType() != Material.AIR) {
            normalView = Bukkit.createInventory(null, 9 * 4, "§5§lNewt's Briefcase");
            ownerView = Bukkit.createInventory(null, 9 * 4, "§5§lNewt's Briefcase Settings");

            ItemStack placeHolder = armorStandContent.getChestplate();
            List<String> info = placeHolder.getItemMeta().getLore();
            this.price = Double.parseDouble(info.get(0));
            this.quantity = Integer.parseInt(info.get(1));
            this.selling = Boolean.parseBoolean(info.get(2));

            // Setting up UI elements on the last row.
            int[] blackSlots = new int[] { 0, 1, 2, 3, 5, 6, 7, 8 };
            for (int i : blackSlots) {
                UIUtils.createItem(normalView, 3, i, Material.BLACK_STAINED_GLASS_PANE, 1, " ");
                UIUtils.createItem(ownerView, 3, i, Material.BLACK_STAINED_GLASS_PANE, 1, " ");
            }
            UIUtils.createItem(normalView, 3, 4, Material.BARRIER, 1, "§cClose", "");

            UIUtils.createItem(ownerView, 3, 2, Material.OAK_SIGN, 1, "§6" + (this.selling ? "Selling" : "Buying"),
                    "§2Click to toggle");
            UIUtils.createItem(ownerView, 3, 3, Material.NAME_TAG, 1, "§6Change Price", "§2Click to change");
            UIUtils.createItem(ownerView, 3, 4, Material.HOPPER_MINECART, 1, "§6Add Items",
                    "§2Click to add items to shop");
            UIUtils.createItem(ownerView, 3, 5, Material.CHEST_MINECART, 1, "§6Retrieve Items",
                    "§2Click to retrieve items from shop");
            UIUtils.createItem(ownerView, 3, 6, Material.BARRIER, 1, "§cClose", "");

            ItemMeta meta = item.getItemMeta();
            if (meta.hasLore()) {
                List<String> lore = meta.getLore();
                lore.add("§7--------------------");
                lore.add("§5Current Stock: §e" + String.format("%,.0f", Double.valueOf(this.quantity)));
                lore.add("§5" + (this.selling ? "Selling " : "Buying"));
                meta.setLore(lore);
            } else {
                List<String> lore = new ArrayList<>();
                lore.add("§7--------------------");
                lore.add("§5Current Stock: §e" + String.format("%,.0f", Double.valueOf(this.quantity)));
                lore.add("§5" + (this.selling ? "Selling " : "Buying"));
                meta.setLore(lore);
            }
            item.setItemMeta(meta);

            normalView.setItem(13, UIUtils.setPrice(item, this.price, false));
            ownerView.setItem(13, UIUtils.setPrice(item, this.price, false));
        }
    }

    /**
     * Returns a copy of item that the shop is selling/buying. {@code null} or
     * {@link ItemStack} with type {@link Material#AIR} if no such item exists.
     *
     * @return item that shop is selling/buying
     */
    public ItemStack getItem() {
        return this.armorStand.getEquipment().getLeggings();
    }

    public boolean hasItem() {
        ItemStack item = this.armorStand.getEquipment().getLeggings();
        return item != null && item.getType() != Material.AIR;
    }

    /**
     * Initializes the item that the shop is buying/selling and its price.
     * {@inheritDoc}
     */
    public String listPrice(ItemStack item, double price) {
        if (item == null || item.getType() == Material.AIR) {
            return "§cYou are not holding anything in your main hand!";
        } else {
            EntityEquipment armorStandContent = this.armorStand.getEquipment();
            item.setAmount(1);
            armorStandContent.setLeggings(item);

            ItemStack placeHolder = armorStandContent.getChestplate();
            ItemMeta meta = placeHolder.getItemMeta();
            List<String> lore = meta.getLore();

            lore.set(0, String.valueOf(price));

            meta.setLore(lore);
            placeHolder.setItemMeta(meta);
            armorStandContent.setChestplate(placeHolder);

            ItemMeta itemMeta = item.getItemMeta();

            String name = itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : item.getType().toString();
            return "§aSuccessfully listed " + name + "§a for $" + MessageUtils.getHumanReadablePriceFromNumber(price)
                    + "!";
        }
    }

    @Override
    public void purchaseItem(ItemStack item, int amount) {
        if (item == null) {
            viewer.sendMessage("§cItem is null...");
            return;
        }
        if (this.quantity < amount) {
            viewer.sendMessage(
                    MessageUtils.convertMessage(CustomShop.getPlugin().getConfig().getString("customer-buy-fail-item"),
                            ownerID, viewer, 0, item, amount));
            return;
        }
        Inventory pInventory = viewer.getInventory();
        int totalSpace = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack pItem = pInventory.getItem(i);
            if (pItem == null) {
                totalSpace += item.getMaxStackSize();
            } else if (pItem.isSimilar(item)) {
                totalSpace += pItem.getMaxStackSize() - pItem.getAmount();
            }
        }

        double totalCost = amount * price;

        if (totalSpace < amount) {
            viewer.sendMessage(
                    MessageUtils.convertMessage(CustomShop.getPlugin().getConfig().getString("customer-buy-fail-space"),
                            ownerID, viewer, totalCost, item, amount));
        } else if (super.ownerSell(amount, totalCost, item)) { // Valid transaction
            item.setAmount(amount);
            pInventory.addItem(item);
            EntityEquipment armorStandContent = this.armorStand.getEquipment();
            ItemStack placeHolder = armorStandContent.getChestplate();
            if (placeHolder.getType() == Material.AIR)
                CustomShop.getPlugin().getServer().getConsoleSender()
                        .sendMessage("§c§l[CustomShop] Briefcase without placeHolder detected at "
                                + this.armorStand.getLocation() + ", unable to update shop info. "
                                + "Report this error!");

            ItemMeta meta = placeHolder.getItemMeta();
            if (!meta.hasLore())
                CustomShop.getPlugin().getServer().getConsoleSender()
                        .sendMessage("§c§l[CustomShop] Briefcase's placeHolder without lore detected at "
                                + this.armorStand.getLocation() + ", unable to update shop info. "
                                + "Report this error!");

            List<String> lore = meta.getLore();
            lore.set(1, String.valueOf(this.quantity - amount));
            meta.setLore(lore);
            placeHolder.setItemMeta(meta);
            armorStandContent.setChestplate(placeHolder);
        }
    }

    /**
     * Getter for whether the shop is selling items, the alternative being buying.
     *
     * @return {@code true} if the shop is selling
     */
    public boolean isSelling() {
        return this.selling;
    }

    /**
     * Setter for whether the shop is selling items, the alternative being buying.
     *
     * @param selling whether the shop is selling
     */
    public void setSelling(boolean selling) {
        this.selling = selling;
        EntityEquipment armorStandContent = this.armorStand.getEquipment();
        ItemStack placeHolder = armorStandContent.getChestplate();
        if (placeHolder.getType() == Material.AIR)
            CustomShop.getPlugin().getServer().getConsoleSender()
                    .sendMessage("§c§l[CustomShop] Briefcase without placeHolder detected at "
                            + this.armorStand.getLocation() + ", unable to update shop info. " + "Report this error!");

        ItemMeta meta = placeHolder.getItemMeta();
        if (!meta.hasLore())
            CustomShop.getPlugin().getServer().getConsoleSender()
                    .sendMessage("§c§l[CustomShop] Briefcase's placeHolder without lore detected at "
                            + this.armorStand.getLocation() + ", unable to update shop info. " + "Report this error!");

        List<String> lore = meta.getLore();
        lore.set(2, String.valueOf(selling));
        meta.setLore(lore);
        placeHolder.setItemMeta(meta);
        armorStandContent.setChestplate(placeHolder);

        UIUtils.createItem(ownerView, 3, 2, Material.OAK_SIGN, 1, "§6" + (this.selling ? "Selling" : "Buying"),
                "§2Click to toggle");

        ItemStack item = ownerView.getItem(13);
        ItemMeta itemMeta = item.getItemMeta();
        List<String> itemLore = itemMeta.getLore();
        itemLore.set(itemLore.size() - 2, "§5" + (this.selling ? "Selling " : "Buying"));
        itemMeta.setLore(itemLore);
        item.setItemMeta(itemMeta);
    }

    /**
     * Viewer (inferred to be the owner) retrieves items from the shop.
     *
     * @param amount amount of item to retrieve from shop
     */
    public void retrieveItem(int amount) {
        if (!this.hasItem()) {
            viewer.sendMessage("§cItem doesn't exist...");
            return;
        }
        ItemStack item = this.getItem();
        if (this.quantity < amount) {
            viewer.sendMessage(
                    MessageUtils.convertMessage(CustomShop.getPlugin().getConfig().getString("customer-buy-fail-item"),
                            ownerID, viewer, 0, item, amount));
            return;
        }
        Inventory pInventory = viewer.getInventory();
        int totalSpace = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack pItem = pInventory.getItem(i);
            if (pItem == null) {
                totalSpace += item.getMaxStackSize();
            } else if (pItem.isSimilar(item)) {
                totalSpace += pItem.getMaxStackSize() - pItem.getAmount();
            }
        }

        if (totalSpace < amount) {
            viewer.sendMessage(
                    MessageUtils.convertMessage(CustomShop.getPlugin().getConfig().getString("customer-buy-fail-space"),
                            ownerID, viewer, 0, item, amount));
        } else { // Valid operation
            item.setAmount(amount);
            pInventory.addItem(item);
            EntityEquipment armorStandContent = this.armorStand.getEquipment();
            ItemStack placeHolder = armorStandContent.getChestplate();
            if (placeHolder.getType() == Material.AIR)
                CustomShop.getPlugin().getServer().getConsoleSender()
                        .sendMessage("§c§l[CustomShop] Briefcase without placeHolder detected at "
                                + this.armorStand.getLocation() + ", unable to update shop info. "
                                + "Report this error!");

            ItemMeta meta = placeHolder.getItemMeta();
            if (!meta.hasLore())
                CustomShop.getPlugin().getServer().getConsoleSender()
                        .sendMessage("§c§l[CustomShop] Briefcase's placeHolder without lore detected at "
                                + this.armorStand.getLocation() + ", unable to update shop info. "
                                + "Report this error!");

            List<String> lore = meta.getLore();
            lore.set(1, String.valueOf(this.quantity - amount));
            meta.setLore(lore);
            placeHolder.setItemMeta(meta);
            armorStandContent.setChestplate(placeHolder);
        }
    }

    /**
     * Viewer (inferred to be the owner) addss items to the shop.
     *
     * @param amount amount of item to add to shop
     */
    public void addItem(int amount) {
        if (!this.hasItem()) {
            viewer.sendMessage("§cItem doesn't exist...");
            return;
        }
        ItemStack item = this.getItem();

        Inventory pInventory = viewer.getInventory();
        int availableAmount = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack pItem = pInventory.getItem(i);
            if (item.isSimilar(pItem)) {
                availableAmount += pItem.getAmount();
            }
        }

        if (availableAmount < amount) {
            viewer.sendMessage(
                    MessageUtils.convertMessage(CustomShop.getPlugin().getConfig().getString("customer-sell-fail-item"),
                            ownerID, viewer, 0, item, amount));
            return;
        }

        long quantityLong = this.quantity;
        long amountLong = amount;
        if (quantityLong + amountLong > Integer.MAX_VALUE) {
            viewer.sendMessage("§cShop limit reached! You are only able to add " + (Integer.MAX_VALUE - this.quantity)
                    + " more items!");
        } else { // Valid operation
            EntityEquipment armorStandContent = this.armorStand.getEquipment();
            ItemStack placeHolder = armorStandContent.getChestplate();
            if (placeHolder.getType() == Material.AIR)
                CustomShop.getPlugin().getServer().getConsoleSender()
                        .sendMessage("§c§l[CustomShop] Briefcase without placeHolder detected at "
                                + this.armorStand.getLocation() + ", unable to update shop info. "
                                + "Report this error!");

            ItemMeta meta = placeHolder.getItemMeta();
            if (!meta.hasLore())
                CustomShop.getPlugin().getServer().getConsoleSender()
                        .sendMessage("§c§l[CustomShop] Briefcase's placeHolder without lore detected at "
                                + this.armorStand.getLocation() + ", unable to update shop info. "
                                + "Report this error!");
            List<String> lore = meta.getLore();
            lore.set(1, String.valueOf(this.quantity + amount));
            meta.setLore(lore);
            placeHolder.setItemMeta(meta);
            armorStandContent.setChestplate(placeHolder);

            for (int i = pInventory.getSize() - 1; i >= 0 && amount > 0; i--) {
                ItemStack sItem = pInventory.getItem(i);
                if (sItem != null && sItem.isSimilar(item)) {
                    int currentStackSize = sItem.getAmount();
                    if (currentStackSize - amount > 0) {
                        sItem.setAmount(currentStackSize - amount);
                        amount = 0;
                    } else {
                        sItem.setAmount(0);
                        amount -= currentStackSize;
                    }
                }
            }
        }
    }

    @Override
    public void openUI() {
        if (normalView == null) {
            viewer.sendMessage("§cThe shop is not selling/buying any items!");
        } else {
            viewer.playSound(armorStand.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 0.5F, 1.0F);
            viewer.openInventory(normalView);
        }
    }

    @Override
    public void openOwnerUI() {
        if (ownerView == null) {
            viewer.sendMessage("§cThe shop is not selling/buying any items!");
        } else {
            viewer.playSound(armorStand.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 0.5F, 1.0F);
            viewer.openInventory(ownerView);
        }
    }

    @Override
    public void saveInventories() {
        // Noting to do for briefcases.
    }
}
