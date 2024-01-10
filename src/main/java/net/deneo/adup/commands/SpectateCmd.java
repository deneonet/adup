package net.deneo.adup.commands;

import net.deneo.adup.utility.MessagesUtil;
import net.deneo.adup.utility.PermissionsUtil;
import net.deneo.adup.utility.SpectateUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SpectateCmd {
    public static void cmd(Player player, String[] args) {
        if (PermissionsUtil.hasSpectateCmdPermission(player)) {
            if (args.length == 2) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    MessagesUtil.sendPlayerNotOnline(player);
                    return;
                }

                if (SpectateUtil.getSpectatingPlayers().contains(player.getUniqueId())) {
                    SpectateUtil.unSpectatePlayer(player, target);
                    return;
                }

                SpectateUtil.spectatePlayer(player, target);
                return;
            }

            MessagesUtil.sendMissingTargetArgument(player, "/adup spectate <player>");
            return;
        }

        MessagesUtil.sendInsufficientPermissions(player, "/adup spectate <player>");
    }
}
