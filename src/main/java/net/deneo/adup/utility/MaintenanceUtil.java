package net.deneo.adup.utility;

import net.deneo.adup.data.AdupPlayer;
import net.deneo.adup.database.tables.GlobalTables;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MaintenanceUtil {
    public static void toggleWhitelist() {
        boolean newMaintenanceStatus = !ConfigUtil.getBoolean("maintenance.enabled");
        ConfigUtil.setValue("maintenance.enabled", newMaintenanceStatus);

        if (newMaintenanceStatus) {
            String maintenanceKickReason = ConfigUtil.getString("maintenance.kick_reason");

            for (AdupPlayer adupPlayer : GlobalTables.playersTable.getNotWhitelistedPlayers()) {
                Player player = Bukkit.getPlayer(adupPlayer.uuid);
                if (player == null || PermissionsUtil.hasMaintenanceBypassPerm(player)) {
                    continue;
                }

                player.kickPlayer(maintenanceKickReason);
            }
        }
    }
}
