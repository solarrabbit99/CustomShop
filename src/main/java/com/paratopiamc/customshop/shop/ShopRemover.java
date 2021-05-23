/*
 *  This file is part of CustomShop. Copyright (c) 2021 Paratopia.
 *
 *  CustomShop is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  CustomShop is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CustomShop. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package com.paratopiamc.customshop.shop;

import java.util.UUID;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;

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
    protected ArmorStand armorStand;
    protected UUID ownerUUID;

    /**
     * Main method of the removal to search and remove all relevant entities/blocks
     * related to the custom shop that is currently being removed.
     *
     * @return shop owner's UUID
     */
    public abstract UUID removeShop();
}
