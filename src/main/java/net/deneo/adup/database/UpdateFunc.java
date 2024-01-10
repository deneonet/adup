package net.deneo.adup.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface UpdateFunc {
    int stmt(PreparedStatement statement) throws SQLException;
}
