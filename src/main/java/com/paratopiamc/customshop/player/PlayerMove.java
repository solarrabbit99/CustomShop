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

package com.paratopiamc.customshop.player;

import java.util.Optional;

import com.paratopiamc.customshop.gui.ShopGUI;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * PlayerMoveEvent handlers.
 */
public class PlayerMove implements Listener {
    /**
     * Abandon any conversation with custom shops if player is more than 5 meters
     * away from the shop's armor stand.
     * 
     * @param evt event of player moving
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent evt) {
        Player player = evt.getPlayer();
        PlayerState state = PlayerState.getPlayerState(player);
        Optional<ShopGUI> shopGUI = Optional.ofNullable(state.getShopGUI());
        Optional<ArmorStand> armorStand = shopGUI.map(gui -> gui.getArmorStand());
        if (armorStand.isPresent()) {
            Location loc1 = armorStand.get().getLocation();
            Location loc2 = player.getLocation();
            if (!loc1.getWorld().equals(loc2.getWorld()) || loc1.distanceSquared(loc2) > 25) {
                state.clearShopInteractions();
            }
        }
    }
}
