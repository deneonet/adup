package net.deneo.adup.commands;

import net.deneo.adup.data.AdupPlayer;
import net.deneo.adup.database.tables.GlobalTables;
import net.deneo.adup.utility.MessagesUtil;
import net.deneo.adup.utility.PermissionsUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class UnbanCmd {
    public static void cmd(ConsoleCommandSender console, String[] args) {
        if (args.length == 2) {
            unban(args[1]);
            MessagesUtil.sendUnbanComplete(console, args[1]);
            return;
        }

        MessagesUtil.sendInvalidCommand(null, console);
    }

    public static void cmd(Player player, String[] args) {
        if (PermissionsUtil.hasUnbanCmdPermission(player)) {
            if (args.length == 2) {
                unban(args[1]);
                MessagesUtil.sendUnbanComplete(player, args[1]);
                return;
            }

            MessagesUtil.sendMissingTargetArgument(player, "/adup unban <player>");
            return;
        }

        MessagesUtil.sendInsufficientPermissions(player, "/adup unban");
    }

    private static void unban(String name) {
        /*
            todo: find a better solution here

            Issue(s):
              1. getOfflinePlayer is deprecated and slow
            Solution(s):
              1. storing the name + uuid -
                  checking then on every login if the name matches the stored uuid
         */

        OfflinePlayer target = Bukkit.getOfflinePlayer(name);

        AdupPlayer player = GlobalTables.playersTable.getPlayer(target.getUniqueId());
        GlobalTables.playersTable.resetBan(player);
    }
}
