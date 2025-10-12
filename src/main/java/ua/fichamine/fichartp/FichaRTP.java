package ua.fichamine.fichartp;

import org.bukkit.plugin.java.JavaPlugin;
import ua.fichamine.fichartp.commands.RtpCommand;
import ua.fichamine.fichartp.config.ConfigManager;
import ua.fichamine.fichartp.listeners.PlayerMoveListener;
import ua.fichamine.fichartp.managers.CooldownManager;
import ua.fichamine.fichartp.managers.RegionManager;
import ua.fichamine.fichartp.managers.TeleportManager;

public class FichaRTP extends JavaPlugin {

    private static FichaRTP instance;
    private ConfigManager configManager;
    private TeleportManager teleportManager;
    private CooldownManager cooldownManager;
    private RegionManager regionManager;

    @Override
    public void onEnable() {
        instance = this;

        this.configManager = new ConfigManager(this);
        this.cooldownManager = new CooldownManager();
        this.regionManager = new RegionManager(this);
        this.teleportManager = new TeleportManager(this);

        getCommand("rtp").setExecutor(new RtpCommand(this));
        getCommand("rtp").setTabCompleter(new RtpCommand(this));
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);

        getLogger().info("FichaRTP включен");
    }

    @Override
    public void onDisable() {
        getLogger().info("FichaRTP выключен");
    }

    public static FichaRTP getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public RegionManager getRegionManager() {
        return regionManager;
    }
}