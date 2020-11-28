package elicode.parkour.util.databases;

import com.mysql.jdbc.Util;
import com.zaxxer.hikari.HikariDataSource;
import elicode.parkour.Main;
import elicode.parkour.mysql.Database;
import elicode.parkour.mysql.sqlite.SQLite;
import elicode.parkour.util.DynamicDataSource;
import elicode.parkour.util.Utils;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManager {

    private Database dataSource;
    private boolean useMySQL;

    private RowSetFactory factory;

    private static DatabaseManager instance = null;

    /**
     * Get the instance of the command manager
     *
     * @return the database manager instance
     */
    public static DatabaseManager get() {
        return instance == null ? instance = new DatabaseManager() : instance;
    }

    /**
     * Initially connects to the database and sets up the required tables of they don't already exist.
     *
     * @param useMySQLServer whether to preferably use MySQL (uses HSQLDB as fallback)
     */
    public void setup(boolean useMySQLServer) {
        useMySQL = useMySQLServer;

        dataSource = Main.getPlugin().getDatabase();

        DatabaseUtils.type = DatabaseType.SQLite;

        dataSource = new SQLite("parkour.db");;

        try {
            dataSource.openConnection();

            Main.getPlugin().setDatabase(dataSource);
        } catch (Exception e) {
            Main.getPlugin().debugException(e);
        }

        executeStatement(SQLQuery.CREATE_TABLE_PARKOUR);
        executeStatement(SQLQuery.CREATE_TABLE_CLEARED_COURSE);
        //executeStatement();
        executeStatement(SQLQuery.CREATE_TABLE_PARKOUR);

        executeStatement(SQLQuery.CREATE_TABLE_COURSE);
        executeStatement(SQLQuery.CREATE_TABLE_TIME);
        executeStatement(SQLQuery.CREATE_TABLE_VOTE);

    }

    /**
     * Shuts down the HSQLDB if used.
     */
    public void shutdown() {
        if (!useMySQL) {
            try(Connection connection = dataSource.getConnection(); final PreparedStatement statement = connection.prepareStatement("SHUTDOWN")){
                statement.execute();
            }catch (SQLException | NullPointerException exc){
                Utils.log("An unexpected error has occurred turning off the database");
                Main.getPlugin().debugException(exc);
            }
        }

        dataSource.closeConnection();
    }

    private CachedRowSet createCachedRowSet() throws SQLException {
        if (factory == null) {
            factory = RowSetProvider.newFactory();
        }
        return factory.createCachedRowSet();
    }

    /**
     * Execute a sql statement without any results.
     *
     * @param sql        the sql statement
     * @param parameters the parameters
     */
    public void executeStatement(SQLQuery sql, Object... parameters) {
        executeStatement(sql, false, parameters);
    }

    /**
     * Execute a sql statement.
     *
     * @param sql        the sql statement
     * @param parameters the parameters
     * @return the result set
     */
    public ResultSet executeResultStatement(SQLQuery sql, Object... parameters) {
        return executeStatement(sql, true, parameters);
    }

    private ResultSet executeStatement(SQLQuery sql, boolean result, Object... parameters) {
        return executeStatement(sql.toString(), result, parameters);
    }

    private synchronized ResultSet executeStatement(String sql, boolean result, Object... parameters) {
        try (Connection connection = dataSource.openConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
          /*  PreparedStatement ps = Main.getPlugin().getDatabase().openConnection().prepareStatement("SELECT courseId FROM course WHERE name = ?;");
            ps.setString(1, courseName.colorlessName());
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                courseId = rs.getInt("courseId");*/
            for (int i = 0; i < parameters.length; i++) {
                statement.setObject(i + 1, parameters[i]);
            }

            if (result) {
                CachedRowSet results = createCachedRowSet();
                results.populate(statement.executeQuery());
                return results;
            }
            statement.execute();
            dataSource.closeConnection();
        } catch (SQLException ex) {
            Utils.log(
                    "An unexpected error has occurred executing an Statement in the database\n"
                            + "Please check the plugins/Parkour/logs/latest.log file and report this "
                            + "error in: "
            );
            Main.getPlugin().debug("Query: \n" + sql);
            Main.getPlugin().debugSqlException(ex);
        } catch (NullPointerException ex) {
            Utils.log(
                    "An unexpected error has occurred connecting to the database\n"
                            + "Check if your MySQL data is correct and if your MySQL-Server is online\n"
                            + "Please check the plugins/Parkour/logs/latest.log file and report this "
                            + "error in: "
            );
           Main.getPlugin().debugException(ex);
        }
        return null;
    }

    /**
     * Check whether there is a valid connection to the database.
     *
     * @return whether there is a valid connection
     */
    /*public boolean isConnectionValid() {
        return dataSource.isRunning();
    }*/

    /**
     * Check whether MySQL is actually used.
     *
     * @return whether MySQL is used
     */
    public boolean isUseMySQL() {
        return useMySQL;
    }
}
