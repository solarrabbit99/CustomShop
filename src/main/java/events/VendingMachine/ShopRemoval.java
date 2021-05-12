package events.VendingMachine;

import java.util.Collection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import plugin.CustomShop;

public class ShopRemoval implements CommandExecutor {
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
        Location loc = new Location(targetBlock.getWorld(), targetBlock.getX() + 0.5, targetBlock.getY(),
                targetBlock.getZ() + 0.5);
        if (loc.getBlock().getType() != Material.BARRIER) {
            player.sendMessage("§cInvalid target...");
            return false;
        }
        int delta = linearSearch(targetBlock) ? 1 : -1;
        Location locTheOther = new Location(targetBlock.getWorld(), targetBlock.getX(), targetBlock.getY() + delta,
                targetBlock.getZ());
        Collection<Entity> list = targetBlock.getWorld().getNearbyEntities(loc, 0.5, 0.5, 0.5);
        Entity shopEntity = (Entity) list.toArray()[0];
        if (shopEntity instanceof ArmorStand) {
            shopEntity.remove();
            loc.getBlock().setType(Material.AIR);
            locTheOther.getBlock().setType(Material.AIR);
            CustomShop.getPlugin().getDatabase().decrementTotalShopsOwned(player);
        } else {
            player.sendMessage("§cInvalid target...");
        }
        return false;
    }

    /**
     * Search if the location is the bottom of the two barrier blocks. A linear
     * search is performed in downwards direction. This is only guaranteed to work
     * if barrier blocks across the y level in the same x and z position are used to
     * only contain custom shops.
     *
     * @param targetBlock block targeted by the player
     * @return whether the block that the player targeted is the bottom one
     */
    private static boolean linearSearch(Block targetBlock) {
        final World world = targetBlock.getWorld();
        final int x = targetBlock.getX();
        int y = targetBlock.getY() - 1;
        final int z = targetBlock.getZ();
        Location newLocation = new Location(world, x, y, z);
        boolean bottom = true;
        while (newLocation.getBlock().getType().equals(Material.BARRIER)) {
            bottom = !bottom;
            y--;
            newLocation = new Location(world, x, y, z);
        }
        return bottom;
    }
}
