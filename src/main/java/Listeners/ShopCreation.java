package Listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import CustomUIs.CreationGUI;
import Listeners.VendingMachine.VendingMachineCreator;
import Plugin.CustomShop;

/**
 * Encapsulates a shop creation process. Player runs {@code /newshop} to spawn a
 * GUI with the {@link CommandExecutor}. Afterwhich, a shop will attempt to
 * spawn if player clicks on a design listed in the GUI.
 */
public class ShopCreation implements CommandExecutor, Listener {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        Block targetBlock = player.getTargetBlockExact(5);
        if (targetBlock == null) {
            player.sendMessage("§cYou are not targeting any block...");
            return false;
        }
        CreationGUI.openFirstPage(player);
        return false;
    }

    /**
     * Creates a shop when player clicks on a valid and unlocked shop design in the
     * GUI.
     *
     * @param evt corresponding {@link InventoryClickEvent}
     */
    @EventHandler
    public void createShop(InventoryClickEvent evt) {
        ItemStack item = evt.getCurrentItem();
        if (item == null) {
            return;
        }
        InventoryHolder holder = evt.getClickedInventory().getHolder();
        Player player = (Player) evt.getWhoClicked();
        String title = evt.getView().getTitle();
        if (title.equalsIgnoreCase("§e§lCustom Shops")) {
            evt.setCancelled(true);
            if (holder == null) {
                ItemMeta itemMeta = item.getItemMeta();
                if (itemMeta.hasDisplayName() && itemMeta.getDisplayName().equals("§cClose")) {
                    Bukkit.getScheduler().runTask(CustomShop.getPlugin(), () -> player.closeInventory());
                    CreationGUI.playerClosedGUI(player);
                } else if (itemMeta.hasDisplayName() && itemMeta.getDisplayName().equals("§eBack")) {
                    CreationGUI.previousPage(player);
                } else if (itemMeta.hasDisplayName() && itemMeta.getDisplayName().equals("§eNext")) {
                    CreationGUI.nextPage(player);
                } else if (evt.getSlot() < 27) {
                    Block targetBlock = player.getTargetBlockExact(5);
                    if (CustomShop.getPlugin().getDatabase().getTotalShopOwned(player).equals(5)) {
                        player.sendMessage("§cYou have reached the maximum number of custom shops created!");
                        Bukkit.getScheduler().runTask(CustomShop.getPlugin(), () -> player.closeInventory());
                        CreationGUI.playerClosedGUI(player);
                        return;
                    }
                    if (targetBlock == null) {
                        player.sendMessage("§cYou are not targeting any block...");
                        return;
                    }
                    Location location = getCreationLocation(targetBlock, player);
                    ShopCreator creator = getShopCreator(itemMeta);
                    player.sendMessage(creator.createShop(location, player, item));
                    Bukkit.getScheduler().runTask(CustomShop.getPlugin(), () -> player.closeInventory());
                    CreationGUI.playerClosedGUI(player);
                }
            }
        }
    }

    /**
     * Returns location where the shop will attempt to spawn at. The returned
     * location faces the player.
     *
     * @param targetBlock block that the player is targeting
     * @param player      player of interest
     * @return resulting location
     */
    private static Location getCreationLocation(Block targetBlock, Player player) {
        Location result = targetBlock.getLocation().clone();
        result.add(0.5, 1, 0.5);
        // The yaw of a player, on top of what is stated in spigot's API documentation,
        // ranges from -360 to 360. Hence we add 540 here to ensure positivity. Somehow
        // fixes the issue of yaw not rounding off correctly.
        int yaw = ((Float) (player.getLocation().getYaw() + 540)).intValue();
        yaw += 45;
        yaw -= yaw % 90;
        result.setYaw(yaw);
        return result;
    }

    /**
     * Returns a specific {@link ShopCreator} for the selected item.
     *
     * @param meta item meta of the clicked item
     * @return ShopCreator corresponding to the clicked item
     * @throws NoSuchShopException if the given item meta does not match any shop
     *                             designs
     */
    private static ShopCreator getShopCreator(ItemMeta meta) {
        if (!meta.hasDisplayName()) {
            throw new NoSuchShopException();
        }
        String name = meta.getDisplayName();
        if (name.contains("Vending Machine")) {
            return new VendingMachineCreator();
        } else {
            throw new NoSuchShopException(name);
        }
    }

    /**
     * Encapsulates an exception where no shop designs corresponds to the item that
     * the player picked. This exception should never be thrown.
     */
    private static class NoSuchShopException extends RuntimeException {
        private NoSuchShopException(String name) {
            super("Selected item did not match any kind of shops: " + name);
        }

        private NoSuchShopException() {
            super("Selected item did not match any kind of shops!");
        }
    }
}
