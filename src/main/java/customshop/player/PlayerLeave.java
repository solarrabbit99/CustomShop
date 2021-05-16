package customshop.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import customshop.gui.VMGUI;

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
            VMGUI.saveInventory(evt.getPlayer());
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
            VMGUI.saveInventory(evt.getPlayer());
        }
    }
}
