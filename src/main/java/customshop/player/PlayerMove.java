package customshop.player;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMove implements Listener {
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent evt) {
        Player player = evt.getPlayer();
        PlayerState state = PlayerState.getPlayerState(player);
        ArmorStand armorStand = state.getArmorStand();
        if (armorStand != null) {
            Location loc1 = armorStand.getLocation();
            Location loc2 = player.getLocation();
            if (!loc1.getWorld().equals(loc2.getWorld()) || loc1.distanceSquared(loc2) > 25) {
                state.abandonConversation();
            }
        }
    }
}
