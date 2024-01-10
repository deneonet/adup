package net.deneo.adup.listener.server;

import net.deneo.adup.data.AdupPlayer;
import net.deneo.adup.database.tables.GlobalTables;
import net.deneo.adup.utility.BansUtil;
import net.deneo.adup.utility.ConfigUtil;
import net.deneo.adup.utility.PermissionsUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerLoginEvent implements Listener {
    @EventHandler
    public void onPlayerLogin(org.bukkit.event.player.PlayerLoginEvent e) {
        final Player player = e.getPlayer();
        final AdupPlayer adupPlayer = GlobalTables.playersTable.getOrInsertPlayer(player.getUniqueId());

        if (adupPlayer.isBanned) {
            if (adupPlayer.unbanDate != null && !BansUtil.shouldBeUnbanned(adupPlayer.unbanDate) && !PermissionsUtil.hasBannedBypassPerm(player)) {
                String banReason = ConfigUtil.getString("tracking.auto_ban.ban_reason");
                String banDuration = ConfigUtil.getString("tracking.auto_ban.ban_duration");

                e.setKickMessage(banReason.replace("{duration}", banDuration));
                e.setResult(org.bukkit.event.player.PlayerLoginEvent.Result.KICK_OTHER);

                return;
            }

            GlobalTables.playersTable.resetBan(adupPlayer);
        }

        boolean maintenance = ConfigUtil.getBoolean("maintenance.enabled");
        if (maintenance && !PermissionsUtil.hasMaintenanceBypassPerm(player) && !adupPlayer.isWhitelisted) {
            e.setKickMessage(ConfigUtil.getString("maintenance.kick_reason"));
            e.setResult(org.bukkit.event.player.PlayerLoginEvent.Result.KICK_OTHER);
        }
    }
}
