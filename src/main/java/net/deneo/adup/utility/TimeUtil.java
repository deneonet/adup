package net.deneo.adup.utility;

import net.deneo.adup.Adup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {
    public static int parseTime(String input) {
        return Integer.parseInt(input.replaceAll("\\D", ""));
    }

    public static TimeUnit getTimeUnit(String in) {
        if (in.contains("m")) {
            return TimeUnit.MINUTE;
        }
        if (in.contains("h")) {
            return TimeUnit.HOUR;
        }
        if (in.contains("d")) {
            return TimeUnit.DAY;
        }
        if (in.contains("w")) {
            return TimeUnit.WEEK;
        }
        if (in.contains("y")) {
            return TimeUnit.YEAR;
        }

        Adup.error("REPORT -> Unknown time unit: " + in);
        return TimeUnit.MINUTE;
    }

    public static String getDateAndTime(Date date) {
        String langTag = ConfigUtil.getString("date_time.lang_tag");
        String pattern = ConfigUtil.getString("date_time.pattern");

        SimpleDateFormat timeZoneDate = new SimpleDateFormat(pattern, Locale.forLanguageTag(langTag));
        return timeZoneDate.format(date);
    }

    public enum TimeUnit {
        MINUTE(60000L),
        HOUR(3600000L),
        DAY(86400000L),
        WEEK(604800000L),
        YEAR(220752000000L);

        private final long time;

        TimeUnit(long time) {
            this.time = time;
        }

        public long getTime(int n) {
            return time * n;
        }
    }
}
