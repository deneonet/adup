package net.deneo.adup.database.tables;

import net.deneo.adup.data.AdupPlayer;
import net.deneo.adup.database.Database;
import net.deneo.adup.database.UpdateFunc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class PlayersTable extends Database<AdupPlayer> {
    public PlayersTable() {
        super(true, "players", "unban_date TIMESTAMP, ban_date TIMESTAMP, suspected_date TIMESTAMP, warnings INTEGER NOT NULL, drop_multiplier INTEGER NOT NULL, pick_up_multiplier INTEGER NOT NULL, move_multiplier INTEGER NOT NULL, flags INTEGER NOT NULL");
    }

    public void addPlayer(UUID uuid, AdupPlayer player) {
        insert(uuid.toString(), player, "unban_date, ban_date, suspected_date, warnings, drop_multiplier, pick_up_multiplier, move_multiplier, flags ; VALUES | ?, ?, ?, ?, ?, ?, ?, ? ; ");
    }

    public AdupPlayer getOrInsertPlayer(UUID uuid) {
        AdupPlayer player = getPlayer(uuid);
        if (player == null) {
            player = new AdupPlayer(uuid);
            addPlayer(uuid, player);
        }
        return player;
    }

    public List<AdupPlayer> getSuspectedPlayers() {
        List<AdupPlayer> ret = new ArrayList<>(cSelect());
        ret.removeIf((adupPlayer -> !adupPlayer.isSuspected));
        return ret;
    }

    public List<AdupPlayer> getBannedPlayers() {
        List<AdupPlayer> ret = new ArrayList<>(cSelect());
        ret.removeIf((adupPlayer -> !adupPlayer.isBanned));
        return ret;
    }

    public List<AdupPlayer> getNotWhitelistedPlayers() {
        List<AdupPlayer> ret = new ArrayList<>(cSelect());
        ret.removeIf((adupPlayer -> adupPlayer.isWhitelisted));
        return ret;
    }

    public AdupPlayer getPlayer(UUID uuid) {
        return sSelect(uuid.toString());
    }

    public void resetBan(AdupPlayer adupPlayer) {
        adupPlayer.isBanned = false;
        adupPlayer.unbanDate = null;
        adupPlayer.banDate = null;

        GlobalTables.playersTable.updatePlayer(adupPlayer.uuid, adupPlayer, "SET flags = ?, unban_date = ?, ban_date = ?", (statement -> {
            int combinedFlags = (adupPlayer.isBanned ? 1 : 0) |
                    (adupPlayer.isSuspected ? 1 : 0) << 1 |
                    (adupPlayer.isMarkedAsDuper ? 1 : 0) << 2 |
                    (adupPlayer.isMarkedAsClear ? 1 : 0) << 3 |
                    (adupPlayer.isWhitelisted ? 1 : 0) << 4;
            statement.setInt(1, combinedFlags);
            statement.setTimestamp(2, null);
            statement.setTimestamp(3, null);
            return 4;
        }));
    }

    public void updateFlags(AdupPlayer player) {
        GlobalTables.playersTable.updatePlayer(player.uuid, player, "SET flags = ?", (statement -> {
            int combinedFlags = (player.isBanned ? 1 : 0) |
                    (player.isSuspected ? 1 : 0) << 1 |
                    (player.isMarkedAsDuper ? 1 : 0) << 2 |
                    (player.isMarkedAsClear ? 1 : 0) << 3 |
                    (player.isWhitelisted ? 1 : 0) << 4;
            statement.setInt(1, combinedFlags);
            return 2;
        }));
    }

    public void deletePlayer(UUID uuid) {
        delete(uuid.toString(), "", (statement -> {
        }), (val -> true));
    }

    public void updatePlayer(UUID uuid, AdupPlayer val, String query, UpdateFunc func) {
        update(uuid.toString(), val, query, func);
    }

    @Override
    protected void insertV(AdupPlayer val, PreparedStatement statement) throws SQLException {
        statement.setTimestamp(2, null);
        if (val.unbanDate != null) {
            Timestamp dateToTimestamp = new Timestamp(val.unbanDate.getTime());
            statement.setTimestamp(2, dateToTimestamp);
        }

        statement.setTimestamp(3, null);
        if (val.banDate != null) {
            Timestamp dateToTimestamp = new Timestamp(val.banDate.getTime());
            statement.setTimestamp(3, dateToTimestamp);
        }

        statement.setTimestamp(4, null);
        if (val.suspectedDate != null) {
            Timestamp dateToTimestamp = new Timestamp(val.suspectedDate.getTime());
            statement.setTimestamp(4, dateToTimestamp);
        }

        statement.setInt(5, val.warnings);
        statement.setInt(6, val.dropMultiplier);
        statement.setInt(7, val.pickUpMultiplier);
        statement.setInt(8, val.moveMultiplier);

        int combinedFlags = (val.isBanned ? 1 : 0) |
                (val.isSuspected ? 1 : 0) << 1 |
                (val.isMarkedAsDuper ? 1 : 0) << 2 |
                (val.isMarkedAsClear ? 1 : 0) << 3 |
                (val.isWhitelisted ? 1 : 0) << 4;
        statement.setInt(9, combinedFlags);
    }

    @Override
    protected AdupPlayer sSelectV(ResultSet resultSet) throws SQLException {
        UUID uuid = UUID.fromString(resultSet.getString(1));
        Timestamp timestamp = resultSet.getTimestamp(2);
        Date unbanDate = null;

        if (timestamp != null) {
            long time = timestamp.getTime();
            unbanDate = new Date(time);
        }

        timestamp = resultSet.getTimestamp(3);
        Date banDate = null;

        if (timestamp != null) {
            long time = timestamp.getTime();
            banDate = new Date(time);
        }

        timestamp = resultSet.getTimestamp(4);
        Date suspectedDate = null;

        if (timestamp != null) {
            long time = timestamp.getTime();
            suspectedDate = new Date(time);
        }

        int warnings = resultSet.getInt(5);
        int dropMultiplier = resultSet.getInt(6);
        int pickUpMultiplier = resultSet.getInt(7);
        int moveMultiplier = resultSet.getInt(8);
        int flags = resultSet.getInt(9);
        boolean isBanned = ((flags) & 1) == 1;
        boolean isSuspected = ((flags >> 1) & 1) == 1;
        boolean isMarkedAsDuper = ((flags >> 2) & 1) == 1;
        boolean isMarkedAsClear = ((flags >> 3) & 1) == 1;
        boolean isWhitelisted = ((flags >> 4) & 1) == 1;

        return new AdupPlayer(
                uuid,
                isBanned,
                isSuspected,
                isMarkedAsDuper,
                isMarkedAsClear,
                isWhitelisted,
                banDate,
                unbanDate,
                suspectedDate,
                warnings,
                dropMultiplier,
                pickUpMultiplier,
                moveMultiplier
        );
    }
}
