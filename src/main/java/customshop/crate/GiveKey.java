package customshop.crate;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
            return true;
        }
        try {
            int amount = Integer.parseInt(args[1]);
            ItemStack keys = OpenCrate.getCrateKey(amount);
            HashMap<Integer, ItemStack> remain = player.getInventory().addItem(keys);
            if (remain.isEmpty()) {
                sender.sendMessage("§aGiven " + amount + " keys to specified player!");
            } else {
                int overflow = remain.get(0).getAmount();
                if (overflow == amount) {
                    sender.sendMessage("§cSpecified player doesn't have space in his/her inventory!");
                } else {
                    sender.sendMessage("§6Specified player doesn't have enough space in his/her inventory, given "
                            + (amount - overflow) + " keys to specified player!");
                }
            }
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid input amount!");
        }
        return true;
    }
}
