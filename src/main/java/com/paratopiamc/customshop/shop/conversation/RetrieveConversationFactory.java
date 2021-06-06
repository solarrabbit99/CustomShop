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

import com.paratopiamc.customshop.gui.BriefcaseGUI;
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

public class RetrieveConversationFactory extends ConversationFactory {

    public RetrieveConversationFactory() {
        super(CustomShop.getPlugin());
        this.firstPrompt = new RetrieveConversation();
        this.isModal = false;
        this.localEchoEnabled = false;
        this.abandonedListeners.add(new ConversationAbandonedListener() {
            @Override
            public void conversationAbandoned(ConversationAbandonedEvent abandonedEvent) {
                ConversationCanceller canceller = abandonedEvent.getCanceller();
                Player player = (Player) abandonedEvent.getContext().getForWhom();
                if (canceller != null) {
                    player.sendMessage(LanguageUtils.getString("retrieve-convo-cancelled"));
                }
                PlayerState.getPlayerState(player).clearShopInteractions();
            }
        });
        this.cancellers.add(new InactivityConversationCanceller(plugin, 10));
    }

    private static class RetrieveConversation extends StringPrompt {
        @Override
        public String getPromptText(ConversationContext context) {
            return LanguageUtils.getString("retrieve-convo-prompt");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if (context.getForWhom() instanceof Player) {
                Player player = (Player) context.getForWhom();
                PlayerState state = PlayerState.getPlayerState(player);
                BriefcaseGUI ui = (BriefcaseGUI) state.getShopGUI();
                try {
                    int inputInt = Integer.parseInt(input);
                    double inputDouble = Double.parseDouble(input);

                    if (inputInt != inputDouble || inputDouble <= 0) {
                        player.sendMessage(LanguageUtils.getString("invalid-input"));
                    } else if (context.getForWhom() instanceof Player) {
                        ui.retrieveItem(inputInt);
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
