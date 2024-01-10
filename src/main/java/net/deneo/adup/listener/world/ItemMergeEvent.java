package net.deneo.adup.listener.world;

import net.deneo.adup.database.tables.GlobalTables;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class ItemMergeEvent implements Listener {
    @EventHandler
    public void onItemMerge(org.bukkit.event.entity.ItemMergeEvent e) {
        UUID mainItemID = e.getEntity().getUniqueId();
        if (GlobalTables.itemsTable.hasBeenDropped(mainItemID)) {
            GlobalTables.itemsTable.deleteItem(mainItemID);
        }
    }
}
