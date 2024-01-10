package net.deneo.adup.listener.world;

import net.deneo.adup.database.tables.GlobalTables;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class ItemDespawnEvent implements Listener {
    @EventHandler
    public void onItemDespawn(org.bukkit.event.entity.ItemDespawnEvent e) {
        UUID itemID = e.getEntity().getUniqueId();
        if (GlobalTables.itemsTable.hasBeenDropped(itemID)) {
            GlobalTables.itemsTable.deleteItem(itemID);
        }
    }
}
