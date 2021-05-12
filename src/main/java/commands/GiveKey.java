package commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import events.OpenCrate;

/**
 * Gives specified player in the first position of args when using
 * {@code /givekey} the amount of crate keys specified in the second position of
 * args. Process fails if target player doesn't have enough inventory space.
 */
public class GiveKey implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cInvalid number of arguments!");
            return false;
        }
        Player player = Bukkit.getPlayerExact(args[0]);
        if (player == null) {
            sender.sendMessage("§cCannot find specified player!");
            return false;
        }
        if (player.getInventory().firstEmpty() == -1) {
            // TODO: Handle giving more than a stack.
            sender.sendMessage("§cSpecified player doesn't have space in his/her inventory!");
            return false;
        }
        try {
            int amount = Integer.parseInt(args[1]);
            ItemStack keys = OpenCrate.getCrateKey(amount);
            sender.sendMessage("§aGiven " + amount + " keys to specified player!");
            player.getInventory().addItem(keys);
            return true;
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid input amount!");
        }
        return false;
    }
}
