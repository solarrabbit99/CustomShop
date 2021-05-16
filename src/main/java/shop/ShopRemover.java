package shop;

import org.bukkit.block.Block;

/**
 * Encapsulates a remover of a shop. Each type of custom shops should have a
 * remover of its own type that extends this abstract class. The main job of a
 * remover is to handle the different constructs of each custom shop type,
 * particularly the usage of barrier blocks and the method to search and remove
 * them.
 */
public abstract class ShopRemover {
    /**
     * Target block that the shop owner is targeting. Presumably a barrier block
     * containing the custom shop's armor stand entity.
     */
    protected Block targetBlock;

    public ShopRemover(Block targetBlock) {
        this.targetBlock = targetBlock;
    }

    /**
     * Main method of the removal to search and remove all relevant entities/blocks
     * related to the custom shop that is currently being removed.
     */
    public abstract void removeShop();
}
