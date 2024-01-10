package net.deneo.adup.database;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import lombok.Getter;
import net.deneo.adup.Adup;
import net.deneo.adup.utility.ConfigUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <h3>A extendable class to store values assign to a specific key in SQLite or MySQL</h3>
 * It contains caching and a overrideable method 'purge' to only store data that is access frequently and only data that is needed; otherwise use 'ncSelect' to access data directly from the database
 *
 * @param <T> The type to store
 * @since R-1.5
 * {@code }
 */
public abstract class Database<T> {
    protected static Connection connection;
    private final String tableName;
    private final boolean keyAsPrimary;
    private final HashMap<String, T> data;
    private final HashMap<String, Integer> index;
    private final List<Query> queries;

    /**
     * <h3>Creates a Instance</h3>
     * It just creates a table based on the given params:
     *
     * @param keyAsPrimary If set to true, you can't assign multiple values to one key
     * @param tableName    The table name to create, if it doesn't exist
     * @param dataQuery    The data that this table contains in a SQL query
     * @since R-1.5
     */
    protected Database(boolean keyAsPrimary, String tableName, String dataQuery) {
        this.tableName = tableName;
        this.keyAsPrimary = keyAsPrimary;
        this.index = new HashMap<>();
        this.data = new HashMap<>();
        this.queries = new ArrayList<>();

        if (connection == null) {
            return;
        }

        try {
            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE TABLE IF NOT EXISTS " + tableName + "( key TEXT " + (keyAsPrimary ? "PRIMARY KEY" : "") + ", " + dataQuery + ")");
            }
        } catch (SQLException e) {
            Adup.fatal("Creating " + tableName + " table failed!", e);
        }
    }

    /**
     * <h3>Initialised a connection to the SQLite/MySQL database</h3>
     *
     * @since R-1.5
     */
    public static boolean init() {
        Adup.log("Initialising database...");

        boolean isMySql = ConfigUtil.getBoolean("mysql.enabled");
        if (isMySql) {
            try {
                String host = ConfigUtil.getString("mysql.host");
                int port = ConfigUtil.getInt("mysql.port");

                String database = ConfigUtil.getString("mysql.database");

                String userId = ConfigUtil.getString("mysql.user");
                String password = ConfigUtil.getString("mysql.password");

                MysqlDataSource dataSource = new MysqlConnectionPoolDataSource();

                dataSource.setServerName(host);
                dataSource.setPortNumber(port);
                dataSource.setDatabaseName(database);
                dataSource.setUser(userId);
                dataSource.setPassword(password);

                Database.connection = dataSource.getConnection();
                Adup.log("Established connection to MySQL database!");

                return true;
            } catch (SQLException e) {
                Adup.fatal("Establishing connection to MySQL database failed!", e);

                if (e instanceof CommunicationsException) {
                    Adup.log("DON'T REPORT -> Not a ADUP issue: Check your MySQL credentials in the config.yml again!");
                }
            }

            return false;
        }

        try {
            Database.connection = DriverManager.getConnection("jdbc:sqlite:plugins/Adup/data.db");
            Adup.log("Established connection to SQLite database!");
            return true;
        } catch (SQLException e) {
            Adup.fatal("Establishing connection to SQLite database failed!", e);
        }

        return false;
    }

    /**
     * <h3>Closes the connection to the SQLite/MySQL database</h3>
     *
     * @since R-1.5
     */
    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            Adup.fatal("Closing database connection failed!", e);
        }
    }

    /**
     * <h3>Saves the in-cache stored data to the database</h3>
     * Creates calls to the database based on the stored queries.
     *
     * @see Query
     * @see Method
     * @since R-1.5
     */
    public synchronized void save() {
        for (Query query : queries) {
            String newQuery = query.query.replace(";", ")").replace("|", "( ?, ");
            String insertQuery = "INSERT INTO " + tableName + " ( key, " + newQuery;
            String updateQuery = "UPDATE " + tableName + " " + newQuery + " WHERE key = ?";
            String deleteQuery = "DELETE FROM " + tableName + " WHERE key = ? " + newQuery;

            String finalQuery = query.method == Method.UPDATE ? updateQuery : query.method == Method.DELETE ? deleteQuery : insertQuery;
            try (PreparedStatement statement = connection.prepareStatement(finalQuery)) {
                if (query.method == Method.UPDATE) {
                    int end = query.updateFunc.stmt(statement);
                    statement.setString(end, query.key);
                } else if (query.method == Method.DELETE) {
                    statement.setString(1, query.key);
                    query.func.stmt(statement);
                } else {
                    statement.setString(1, query.key);
                    insertV(query.val, statement);
                }
                statement.executeUpdate();
            } catch (SQLException e) {
                String finalMethod = query.method == Method.UPDATE ? "Updating" : query.method == Method.DELETE ? "Deleting" : "Inserting";
                Adup.fatal(finalMethod + " into the database failed!", e);
            }
        }

        queries.clear();
    }

    /**
     * <h3>Loads the data from the database into the cache</h3>
     *
     * @since R-1.5
     */
    public synchronized void load() {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + tableName)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String key = resultSet.getString(1);
                T val = sSelectV(resultSet);
                if (data.containsKey(key)) {
                    if (keyAsPrimary) {
                        Adup.fatal("REPORT -> " + key + " already has a value assigned! IN 'load'.");
                        return;
                    }
                    Object i = index.putIfAbsent(key, 0);
                    index.put(key, i == null ? 1 : (int) i + 1);
                    data.put(key + "_" + (i == null ? 0 : i), val);
                } else {
                    data.put(key, val);
                }
            }
        } catch (SQLException e) {
            Adup.fatal("Loading data from database failed!", e);
        }

        filter(data);
    }

    /**
     * <h3>A overrideable method to filter out unnecessary cached data</h3>
     *
     * @param data The cached data
     * @since R-1.5
     */
    protected void filter(HashMap<String, T> data) {

    }

    /**
     * <h3>Inserts a value to the specific key</h3>
     * If the key already has a value assigned to it and {@code keyAsPrimary} is set to true, it will throw a FATAL error
     *
     * @param key   The key, that the value is assigned to
     * @param val   The value
     * @param query The SQL query containing what data is inserted
     * @since R-1.5
     */
    protected void insert(String key, T val, String query) {
        if (data.containsKey(key)) {
            if (keyAsPrimary) {
                Adup.fatal("REPORT -> " + key + " already has a value assigned! IN 'insert'.");
                return;
            }
            Object i = index.putIfAbsent(key, 0);
            index.put(key, i == null ? 1 : (int) i + 1);
            data.put(key + "_" + (i == null ? 0 : i), val);
        } else {
            data.put(key, val);
        }
        queries.add(new Query(key, val, query));
    }

    /**
     * <h3>Inserts or updates a value to a specific key</h3>
     * If the key already has a value assigned to it, the old value is replaced with the new; otherwise the value is inserted to that key
     *
     * @param key         The key, that the value is assigned to
     * @param val         The value
     * @param insertQuery The SQL query containing what data is inserted
     * @param updateQuery The SQL query containing what data is updated
     * @since R-1.5
     */
    protected void insertOrUpdate(String key, T val, String insertQuery, String updateQuery, UpdateFunc func) {
        if (data.putIfAbsent(key, val) == null) {
            queries.add(new Query(key, val, insertQuery));
            return;
        }
        queries.add(new Query(key, updateQuery, func));
        data.put(key, val);
    }

    /**
     * <h3>Updates a value to a specific key</h3>
     * If the key doesn't have a value assigned to it, it will throw a FATAL error
     *
     * @param key   The key, that the value is assigned to
     * @param val   The value
     * @param query The SQL query containing what data is updated
     * @since R-1.5
     */
    protected void update(String key, T val, String query, UpdateFunc func) {
        if (!data.containsKey(key)) {
            Adup.fatal("No value to update! IN 'update'");
            return;
        }
        data.put(key, val);
        queries.add(new Query(key, query, func));
    }

    /**
     * <h3>Deletes a value from a specific key</h3>
     * If the key doesn't have a value assigned to it, it will throw a FATAL error
     *
     * @param key     The key, that the value is assigned to
     * @param query   The SQL query containing additional WHERE's
     * @param func    A function to set the additional WHERE's in the SQL query
     * @param delFunc A function to return if value should be deleted or not
     * @since R-1.5
     */
    public void delete(String key, String query, StatementFunc func, DeleteFunc<T> delFunc) {
        List<String> keysToRemove = new ArrayList<>();
        Object val = data.get(key);
        if (val != null && delFunc.delete((T) val)) {
            keysToRemove.add(key);
        }
        Object indx = index.get(key);
        if (indx != null) {
            for (int i = 0; i < (int) indx; i++) {
                String newKey = key + "_" + i;
                val = data.get(newKey);
                if (val != null && delFunc.delete((T) val)) {
                    keysToRemove.add(newKey);
                }
            }
        }
        for (String rKey : keysToRemove) {
            data.remove(rKey);
        }
        queries.add(new Query(key, query, func));
    }

    /**
     * <h3>Returns all values assigned to the specific key directly from the database</h3>
     *
     * @param key   The key
     * @param query The SQL query containing additional WHERE's
     * @param func  A function to set the additional WHERE's in the SQL query
     * @return The values assigned to the specific key in a list
     */
    public List<T> ncSelect(String key, String query, StatementFunc func) {
        List<T> ret = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE key = ? " + query)) {
            statement.setString(1, key);
            func.stmt(statement);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                T val = sSelectV(resultSet);
                ret.add(val);
            }
        } catch (SQLException e) {
            Adup.fatal("Selecting from database failed (No-Cache)!", e);
        }
        return ret;
    }

    /**
     * <h3>Deletes all values assigned to the specific key directly from the database</h3>
     *
     * @param key   The key
     * @param query The SQL query containing additional WHERE's
     * @param func  A function to set the additional WHERE's in the SQL query
     */
    public void ncDelete(String key, String query, StatementFunc func) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM " + tableName + " WHERE key = ? " + query)) {
            statement.setString(1, key);
            func.stmt(statement);
            statement.execute();
        } catch (SQLException e) {
            Adup.fatal("Deleting from database failed (No-Cache)!", e);
        }
    }

    /**
     * <h3>Returns all values inside the cache</h3>
     *
     * @return The values inside the cache
     */
    public List<T> cSelect() {
        return new ArrayList<>(data.values());
    }

    /**
     * <h3>Returns the first value ever assigned to the specific key</h3>
     *
     * @param key The key
     * @return The first value ever assigned to the key
     */
    public T sSelect(String key) {
        return data.get(key);
    }

    /**
     * <h3>Returns all values assigned to the specific key</h3>
     *
     * @param key The key
     * @return All values from the key in a list
     */
    public List<T> bSelect(String key) {
        List<T> ret = new ArrayList<>();
        Object original = data.get(key);
        if (original != null) {
            ret.add((T) original);
        }
        Object indx = index.get(key);
        if (indx != null) {
            for (int i = 0; i < (int) indx; i++) {
                Object val = data.get(key + "_" + i);
                if (val != null) {
                    ret.add((T) val);
                }
            }
        }
        return ret;
    }

    /**
     * Insert the 'val' into the statement, start with parameterIndex '2' in 'statement'
     *
     * @since R-1.5
     */
    protected void insertV(T val, PreparedStatement statement) throws SQLException {
        Adup.error("REPORT -> Non implemented `insertV`!");
    }

    /**
     * Return the value based on the 'resultSet', start with columnIndex '2' in 'resultSet'
     *
     * @since R-1.5
     */
    protected T sSelectV(ResultSet resultSet) throws SQLException {
        Adup.error("REPORT -> Non implemented `sSelectV`!");
        return null;
    }

    /**
     * <h3>All the SQL methods that this class supports</h3>
     *
     * @since R-1.5
     */
    private enum Method {
        UPDATE,
        INSERT,
        DELETE
    }

    /**
     * <h3>A class holding information about a query to execute later in 'save'</h3>
     *
     * @since R-1.5
     */
    private class Query {
        @Getter
        private final String key;
        @Getter
        private final T val;
        @Getter
        private final String query;
        @Getter
        private final Method method;
        @Getter
        private final StatementFunc func;
        @Getter
        private final UpdateFunc updateFunc;

        public Query(String key, T val, String query) {
            this.key = key;
            this.val = val;
            this.query = query;
            this.method = Method.INSERT;
            this.func = null;
            this.updateFunc = null;
        }

        public Query(String key, String query, StatementFunc func) {
            this.key = key;
            this.val = null;
            this.query = query;
            this.method = Method.DELETE;
            this.func = func;
            this.updateFunc = null;
        }

        public Query(String key, String query, UpdateFunc func) {
            this.key = key;
            this.val = null;
            this.query = query;
            this.method = Method.UPDATE;
            this.updateFunc = func;
            this.func = null;
        }
    }
}
