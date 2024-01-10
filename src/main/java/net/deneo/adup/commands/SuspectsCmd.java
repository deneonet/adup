package net.deneo.adup.commands;

import net.deneo.adup.gui.suspects.SuspectsMenu;
import net.deneo.adup.utility.MessagesUtil;
import net.deneo.adup.utility.PermissionsUtil;
import org.bukkit.entity.Player;

public class SuspectsCmd {
    public static void cmd(Player player) {
        if (PermissionsUtil.hasSuspectsCmdPermission(player)) {
            new SuspectsMenu(player).displayTo(player);
            return;
        }

        MessagesUtil.sendInsufficientPermissions(player, "/adup suspects");
    }
}
