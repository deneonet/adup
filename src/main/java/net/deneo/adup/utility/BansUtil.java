package net.deneo.adup.utility;

import org.bukkit.Bukkit;

import java.util.Date;
import java.util.UUID;

public class BansUtil {
    public static Date calculateUnban() {
        String banDuration = ConfigUtil.getString("tracking.auto_ban.ban_duration");
        long currentMs = new Date().getTime();
        int parsedTime = TimeUtil.parseTime(banDuration);
        currentMs += TimeUtil.getTimeUnit(banDuration).getTime(parsedTime);
        return new Date(currentMs);
    }

    public static boolean shouldBeUnbanned(Date unbanDate) {
        return unbanDate.getTime() <= new Date().getTime();
    }

    public static void kickPlayer(UUID uuid) {
        String banReason = ConfigUtil.getString("tracking.auto_ban.ban_reason");
        String banDuration = ConfigUtil.getString("tracking.auto_ban.ban_duration");
        Bukkit.getPlayer(uuid).kickPlayer(banReason.replace("{duration}", banDuration));
    }
}
