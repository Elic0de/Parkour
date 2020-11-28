package elicode.parkour.mysql.sqlite;

import elicode.parkour.mysql.Database;
import elicode.parkour.util.Utils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLite extends Database {

    private final String dbLocation;

    /**
     * Creates a new SQLite instance.
     *
     * @param dbLocation Location of the Database (Must end in .db)
     */
    public SQLite(String dbLocation) {

        this.dbLocation = dbLocation;
    }

    @Override
    public final Connection openConnection() {
        if (checkConnection()) {
            return getConnection();
        }

        String pathOverride = "";
        String path = pathOverride.isEmpty() ? "plugins/Parkour/sqlite-db" : pathOverride;

        File dataFolder = new File(path);
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File databaseFile = new File(dataFolder, dbLocation);
        if (!databaseFile.exists()) {
            try {
                databaseFile.createNewFile();
            } catch (IOException e) {
                Utils.log("Unable to create database: " + e.getMessage(), 2);
            }
        }

        try {
            Class.forName("org.sqlite.JDBC");
            setConnection(DriverManager.getConnection("jdbc:sqlite:" + dataFolder + "/" + dbLocation));
        } catch (SQLException | ClassNotFoundException ex) {
            Utils.log("Error occurred: " + ex.getMessage(), 2);
        }
        return getConnection();
    }
}