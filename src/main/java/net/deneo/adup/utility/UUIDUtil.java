package net.deneo.adup.utility;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UUIDUtil {
    public static String getName(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            return Bukkit.getPlayer(uuid).getName();
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        return offlinePlayer != null ? offlinePlayer.getName() : "";
    }
}
