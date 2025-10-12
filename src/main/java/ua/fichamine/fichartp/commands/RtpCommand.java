package ua.fichamine.fichartp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ua.fichamine.fichartp.FichaRTP;
import ua.fichamine.fichartp.utils.ColorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RtpCommand implements CommandExecutor, TabCompleter {

    private final FichaRTP plugin;
    private final List<String> subCommands = Arrays.asList("default", "long", "player", "base");

    public RtpCommand(FichaRTP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("это консоль");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("rtp.use")) {
            player.sendMessage(ColorUtils.colorize(plugin.getConfigManager().getNoPermissionMessage()));
            return true;
        }

        if (!isWorldAllowed(player)) {
            player.sendMessage(ColorUtils.colorize(plugin.getConfigManager().getWorldNotAllowedMessage()));
            return true;
        }

        String type = "default";
        if (args.length > 0) {
            String arg = args[0].toLowerCase();
            if (subCommands.contains(arg)) {
                type = arg;
            }
        }

        return executeRtp(player, type);
    }

    private boolean executeRtp(Player player, String type) {
        switch (type) {
            case "default":
                return executeDefaultRtp(player);

            case "long":
                return executeLongRtp(player);

            case "player":
                return executePlayerRtp(player);

            case "base":
                return executeBaseRtp(player);

            default:
                return executeDefaultRtp(player);
        }
    }

    private boolean executeDefaultRtp(Player player) {
        if (!plugin.getConfigManager().isDefaultEnabled()) {
            player.sendMessage(ColorUtils.colorize("&cDefault RTP is disabled."));
            return true;
        }

        if (!player.hasPermission("rtp.bypass.cooldown") &&
                plugin.getCooldownManager().hasCooldown(player, "default")) {
            long remaining = plugin.getCooldownManager().getRemainingCooldown(player, "default");
            String message = plugin.getConfigManager().getCooldownDefaultMessage()
                    .replace("%time%", String.valueOf(remaining));
            player.sendMessage(ColorUtils.colorize(message));
            return true;
        }

        plugin.getTeleportManager().teleportDefault(player);
        return true;
    }

    private boolean executeLongRtp(Player player) {
        if (!plugin.getConfigManager().isLongEnabled()) {
            player.sendMessage(ColorUtils.colorize("&cLong RTP is disabled."));
            return true;
        }

        if (!player.hasPermission("rtp.long")) {
            player.sendMessage(ColorUtils.colorize(plugin.getConfigManager().getNoPermissionMessage()));
            return true;
        }

        if (!player.hasPermission("rtp.bypass.cooldown") &&
                plugin.getCooldownManager().hasCooldown(player, "long")) {
            long remaining = plugin.getCooldownManager().getRemainingCooldown(player, "long");
            String message = plugin.getConfigManager().getCooldownCustomMessage()
                    .replace("%time%", String.valueOf(remaining));
            player.sendMessage(ColorUtils.colorize(message));
            return true;
        }

        plugin.getTeleportManager().teleportLong(player);
        return true;
    }

    private boolean executePlayerRtp(Player player) {
        if (!plugin.getConfigManager().isPlayerEnabled()) {
            player.sendMessage(ColorUtils.colorize("&cPlayer RTP is disabled."));
            return true;
        }

        if (!player.hasPermission("rtp.player")) {
            player.sendMessage(ColorUtils.colorize(plugin.getConfigManager().getNoPermissionMessage()));
            return true;
        }

        if (!player.hasPermission("rtp.bypass.cooldown") &&
                plugin.getCooldownManager().hasCooldown(player, "player")) {
            long remaining = plugin.getCooldownManager().getRemainingCooldown(player, "player");
            String message = plugin.getConfigManager().getCooldownCustomMessage()
                    .replace("%time%", String.valueOf(remaining));
            player.sendMessage(ColorUtils.colorize(message));
            return true;
        }

        plugin.getTeleportManager().teleportToPlayer(player);
        return true;
    }

    private boolean executeBaseRtp(Player player) {
        if (!plugin.getConfigManager().isBaseEnabled()) {
            player.sendMessage(ColorUtils.colorize("&cBase RTP is disabled."));
            return true;
        }

        if (!player.hasPermission("rtp.base")) {
            player.sendMessage(ColorUtils.colorize(plugin.getConfigManager().getNoPermissionMessage()));
            return true;
        }

        if (!player.hasPermission("rtp.bypass.cooldown") &&
                plugin.getCooldownManager().hasCooldown(player, "base")) {
            long remaining = plugin.getCooldownManager().getRemainingCooldown(player, "base");
            String message = plugin.getConfigManager().getCooldownCustomMessage()
                    .replace("%time%", String.valueOf(remaining));
            player.sendMessage(ColorUtils.colorize(message));
            return true;
        }

        plugin.getTeleportManager().teleportToBase(player);
        return true;
    }

    private boolean isWorldAllowed(Player player) {
        List<String> allowedWorlds = plugin.getConfigManager().getAllowedWorlds();
        return allowedWorlds.contains(player.getWorld().getName());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return new ArrayList<>();
        }

        Player player = (Player) sender;
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String partial = args[0].toLowerCase();

            if ("default".startsWith(partial)) {
                completions.add("default");
            }

            if ("long".startsWith(partial) && player.hasPermission("rtp.long")) {
                completions.add("long");
            }

            if ("player".startsWith(partial) && player.hasPermission("rtp.player")) {
                completions.add("player");
            }

            if ("base".startsWith(partial) && player.hasPermission("rtp.base")) {
                completions.add("base");
            }
        }

        return completions;
    }
}