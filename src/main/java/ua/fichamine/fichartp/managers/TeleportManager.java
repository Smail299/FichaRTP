package ua.fichamine.fichartp.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import ua.fichamine.fichartp.FichaRTP;
import ua.fichamine.fichartp.utils.ActionBarUtils;
import ua.fichamine.fichartp.utils.ColorUtils;
import ua.fichamine.fichartp.utils.LocationUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TeleportManager {

    private final FichaRTP plugin;
    private final Map<UUID, BukkitRunnable> teleportTasks = new HashMap<>();
    private final Map<UUID, Location> lastLocations = new HashMap<>();

    public TeleportManager(FichaRTP plugin) {
        this.plugin = plugin;
    }

    public void teleportDefault(Player player) {
        teleportWithDelay(player, "default", plugin.getConfigManager().getDefaultTeleportDelay());
    }

    public void teleportLong(Player player) {
        World targetWorld = Bukkit.getWorld(plugin.getConfigManager().getTargetWorld());
        if (targetWorld == null) {
            return;
        }

        int minRadius = plugin.getConfigManager().getLongMinRadius();
        int maxRadius = plugin.getConfigManager().getLongMaxRadius();
        Location location = LocationUtils.getRandomLocation(targetWorld, minRadius, maxRadius);

        teleportPlayer(player, location);
        plugin.getCooldownManager().setCooldown(player, "long", plugin.getConfigManager().getLongCooldown());
    }

    public void teleportToPlayer(Player player) {
        List<Player> validPlayers = getValidPlayersForTeleport(player);

        if (validPlayers.size() < plugin.getConfigManager().getMinPlayersOnline()) {
            player.sendMessage(ColorUtils.colorize(plugin.getConfigManager().getNotEnoughPlayersMessage()));
            return;
        }

        if (validPlayers.isEmpty()) {
            player.sendMessage(ColorUtils.colorize(plugin.getConfigManager().getNotEnoughPlayersMessage()));
            return;
        }

        Player targetPlayer = validPlayers.get((int) (Math.random() * validPlayers.size()));
        Location targetLocation = targetPlayer.getLocation();

        Location safeLocation = LocationUtils.getRandomLocationAroundPoint(targetLocation, 10, 50);

        teleportPlayer(player, safeLocation);
        plugin.getCooldownManager().setCooldown(player, "player", plugin.getConfigManager().getPlayerCooldown());
    }

    public void teleportToBase(Player player) {
        Location baseLocation = plugin.getRegionManager().getRandomRegionLocation(player);

        if (baseLocation == null) {
            player.sendMessage(ColorUtils.colorize(plugin.getConfigManager().getNoBasesFoundMessage()));
            return;
        }

        teleportPlayer(player, baseLocation);
        plugin.getCooldownManager().setCooldown(player, "base", plugin.getConfigManager().getBaseCooldown());
    }

    private void teleportWithDelay(Player player, String type, int delay) {
        UUID playerId = player.getUniqueId();

        if (shouldBypassDelay(player)) {
            performDefaultTeleport(player, type);
            return;
        }

        cancelTeleport(player);

        lastLocations.put(playerId, player.getLocation().clone());

        BukkitRunnable task = new BukkitRunnable() {
            int timeLeft = delay;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    cleanup(playerId);
                    return;
                }

                if (timeLeft <= 0) {
                    performDefaultTeleport(player, type);
                    cleanup(playerId);
                    cancel();
                    return;
                }

                String message = plugin.getConfigManager().getTeleportingMessage()
                        .replace("%time%", String.valueOf(timeLeft));
                ActionBarUtils.sendActionBar(player, message);

                timeLeft--;
            }
        };

        teleportTasks.put(playerId, task);
        task.runTaskTimer(plugin, 0L, 20L);
    }

    private void performDefaultTeleport(Player player, String type) {
        World targetWorld = Bukkit.getWorld(plugin.getConfigManager().getTargetWorld());
        if (targetWorld == null) {
            return;
        }

        int minRadius = plugin.getConfigManager().getDefaultMinRadius();
        int maxRadius = plugin.getConfigManager().getDefaultMaxRadius();
        Location location = LocationUtils.getRandomLocation(targetWorld, minRadius, maxRadius);

        teleportPlayer(player, location);

        int cooldown = type.equals("default") ?
                plugin.getConfigManager().getDefaultCooldown() :
                plugin.getConfigManager().getDefaultCooldown();
        plugin.getCooldownManager().setCooldown(player, type, cooldown);
    }

    private void teleportPlayer(Player player, Location location) {
        player.teleport(location);
        ActionBarUtils.clearActionBar(player);

        String message = plugin.getConfigManager().getTeleportSuccessMessage()
                .replace("%x%", String.valueOf((int) location.getX()))
                .replace("%y%", String.valueOf((int) location.getY()))
                .replace("%z%", String.valueOf((int) location.getZ()));
        player.sendMessage(ColorUtils.colorize(message));
    }

    public void cancelTeleport(Player player) {
        UUID playerId = player.getUniqueId();

        if (teleportTasks.containsKey(playerId)) {
            teleportTasks.get(playerId).cancel();
            cleanup(playerId);

            player.sendMessage(ColorUtils.colorize(plugin.getConfigManager().getTeleportCancelledMessage()));
        }
    }

    public boolean isTeleporting(Player player) {
        return teleportTasks.containsKey(player.getUniqueId());
    }

    public boolean hasPlayerMoved(Player player) {
        UUID playerId = player.getUniqueId();

        if (!lastLocations.containsKey(playerId)) {
            return false;
        }

        Location lastLoc = lastLocations.get(playerId);
        Location currentLoc = player.getLocation();

        return lastLoc.distanceSquared(currentLoc) > 0.01;
    }

    private boolean shouldBypassDelay(Player player) {
        if (player.hasPermission("rtp.bypass.delay")) {
            return true;
        }

        if (plugin.getConfigManager().isBypassDelayInSpawn()) {
            String spawnWorld = plugin.getConfigManager().getSpawnWorld();
            return player.getWorld().getName().equals(spawnWorld);
        }

        return false;
    }

    private List<Player> getValidPlayersForTeleport(Player excludePlayer) {
        List<Player> validPlayers = new ArrayList<>();
        List<String> excludedWorlds = plugin.getConfigManager().getPlayerExcludedWorlds();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.equals(excludePlayer)) {
                continue;
            }

            if (excludedWorlds.contains(onlinePlayer.getWorld().getName())) {
                continue;
            }

            if (plugin.getConfigManager().isIgnoreVanishedPlayers()) {
                if (onlinePlayer.hasMetadata("vanished") ||
                        !onlinePlayer.canSee(excludePlayer)) {
                    continue;
                }
            }

            if (plugin.getConfigManager().isIgnoreInvisiblePlayers()) {
                if (onlinePlayer.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                    continue;
                }
            }

            validPlayers.add(onlinePlayer);
        }

        return validPlayers;
    }

    private void cleanup(UUID playerId) {
        teleportTasks.remove(playerId);
        lastLocations.remove(playerId);
    }
}