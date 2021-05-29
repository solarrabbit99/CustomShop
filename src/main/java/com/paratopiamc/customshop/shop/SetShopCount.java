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

import java.util.concurrent.CompletableFuture;
import com.paratopiamc.customshop.player.PlayerState;
import com.paratopiamc.customshop.plugin.CSComd;
import com.paratopiamc.customshop.plugin.CustomShop;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationCanceller;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.InactivityConversationCanceller;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class SetShopCount extends CSComd {
    private static ConversationFactory confirmingConversation;
    private int newCount;

    public SetShopCount(CommandSender sender, String[] args) {
        this.sender = sender;
        this.args = args;
        initConversationFactory();
    }

    @Override
    public boolean exec() {
        if (!sender.hasPermission("customshop.resetshop") || !(sender instanceof Player)) {
            sender.sendMessage("§cYou do not have permission to use this command.");
            return false;
        }
        if (args.length < 3) {
            sender.sendMessage("§cInvalid number of arguments!");
            return false;
        }
        Player player = Bukkit.getPlayerExact(args[1]);
        if (player == null) {
            sender.sendMessage("§cCannot find specified player or the player is not online!");
            return true;
        }
        try {
            this.newCount = Integer.parseInt(SetShopCount.this.args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage("§cInvalid number input!");
            return false;
        }
        PlayerState state = PlayerState.getPlayerState(player);
        if (!state.startConversation(confirmingConversation)) {
            player.sendMessage("§cYou are still engaged in another conversation!");
        }
        return true;
    }

    /**
     * Initialises conversation factory for shop purchases.
     *
     * @param plugin instance of plugin that owns the factory
     */
    private void initConversationFactory() {
        Plugin plugin = CustomShop.getPlugin();
        confirmingConversation = new ConversationFactory(plugin).withFirstPrompt(new AmountPrompt()).withModality(false)
                .withLocalEcho(false)
                .withConversationCanceller(new InactivityConversationCanceller(CustomShop.getPlugin(), 10))
                .addConversationAbandonedListener(new ConversationAbandonedListener() {
                    @Override
                    public void conversationAbandoned(ConversationAbandonedEvent abandonedEvent) {
                        ConversationCanceller canceller = abandonedEvent.getCanceller();
                        Player player = (Player) abandonedEvent.getContext().getForWhom();
                        if (canceller != null) {
                            player.sendMessage("§cOperation cancelled...");
                        }
                        PlayerState.getPlayerState(player).clearShopInteractions();
                    }
                });
    }

    /**
     * Prompt when player attempts to purchase from Vending Machine.
     */
    private class AmountPrompt extends StringPrompt {
        @Override
        public String getPromptText(ConversationContext context) {
            return "§9Be sure that the player has the correct number of existing shops before issuing the command. "
                    + "Confirm reset? (y/n)";
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            if (context.getForWhom() instanceof Player) {
                Player player = (Player) context.getForWhom();
                switch (input) {
                case "y":
                    try {
                        CompletableFuture<Void> voidcf = CompletableFuture.runAsync(() -> CustomShop.getPlugin()
                                .getDatabase().setShopsOwned(player.getUniqueId(), SetShopCount.this.newCount));
                        voidcf.thenRun(() -> player
                                .sendMessage("§aPlayer total shop count set to " + SetShopCount.this.newCount + "!"));
                    } catch (NumberFormatException e) {
                        player.sendMessage("§cInvalid number input!");
                    }
                    break;
                case "n":
                    player.sendMessage("§cOperation cancelled...");
                    break;
                default:
                    player.sendMessage("§cInvalid input!");
                    break;
                }
            } else {
                // Should not get here.
                context.getForWhom().sendRawMessage("This is a player-only command.");
            }
            return END_OF_CONVERSATION;
        }
    }

}
