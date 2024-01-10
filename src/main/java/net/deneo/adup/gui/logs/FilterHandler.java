package net.deneo.adup.gui.logs;

import lombok.Getter;
import lombok.Setter;
import net.deneo.adup.data.Log;
import net.deneo.adup.database.tables.GlobalTables;
import net.deneo.adup.utility.LogsUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class FilterHandler {
    private static final HashMap<UUID, Filter> filters = new HashMap<>();

    public static List<Log> loadItems(UUID uuid, Log.Type type) {
        UUID target = LogsUtil.getTarget(uuid);
        List<Log> logs = GlobalTables.logsTable.getLogsNoCache(target);

        for (Log log : GlobalTables.logsTable.getLogs(target)) {
            if (!logs.contains(log)) {
                logs.add(log);
            }
        }

        logs.removeIf((log) -> log.type != type || (getIsOnlyTrackedItems(uuid) && log.trackedName.isEmpty()) || (getIsOnlyUntrackedItems(uuid) && !log.trackedName.isEmpty()) || (getIsOnlyRemovedItems(uuid) && !log.taken) || (getIsOnlyAddedItems(uuid) && log.taken));
        Collections.reverse(logs);
        return logs;
    }

    public static void setIsOnlyAddedItems(UUID uuid, boolean oai) {
        Filter filter = filters.get(uuid);

        if (filter == null) {
            filters.put(uuid, new Filter(false, oai, false, false));
            return;
        }

        filter.setOnlyAddedItems(oai);
        filters.put(uuid, filter);
    }

    public static boolean getIsOnlyAddedItems(UUID uuid) {
        Filter filter = filters.get(uuid);
        return filter != null && filter.isOnlyAddedItems;
    }

    public static void setIsOnlyRemovedItems(UUID uuid, boolean ori) {
        Filter filter = filters.get(uuid);

        if (filter == null) {
            filters.put(uuid, new Filter(false, false, false, ori));
            return;
        }

        filter.setOnlyRemovedItems(ori);
        filters.put(uuid, filter);
    }

    public static boolean getIsOnlyRemovedItems(UUID uuid) {
        Filter filter = filters.get(uuid);
        return filter != null && filter.isOnlyRemovedItems;
    }

    public static void setIsOnlyUntrackedItems(UUID uuid, boolean oui) {
        Filter filter = filters.get(uuid);

        if (filter == null) {
            filters.put(uuid, new Filter(false, false, oui, false));
            return;
        }

        filter.setOnlyUntrackedItems(oui);
        filters.put(uuid, filter);
    }

    public static boolean getIsOnlyUntrackedItems(UUID uuid) {
        Filter filter = filters.get(uuid);
        return filter != null && filter.isOnlyUntrackedItems;
    }

    public static void setIsOnlyTrackedItems(UUID uuid, boolean oti) {
        Filter filter = filters.get(uuid);

        if (filter == null) {
            filters.put(uuid, new Filter(oti, false, false, false));
            return;
        }

        filter.setOnlyTrackedItems(oti);
        filters.put(uuid, filter);
    }

    public static boolean getIsOnlyTrackedItems(UUID uuid) {
        Filter filter = filters.get(uuid);
        return filter != null && filter.isOnlyTrackedItems;
    }

    private static class Filter {
        @Setter
        @Getter
        private boolean isOnlyRemovedItems;

        @Setter
        @Getter
        private boolean isOnlyAddedItems;

        @Setter
        @Getter
        private boolean isOnlyTrackedItems;

        @Setter
        @Getter
        private boolean isOnlyUntrackedItems;

        public Filter(boolean oti, boolean oai, boolean oui, boolean ori) {
            this.isOnlyRemovedItems = ori;
            this.isOnlyTrackedItems = oti;
            this.isOnlyAddedItems = oai;
            this.isOnlyUntrackedItems = oui;
        }
    }
}
