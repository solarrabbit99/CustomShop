package events.vm;

import java.util.Collection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import events.ShopRemover;

/**
 * Vending machine's shop remover.
 */
public class VMRemover extends ShopRemover {
    public VMRemover(Block targetBlock) {
        super(targetBlock);
    }

    @Override
    public void removeShop() {
        Location loc = new Location(targetBlock.getWorld(), targetBlock.getX() + 0.5, targetBlock.getY(),
                targetBlock.getZ() + 0.5);

        int delta = linearSearch(targetBlock) ? 1 : -1;
        Location locTheOther = new Location(targetBlock.getWorld(), targetBlock.getX(), targetBlock.getY() + delta,
                targetBlock.getZ());
        Collection<Entity> list = targetBlock.getWorld().getNearbyEntities(loc, 0.5, 0.5, 0.5);
        Entity shopEntity = (Entity) list.toArray()[0];

        shopEntity.remove();
        loc.getBlock().setType(Material.AIR);
        locTheOther.getBlock().setType(Material.AIR);
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
