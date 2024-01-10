package net.deneo.adup.listener.server;

import net.deneo.adup.utility.SpectateUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerQuitEvent implements Listener {
    @EventHandler
    public void onPlayerQuit(org.bukkit.event.player.PlayerQuitEvent e) {
        final Player player = e.getPlayer();
        SpectateUtil.removeSpectatingPlayer(player.getUniqueId());
    }
}
