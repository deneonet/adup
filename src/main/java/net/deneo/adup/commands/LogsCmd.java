package net.deneo.adup.commands;

import net.deneo.adup.gui.logs.LogsMenu;
import net.deneo.adup.utility.LogsUtil;
import net.deneo.adup.utility.MessagesUtil;
import net.deneo.adup.utility.PermissionsUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class LogsCmd {
    public static void cmd(Player player, String[] args) {
        if (PermissionsUtil.hasLogsCmdPermission(player)) {
            if (args.length == 2) {
                String target = args[1];

                /*
                  todo: find a better solution here

                  Issue(s):
                    1. getOfflinePlayer is deprecated and slow
                  Solution(s):
                    1. storing the name + uuid -
                        checking then on every login if the name matches the stored uuid
                */
                OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(target);

                LogsUtil.setTarget(player.getUniqueId(), targetPlayer.getUniqueId());
                new LogsMenu().displayTo(player);

                return;
            }

            MessagesUtil.sendMissingTargetArgument(player, "/adup logs <player>");
            return;
        }

        MessagesUtil.sendInsufficientPermissions(player, "/adup logs");
    }
}
