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

package com.paratopiamc.customshop.plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerDigType;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.plugin.Plugin;

public class ProtocolLibHandler extends PacketAdapter {
    private BlockDamageEvent evt;

    public ProtocolLibHandler(Plugin plugin, PacketType type, BlockDamageEvent evt) {
        super(plugin, type);
        this.evt = evt;
    }

    @Override
    public void onPacketReceiving(PacketEvent e) {
        PacketContainer packet = e.getPacket();
        PlayerDigType digType = packet.getPlayerDigTypes().getValues().get(0);
        if (digType.name().equals("ABORT_DESTROY_BLOCK")) {
            this.evt.setCancelled(true);
        }
    }
}
