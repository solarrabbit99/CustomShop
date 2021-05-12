package commands;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugin.CustomShop;

/** Set the position of custom shop crate in {@code config.yml}. */
public class SetCrate implements CommandExecutor {
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
        } else if (!targetBlock.getType().equals(Material.CHEST)) {
            player.sendMessage("§cYou are not targeting any chests!");
        } else {
            CustomShop.getPlugin().getConfig().set("crate-location.world",
                    targetBlock.getLocation().getWorld().getName());
            CustomShop.getPlugin().getConfig().set("crate-location.x", targetBlock.getLocation().getX());
            CustomShop.getPlugin().getConfig().set("crate-location.y", targetBlock.getLocation().getY());
            CustomShop.getPlugin().getConfig().set("crate-location.z", targetBlock.getLocation().getZ());
            CustomShop.getPlugin().saveConfig();
            player.sendMessage("§aSet chest as crate chest!");
            return true;
        }
        return false;
    }
}
