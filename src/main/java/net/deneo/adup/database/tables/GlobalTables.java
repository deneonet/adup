package net.deneo.adup.database.tables;

import net.deneo.adup.Adup;
import net.deneo.adup.utility.ConfigUtil;
import org.bukkit.Bukkit;

public class GlobalTables {
    public static LogsTable logsTable;
    public static PlayersTable playersTable;
    public static ItemsTable itemsTable;

    public static void start() {
        playersTable = new PlayersTable();
        logsTable = new LogsTable();
        itemsTable = new ItemsTable();

        playersTable.load();
        logsTable.load();
        itemsTable.load();

        long interval = 20L * 60L * ConfigUtil.getInt("auto_save_data_interval");

        Bukkit.getScheduler().runTaskTimerAsynchronously(Adup.getInstance(), () -> {
            logsTable.save();
            playersTable.save();
            itemsTable.save();
        }, interval, interval);
    }
}
