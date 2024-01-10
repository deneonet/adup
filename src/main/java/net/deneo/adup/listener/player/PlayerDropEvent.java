package net.deneo.adup.listener.player;

import net.deneo.adup.data.AdupPlayer;
import net.deneo.adup.data.Log;
import net.deneo.adup.database.tables.GlobalTables;
import net.deneo.adup.utility.ConfigUtil;
import net.deneo.adup.utility.PermissionsUtil;
import net.deneo.adup.utility.WarningsUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class PlayerDropEvent implements Listener {
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        Player player = e.getPlayer();
        if (player.isDead()) {
            return;
        }

        Date date = new Date();
        GlobalTables.itemsTable.addItem(e.getItemDrop().getUniqueId(), player.getUniqueId(), date);

        if (PermissionsUtil.hasTrackBypassPerm(player)) {
            return;
        }

        boolean isDropTrackingEnabled = ConfigUtil.getBoolean("tracking.drop");
        if (!isDropTrackingEnabled) {
            return;
        }

        String world = player.getWorld().getName();

        final ItemStack itemStack = e.getItemDrop().getItemStack();

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
                int threshold = (Integer) item.get("drop_threshold");
                if (threshold == 0) {
                    continue;
                }

                name = (String) item.get("name");
                contains = true;
                maxCount = threshold;
                break;
            }
        }

        if (contains) {
            List<Log> logs = GlobalTables.logsTable.bSelect(player.getUniqueId().toString());
            for (Log log : logs) {
                if (log.type != Log.Type.DROPPED) {
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
            adupPlayer.dropMultiplier = 0;

            GlobalTables.playersTable.updatePlayer(player.getUniqueId(), adupPlayer, "SET warnings = ?, drop_multiplier = ?", (statement -> {
                statement.setInt(1, 0);
                statement.setInt(2, 0);
                return 3;
            }));
        }

        if (contains) {
            amount += itemStack.getAmount() + 1;
        }

        int multiplier = GlobalTables.playersTable.getOrInsertPlayer(player.getUniqueId()).dropMultiplier;
        if (multiplier == 0) {
            multiplier = 1;
        }

        AdupPlayer adupPlayer = GlobalTables.playersTable.getPlayer(player.getUniqueId());
        if (amount > 0 && maxCount > 0) {
            adupPlayer.dropMultiplier = Math.max(amount / maxCount, 1);
        }

        Log log = new Log(itemStack.clone(), name, date, world, Log.Type.DROPPED);
        if (amount >= maxCount * multiplier) {
            adupPlayer.dropMultiplier = multiplier + amount / maxCount;

            GlobalTables.playersTable.updatePlayer(player.getUniqueId(), adupPlayer, "SET drop_multiplier = ?", (statement -> {
                statement.setInt(1, adupPlayer.dropMultiplier);
                return 2;
            }));

            WarningsUtil.warn(player.getUniqueId(), amount, log, "Dropped");
        }

        GlobalTables.logsTable.addLog(player.getUniqueId(), log);
    }
}