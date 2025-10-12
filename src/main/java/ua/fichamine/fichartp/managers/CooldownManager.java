package ua.fichamine.fichartp.managers;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    public void setCooldown(Player player, String type, int seconds) {
        UUID playerId = player.getUniqueId();
        cooldowns.computeIfAbsent(playerId, k -> new HashMap<>());
        cooldowns.get(playerId).put(type, System.currentTimeMillis() + (seconds * 1000L));
    }

    public boolean hasCooldown(Player player, String type) {
        UUID playerId = player.getUniqueId();
        if (!cooldowns.containsKey(playerId) || !cooldowns.get(playerId).containsKey(type)) {
            return false;
        }

        long expireTime = cooldowns.get(playerId).get(type);
        if (System.currentTimeMillis() >= expireTime) {
            cooldowns.get(playerId).remove(type);
            if (cooldowns.get(playerId).isEmpty()) {
                cooldowns.remove(playerId);
            }
            return false;
        }

        return true;
    }

    public long getRemainingCooldown(Player player, String type) {
        UUID playerId = player.getUniqueId();
        if (!cooldowns.containsKey(playerId) || !cooldowns.get(playerId).containsKey(type)) {
            return 0;
        }

        long expireTime = cooldowns.get(playerId).get(type);
        long remaining = expireTime - System.currentTimeMillis();
        return Math.max(0, remaining / 1000);
    }

    public void removeCooldown(Player player, String type) {
        UUID playerId = player.getUniqueId();
        if (cooldowns.containsKey(playerId)) {
            cooldowns.get(playerId).remove(type);
            if (cooldowns.get(playerId).isEmpty()) {
                cooldowns.remove(playerId);
            }
        }
    }
}