package net.deneo.adup.utility;

import net.deneo.adup.Adup;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.UUID;

public class LogsUtil {
    private static final HashMap<UUID, UUID> targets = new HashMap<>();

    public static String getFormat(String name, Material type, int amount) {
        return ConfigUtil.getString(
                        name.isEmpty() ? "tracking.item_format_no_name" : "tracking.item_format"
                )
                .replace("{tracked_name}", name)
                .replace("{item_type}", "" + type)
                .replace("{item_amount}", "" + amount);
    }

    public static void setTarget(UUID uuid, UUID target) {
        targets.put(uuid, target);
    }

    public static UUID getTarget(UUID uuid) {
        if (!targets.containsKey(uuid)) {
            Adup.error("REPORT -> Target with UUID " + uuid + " doesn't exists");
        }
        return targets.get(uuid);
    }
}
