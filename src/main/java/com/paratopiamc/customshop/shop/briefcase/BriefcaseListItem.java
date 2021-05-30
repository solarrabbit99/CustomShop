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

package com.paratopiamc.customshop.shop.briefcase;

import com.paratopiamc.customshop.gui.BriefcaseGUI;
import com.paratopiamc.customshop.player.PlayerState;
import com.paratopiamc.customshop.plugin.CustomShop;
import com.paratopiamc.customshop.shop.conversation.SetPriceConversationFactory;
import com.paratopiamc.customshop.utils.UIUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class BriefcaseListItem implements Listener {
    @EventHandler
    public void listItem(PlayerInteractEvent evt) {
        EquipmentSlot hand = evt.getHand();
        Player player = evt.getPlayer();
        if (!hand.equals(EquipmentSlot.HAND) || !evt.getAction().equals(Action.LEFT_CLICK_BLOCK)
                || !player.isSneaking()) {
            return;
        }
        Block targetBlock = evt.getClickedBlock();
        ArmorStand armorStand = UIUtils.getArmorStand(targetBlock);
        if (armorStand == null) {
            return;
        }
        if (!armorStand.getCustomName().equals("§5§lNewt's Briefcase")) {
            return;
        } else {
            // For creative mode
            evt.setCancelled(true);
            PlayerState state = PlayerState.getPlayerState(player);
            state.clearShopInteractions();
            if (!UIUtils.hasShopPermission(armorStand, player)) {
                player.sendMessage("§cYou do not own the briefcase!");
                return;
            }
            if (PlayerState.getInteractingPlayer(armorStand) != null) {
                player.sendMessage("§cShop current in use, please wait...");
                return;
            }
            BriefcaseGUI ui = new BriefcaseGUI(armorStand, player);
            if (player.getEquipment().getItemInMainHand().getType().equals(Material.AIR)) {
                ui.openOwnerUI();
                state.setShopGUI(ui);
            } else {
                if (ui.hasItem()) {
                    player.sendMessage("§cItem already set for the briefcase!");
                } else {
                    // New conversation must begin in a different tick that cancelled
                    Bukkit.getScheduler().runTask(CustomShop.getPlugin(),
                            () -> state.startConversation(new SetPriceConversationFactory()));
                    state.setShopGUI(ui);
                }
            }
        }
    }
}
