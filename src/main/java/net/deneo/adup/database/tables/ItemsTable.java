package net.deneo.adup.database.tables;

import net.deneo.adup.data.ItemDrop;
import net.deneo.adup.database.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class ItemsTable extends Database<ItemDrop> {
    public ItemsTable() {
        super(false, "items", "player TEXT NOT NULL, date TIMESTAMP NOT NULL");
    }

    public void addItem(UUID uuid, UUID player, Date dropDate) {
        insert(uuid.toString(), new ItemDrop(player, dropDate), "player, date ; VALUES | ?, ? ; ");
    }

    public void deleteItem(UUID uuid) {
        delete(uuid.toString(), "", (statement -> {
        }), (val -> true));
    }

    public ItemDrop getItem(UUID uuid) {
        return sSelect(uuid.toString());
    }

    public boolean hasBeenDropped(UUID uuid) {
        return sSelect(uuid.toString()) != null;
    }

    @Override
    protected void insertV(ItemDrop val, PreparedStatement statement) throws SQLException {
        statement.setString(2, val.player.toString());
        Timestamp timestamp = new Timestamp(val.date.getTime());
        statement.setTimestamp(3, timestamp);
    }

    @Override
    protected ItemDrop sSelectV(ResultSet resultSet) throws SQLException {
        String uuidStr = resultSet.getString(2);
        Timestamp timestamp = resultSet.getTimestamp(3);
        return new ItemDrop(UUID.fromString(uuidStr), new Date(timestamp.getTime()));
    }
}
