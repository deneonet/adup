package net.deneo.adup.commands;

import net.deneo.adup.gui.bans.BansMenu;
import net.deneo.adup.utility.MessagesUtil;
import net.deneo.adup.utility.PermissionsUtil;
import org.bukkit.entity.Player;

public class BansCmd {
    public static void cmd(Player player) {
        if (PermissionsUtil.hasBansCmdPermission(player)) {
            new BansMenu(player).displayTo(player);
            return;
        }

        MessagesUtil.sendInsufficientPermissions(player, "/adup bans");
    }
}
