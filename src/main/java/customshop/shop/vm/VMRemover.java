package customshop.shop.vm;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import customshop.shop.ShopRemover;

/**
 * Vending machine's shop remover.
 */
public class VMRemover extends ShopRemover {
    Entity shopEntity;

    public VMRemover(Block targetBlock, Entity shopEntity) {
        super(targetBlock);
        this.shopEntity = shopEntity;
    }

    @Override
    public void removeShop() {
        Location bottom = shopEntity.getLocation();
        Location top = shopEntity.getLocation();
        top.setY(top.getY() + 1);
        bottom.getBlock().setType(Material.AIR);
        top.getBlock().setType(Material.AIR);

        shopEntity.remove();
    }
}
