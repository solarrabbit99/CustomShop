package customshop.crate;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import customshop.plugin.CustomShop;

/** Locks and resets all unlocked custom shops of the specified player. */
public class LockAll implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("§cInvalid number of arguments!");
            return false;
        }
        Player player = Bukkit.getPlayerExact(args[0]);
        if (player == null) {
            sender.sendMessage("§cCannot find specified player!");
            return false;
        }
        CustomShop.getPlugin().getDatabase().setUnlockedShops(player, new ArrayList<>());
        sender.sendMessage("§aSuccessfully locked all custom shops of the specified player!");
        return true;
    }
}
