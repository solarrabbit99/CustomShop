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

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import com.palmergames.bukkit.towny.object.TownyPermission.ActionType;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import com.palmergames.bukkit.towny.utils.ShopPlotUtil;
import com.paratopiamc.customshop.gui.CreationGUI;
import com.paratopiamc.customshop.plugin.CSComd;
import com.paratopiamc.customshop.plugin.CustomShop;
import com.paratopiamc.customshop.shop.briefcase.BriefcaseCreator;
import com.paratopiamc.customshop.shop.vm.VMCreator;
import com.paratopiamc.customshop.utils.LanguageUtils;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Encapsulates a shop creation process. Player runs {@code /customshop newshop}
 * to spawn a GUI. Afterwhich, a shop will attempt to spawn if player clicks on
 * a design listed in the GUI.
 */
public class ShopCreation extends CSComd implements Listener {
    private boolean isAdmin;

    public ShopCreation() {
        super(null, null);
    }

    public ShopCreation(CommandSender sender, boolean isAdmin) {
        super(sender, null);
        this.isAdmin = isAdmin;
    }

    @Override
    public boolean exec() {
        if (!(sender instanceof Player)) {
            return false;
        } else if (!sender.hasPermission("customshop.createshop")) {
            sender.sendMessage(LanguageUtils.getString("command-no-perms"));
            return false;
        } else if (!sender.hasPermission("customshop.admin") && this.isAdmin) {
            sender.sendMessage(LanguageUtils.getString("command-no-perms"));
            return false;
        }
        Player player = (Player) sender;
        Block targetBlock = player.getTargetBlockExact(5);
        if (targetBlock == null) {
            player.sendMessage(LanguageUtils.getString("create.invalid-block"));
            return false;
        }
        CreationGUI.openFirstPage(player, isAdmin);
        return false;
    }

    /**
     * Creates a shop when player clicks on a valid and unlocked shop design in the
     * GUI.
     *
     * @param evt corresponding {@link InventoryClickEvent}
     */
    @EventHandler
    public void createShop(InventoryClickEvent evt) {
        ItemStack item = evt.getCurrentItem();
        if (item == null) {
            return;
        }
        InventoryHolder holder = evt.getClickedInventory().getHolder();
        Player player = (Player) evt.getWhoClicked();
        String title = evt.getView().getTitle();
        if (title.equalsIgnoreCase(LanguageUtils.getString("shop-creation"))
                || title.equalsIgnoreCase(LanguageUtils.getString("admin-shop-creation"))) {
            this.isAdmin = title.equalsIgnoreCase(LanguageUtils.getString("admin-shop-creation"));
            evt.setCancelled(true);
            if (holder == null) {
                ItemMeta itemMeta = item.getItemMeta();
                if (itemMeta.hasDisplayName()
                        && itemMeta.getDisplayName().equals("§c" + LanguageUtils.getString("icons.close"))) {
                    CreationGUI.closeGUI(player);
                } else if (itemMeta.hasDisplayName()
                        && itemMeta.getDisplayName().equals("§e" + LanguageUtils.getString("icons.previous"))) {
                    CreationGUI.previousPage(player);
                } else if (itemMeta.hasDisplayName()
                        && itemMeta.getDisplayName().equals("§e" + LanguageUtils.getString("icons.next"))) {
                    CreationGUI.nextPage(player);
                } else if (evt.getSlot() < 27) {
                    Block targetBlock = player.getTargetBlockExact(5);
                    int maxShops = CustomShop.getPlugin().getConfig().getInt("max-shops");
                    CompletableFuture<Integer> numbercf = CompletableFuture.supplyAsync(
                            () -> CustomShop.getPlugin().getDatabase().getTotalShopOwned(player.getUniqueId()));
                    numbercf.thenAccept(number -> {
                        BukkitRunnable runnable = new BukkitRunnable() {
                            @Override
                            public void run() {
                                CreationGUI.closeGUI(player);
                                if (number.intValue() >= maxShops) {
                                    player.sendMessage(LanguageUtils.getString("create.reached-max"));
                                    return;
                                }
                                if (targetBlock == null) {
                                    player.sendMessage(LanguageUtils.getString("create.invalid-block"));
                                    return;
                                }
                                Location location = getCreationLocation(targetBlock, player);

                                // Check for towny and worldguard restrictions
                                if (!hasTownyPerms(location, player) || !hasWorldGuardPerms(location, player)) {
                                    player.sendMessage(LanguageUtils.getString("create.no-perms"));
                                    return;
                                }

                                ShopCreator creator = getShopCreator(itemMeta);
                                creator.createShop(location, player, item, isAdmin);
                                CreationGUI.closeGUI(player);
                            }
                        };
                        runnable.runTask(CustomShop.getPlugin());
                    });
                }
            }
        }
    }

    /**
     * Checks if towny will not restrict player from placing custom shop, which is
     * vacuously true if Towny is not installed or is disabled for the plugin.
     *
     * @param location location where the shop will be placed
     * @return {@code true} if player is not restricted to build by towny
     */
    private static boolean hasTownyPerms(Location location, Player player) {
        if (!CustomShop.getPlugin().hasTowny())
            return true;
        if (!CustomShop.getPlugin().getConfig().getBoolean("towny-enabled"))
            return true;

        boolean onlyShopPlots = CustomShop.getPlugin().getConfig().getBoolean("only-shop-plots");
        if (onlyShopPlots) {
            return ShopPlotUtil.doesPlayerHaveAbilityToEditShopPlot(player, location);
        } else {
            return PlayerCacheUtil.getCachePermission(player, location, Material.STONE, ActionType.BUILD);
        }
    }

    /**
     * Checks if worldguard will not restrict player from placing custom shop, which
     * is vacuously true if WorldGuard is not installed or is disabled for the
     * plugin.
     *
     * @param location location where the shop will be placed
     * @return {@code true} if player is not restricted to build by worldguard
     */
    private static boolean hasWorldGuardPerms(Location location, Player player) {
        if (!CustomShop.getPlugin().hasWorldGuard())
            return true;
        if (!CustomShop.getPlugin().getConfig().getBoolean("worldguard-enabled"))
            return true;

        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        if (WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, localPlayer.getWorld())) {
            return true;
        }

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        return query.testState(BukkitAdapter.adapt(location), localPlayer, Flags.BUILD);
    }

    /**
     * Returns location where the shop will attempt to spawn at. The returned
     * location faces the player.
     *
     * @param targetBlock block that the player is targeting
     * @param player      player of interest
     * @return resulting location
     */
    private static Location getCreationLocation(Block targetBlock, Player player) {
        Location result = targetBlock.getLocation().clone();
        result.add(0.5, 1, 0.5);
        // The yaw of a player, on top of what is stated in spigot's API documentation,
        // ranges from -360 to 360. Hence we add 540 here to ensure positivity. Somehow
        // fixes the issue of yaw not rounding off correctly.
        int yaw = ((Float) (player.getLocation().getYaw() + 540)).intValue();
        yaw += 45;
        yaw -= yaw % 90;
        result.setYaw(yaw);
        return result;
    }

    /**
     * Returns a specific {@link ShopCreator} for the selected item.
     *
     * @param meta item meta of the clicked item
     * @return ShopCreator corresponding to the clicked item
     * @throws NoSuchShopException if the given item meta does not match any shop
     *                             designs
     */
    private static ShopCreator getShopCreator(ItemMeta meta) {
        if (!meta.hasDisplayName()) {
            throw new NoSuchShopException();
        } else if (!meta.hasCustomModelData()) {
            throw new NoSuchShopException(meta.getDisplayName());
        } else {
            int model = meta.getCustomModelData();

            int defaultVM = CustomShop.getPlugin().getConfig().getInt("defaults.vending-machine");
            if (defaultVM == model) {
                return new VMCreator();
            }
            Set<String> vm = CustomShop.getPlugin().getConfig().getConfigurationSection("vending-machine")
                    .getKeys(false);
            for (String e : vm) {
                int customModelData = CustomShop.getPlugin().getConfig().getInt("vending-machine." + e + ".model-data");
                if (customModelData == model) {
                    return new VMCreator();
                }
            }

            return new BriefcaseCreator();
        }
    }

    /**
     * Encapsulates an exception where no shop designs corresponds to the item that
     * the player picked. This exception should never be thrown.
     */
    private static class NoSuchShopException extends RuntimeException {
        private NoSuchShopException(String name) {
            super("Selected item did not match any kind of shops: " + name);
        }

        private NoSuchShopException() {
            super("Selected item did not match any kind of shops!");
        }
    }
}
