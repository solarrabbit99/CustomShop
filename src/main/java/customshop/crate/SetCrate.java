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

package customshop.crate;

import java.io.File;
import java.io.IOException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/** Set the position of custom shop crate in {@code config.yml}. */
public class SetCrate implements CommandExecutor {
    private static File crateLocationFile;
    private static FileConfiguration crateLocation;

    /**
     * Constructor that calls {@link #createCustomConfig(JavaPlugin)} given plugin
     * instance.
     *
     * @param plugin plugin instance
     */
    public SetCrate(JavaPlugin plugin) {
        createCustomConfig(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        Block targetBlock = player.getTargetBlockExact(5);
        if (targetBlock == null) {
            player.sendMessage("§cYou are not targeting any block...");
            return false;
        } else if (!targetBlock.getType().equals(Material.CHEST)) {
            player.sendMessage("§cYou are not targeting any chests!");
        } else {
            crateLocation.set("crate-location.world", targetBlock.getLocation().getWorld().getName());
            crateLocation.set("crate-location.x", targetBlock.getLocation().getX());
            crateLocation.set("crate-location.y", targetBlock.getLocation().getY());
            crateLocation.set("crate-location.z", targetBlock.getLocation().getZ());
            try {
                crateLocation.save(crateLocationFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            player.sendMessage("§aSet chest as crate chest!");
            return true;
        }
        return false;
    }

    /**
     * Create YAML file for crate-location if not already exists. Load configuration
     * file thereafter.
     *
     * @param plugin plugin instance
     */
    private void createCustomConfig(JavaPlugin plugin) {
        crateLocationFile = new File(plugin.getDataFolder(), "crate-location.yml");
        if (!crateLocationFile.exists()) {
            crateLocationFile.getParentFile().mkdirs();
            plugin.saveResource("crate-location.yml", false);
        }
        crateLocation = new YamlConfiguration();
        try {
            crateLocation.load(crateLocationFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Verify if a given location is the location of the crate saved in
     * {@code config.yml}.
     *
     * @param location location to verify
     * @return {@code true} if given location is the location of the crate
     */
    public static boolean verifyCrateLocation(Location location) {
        String worldName = location.getWorld().getName();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        if (crateLocation.getString("crate-location.world") == null) {
            return false;
        }
        return crateLocation.getString("crate-location.world").equals(worldName)
                && crateLocation.getDouble("crate-location.x") == x && crateLocation.getDouble("crate-location.y") == y
                && crateLocation.getDouble("crate-location.z") == z;
    }
}
