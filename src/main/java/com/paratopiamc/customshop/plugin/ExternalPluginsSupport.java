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

public class ExternalPluginsSupport {
    private CustomShop plugin;

    public ExternalPluginsSupport(CustomShop plugin) {
        this.plugin = plugin;

    }

    public void init() {
        if (this.hasTowny()) {
            CustomShopLogger.sendMessage("Towny plugin detected, successfully hooked into towny.", Level.SUCCESS);
        }

        if (this.hasWorldGuard()) {
            CustomShopLogger.sendMessage("WorldGuard plugin detected, successfully hooked into worldguard.",
                    Level.SUCCESS);
        }
    }

    /**
     * Checks if towny plugin is installed by name.
     * 
     * @return {@code true} if the server has towny plugin installed
     */
    public boolean hasTowny() {
        return this.plugin.getServer().getPluginManager().getPlugin("Towny") != null;
    }

    /**
     * Checks if worldguard plugin is installed by name.
     * 
     * @return {@code true} if the server has worldguard plugin installed
     */
    public boolean hasWorldGuard() {
        return this.plugin.getServer().getPluginManager().getPlugin("WorldGuard") != null;
    }

    // ################################################################### //
    // Shop creation permissions, currently registering: Towny, WorldGuard //
    // ################################################################### //

    public boolean hasCreatePerms(Location location, Player player) {
        return hasTownyCreatePerms(location, player) && hasWorldGuardCreatePerms(location, player);
    }

    /**
     * Checks if towny will not restrict player from placing custom shop, which is
     * vacuously true if Towny is not installed or is disabled for the plugin.
     *
     * @param location location where the shop will be placed
     * @return {@code true} if player is not restricted to build by towny
     */
    private boolean hasTownyCreatePerms(Location location, Player player) {
        if (!this.hasTowny())
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
        if (!this.hasWorldGuard())
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

    // ################################################################### //
    // Shop removal permissions, currently registering: Towny, WorldGuard //
    // ################################################################### //

    public boolean hasRemovePerms(Location location, Player player) {
        return hasTownyRemovePerms(location, player) && hasWorldGuardRemovePerms(location, player);
    }

    /**
     * Checks if towny will not restrict player from placing custom shop, which is
     * vacuously true if Towny is not installed or is disabled for the plugin.
     *
     * @param location location of the shop
     * @param player   player removing the shop
     * @return {@code true} if player is not restricted to build by towny
     */
    private boolean hasTownyRemovePerms(Location location, Player player) {
        if (!this.hasTowny())
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
     * Checks if worldguard will not restrict player from placing custom shop, which
     * is vacuously true if WorldGuard is not installed or is disabled for the
     * plugin.
     *
     * @param location location of the shop
     * @param player   player removing the shop
     * @return {@code true} if player is not restricted to build by worldguard
     */
    private boolean hasWorldGuardRemovePerms(Location location, Player player) {
        if (!this.hasWorldGuard())
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
}
