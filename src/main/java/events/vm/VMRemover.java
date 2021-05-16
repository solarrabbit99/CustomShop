package events.vm;

import java.util.Collection;
import org.bukkit.Location;
import org.bukkit.Material;
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

        Collection<Entity> list = targetBlock.getWorld().getNearbyEntities(loc, 0.5, 0.5, 0.5);
        Entity shopEntity = (Entity) list.toArray()[0];

        Location bottom = shopEntity.getLocation();
        Location top = shopEntity.getLocation();
        top.setY(top.getY() + 1);
        bottom.getBlock().setType(Material.AIR);
        top.getBlock().setType(Material.AIR);

        shopEntity.remove();
    }
}
