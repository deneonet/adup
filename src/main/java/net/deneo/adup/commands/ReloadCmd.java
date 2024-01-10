package net.deneo.adup.commands;

import net.deneo.adup.Adup;
import net.deneo.adup.utility.ConfigUtil;
import net.deneo.adup.utility.MessagesUtil;
import net.deneo.adup.utility.PermissionsUtil;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class ReloadCmd {
    public static void cmd(ConsoleCommandSender console, Adup plugin) {
        reload(plugin, null, console);
    }

    public static void cmd(Player player, Adup plugin) {
        if (PermissionsUtil.hasReloadCmdPermission(player)) {
            reload(plugin, player, null);
            return;
        }

        MessagesUtil.sendInsufficientPermissions(player, "/adup reload");
    }

    public static void reload(Adup plugin, Player player, ConsoleCommandSender console) {
        plugin.reloadConfig();
        ConfigUtil.refresh();

        if (console == null) {
            player.sendMessage(Adup.prefix + "Reloaded!");
            return;
        }

        Adup.log("§7Reloaded!");
    }
}
