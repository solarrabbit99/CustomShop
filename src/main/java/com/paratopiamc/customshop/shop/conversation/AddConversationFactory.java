package com.paratopiamc.customshop.shop.conversation;

import com.paratopiamc.customshop.gui.BriefcaseGUI;
import com.paratopiamc.customshop.player.PlayerState;
import com.paratopiamc.customshop.plugin.CustomShop;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationCanceller;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.InactivityConversationCanceller;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

public class AddConversationFactory extends ConversationFactory {

    public AddConversationFactory() {
        super(CustomShop.getPlugin());
        this.firstPrompt = new AddConversation();
        this.isModal = false;
        this.localEchoEnabled = false;
        this.abandonedListeners.add(new ConversationAbandonedListener() {
            @Override
            public void conversationAbandoned(ConversationAbandonedEvent abandonedEvent) {
                ConversationCanceller canceller = abandonedEvent.getCanceller();
                Player player = (Player) abandonedEvent.getContext().getForWhom();
                if (canceller != null) {
                    player.sendMessage("§cItem add cancelled...");
                }
                PlayerState.getPlayerState(player).clearShopInteractions();
            }
        });
        this.cancellers.add(new InactivityConversationCanceller(plugin, 10));
    }

    private static class AddConversation extends StringPrompt {
        @Override
        public String getPromptText(ConversationContext context) {
            return "§aEnter the amount of item that you want to add into the shop...";
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
                        player.sendMessage("§cInvalid input!");
                    } else if (context.getForWhom() instanceof Player) {
                        ui.addItem(inputInt);
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
