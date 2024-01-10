package net.deneo.adup.commands;

import net.deneo.adup.gui.mce.MaintenanceMenu;
import net.deneo.adup.utility.MaintenanceUtil;
import net.deneo.adup.utility.MessagesUtil;
import net.deneo.adup.utility.PermissionsUtil;
import org.bukkit.entity.Player;

public class MaintenanceCmd {
    public static void cmd() {
        MaintenanceUtil.toggleWhitelist();
    }

    public static void cmd(Player player) {
        if (PermissionsUtil.hasMaintenanceCmdPermission(player)) {
            new MaintenanceMenu().displayTo(player);
            return;
        }

        MessagesUtil.sendInsufficientPermissions(player, "/adup mce");
    }
}
