package ua.fichamine.fichartp.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import ua.fichamine.fichartp.FichaRTP;

import java.util.List;
import java.util.Random;

public class LocationUtils {

    private static final Random random = new Random();

    public static Location getRandomLocation(World world, int minRadius, int maxRadius) {
        int attempts = 0;
        int maxAttempts = 50;

        while (attempts < maxAttempts) {
            attempts++;

            int x = random.nextInt(maxRadius - minRadius) + minRadius;
            int z = random.nextInt(maxRadius - minRadius) + minRadius;

            if (random.nextBoolean()) x = -x;
            if (random.nextBoolean()) z = -z;

            if (!isWithinWorldBorder(world, x, z)) {
                continue;
            }

            int y = world.getHighestBlockYAt(x, z);
            Location location = new Location(world, x + 0.5, y + 1, z + 0.5);

            if (isSafeLocation(location)) {
                return location;
            }
        }

        return world.getSpawnLocation();
    }

    public static boolean isSafeLocation(Location location) {
        if (location == null || location.getWorld() == null) {
            return false;
        }

        Block block = location.getBlock();
        Block below = block.getRelative(0, -1, 0);
        Block above = block.getRelative(0, 1, 0);

        if (!block.getType().equals(Material.AIR) || !above.getType().equals(Material.AIR)) {
            return false;
        }

        if (!below.getType().isSolid()) {
            return false;
        }

        List<String> unsafeBlocks = FichaRTP.getInstance().getConfigManager().getUnsafeBlocks();

        if (unsafeBlocks.contains(below.getType().name()) ||
                unsafeBlocks.contains(block.getType().name()) ||
                unsafeBlocks.contains(above.getType().name())) {
            return false;
        }

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Block surrounding = block.getRelative(x, 0, z);
                if (unsafeBlocks.contains(surrounding.getType().name())) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean isWithinWorldBorder(World world, int x, int z) {
        org.bukkit.WorldBorder border = world.getWorldBorder();
        Location center = border.getCenter();
        double size = border.getSize() / 2;

        return Math.abs(x - center.getX()) <= size && Math.abs(z - center.getZ()) <= size;
    }

    public static Location getRandomLocationAroundPoint(Location center, int minRadius, int maxRadius) {
        int attempts = 0;
        int maxAttempts = 30;

        while (attempts < maxAttempts) {
            attempts++;

            double angle = random.nextDouble() * 2 * Math.PI;
            int radius = random.nextInt(maxRadius - minRadius) + minRadius;

            int x = (int) (center.getX() + radius * Math.cos(angle));
            int z = (int) (center.getZ() + radius * Math.sin(angle));

            if (!isWithinWorldBorder(center.getWorld(), x, z)) {
                continue;
            }

            int y = center.getWorld().getHighestBlockYAt(x, z);
            Location location = new Location(center.getWorld(), x + 0.5, y + 1, z + 0.5);

            if (isSafeLocation(location)) {
                return location;
            }
        }

        return center;
    }
}