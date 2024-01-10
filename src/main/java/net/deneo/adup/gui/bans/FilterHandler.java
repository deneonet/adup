package net.deneo.adup.gui.bans;

import lombok.Getter;
import lombok.Setter;
import net.deneo.adup.data.AdupPlayer;
import net.deneo.adup.database.tables.GlobalTables;
import net.deneo.adup.utility.TimeUtil;

import java.util.*;

public class FilterHandler {
    private static final HashMap<UUID, FilterLogs> filters = new HashMap<>();

    public static List<AdupPlayer> loadBans(UUID uuid) {
        List<AdupPlayer> players = new ArrayList<>(GlobalTables.playersTable.getBannedPlayers());
        String timeSpan = getTimeSpan(uuid);

        if (!timeSpan.isEmpty()) {
            players.removeIf((player) -> {
                Date currentDate = new Date();

                String[] timeSpanSplit = timeSpan.split(":");
                String timeSpanStart = timeSpanSplit[0];
                String timeSpanEnd = timeSpanSplit[1];

                int parsedTime = TimeUtil.parseTime(timeSpanStart);
                Date start = new Date(player.banDate.getTime() + TimeUtil.getTimeUnit(timeSpanStart).getTime(parsedTime));

                parsedTime = TimeUtil.parseTime(timeSpanEnd);
                Date end = new Date(player.banDate.getTime() + TimeUtil.getTimeUnit(timeSpanEnd).getTime(parsedTime));

                return !currentDate.after(start) || !currentDate.before(end);
            });
        }

        Collections.reverse(players);

        return players;
    }

    public static void setTimeSpan(UUID uuid, String timeSpan) {
        FilterLogs filterLogs = filters.get(uuid);

        if (filterLogs == null) {
            filters.put(uuid, new FilterLogs(timeSpan));
            return;
        }

        filterLogs.setTimeSpan(timeSpan);
        filters.put(uuid, filterLogs);
    }

    public static String getTimeSpan(UUID uuid) {
        FilterLogs filterLogs = filters.get(uuid);
        return filterLogs == null ? "" : filterLogs.timeSpan;
    }

    private static class FilterLogs {
        @Setter
        @Getter
        private String timeSpan;

        public FilterLogs(String timeSpan) {
            this.timeSpan = timeSpan;
        }
    }
}
