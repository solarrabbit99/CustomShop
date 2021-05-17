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

package com.paratopiamc.customshop.shop.vm;

import com.paratopiamc.customshop.gui.VMGUI;
import com.paratopiamc.customshop.player.PlayerState;
import com.paratopiamc.customshop.plugin.CustomShop;
import com.paratopiamc.customshop.utils.UIUtils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationCanceller;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.InactivityConversationCanceller;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Listener for players interacting with custom shops, containing handlers for
 * which the owner left clicks on shops to list items.
 */
public class VMListItem implements Listener {
    private static ConversationFactory listingConversation;

    @EventHandler
    public void listItem(PlayerInteractEvent evt) {
        EquipmentSlot hand = evt.getHand();
        if (!hand.equals(EquipmentSlot.HAND) || !evt.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            return;
        }
        Block targetBlock = evt.getClickedBlock();
        ArmorStand armorStand = UIUtils.getArmorStand(targetBlock);
        if (armorStand != null) {
            evt.setCancelled(true);
            Player player = evt.getPlayer();
            PlayerState state = PlayerState.getPlayerState(player);
            state.clearShopInteractions();
            if (!UIUtils.hasShopPermission(armorStand, player)) {
                player.sendMessage("§cYou do not own the vending machine!");
                return;
            }
            if (PlayerState.getInteractingPlayer(armorStand) != null) {
                player.sendMessage("§cVending machine current in use, please wait...");
                return;
            }
            VMGUI ui = new VMGUI(armorStand, player);
            if (player.getEquipment().getItemInMainHand().getType().equals(Material.AIR)) {
                ui.openOwnerUI();
            } else {
                // New conversation must begin in a different tick that cancelled conversation
                Bukkit.getScheduler().runTask(CustomShop.getPlugin(),
                        () -> state.startConversation(listingConversation));
            }
            state.setShopGUI(ui);
        }
    }

    /**
     * Initialises conversation factory for shop purchases.
     *
     * @param plugin instance of plugin that owns the factory
     */
    public static void initConversationFactory(CustomShop plugin) {
        listingConversation = new ConversationFactory(plugin).withFirstPrompt(new PricePrompt()).withModality(false)
                .withLocalEcho(false)
                .withConversationCanceller(new InactivityConversationCanceller(CustomShop.getPlugin(), 10))
                .addConversationAbandonedListener(new ConversationAbandonedListener() {
                    @Override
                    public void conversationAbandoned(ConversationAbandonedEvent abandonedEvent) {
                        ConversationCanceller canceller = abandonedEvent.getCanceller();
                        Player player = (Player) abandonedEvent.getContext().getForWhom();
                        if (canceller != null) {
                            player.sendMessage("§cShop listing cancelled...");
                        }
                        PlayerState.getPlayerState(player).clearShopInteractions();
                    }
                });
    }

    /**
     * Prompt when player attempts to list a new price to all items in shop similar
     * to the item in hand.
     */
    private static class PricePrompt extends StringPrompt {
        @Override
        public String getPromptText(ConversationContext context) {
            return "§aEnter the price of the item that you want to list...";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if (context.getForWhom() instanceof Player) {
                Player player = (Player) context.getForWhom();
                PlayerState state = PlayerState.getPlayerState(player);
                VMGUI ui = (VMGUI) state.getShopGUI();
                try {
                    double price = ((Double) (Double.parseDouble(input) * 100)).intValue() / 100.0;
                    if (price <= 0) {
                        player.sendMessage("§cPrice must be more than 0!");
                    } else {
                        PlayerInventory playerInventory = player.getInventory();
                        ItemStack item = playerInventory.getItemInMainHand();
                        player.sendMessage(ui.listPrice(item, price));
                    }
                } catch (NumberFormatException e) {
                    player.sendMessage("§cInvalid input!");
                }
            } else {
                // Should not get here.
                context.getForWhom().sendRawMessage("This is a player-only command.");
            }
            return END_OF_CONVERSATION;
        }
    }
}
