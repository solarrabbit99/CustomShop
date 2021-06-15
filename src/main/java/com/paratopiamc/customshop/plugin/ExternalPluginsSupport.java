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

import java.util.HashMap;
import java.util.Set;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerDigType;
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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import dev.lone.itemsadder.api.CustomStack;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
// import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.angeschossen.lands.api.integration.LandsIntegration;

public class ExternalPluginsSupport {
    private CustomShop plugin;
    private final String[] landProtectionPlugins = new String[] { "Towny", "WorldGuard", "GriefPrevention", "Lands" };
    private final String[] customItemsPlugins = new String[] { "ItemsAdder" };
    private final String[] protocolHandlers = new String[] { "ProtocolLib" };

    public ExternalPluginsSupport(CustomShop plugin) {
        this.plugin = plugin;
    }

    public void init() {
        for (String plugin : landProtectionPlugins) {
            if (this.has(plugin))
                CustomShopLogger.sendMessage("Successfully hooked into " + plugin + ".", Level.SUCCESS);
        }
        for (String plugin : customItemsPlugins) {
            if (this.has(plugin))
                CustomShopLogger.sendMessage("Successfully hooked into " + plugin + ".", Level.SUCCESS);
        }
        for (String plugin : protocolHandlers) {
            if (this.has(plugin))
                CustomShopLogger.sendMessage("Successfully hooked into " + plugin + ".", Level.SUCCESS);
        }
    }

    private boolean has(String pluginName) {
        return this.plugin.getServer().getPluginManager().getPlugin(pluginName) != null;
    }

    // #######################################################################################################################
    // ItemsAdder handler here
    // #######################################################################################################################

    public HashMap<Integer, ItemStack> getModelDataToShopMapping() {
        if (!has("ItemsAdder"))
            return null;
        HashMap<Integer, ItemStack> map = new HashMap<>();

        Set<String> vm = this.plugin.getConfig().getConfigurationSection("vending-machine").getKeys(false);
        for (String shop : vm) {
            Integer customModelData = this.plugin.getConfig().getInt("vending-machine." + shop + ".model-data");
            ItemStack item = CustomStack.getInstance("customshop:" + shop + "_vending_machine").getItemStack();
            map.put(customModelData, item);
        }
        Set<String> nb = this.plugin.getConfig().getConfigurationSection("briefcase").getKeys(false);
        for (String shop : nb) {
            Integer customModelData = this.plugin.getConfig().getInt("briefcase." + shop + ".model-data");
            ItemStack item = CustomStack.getInstance("customshop:" + shop + "_briefcase").getItemStack();
            map.put(customModelData, item);
        }

        Integer defaultVM = this.plugin.getConfig().getInt("defaults.vending-machine");
        ItemStack defaultVMItem = CustomStack.getInstance("customshop:default_vending_machine").getItemStack();
        map.put(defaultVM, defaultVMItem);
        Integer defaultBriefcase = this.plugin.getConfig().getInt("defaults.briefcase");
        ItemStack defaultBriefcaseItem = CustomStack.getInstance("customshop:default_briefcase").getItemStack();
        map.put(defaultBriefcase, defaultBriefcaseItem);

        return map;
    }

    // #######################################################################################################################
    // ProtocolLib handler here
    // #######################################################################################################################

    public void blockDamagePacketHandler(BlockDamageEvent evt) {
        if (!has("ProtocolLib")) {
            createPipeline(evt);
        } else {
            protocolLibHandler(evt);
        }
    }

    /**
     * Use ProtocolLib's API for handling {@code ABORT_DESTROY_BLOCK} packets. This
     * should be use if ProtocolLib is present for a safer workaround (avoid access
     * conflict) with the ProtocolLib plugin.
     * 
     * @param evt event of player damaging the block
     */
    private void protocolLibHandler(BlockDamageEvent evt) {
        ProtocolLibrary.getProtocolManager()
                .addPacketListener(new PacketAdapter(CustomShop.getPlugin(), Client.BLOCK_DIG) {
                    @Override
                    public void onPacketReceiving(PacketEvent e) {
                        PacketContainer packet = e.getPacket();
                        PlayerDigType digType = packet.getPlayerDigTypes().getValues().get(0);
                        if (digType.name().equals("ABORT_DESTROY_BLOCK")) {
                            evt.setCancelled(true);
                        }
                    }
                });
    }

    /**
     * Creates a {@link ChannelPipeline} that listens in to packets where player
     * aborts damaging barrier blocks. If player pre-maturely aborts damaging, the
     * event is set as cancelled.
     * 
     * @param evt event of player damaging the block
     */
    private void createPipeline(BlockDamageEvent evt) {
        Class<?> PacketPlayInBlockDig, EnumPlayerDigType, CraftPlayer;
        try {
            PacketPlayInBlockDig = getNMSClass("PacketPlayInBlockDig");
            EnumPlayerDigType = PacketPlayInBlockDig.getDeclaredClasses()[0];
            CraftPlayer = getCraftBukkitClass("entity.CraftPlayer");

            Player player = evt.getPlayer();
            Object handle = CraftPlayer.getMethod("getHandle").invoke(player);
            Object channel;
            if (Bukkit.getVersion().contains("1.17")) {
                Object playerConnection = handle.getClass().getField("b").get(handle);
                Object networkManager = playerConnection.getClass().getField("a").get(playerConnection);
                channel = networkManager.getClass().getField("k").get(networkManager);
            } else {
                Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
                Object networkManager = playerConnection.getClass().getField("networkManager").get(playerConnection);
                channel = networkManager.getClass().getField("channel").get(networkManager);
            }
            Object pipeline = channel.getClass().getMethod("pipeline").invoke(channel);

            ChannelDuplexHandler handler = new ChannelDuplexHandler() {
                @Override
                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                    if (PacketPlayInBlockDig.isInstance(msg)) {
                        Object detectedEnum = PacketPlayInBlockDig.getMethod("d").invoke(msg);
                        Object digType = Enum.class.getMethod("valueOf", Class.class, String.class).invoke(null,
                                EnumPlayerDigType, "ABORT_DESTROY_BLOCK");
                        if (detectedEnum.equals(digType)) {
                            evt.setCancelled(true);
                            pipeline.getClass().getMethod("remove", ChannelHandler.class).invoke(pipeline, this);
                        }
                    }
                    super.channelRead(ctx, msg);
                }
            };

            if (pipeline.getClass().getMethod("get", String.class).invoke(pipeline, player.getName()) == null) {
                pipeline.getClass().getMethod("addBefore", String.class, String.class, ChannelHandler.class)
                        .invoke(pipeline, "packet_handler", player.getName(), handler);
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get class by name under {@code net.minecraft.server.*}.
     *
     * @param className name of the class
     * @return the {@code Class} object of the class with given name
     * @throws ClassNotFoundException if class with the given name cannot be found
     */
    private Class<?> getNMSClass(String className) throws ClassNotFoundException {
        if (Bukkit.getVersion().contains("1.17")) {
            return Class.forName("net.minecraft.network.protocol.game." + className);
        } else {
            return Class.forName("net.minecraft.server."
                    + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + className);
        }
    }

    /**
     * Get class by name under {@code org.bukkit.craftbukkit.*}.
     *
     * @param className name of the class
     * @return the {@code Class} object of the class with given name
     * @throws ClassNotFoundException if class with the given name cannot be found
     */
    private Class<?> getCraftBukkitClass(String className) throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit."
                + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + className);
    }

    // #######################################################################################################################
    // Shop creation permissions, currently registering: Towny, WorldGuard
    // #######################################################################################################################

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
