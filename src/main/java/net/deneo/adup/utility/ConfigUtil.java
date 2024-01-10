package net.deneo.adup.utility;

import net.deneo.adup.Adup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigUtil {
    private static Adup plugin;
    private static HashMap<String, Object> content;

    public static void init(Adup plugin) {
        ConfigUtil.plugin = plugin;
        content = new HashMap<>();
    }

    public static void refresh() {
        content.clear();
    }

    private static Object getConfigValue(String key) {
        if (!content.containsKey(key)) {
            content.put(key, plugin.getConfig().get(key));
        }
        return content.get(key);
    }

    public static String getString(String key) {
        Object val = getConfigValue(key);
        if (val instanceof String) {
            return (String) val;
        } else {
            Adup.error(key + " is not a string (WRONG CONFIG)");
            return "";
        }
    }

    public static boolean getBoolean(String key) {
        Object val = getConfigValue(key);
        if (val instanceof Boolean) {
            return (boolean) val;
        } else {
            Adup.error(key + " is not a boolean (WRONG CONFIG)");
            return false;
        }
    }

    public static int getInt(String key) {
        Object val = getConfigValue(key);
        if (val instanceof Integer) {
            return (int) val;
        } else {
            Adup.error(key + " is not a int (WRONG CONFIG)");
            return 0;
        }
    }

    public static <K, V> List<Map<K, V>> getMapList(String key) {
        return ((List<Map<K, V>>) getConfigValue(key));
    }

    public static <T> List<T> getList(String key) {
        return ((List<T>) getConfigValue(key));
    }

    public static void setValue(String key, Object value) {
        plugin.getConfig().set(key, value);
        plugin.saveConfig();

        content.put(key, value);
    }
}
