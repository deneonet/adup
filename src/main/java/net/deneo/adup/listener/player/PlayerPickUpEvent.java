package net.deneo.adup.listener.player;

import net.deneo.adup.data.AdupPlayer;
import net.deneo.adup.data.ItemDrop;
import net.deneo.adup.data.Log;
import net.deneo.adup.database.tables.GlobalTables;
import net.deneo.adup.utility.ConfigUtil;
import net.deneo.adup.utility.PermissionsUtil;
import net.deneo.adup.utility.WarningsUtil;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerPickUpEvent implements Listener {
    @EventHandler
    public void onPickupItem(EntityPickupItemEvent e) {
        if (!e.getEntityType().equals(EntityType.PLAYER)) {
            return;
        }

        Player player = (Player) e.getEntity();

        UUID itemID = e.getItem().getUniqueId();
        UUID sourceID = null;
        Date dropDate = null;
        Log.Type pickUpType = Log.Type.PICKED_UP_UNKNOWN;

        if (GlobalTables.itemsTable.hasBeenDropped(itemID)) {
            ItemDrop item = GlobalTables.itemsTable.getItem(itemID);
            sourceID = item.player;
            dropDate = item.date;
            pickUpType = Log.Type.PICKED_UP_PLAYER;

            if (item.player == player.getUniqueId()) {
                pickUpType = Log.Type.PICKED_UP_SELF_DROPPED;
            }

            GlobalTables.itemsTable.deleteItem(itemID);
        }

        if (PermissionsUtil.hasTrackBypassPerm(player)) {
            return;
        }

        boolean isPickUpTrackingEnabled = ConfigUtil.getBoolean("tracking.pick_up");
        if (!isPickUpTrackingEnabled) {
            return;
        }

        String world = player.getWorld().getName();

        Date date = new Date();
        ItemStack itemStack = e.getItem().getItemStack();

        int amount = -1;
        int maxCount = 0;

        List<Map<String, Object>> trackedItems = ConfigUtil.getMapList("tracking.items");

        boolean contains = false;
        boolean enchantments = false;
        boolean displayName = false;
        boolean lore = false;

        String name = "";

        for (Map<String, Object> item : trackedItems) {
            ItemMeta trackedItemMeta = (ItemMeta) item.get("metadata");
            ItemMeta itemMeta = itemStack.getItemMeta();
            lore = (boolean) item.get("lore");
            enchantments = (boolean) item.get("enchantments");
            displayName = (boolean) item.get("display_name");

            if (enchantments && !trackedItemMeta.getEnchants().equals(itemMeta.getEnchants())) {
                continue;
            }
            if (displayName && !trackedItemMeta.getDisplayName().equals(itemMeta.getDisplayName())) {
                continue;
            }
            if (lore && !trackedItemMeta.getLore().equals(itemMeta.getLore())) {
                continue;
            }

            String type = (String) item.get("type");
            if (type.equals(itemStack.getType().toString())) {
                int threshold = (Integer) item.get("pick_up_threshold");
                if (threshold == 0) {
                    continue;
                }

                name = (String) item.get("name");
                contains = true;
                maxCount = threshold;
                break;
            }
        }

        Log logToAdd = new Log(itemStack, name, date, world, pickUpType, sourceID);

        if (pickUpType != Log.Type.PICKED_UP_PLAYER) {
            /*if (pickUpType == Log.Type.PICKED_UP_SELF_DROPPED) {
                GlobalTables.logsTable.deleteLog(sourceID, dropDate);
            }*/

            GlobalTables.logsTable.addLog(player.getUniqueId(), logToAdd);
            return;
        }

        if (contains) {
            List<Log> logs = GlobalTables.logsTable.bSelect(player.getUniqueId().toString());
            for (Log log : logs) {
                if (log.type != Log.Type.PICKED_UP_PLAYER) {
                    continue;
                }

                ItemMeta logItemMeta = log.itemStack.getItemMeta();
                ItemMeta itemMeta = itemStack.getItemMeta();

                if (enchantments && !logItemMeta.getEnchants().equals(itemMeta.getEnchants())) {
                    continue;
                }
                if (displayName && !logItemMeta.getDisplayName().equals(itemMeta.getDisplayName())) {
                    continue;
                }
                if (lore && !logItemMeta.getLore().equals(itemMeta.getLore())) {
                    continue;
                }

                if (log.itemStack.getType().equals(itemStack.getType())) {
                    amount += log.itemStack.getAmount();
                }
            }
        }

        if (amount == -1) {
            AdupPlayer adupPlayer = GlobalTables.playersTable.getOrInsertPlayer(player.getUniqueId());
            adupPlayer.warnings = 0;
            adupPlayer.pickUpMultiplier = 0;

            GlobalTables.playersTable.updatePlayer(player.getUniqueId(), adupPlayer, "SET warnings = ?, pick_up_multiplier = ?", (statement -> {
                statement.setInt(1, 0);
                statement.setInt(2, 0);
                return 3;
            }));
        }

        if (contains) {
            amount += itemStack.getAmount() + 1;
        }

        int multiplier = GlobalTables.playersTable.getOrInsertPlayer(player.getUniqueId()).pickUpMultiplier;
        if (multiplier == 0) {
            multiplier = 1;
        }

        AdupPlayer adupPlayer = GlobalTables.playersTable.getPlayer(player.getUniqueId());
        if (amount > 0 && maxCount > 0) {
            adupPlayer.pickUpMultiplier = Math.max(amount / maxCount, 1);
        }

        if (amount >= maxCount * multiplier) {
            adupPlayer.pickUpMultiplier = multiplier + amount / maxCount;

            GlobalTables.playersTable.updatePlayer(player.getUniqueId(), adupPlayer, "SET pick_up_multiplier = ?", (statement -> {
                statement.setInt(1, adupPlayer.pickUpMultiplier);
                return 2;
            }));

            WarningsUtil.warn(player.getUniqueId(), amount, logToAdd, "Picked Up");
        }

        GlobalTables.logsTable.addLog(player.getUniqueId(), logToAdd);
    }
}
