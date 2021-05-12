package Listeners.Crates;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import Plugin.CustomShops;

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
        CustomShops.getPlugin().getDatabase().setUnlockedShops(player, new ArrayList<>());
        sender.sendMessage("§aSuccessfully locked all custom shops of the specified player!");
        return true;
    }

}
