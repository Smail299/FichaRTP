package ua.fichamine.fichartp.config;

import org.bukkit.configuration.file.FileConfiguration;
import ua.fichamine.fichartp.FichaRTP;

import java.util.List;

public class ConfigManager {

    private final FichaRTP plugin;
    private FileConfiguration config;

    public ConfigManager(FichaRTP plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public List<String> getAllowedWorlds() {
        return config.getStringList("settings.allowed-worlds");
    }

    public String getTargetWorld() {
        return config.getString("settings.target-world", "world");
    }

    public String getSpawnWorld() {
        return config.getString("settings.spawn-world", "spawn");
    }

    public boolean isDefaultEnabled() {
        return config.getBoolean("default.enabled", true);
    }

    public int getDefaultTeleportDelay() {
        return config.getInt("default.teleport-delay", 3);
    }

    public int getDefaultCooldown() {
        return config.getInt("default.cooldown", 10);
    }

    public int getDefaultMinRadius() {
        return config.getInt("default.min-radius", 1000);
    }

    public int getDefaultMaxRadius() {
        return config.getInt("default.max-radius", 10000);
    }

    public boolean isBypassDelayInSpawn() {
        return config.getBoolean("default.bypass-delay-in-spawn", true);
    }

    public boolean isLongEnabled() {
        return config.getBoolean("long.enabled", true);
    }

    public int getLongCooldown() {
        return config.getInt("long.cooldown", 45);
    }

    public int getLongMinRadius() {
        return config.getInt("long.min-radius", 15000);
    }

    public int getLongMaxRadius() {
        return config.getInt("long.max-radius", 30000);
    }

    public boolean isPlayerEnabled() {
        return config.getBoolean("player.enabled", true);
    }

    public int getPlayerCooldown() {
        return config.getInt("player.cooldown", 60);
    }

    public int getMinPlayersOnline() {
        return config.getInt("player.min-players-online", 5);
    }

    public boolean isIgnoreVanishedPlayers() {
        return config.getBoolean("player.ignore-vanished-players", true);
    }

    public boolean isIgnoreInvisiblePlayers() {
        return config.getBoolean("player.ignore-invisible-players", true);
    }

    public List<String> getPlayerExcludedWorlds() {
        return config.getStringList("player.excluded-worlds");
    }

    public boolean isBaseEnabled() {
        return config.getBoolean("base.enabled", true);
    }

    public int getBaseCooldown() {
        return config.getInt("base.cooldown", 65);
    }

    public int getBaseMinRadius() {
        return config.getInt("base.min-radius-from-region", 50);
    }

    public int getBaseMaxRadius() {
        return config.getInt("base.max-radius-from-region", 300);
    }

    public boolean isUseProtectionStones() {
        return config.getBoolean("base.use-protectionstones", true);
    }

    public boolean isUseWorldGuard() {
        return config.getBoolean("base.use-worldguard", true);
    }

    public List<String> getBaseExcludedWorlds() {
        return config.getStringList("base.excluded-worlds");
    }

    public List<String> getUnsafeBlocks() {
        return config.getStringList("unsafe-blocks");
    }

    public String getMessage(String path) {
        return config.getString("messages." + path, "");
    }

    public String getTeleportingMessage() {
        return getMessage("teleporting");
    }

    public String getTeleportCancelledMessage() {
        return getMessage("teleport-cancelled");
    }

    public String getTeleportSuccessMessage() {
        return getMessage("teleport-success");
    }

    public String getCooldownDefaultMessage() {
        return getMessage("cooldown-default");
    }

    public String getCooldownCustomMessage() {
        return getMessage("cooldown-custom");
    }

    public String getWorldNotAllowedMessage() {
        return getMessage("world-not-allowed");
    }

    public String getNotEnoughPlayersMessage() {
        return getMessage("not-enough-players");
    }

    public String getNoPermissionMessage() {
        return getMessage("no-permission");
    }

    public String getNoBasesFoundMessage() {
        return getMessage("no-bases-found");
    }
}