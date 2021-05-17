package customshop.shop;

import java.util.Optional;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import customshop.gui.ShopGUI;
import customshop.gui.VMGUI;
import customshop.player.PlayerState;
import customshop.utils.UIUtils;

public class ShopOpening implements Listener {
    /**
     * Open vending machine's UI when player right clicks the armor stand.
     *
     * @param evt event of player right clicking entity
     */
    @EventHandler
    public void openShop(PlayerInteractEvent evt) {
        EquipmentSlot hand = evt.getHand();
        if (!hand.equals(EquipmentSlot.HAND) || !evt.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        Block targetBlock = evt.getClickedBlock();
        Player player = evt.getPlayer();

        ArmorStand armorStand = UIUtils.getArmorStand(targetBlock);
        if (armorStand != null) {
            ShopGUI gui = getShopOpener(armorStand, player);
            if (gui != null) {
                PlayerState state = PlayerState.getPlayerState(player);
                if (PlayerState.getInteractingPlayer(armorStand) != null) {
                    evt.getPlayer().sendMessage("§cVending machine current in use, please wait...");
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
     * barrier block. Return {@code null} if player is not owner of the shop or no
     * ShopGUI can be used.
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
            default:
                result = Optional.empty();
                break;
        }
        return result.filter(gui -> gui.isOwner()).orElse(null);
    }
}
