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

package com.paratopiamc.customshop.plugin;

import com.palmergames.bukkit.towny.object.TownyPermission.ActionType;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import com.palmergames.bukkit.towny.utils.ShopPlotUtil;
import com.paratopiamc.customshop.plugin.CustomShopLogger.Level;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
// import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.angeschossen.lands.api.integration.LandsIntegration;

public class ExternalPluginsSupport {
    private CustomShop plugin;
    private final String[] plugins = new String[] { "Towny", "WorldGuard", "GriefPrevention", "Lands" };

    public ExternalPluginsSupport(CustomShop plugin) {
        this.plugin = plugin;
    }

    public void init() {
        for (String plugin : plugins) {
            if (this.has(plugin))
                CustomShopLogger.sendMessage("Successfully hooked into " + plugin + ".", Level.SUCCESS);
        }
    }

    private boolean has(String pluginName) {
        return this.plugin.getServer().getPluginManager().getPlugin(pluginName) != null;
    }

    // ################################################################### //
    // Shop creation permissions, currently registering: Towny, WorldGuard //
    // ################################################################### //

    public boolean hasCreatePerms(Location location, Player player) {
        return hasTownyCreatePerms(location, player) && hasWorldGuardCreatePerms(location, player)
                && hasLandsCreatePerms(location, player);
    }

    /**
     * Checks if towny will not restrict player from placing custom shop, which is
     * vacuously true if Towny is not installed or is disabled for the plugin.
     *
     * @param location location where the shop will be placed
     * @return {@code true} if player is not restricted to build by towny
     */
    private boolean hasTownyCreatePerms(Location location, Player player) {
        if (!this.has("Towny"))
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
    private boolean hasWorldGuardCreatePerms(Location location, Player player) {
        if (!this.has("WorldGuard"))
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
     * Checks if lands will not restrict player from placing custom shop (if the
     * player has {@link me.angeschossen.lands.api.flags.Flags#BLOCK_PLACE} flag in
     * specified location), which is vacuously true if Lands is not installed or is
     * disabled for the plugin.
     *
     * @param location location where the shop will be placed
     * @return {@code true} if player is not restricted to block placing by lands
     */
    private boolean hasLandsCreatePerms(Location location, Player player) {
        if (!this.has("Lands"))
            return true;
        if (!CustomShop.getPlugin().getConfig().getBoolean("lands-enabled"))
            return true;

        return new LandsIntegration(this.plugin).getAreaByLoc(location).hasFlag(player.getUniqueId(),
                me.angeschossen.lands.api.flags.Flags.BLOCK_PLACE);
    }

    // private boolean hasGriefPreventionCreatePerms(Location location, Player
    // player) {
    // if (!this.hasWorldGuard())
    // return true;
    // if
    // (!CustomShop.getPlugin().getConfig().getBoolean("griefprevention-enabled"))
    // return true;

    // return GriefPrevention.instance.allowBuild(player, location) == null;
    // }

    // #######################################################################################################################
    // Shop removal permissions, currently registering: Towny, WorldGuard
    // #######################################################################################################################

    public boolean hasRemovePerms(Location location, Player player) {
        return hasTownyRemovePerms(location, player) && hasWorldGuardRemovePerms(location, player)
                && hasLandsRemovePerms(location, player);
    }

    /**
     * Checks if towny will not restrict player from removing custom shop, which is
     * vacuously true if Towny is not installed or is disabled for the plugin.
     *
     * @param location location of the shop
     * @param player   player removing the shop
     * @return {@code true} if player is not restricted to destroy by towny
     */
    private boolean hasTownyRemovePerms(Location location, Player player) {
        if (!this.has("Towny"))
            return true;
        if (!CustomShop.getPlugin().getConfig().getBoolean("towny-enabled"))
            return true;

        boolean onlyShopPlots = CustomShop.getPlugin().getConfig().getBoolean("only-shop-plots");
        if (onlyShopPlots) {
            return ShopPlotUtil.doesPlayerHaveAbilityToEditShopPlot(player, location);
        } else {
            return PlayerCacheUtil.getCachePermission(player, location, Material.STONE, ActionType.DESTROY);
        }
    }

    /**
     * Checks if worldguard will not restrict player from removing custom shop,
     * which is vacuously true if WorldGuard is not installed or is disabled for the
     * plugin.
     *
     * @param location location of the shop
     * @param player   player removing the shop
     * @return {@code true} if player is not restricted to build by worldguard
     */
    private boolean hasWorldGuardRemovePerms(Location location, Player player) {
        if (!this.has("WorldGuard"))
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
     * Checks if lands will not restrict player from removing custom shop (if the
     * player has {@link me.angeschossen.lands.api.flags.Flags#BLOCK_BREAK} flag in
     * specified location), which is vacuously true if Lands is not installed or is
     * disabled for the plugin.
     *
     * @param location location where the shop will be placed
     * @return {@code true} if player is not restricted to block breaking by lands
     */
    private boolean hasLandsRemovePerms(Location location, Player player) {
        if (!this.has("Lands"))
            return true;
        if (!CustomShop.getPlugin().getConfig().getBoolean("lands-enabled"))
            return true;

        return new LandsIntegration(this.plugin).getAreaByLoc(location).hasFlag(player.getUniqueId(),
                me.angeschossen.lands.api.flags.Flags.BLOCK_BREAK);
    }

    // private boolean hasGriefPreventionRemovePerms(Location location, Player
    // player) {
    // if (!this.hasWorldGuard())
    // return true;
    // if
    // (!CustomShop.getPlugin().getConfig().getBoolean("griefprevention-enabled"))
    // return true;

    // // return GriefPrevention.instance.allowBreak(player,
    // // Material.STONE, location) == null;
    // return false;
    // }
}
