package ua.fichamine.fichartp.managers;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.espi.protectionstones.PSRegion;
import dev.espi.protectionstones.ProtectionStones;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import ua.fichamine.fichartp.FichaRTP;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RegionManager {

    private final FichaRTP plugin;
    private final Random random = new Random();
    private boolean worldGuardEnabled = false;
    private boolean protectionStonesEnabled = false;

    public RegionManager(FichaRTP plugin) {
        this.plugin = plugin;
        checkPlugins();
    }

    private void checkPlugins() {
        worldGuardEnabled = Bukkit.getPluginManager().getPlugin("WorldGuard") != null;
        protectionStonesEnabled = Bukkit.getPluginManager().getPlugin("ProtectionStones") != null;
    }

    public Location getRandomRegionLocation(Player player) {
        List<Location> regionCenters = new ArrayList<>();

        if (worldGuardEnabled && plugin.getConfigManager().isUseWorldGuard()) {
            regionCenters.addAll(getWorldGuardRegions(player));
        }

        if (protectionStonesEnabled && plugin.getConfigManager().isUseProtectionStones()) {
            regionCenters.addAll(getProtectionStonesRegions(player));
        }

        if (regionCenters.isEmpty()) {
            return null;
        }

        Location regionCenter = regionCenters.get(random.nextInt(regionCenters.size()));

        int minRadius = plugin.getConfigManager().getBaseMinRadius();
        int maxRadius = plugin.getConfigManager().getBaseMaxRadius();

        return ua.fichamine.fichartp.utils.LocationUtils.getRandomLocationAroundPoint(regionCenter, minRadius, maxRadius);
    }

    private List<Location> getWorldGuardRegions(Player player) {
        List<Location> centers = new ArrayList<>();

        if (!worldGuardEnabled) {
            return centers;
        }

        try {
            List<String> excludedWorlds = plugin.getConfigManager().getBaseExcludedWorlds();

            for (World world : Bukkit.getWorlds()) {
                if (excludedWorlds.contains(world.getName())) {
                    continue;
                }

                com.sk89q.worldguard.protection.managers.RegionManager wgRegionManager =
                        WorldGuard.getInstance().getPlatform().getRegionContainer()
                                .get(BukkitAdapter.adapt(world));

                if (wgRegionManager == null) {
                    continue;
                }

                for (ProtectedRegion region : wgRegionManager.getRegions().values()) {
                    if (region.getOwners().size() > 0 || region.getMembers().size() > 0) {
                        com.sk89q.worldedit.math.BlockVector3 min = region.getMinimumPoint();
                        com.sk89q.worldedit.math.BlockVector3 max = region.getMaximumPoint();
                        com.sk89q.worldedit.math.BlockVector3 center = min.add(max).divide(2);

                        Location location = new Location(world, center.getX(),
                                world.getHighestBlockYAt((int)center.getX(), (int)center.getZ()), center.getZ());
                        centers.add(location);
                    }
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Error getting WorldGuard regions: " + e.getMessage());
        }

        return centers;
    }

    private List<Location> getProtectionStonesRegions(Player player) {
        List<Location> centers = new ArrayList<>();

        if (!protectionStonesEnabled) {
            return centers;
        }

        try {
            List<String> excludedWorlds = plugin.getConfigManager().getBaseExcludedWorlds();

            for (World world : Bukkit.getWorlds()) {
                if (excludedWorlds.contains(world.getName())) {
                    continue;
                }

                com.sk89q.worldguard.protection.managers.RegionManager wgRegionManager =
                        WorldGuard.getInstance().getPlatform().getRegionContainer()
                                .get(BukkitAdapter.adapt(world));

                if (wgRegionManager == null) {
                    continue;
                }

                for (ProtectedRegion region : wgRegionManager.getRegions().values()) {
                    if (region.getId().startsWith("ps") &&
                            (region.getOwners().size() > 0 || region.getMembers().size() > 0)) {

                        com.sk89q.worldedit.math.BlockVector3 min = region.getMinimumPoint();
                        com.sk89q.worldedit.math.BlockVector3 max = region.getMaximumPoint();
                        com.sk89q.worldedit.math.BlockVector3 center = min.add(max).divide(2);

                        Location location = new Location(world, center.getX(),
                                world.getHighestBlockYAt((int)center.getX(), (int)center.getZ()), center.getZ());
                        centers.add(location);
                    }
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Error getting ProtectionStones regions via WorldGuard: " + e.getMessage());
        }

        return centers;
    }
}
