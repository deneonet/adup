package net.deneo.adup.utility;

import net.deneo.adup.Adup;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpectateUtil {
    private static final List<UUID> spectatingPlayers = new ArrayList<>();

    public static void spectatePlayer(Player player, Player target) {
        MessagesUtil.sendSpectating(player, target.getDisplayName());

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.hidePlayer(Adup.getInstance(), player);
        }

        player.teleport(target);
        spectatingPlayers.add(player.getUniqueId());
    }

    public static void unSpectatePlayer(Player player, Player target) {
        MessagesUtil.sendUnSpectating(player, target.getDisplayName());

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.showPlayer(Adup.getInstance(), player);
        }

        spectatingPlayers.remove(player.getUniqueId());
    }

    public static void removeSpectatingPlayer(UUID uuid) {
        spectatingPlayers.remove(uuid);
    }

    public static List<UUID> getSpectatingPlayers() {
        return spectatingPlayers;
    }
}
