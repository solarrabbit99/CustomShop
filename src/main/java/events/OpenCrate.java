package events;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import plugin.CustomShop;

public class OpenCrate implements Listener {
    private static ItemStack crateKey;

    public OpenCrate() {
        ItemStack template = new ItemStack(Material.TRIPWIRE_HOOK);
        ItemMeta meta = template.getItemMeta();
        meta.setDisplayName("§r[§5§lCustom Shop Crate Key§f]");
        List<String> lore = new ArrayList<>();
        lore.add("§9Use key on Custom Shop Crate!");
        meta.setLore(lore);
        template.setItemMeta(meta);
        crateKey = template;
    }

    @EventHandler
    public static void onOpenCrate(PlayerInteractEvent evt) {
        EquipmentSlot hand = evt.getHand();
        if (!hand.equals(EquipmentSlot.HAND) || !evt.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        } else if (!verifyCrateLocation(evt.getClickedBlock().getLocation())) {
            return;
        } else {
            evt.setCancelled(true);
            Player player = evt.getPlayer();
            ItemStack item = player.getEquipment().getItemInMainHand();
            if (!item.isSimilar(crateKey)) {
                player.sendMessage("§cYou do not have the required crate key in main hand!");
                return;
            } else {
                ItemStack itemInHand = player.getInventory().getItemInMainHand();
                itemInHand.setAmount(itemInHand.getAmount() - 1);
                player.getInventory().setItemInMainHand(itemInHand);
                Random rng = new Random();
                int unlocked = rng.nextInt(3) + 100001;
                List<Integer> lst = CustomShop.getPlugin().getDatabase().getUnlockedShops(player);
                player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 2.0F, 1.0F);
                if (lst.contains(unlocked)) {
                    player.sendMessage("§6You already have " + unlocked + " unlocked :(");
                } else {
                    lst.add(unlocked);
                    CustomShop.getPlugin().getDatabase().setUnlockedShops(player, lst);
                    player.sendMessage("§aNew custom shop unlocked! " + unlocked);
                }
            }
        }
    }

    private static boolean verifyCrateLocation(Location location) {
        FileConfiguration conf = CustomShop.getPlugin().getConfig();
        if (conf.getString("crate-location.world") == null) {
            CustomShop.getPlugin().getServer().getPluginManager().disablePlugin(CustomShop.getPlugin());
            return false;
        }
        String worldName = location.getWorld().getName();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        return conf.getString("crate-location.world").equals(worldName) && conf.getDouble("crate-location.x") == x
                && conf.getDouble("crate-location.y") == y && conf.getDouble("crate-location.z") == z;
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
