package Listeners;

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

        // Creating GUI for player to select the custom shop of his/her choice.
        CreationGUI.openFirstPage(player);
        return false;
    }

    @EventHandler
    public void createShop(InventoryClickEvent evt) {
        ItemStack item = evt.getCurrentItem();
        if (item == null) {
            return;
        }
        InventoryHolder holder = evt.getClickedInventory().getHolder();
        Player player = (Player) evt.getWhoClicked();
        String title = evt.getView().getTitle();
        if (holder == null && title.equalsIgnoreCase("§5§lCustom Shops")) {
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta.hasDisplayName() && itemMeta.getDisplayName().equals("§cClose")) {
                player.closeInventory();
                CreationGUI.playerClosedGUI(player);
            } else if (itemMeta.hasDisplayName() && itemMeta.getDisplayName().equals("§eBack")) {
                CreationGUI.previousPage(player);
            } else if (itemMeta.hasDisplayName() && itemMeta.getDisplayName().equals("§eNext")) {
                CreationGUI.nextPage(player);
            } else if (evt.getSlot() < 27) {
                Block targetBlock = player.getTargetBlockExact(5);
                if (targetBlock == null) {
                    player.sendMessage("§cYou are not targeting any block...");
                    return;
                }
                Location location = new Location(targetBlock.getWorld(), targetBlock.getX() + 0.5,
                        targetBlock.getY() + 1, targetBlock.getZ() + 0.5);
                ShopCreator creator = getShopCreator(itemMeta);
                player.sendMessage(creator.createShop(location, player, item));
                player.closeInventory();
                CreationGUI.playerClosedGUI(player);
            }
            evt.setCancelled(true);
        }
    }

    /**
     * Return a specific ShopCreator for the selected item.
     *
     * @param meta item meta of the clicked item
     * @return ShopCreator corresponding to the clicked item
     * @throws NoSuchShopException if the given item meta does not match any shops
     */
    private static ShopCreator getShopCreator(ItemMeta meta) {
        if (!meta.hasDisplayName() || !meta.getDisplayName().contains("§5§l")) {
            throw new NoSuchShopException();
        }
        String name = meta.getDisplayName();
        if (name.contains("Vending Machine")) {
            return new VendingMachineCreator();
        } else {
            throw new NoSuchShopException(name);
        }

    }

    private static class NoSuchShopException extends RuntimeException {
        private NoSuchShopException(String name) {
            super("Selected item did not match any kind of shops: " + name);
        }

        private NoSuchShopException() {
            super("Selected item did not match any kind of shops!");
        }
    }
}
