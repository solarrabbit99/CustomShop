package shop;

import java.util.Collection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import plugin.CustomShop;
import shop.vm.VMRemover;

public class ShopRemoval implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        Block targetBlock = player.getTargetBlockExact(5);
        if (targetBlock == null) {
            player.sendMessage("§cYou are not targeting any block...");
            return true;
        }
        ShopRemover remover = getShopRemover(targetBlock);
        if (remover != null) {
            getShopRemover(targetBlock).removeShop();
            CustomShop.getPlugin().getDatabase().decrementTotalShopsOwned(player);
        } else {
            player.sendMessage("§cInvalid target...");
        }
        return true;
    }

    /**
     * Checker for which subtype of {@link ShopRemover} to be used. It is assumed
     * that a custom shop, if there is any, is the only entity within a particular
     * barrier block. Conversely, each custom shop has an armor stand embedded in at
     * least a barrier block.
     *
     * @param targetBlock block targeted by player
     * @return correspond remover for the type of shop
     */
    private static ShopRemover getShopRemover(Block targetBlock) {
        Location loc = new Location(targetBlock.getWorld(), targetBlock.getX() + 0.5, targetBlock.getY(),
                targetBlock.getZ() + 0.5);
        Collection<Entity> list = targetBlock.getWorld().getNearbyEntities(loc, 0.5, 0.5, 0.5);
        if (targetBlock.getType() != Material.BARRIER && !list.isEmpty()) {
            return null;
        } else {
            Entity shopEntity = (Entity) list.toArray()[0];
            if (shopEntity instanceof ArmorStand) {
                String customName = shopEntity.getCustomName();
                ShopRemover result;
                switch (customName) {
                    case "§5§lVending Machine":
                        result = new VMRemover(targetBlock, shopEntity);
                        break;
                    default:
                        result = null;
                        break;
                }
                return result;
            }
            return null;
        }
    }
}
