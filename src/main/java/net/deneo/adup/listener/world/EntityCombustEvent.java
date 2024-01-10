package net.deneo.adup.listener.world;

import net.deneo.adup.database.tables.GlobalTables;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class EntityCombustEvent implements Listener {
    @EventHandler
    public void onEntityCombust(org.bukkit.event.entity.EntityCombustEvent e) {
        if (!(e.getEntity() instanceof Item)) {
            return;
        }

        UUID itemID = e.getEntity().getUniqueId();
        if (GlobalTables.itemsTable.hasBeenDropped(itemID)) {
            GlobalTables.itemsTable.deleteItem(itemID);
        }
    }
}
