package net.deneo.adup.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface StatementFunc {
    void stmt(PreparedStatement statement) throws SQLException;
}
