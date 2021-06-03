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

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * PlayerTeleportEvent handlers.
 */
public class PlayerTeleport implements Listener {
    /**
     * Cancel any shop interactions if player teleports to a differing location,
     * regardless of player's distance to the shop after teleportation.
     *
     * @param evt player teleport event
     */
    @EventHandler
    public void onTeleport(PlayerTeleportEvent evt) {
        PlayerState.getPlayerState(evt.getPlayer()).clearShopInteractions();
    }
}
