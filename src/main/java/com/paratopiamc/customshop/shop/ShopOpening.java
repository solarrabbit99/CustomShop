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

import java.util.Optional;
import com.paratopiamc.customshop.gui.BriefcaseGUI;
import com.paratopiamc.customshop.gui.ShopGUI;
import com.paratopiamc.customshop.gui.VMGUI;
import com.paratopiamc.customshop.player.PlayerState;
import com.paratopiamc.customshop.utils.LanguageUtils;
import com.paratopiamc.customshop.utils.ShopUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class ShopOpening implements Listener {
    /**
     * Open custom shop's GUI when player right clicks barrier containing valid
     * armor stand.
     *
     * @param evt event of player right clicking entity
     */
    @EventHandler
    public void openShop(PlayerInteractEvent evt) {
        EquipmentSlot hand = evt.getHand();
        Player player = evt.getPlayer();
        Block targetBlock = evt.getClickedBlock();
        ArmorStand armorStand = ShopUtils.getArmorStand(targetBlock);
        if (armorStand == null || hand == null) {
            return;
        } else if (hand.equals(EquipmentSlot.OFF_HAND)) {
            // To prevent block placing with left hand
            evt.setCancelled(true);
            return;
        } else if (!hand.equals(EquipmentSlot.HAND) || !evt.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                || player.isSneaking()) {
            return;
        } else {
            evt.setCancelled(true);
            ShopGUI gui = getShopOpener(armorStand, player);
            if (gui != null) {
                PlayerState state = PlayerState.getPlayerState(player);
                state.clearShopInteractions();
                if (PlayerState.getInteractingPlayer(armorStand) != null) {
                    player.sendMessage(LanguageUtils.getString("shop-currently-in-use.shop"));
                    return;
                } else {
                    gui.openUI();
                    state.setShopGUI(gui);
                }
            }
        }
    }

    /**
     * Checker for which subtype of {@link ShopGUI} to be used. It is assumed that a
     * custom shop, if there is any, is the only entity within a particular barrier
     * block. Conversely, each custom shop has an armor stand embedded in at least a
     * barrier block. Return {@code null} if no ShopGUI can be used.
     *
     * @param armorStand {@link ArmorStand} targeted by player
     * @param player     player attempting to access shop
     * @return correspond gui for the type of shop
     */
    private static ShopGUI getShopOpener(ArmorStand armorStand, Player player) {
        String customName = armorStand.getCustomName();
        Optional<ShopGUI> result;
        switch (customName) {
        case "§5§lVending Machine":
            result = Optional.ofNullable(new VMGUI(armorStand, player));
            break;
        case "§5§lNewt's Briefcase":
            result = Optional.ofNullable(new BriefcaseGUI(armorStand, player));
            break;
        default:
            result = Optional.empty();
            break;
        }
        return result.orElse(null);
    }
}
