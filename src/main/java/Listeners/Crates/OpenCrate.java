package Listeners.Crates;

import java.util.List;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import Plugin.CustomShops;

public class OpenCrate implements Listener {
    private static Location crateBlock;
    private static ItemStack crateKey;

    public OpenCrate() {
        ItemStack template = new ItemStack(Material.TRIPWIRE_HOOK);
        ItemMeta meta = template.getItemMeta();
        meta.setDisplayName("[§5§lCustom Shop Crate Key§f]");
        crateKey = template;
    }

    @EventHandler
    public static void onOpenCrate(PlayerInteractEvent evt) {
        EquipmentSlot hand = evt.getHand();
        if (!hand.equals(EquipmentSlot.HAND) || !evt.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        } else if (!evt.getClickedBlock().getLocation().equals(crateBlock)) {
            return;
        } else {
            evt.setCancelled(true);
            Player player = evt.getPlayer();
            ItemStack item = player.getEquipment().getItemInMainHand();
            if (!item.isSimilar(crateKey)) {
                return;
            } else {
                Random rng = new Random();
                int unlocked = rng.nextInt(3) + 100001;
                List<Integer> lst = CustomShops.getPlugin().getDatabase().getUnlockedShops(player);
                if (lst.contains(unlocked)) {
                    player.sendMessage("§6You already have " + unlocked + " unlocked :(");
                } else {
                    lst.add(unlocked);
                    CustomShops.getPlugin().getDatabase().setUnlockedShops(player, lst);
                    player.sendMessage("§aNew custom shop unlocked! " + unlocked);
                }
            }
        }
    }

    public static void setCrateLocation(Location loc) {
        // TODO: save it in database/yaml file instead of static variable
        crateBlock = loc;
    }

    /**
     * Return an {@link ItemStack} of crate key(s) of given amount.
     *
     * @param amount amount of keys
     * @return crate keys
     */
    public static ItemStack getCrateKey(int amount) {
        ItemStack clone = crateKey.clone();
        clone.setAmount(amount);
        return clone;
    }
}
