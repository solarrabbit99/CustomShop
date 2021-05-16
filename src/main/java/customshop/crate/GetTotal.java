package customshop.crate;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import customshop.plugin.CustomShop;

/**
 * A command executor that returns the number of custom shops owned by the
 * sender.
 */
public class GetTotal implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Integer totalShopsOwned = CustomShop.getPlugin().getDatabase().getTotalShopOwned(player);
            player.sendMessage("§9Total custom shops owned: " + totalShopsOwned);
        }
        return false;
    }
}
