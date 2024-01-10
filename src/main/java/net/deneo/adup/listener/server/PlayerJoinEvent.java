package net.deneo.adup.listener.server;

import net.deneo.adup.Adup;
import net.deneo.adup.utility.SpectateUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class PlayerJoinEvent implements Listener {
    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent e) {
        final Player player = e.getPlayer();

        for (UUID playerUUID : SpectateUtil.getSpectatingPlayers()) {
            Player spectatingPlayer = Bukkit.getPlayer(playerUUID);
            if (spectatingPlayer == null) {
                SpectateUtil.removeSpectatingPlayer(playerUUID);
                continue;
            }

            player.hidePlayer(Adup.getInstance(), spectatingPlayer);
        }
    }
}
