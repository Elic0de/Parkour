package elicode.parkour.other;

import elicode.parkour.Main;
import elicode.parkour.mysql.Database;
import elicode.parkour.mysql.sqlite.SQLite;
import elicode.parkour.util.databases.DatabaseType;
import elicode.parkour.util.databases.DatabaseUtils;
import elicode.parkour.util.Utils;
import org.bukkit.configuration.file.FileConfiguration;

public class StartPlugin {

    public static void run() {

        FileConfiguration configuration = Main.getPlugin().getConfig();

        configuration.addDefault("SQLite.PathOverride", "");
        configuration.addDefault("MySQL.Use", false);
        configuration.addDefault("MySQL.Host", "Host");
        configuration.addDefault("MySQL.Port", 3306);
        configuration.addDefault("MySQL.User", "Username");
        configuration.addDefault("MySQL.Password", "Password");
        configuration.addDefault("MySQL.Database", "Database");
        configuration.addDefault("MySQL.Table", "Table");

        initiateSQL();
        Utils.log("Enabled Parkour Elicode");
    }

    private static void initiateSQL() {
        initiateSQL(false);
    }

    private static void initiateSQL(boolean forceSQLite) {
        Database database;
        Database friendsDatabase;
        FileConfiguration config = Main.getPlugin().getConfig();
        DatabaseUtils.type = DatabaseType.SQLite;
        // Only use MySQL if they have enabled it, configured it, and we aren't
        // forcing SQLite (MySQL failed)

            friendsDatabase = new SQLite("friend.db");
            database = new SQLite("parkour.db");
            DatabaseUtils.type = DatabaseType.SQLite;
            System.out.println(forceSQLite);


        try {
            database.openConnection();
            Main.getPlugin().setDatabase(database);
            //DatabaseUtils.setupTables();
            //friendsDatabase.openConnection();
            //Main.getPlugin().setFriendDatabase(friendsDatabase);
             //new FriendDatabase(friendsDatabase);

        } catch (Exception ex) {
            failedSQL(ex);
            Main.getPlugin().debugException(ex);
        }
    }

    private static void failedSQL(Exception ex) {
        Utils.log("[SQL] Connection problem: " + ex.getMessage(), 2);
        Utils.log("[SQL] Defaulting to SQLite...", 1);
        Main.getPlugin().getConfig().set("MySQL.Use", false);
        Main.getPlugin().saveConfig();
        initiateSQL(true);
    }


}
