package customshop.shop;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import customshop.plugin.CustomShop;
import customshop.shop.vm.VMRemover;
import customshop.utils.UIUtils;

/** Player removing shop by command. */
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
        ShopRemover remover = getShopRemover(targetBlock, player);
        if (remover != null) {
            remover.removeShop();
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
     * least a barrier block. Return {@code null} if player is not owner of the shop
     * or no ShopRemover can be used.
     *
     * @param targetBlock block targeted by player
     * @return correspond remover for the type of shop
     */
    private static ShopRemover getShopRemover(Block targetBlock, Player player) {
        ArmorStand armorStand = UIUtils.getArmorStand(targetBlock);
        if (armorStand != null && UIUtils.hasShopPermission(armorStand, player)) {
            String customName = armorStand.getCustomName();
            ShopRemover result;
            switch (customName) {
                case "§5§lVending Machine":
                    result = new VMRemover(targetBlock, armorStand);
                    break;
                default:
                    result = null;
                    break;
            }
            return result;
        } else {
            return null;
        }
    }
}
