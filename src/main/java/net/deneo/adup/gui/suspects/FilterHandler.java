package net.deneo.adup.gui.suspects;

import lombok.Getter;
import lombok.Setter;
import net.deneo.adup.data.AdupPlayer;
import net.deneo.adup.database.tables.GlobalTables;
import net.deneo.adup.utility.TimeUtil;

import java.util.*;

public class FilterHandler {
    private static final HashMap<UUID, Filter> filters = new HashMap<>();

    public static List<AdupPlayer> loadSuspects(UUID uuid) {
        List<AdupPlayer> suspects = GlobalTables.playersTable.getSuspectedPlayers();
        String timeSpan = getTimeSpan(uuid);

        if (!timeSpan.isEmpty()) {
            suspects.removeIf((suspect) -> {
                Date currentDate = new Date();

                String[] timeSpanSplit = timeSpan.split(":");
                String timeSpanStart = timeSpanSplit[0];
                String timeSpanEnd = timeSpanSplit[1];

                int parsedTime = TimeUtil.parseTime(timeSpanStart);
                Date start = new Date(suspect.suspectedDate.getTime() + TimeUtil.getTimeUnit(timeSpanStart).getTime(parsedTime));

                parsedTime = TimeUtil.parseTime(timeSpanEnd);
                Date end = new Date(suspect.suspectedDate.getTime() + TimeUtil.getTimeUnit(timeSpanEnd).getTime(parsedTime));


                return !currentDate.after(start) || !currentDate.before(end);
            });
        }

        Collections.reverse(suspects);

        return suspects;
    }

    public static void setTimeSpan(UUID uuid, String timeSpan) {
        Filter filter = filters.get(uuid);

        if (filter == null) {
            filters.put(uuid, new Filter(timeSpan));
            return;
        }

        filter.setTimeSpan(timeSpan);
        filters.put(uuid, filter);
    }

    public static String getTimeSpan(UUID uuid) {
        Filter filter = filters.get(uuid);
        return filter == null ? "" : filter.timeSpan;
    }

    private static class Filter {
        @Setter
        @Getter
        private String timeSpan;

        public Filter(String timeSpan) {
            this.timeSpan = timeSpan;
        }
    }
}
