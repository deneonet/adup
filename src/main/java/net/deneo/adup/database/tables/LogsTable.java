package net.deneo.adup.database.tables;

import net.deneo.adup.Adup;
import net.deneo.adup.data.Log;
import net.deneo.adup.database.Database;
import net.deneo.adup.utility.ConfigUtil;
import net.deneo.adup.utility.TimeUtil;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

public class LogsTable extends Database<Log> {
    public LogsTable() {
        super(false, "logs", "date_time TIMESTAMP NOT NULL, world TEXT NOT NULL, tracked_name TEXT NOT NULL, item_stack TEXT NOT NULL, type INTEGER NOT NULL, storage INTEGER, source TEXT");
    }

    public void addLog(UUID uuid, Log log) {
        insert(uuid.toString(), log, "date_time, world, tracked_name, item_stack, type, storage, source ; VALUES | ?, ?, ?, ?, ?, ?, ? ; ");
    }

    public List<Log> getLogsNoCache(UUID uuid) {
        return ncSelect(uuid.toString(), "", (statement) -> {
        });
    }

    public List<Log> getLogs(UUID uuid) {
        return bSelect(uuid.toString());
    }

    @Override
    protected synchronized void filter(HashMap<String, Log> data) {
        String timeSpan = ConfigUtil.getString("tracking.time_span");
        int parsedTime = TimeUtil.parseTime(timeSpan);

        data.entrySet().removeIf(entry -> new Date().getTime() - entry.getValue().date.getTime() > TimeUtil.getTimeUnit(timeSpan).getTime(parsedTime));
    }

    public void deleteLog(UUID uuid, Date date) {
        delete(uuid.toString(), "AND date_time = ?", (statement -> {
            Timestamp dateToTimestamp = new Timestamp(date.getTime());
            statement.setTimestamp(2, dateToTimestamp);
        }), (val -> val.date.getTime() == date.getTime()));
    }

    public void deleteLogs(UUID uuid) {
        delete(uuid.toString(), "", (statement -> {
        }), (val -> true));

        ncDelete(uuid.toString(), "", (statement -> {
        }));
    }

    @Override
    protected void insertV(Log val, PreparedStatement statement) throws SQLException {
        Timestamp logDateToTimestamp = new Timestamp(val.date.getTime());
        statement.setTimestamp(2, logDateToTimestamp);
        statement.setString(3, val.world);
        statement.setString(4, val.trackedName);
        statement.setString(5, serializeItemStack(val.itemStack));
        int ordinal = val.type.ordinal();
        int appendedTakenToOrdinal = ((ordinal << 4) | (val.taken ? 1 : 0));
        statement.setInt(6, appendedTakenToOrdinal);
        statement.setInt(7, val.storage == null ? 0 : val.storage.ordinal());
        statement.setString(8, val.source == null ? null : val.source.toString());
    }

    @Override
    protected Log sSelectV(ResultSet resultSet) throws SQLException {
        long time = resultSet.getTimestamp(2).getTime();
        Date date = new Date(time);
        String world = resultSet.getString(3);
        String trackedName = resultSet.getString(4);
        ItemStack itemStack = deserializeItemStack(resultSet.getString(5));
        int takenAppendedOrdinal = resultSet.getInt(6);
        byte mask = (byte) 0b11110000;
        int ordinal = ((takenAppendedOrdinal & mask) >> 4);
        boolean taken = (takenAppendedOrdinal & ~mask) == 1;
        Log.Type type = Log.Type.values()[ordinal];
        ordinal = resultSet.getInt(7);
        Log.Storage storage = Log.Storage.values()[ordinal];
        String source = resultSet.getString(8);
        UUID sourceUUID = source == null ? null : UUID.fromString(source);
        return new Log(itemStack, trackedName, date, world, type, storage, taken, sourceUUID);
    }

    private ItemStack deserializeItemStack(String serializedItemStack) {
        JSONParser parser = new JSONParser();

        try {
            JSONObject serialized = (JSONObject) parser.parse(serializedItemStack);

            ItemStack itemStack = new ItemStack(org.bukkit.Material.getMaterial((String) serialized.get("type")));
            itemStack.setAmount(((Long) serialized.get("amount")).intValue());

            ItemMeta meta = itemStack.getItemMeta();
            if (serialized.containsKey("display_name")) {
                meta.setDisplayName((String) serialized.get("display_name"));
            }
            if (serialized.containsKey("lore")) {
                JSONArray loreArray = (JSONArray) serialized.get("lore");
                List<String> lore = new ArrayList<>();
                for (Object obj : loreArray) {
                    lore.add((String) obj);
                }
                meta.setLore(lore);
            }
            if (serialized.containsKey("enchantments")) {
                JSONObject enchantments = (JSONObject) serialized.get("enchantments");
                for (Object key : enchantments.keySet()) {
                    Enchantment enchantment = Enchantment.getByName((String) key);
                    int level = ((Long) enchantments.get(key)).intValue();
                    meta.addEnchant(enchantment, level, true);
                }
            }

            itemStack.setItemMeta(meta);
            return itemStack;
        } catch (ParseException e) {
            Adup.fatal("Deserializing item stack failed!", e);
        }
        return null;
    }

    private String serializeItemStack(ItemStack itemStack) {
        JSONObject serialized = new JSONObject();

        serialized.put("type", itemStack.getType().name());
        serialized.put("amount", itemStack.getAmount());

        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            if (meta.hasDisplayName()) {
                serialized.put("display_name", meta.getDisplayName());
            }
            if (meta.hasLore()) {
                serialized.put("lore", meta.getLore());
            }
            if (meta.hasEnchants()) {
                JSONObject enchantments = new JSONObject();
                for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
                    enchantments.put(entry.getKey().getKey().getKey(), entry.getValue());
                }
                serialized.put("enchantments", enchantments);
            }
        }

        return serialized.toJSONString();
    }
}
