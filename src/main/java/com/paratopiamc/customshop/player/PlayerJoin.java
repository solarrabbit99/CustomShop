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

import java.util.List;
import java.util.concurrent.CompletableFuture;
import com.paratopiamc.customshop.plugin.CustomShop;
import com.paratopiamc.customshop.utils.MessageUtils;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/** PlayerJoinEvent's handlers */
public class PlayerJoin implements Listener {
    /**
     * Fetching and send messages for transactions that occurred while the player is
     * offline.
     *
     * @param evt player join event
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent evt) {
        CompletableFuture.runAsync(() -> {
            CustomShop plugin = CustomShop.getPlugin();
            List<MessageUtils.Message> messages = plugin.getDatabase()
                    .getMessages(evt.getPlayer().getUniqueId().toString());
            messages.forEach(e -> plugin.support().sendMessage(evt.getPlayer(), e));
        });
    }
}
