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

package com.paratopiamc.customshop.shop.conversation;

import com.paratopiamc.customshop.gui.ShopGUI;
import com.paratopiamc.customshop.player.PlayerState;
import com.paratopiamc.customshop.plugin.CustomShop;
import com.paratopiamc.customshop.utils.LanguageUtils;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationCanceller;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.InactivityConversationCanceller;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SetPriceConversationFactory extends ConversationFactory {
    private ItemStack item;

    public SetPriceConversationFactory(ItemStack item) {
        super(CustomShop.getPlugin());
        this.item = item.clone();
        this.firstPrompt = new PricePrompt();
        this.localEchoEnabled = false;
        this.isModal = false;
        this.abandonedListeners.add(new ConversationAbandonedListener() {
            @Override
            public void conversationAbandoned(ConversationAbandonedEvent abandonedEvent) {
                ConversationCanceller canceller = abandonedEvent.getCanceller();
                Player player = (Player) abandonedEvent.getContext().getForWhom();
                if (canceller != null) {
                    player.sendMessage(LanguageUtils.getString("price-convo-cancelled"));
                }
                PlayerState.getPlayerState(player).clearShopInteractions();
            }
        });
        this.cancellers.add(new InactivityConversationCanceller(CustomShop.getPlugin(), 10));
    }

    /**
     * Prompt when player attempts to list a new price to all items in shop similar
     * to the item in hand.
     */
    private class PricePrompt extends StringPrompt {
        @Override
        public String getPromptText(ConversationContext context) {
            return LanguageUtils.getString("price-convo-prompt");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if (context.getForWhom() instanceof Player) {
                Player player = (Player) context.getForWhom();
                PlayerState state = PlayerState.getPlayerState(player);
                ShopGUI ui = state.getShopGUI();
                try {
                    // round off price input
                    double price = Math.floor((Double.parseDouble(input) * 100)) / 100;
                    price = Math.min(price, CustomShop.getPlugin().getConfig().getDouble("max-price"));
                    if (CustomShop.getPlugin().getConfig().getBoolean("round-down-price"))
                        price = Math.floor(price);

                    if (price <= 0) {
                        player.sendMessage(LanguageUtils.getString("invalid-input"));
                    } else {
                        player.sendMessage(ui.listPrice(item, price));
                    }
                } catch (NumberFormatException e) {
                    player.sendMessage(LanguageUtils.getString("invalid-input"));
                }
            } else {
                // Should not get here.
                context.getForWhom().sendRawMessage("This is a player-only command.");
            }
            return END_OF_CONVERSATION;
        }
    }

}
