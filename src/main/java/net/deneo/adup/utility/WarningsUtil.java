package net.deneo.adup.utility;

import net.deneo.adup.data.AdupPlayer;
import net.deneo.adup.data.Log;
import net.deneo.adup.database.tables.GlobalTables;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class WarningsUtil {
    public static void warn(UUID uuid, int amount, Log log, String type) {
        int maxWarnings = ConfigUtil.getInt("tracking.auto_ban.max_warnings");
        AdupPlayer player = GlobalTables.playersTable.getPlayer(uuid);
        int warnings = player.warnings + 1;

        MessagesUtil.sendAlertMessage(uuid, amount, warnings, log, type, maxWarnings == 0 || warnings == maxWarnings);

        boolean isAutoBanEnabled = ConfigUtil.getBoolean("tracking.auto_ban.enabled");
        if (isAutoBanEnabled) {
            if (warnings == maxWarnings || maxWarnings == 0) {
                player.isBanned = true;
                player.isSuspected = true;
                player.warnings = 0;
                player.dropMultiplier = 0;
                player.pickUpMultiplier = 0;
                player.moveMultiplier = 0;

                BansUtil.kickPlayer(uuid);
                player.unbanDate = BansUtil.calculateUnban();
                player.banDate = new Date();

                GlobalTables.playersTable.updatePlayer(uuid, player, "SET flags = ?, unban_date = ?, ban_date = ?, warnings = ?, drop_multiplier = ?, pick_up_multiplier = ?, move_multiplier = ?", (statement -> {
                    int combinedFlags = (player.isBanned ? 1 : 0) |
                            (player.isSuspected ? 1 : 0) << 1 |
                            (player.isMarkedAsDuper ? 1 : 0) << 2 |
                            (player.isMarkedAsClear ? 1 : 0) << 3;

                    statement.setInt(1, combinedFlags);

                    Timestamp timestamp = new Timestamp(player.unbanDate.getTime());
                    statement.setTimestamp(2, timestamp);

                    timestamp = new Timestamp(player.banDate.getTime());
                    statement.setTimestamp(3, timestamp);

                    statement.setInt(4, 0);
                    statement.setInt(5, 0);
                    statement.setInt(6, 0);
                    statement.setInt(7, 0);

                    return 8;
                }));

                return;
            }

            player.isSuspected = true;
            player.suspectedDate = new Date();
            player.warnings = warnings;

            GlobalTables.playersTable.updatePlayer(uuid, player, "SET flags = ?, warnings = ?, suspected_date = ?", (statement -> {
                int combinedFlags = (player.isBanned ? 1 : 0) |
                        (player.isSuspected ? 1 : 0) << 1 |
                        (player.isMarkedAsDuper ? 1 : 0) << 2 |
                        (player.isMarkedAsClear ? 1 : 0) << 3;

                statement.setInt(1, combinedFlags);
                statement.setInt(2, warnings);

                Timestamp timestamp = new Timestamp(player.suspectedDate.getTime());
                statement.setTimestamp(3, timestamp);

                return 4;
            }));
        }
    }
}
