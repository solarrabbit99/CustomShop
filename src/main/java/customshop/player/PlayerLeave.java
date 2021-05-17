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

package customshop.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeave implements Listener {
    /**
     * Handler in event player gets kicked with GUI opened or in conversation.
     *
     * @param evt event of player kicking
     */
    @EventHandler
    public void playerKick(PlayerKickEvent evt) {
        PlayerState state = PlayerState.getPlayerState(evt.getPlayer());
        if (!state.abandonConversation()) {
            state.clearShopInteractions();
        }
    }

    /**
     * Handler in event player leaves with GUI opened or in conversation.
     *
     * @param evt event of player leaving
     */
    @EventHandler
    public void playerLeave(PlayerQuitEvent evt) {
        PlayerState state = PlayerState.getPlayerState(evt.getPlayer());
        if (!state.abandonConversation()) {
            state.clearShopInteractions();
        }
    }
}
