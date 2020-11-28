package elicode.parkour.util.databases;

import elicode.parkour.Main;
import elicode.parkour.mysql.cache.NoCache;
import elicode.parkour.mysql.cache.PlayerCache;
import elicode.parkour.parkour.Parkour;
import elicode.parkour.parkour.ParkourSet;
import elicode.parkour.user.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import elicode.parkour.util.DynamicDataSource;
import elicode.parkour.util.Utils;
import elicode.parkour.util.databases.DatabaseType;
import org.bukkit.entity.Player;

public class DatabaseUtils {
    public static DatabaseType type;

    private static ParkourSet parkours = ParkourSet.getInstance();

    private static PlayerCache cache;



    public static void setupTables() {
        cache = new NoCache();
        try {
            if (type.equals(DatabaseType.SQLite)) {
                String tableScript = "CREATE TABLE IF NOT EXISTS players (player_id INTEGER PRIMARY KEY, player_name VARCHAR(16) NOT NULL, player_uuid CHAR(38) NOT NULL, 'update' INTEGER NOT NULL,'extend' INTEGER NOT NULL, 'ranked' INTEGER NOT NULL);CREATE TABLE IF NOT EXISTS cleared_course (player_id INTEGER NOT NULL, course_id INTEGER NOT NULL, count INT(5), first_cleared_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL);CREATE TABLE IF NOT EXISTS course (courseId INTEGER PRIMARY KEY, name VARCHAR(15) NOT NULL UNIQUE, category VARCHAR(15) NOT NULL, author VARCHAR(20) NOT NULL,timeattack BOOLEN(1) NOT NULL, created TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL); CREATE TABLE IF NOT EXISTS time (timeId INTEGER PRIMARY KEY, courseId INTEGER NOT NULL, player VARCHAR(20) NOT NULL, time DECIMAL(13,0) NOT NULL, deaths INT(5) NOT NULL, cleared_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, FOREIGN KEY (courseId) REFERENCES course(courseId) ON DELETE CASCADE ON UPDATE CASCADE); CREATE TABLE IF NOT EXISTS vote (courseId INTEGER NOT NULL, player VARCHAR(20) NOT NULL, liked BIT NOT NULL, PRIMARY KEY (courseId, player), FOREIGN KEY (courseId) REFERENCES course(courseId) ON DELETE CASCADE ON UPDATE CASCADE); ";
                Main.getPlugin().getDatabase().updateSQL(tableScript);
            } else if (type.equals(DatabaseType.MySQL)) {
                String tableScript = "CREATE TABLE IF NOT EXISTS course (courseId INTEGER PRIMARY KEY AUTO_INCREMENT, name VARCHAR(15) NOT NULL UNIQUE, category VARCHAR(15) NOT NULL, author VARCHAR(20) NOT NULL, created TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL); ";
                Main.getPlugin().getDatabase().updateSQL(tableScript);
                tableScript = "CREATE TABLE IF NOT EXISTS time (timeId INTEGER PRIMARY KEY AUTO_INCREMENT, courseId INTEGER NOT NULL, player VARCHAR(20) NOT NULL, time DECIMAL(13,0) NOT NULL, deaths INT(5) NOT NULL, cleared_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, FOREIGN KEY (courseId) REFERENCES course(courseId) ON DELETE CASCADE ON UPDATE CASCADE); ";
                Main.getPlugin().getDatabase().updateSQL(tableScript);
                tableScript = "CREATE TABLE IF NOT EXISTS vote (courseId INTEGER NOT NULL, player VARCHAR(20) NOT NULL, liked BIT NOT NULL, PRIMARY KEY (courseId, player), FOREIGN KEY (courseId) REFERENCES course(courseId) ON DELETE CASCADE ON UPDATE CASCADE); ";
                Main.getPlugin().getDatabase().updateSQL(tableScript);
            }
        } catch (ClassNotFoundException|SQLException e) {
            e.printStackTrace();
        } finally {
            Main.getPlugin().getDatabase().closeConnection();
        }
    }

    public static int getPlayerId(UUID uuid) {
        Integer playerID = cache.getPlayerID(uuid);
        if (playerID != null)
            return playerID.intValue();
        Connection con = getConnection();
        try {
            ResultSet rs = con.createStatement().executeQuery("select player_id, player_name from players WHERE player_uuid='" + uuid + "' LIMIT 1");
            if (rs.next()) {
                playerID = Integer.valueOf(rs.getInt("player_id"));
                cache.add(rs.getString("player_name"), uuid, playerID.intValue());
                return playerID.intValue();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Main.getPlugin().getDatabase().closeConnection();
        }
        return 0;
    }

    public static int getCourseId(Parkour courseName) {
        return getCourseId(courseName, true);
    }

    public static int getCourseId(Parkour courseName, boolean printError) {
        int courseId = 0;
        try {
            PreparedStatement ps = Main.getPlugin().getDatabase().openConnection().prepareStatement("SELECT courseId FROM course WHERE name = ?;");
            ps.setString(1, courseName.colorlessName());
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                courseId = rs.getInt("courseId");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Main.getPlugin().getDatabase().closeConnection();
        }
        if (courseId == 0 && printError)
            Utils.log("Course '" + courseName.colorlessName() + "' was not found in the database. Run command '/pa recreate' to fix.", 1);
        return courseId;
    }

    public static void insertCourse(Parkour courseName, String playerName) {
        try {
            PreparedStatement ps = Main.getPlugin().getDatabase().openConnection().prepareStatement("INSERT INTO `course` (`name`,category, `author`, `timeattack`) VALUES (?,?,?,?);");
            ps.setString(1, courseName.colorlessName());
            ps.setString(2, courseName.category.name);
            ps.setString(3, playerName);
            ps.setBoolean(4, courseName.timeAttackEnable);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Main.getPlugin().getDatabase().closeConnection();
        }
    }

    public static void insertClearedCourse(Parkour parkour, Player player) {

        try {
            int player_id = getPlayerId(player.getUniqueId());
            int course_id = getCourseId(parkour);
            if (course_id == 0)
                return;
            if (player_id == 0)
                return;
            PreparedStatement ps = Main.getPlugin().getDatabase().openConnection().prepareStatement("INSERT INTO `cleared_course` (`player_id`, `course_id`, `count` ) VALUES (?, ?, ?);");
            ps.setInt(1, player_id);
            ps.setInt(2, course_id);
            ps.setInt(3, 1);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Main.getPlugin().getDatabase().closeConnection();
        }
    }

    public static void insertPlayerData(User user) {
        Connection con = getConnection();
        Player player = user.asBukkitPlayer();
        try {
            PreparedStatement prepStmt = con.prepareStatement("insert into players (`player_name`,`player_uuid`,`update`, `extend`, `ranked`) values (?, ?, ?, ?, ?)");
            prepStmt.setString(1, player.getName());
            prepStmt.setString(2, player.getUniqueId().toString());
            prepStmt.setInt(3, user.updateRank());
            prepStmt.setInt(4, user.extendRank());
            prepStmt.setInt(5, user.rankedrank());
            prepStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Main.getPlugin().getDatabase().closeConnection();
        }
    }

    public static void insertTime(Parkour courseName, String uuid, long time, int deaths) {
        try {
            int courseId = getCourseId(courseName);
            if (courseId == 0)
                return;
            PreparedStatement ps = Main.getPlugin().getDatabase().openConnection().prepareStatement("INSERT INTO `time` (`courseId`, `player`, `time`, `deaths`) VALUES (?, ?, ?, ?);");
            ps.setInt(1, courseId);
            ps.setString(2, uuid);
            ps.setLong(3, time);
            ps.setInt(4, deaths);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Main.getPlugin().getDatabase().closeConnection();
        }
    }

    public static void insertVote(Parkour courseName, String playerName, Boolean like) {
        try {
            int courseId = getCourseId(courseName);
            if (courseId == 0)
                return;
            PreparedStatement ps = Main.getPlugin().getDatabase().openConnection().prepareStatement("INSERT INTO `vote` (courseId, player, liked) VALUES (?, ?, ?);");
            ps.setInt(1, courseId);
            ps.setString(2, playerName);
            ps.setBoolean(3, like.booleanValue());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Main.getPlugin().getDatabase().closeConnection();
        }
    }

    public static void updateCourse(Parkour parkour) {
        try {
            int courseId = getCourseId(parkour);
            if (courseId == 0)
                return;
            PreparedStatement ps = Main.getPlugin().getDatabase().openConnection().prepareStatement("UPDATE course SET name =?, category =?, author =?, timeattack =? WHERE courseId =?");
            ps.setString(1, parkour.colorlessName());
            ps.setString(2, parkour.category.name);
            ps.setString(3, parkour.creator);
            ps.setBoolean(4, parkour.timeAttackEnable);
            ps.setInt(5, courseId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Main.getPlugin().getDatabase().closeConnection();
        }
    }

    public static void updatePlayerData(User user) {
        Player player = user.asBukkitPlayer();
        int playerId = getPlayerId(player.getUniqueId());
        try {

            if (playerId == 0) return;
            PreparedStatement ps = Main.getPlugin().getDatabase().openConnection().prepareStatement("UPDATE players SET `player_name` =?, `update`=?, `extend`=?, `ranked`=? WHERE player_id =?");
            ps.setString(1, player.getName());
            ps.setInt(2, user.updateRank());
            ps.setInt(3, user.extendRank());
            ps.setInt(4, user.rankedrank());
            ps.setInt(5, playerId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Main.getPlugin().getDatabase().closeConnection();
        }
    }

    public static int getClearedCourseCount(Parkour parkour, Player player) {
        int count = 0;
        int player_id = getPlayerId(player.getUniqueId());
        int courseId = getCourseId(parkour);
        try {
            if (courseId == 0)
                return 0;
            if(player_id == 0)
                return 0;

            PreparedStatement ps = Main.getPlugin().getDatabase().openConnection().prepareStatement("SELECT count FROM cleared_course WHERE course_id=? AND player_id=? LIMIT 1;");
            ps.setInt(1, courseId);
            ps.setInt(2, player_id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt("count");
            } else {
                count = 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Main.getPlugin().getDatabase().closeConnection();
        }
        return count;
    }

    public static void updateClearedCourse(Parkour parkour, Player player) {
        int playerId = getPlayerId(player.getUniqueId());
        int course_id = getCourseId(parkour);
        Connection con = getConnection();
        int count = getClearedCourseCount(parkour, player) + 1;
        try {
            if(playerId == 0 && course_id == 0) return;

            PreparedStatement prepStmt = Main.getPlugin().getDatabase().openConnection().prepareStatement("UPDATE cleared_course SET count =? WHERE player_id ='" + playerId + "' AND course_id='" + course_id + "';");
            prepStmt.setInt(1, count);
            prepStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Main.getPlugin().getDatabase().closeConnection();
        }
    }

    public static void updateCourse(Parkour parkour, String rename) {
        try {
            int courseId = getCourseId(parkour);
            if (courseId == 0)
                return;
            PreparedStatement ps = Main.getPlugin().getDatabase().openConnection().prepareStatement("UPDATE course SET name =?, category =?, author =?, timeattack=? WHERE courseId =?");
            ps.setString(1, rename);
            ps.setString(2, parkour.category.name);
            ps.setString(3, parkour.creator);
            ps.setBoolean(4, parkour.timeAttackEnable);
            ps.setInt(5, courseId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Main.getPlugin().getDatabase().closeConnection();
        }
    }

    public static double getVotePercent(Parkour courseName) {
        double percentage = 0.0D;
        try {
            int courseId = getCourseId(courseName);
            if (courseId == 0)
                return 0.0D;
            PreparedStatement ps = Main.getPlugin().getDatabase().openConnection().prepareStatement("SELECT count(*) AS votes, (SELECT count(*) FROM vote WHERE liked = 1 AND courseId=?) AS likes FROM vote WHERE courseId=?;");
            ps.setInt(1, courseId);
            ps.setInt(2, courseId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int total = rs.getInt("votes");
                int likes = rs.getInt("likes");
                if (total > 0)
                    percentage = likes * 1.0D / total * 100.0D;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Main.getPlugin().getDatabase().closeConnection();
        }
        return percentage;
    }

    public static boolean hasVoted(Parkour courseName, String playerName) {
        boolean voted = true;
        try {
            int courseId = getCourseId(courseName);
            if (courseId == 0)
                return true;
            PreparedStatement ps = Main.getPlugin().getDatabase().openConnection().prepareStatement("SELECT 1 FROM vote WHERE courseId=? AND player=? LIMIT 1;");
            ps.setInt(1, courseId);
            ps.setString(2, playerName);
            ResultSet rs = ps.executeQuery();
            if (!rs.next())
                voted = false;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Main.getPlugin().getDatabase().closeConnection();
        }
        return voted;
    }

    public static void deleteAllTimesForPlayer(String playerName) {
        try {
            PreparedStatement ps = Main.getPlugin().getDatabase().openConnection().prepareStatement("DELETE FROM `time` WHERE `player`=?;");
            ps.setString(1, playerName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Main.getPlugin().getDatabase().closeConnection();
        }
    }

    public static void deleteCourseAndReferences(String courseName) {
        try {
            PreparedStatement ps = Main.getPlugin().getDatabase().openConnection().prepareStatement("DELETE FROM `course` WHERE `name`=?;");
            ps.setString(1, courseName);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Main.getPlugin().getDatabase().closeConnection();
        }
    }

    public static void deleteCourseTimes(Parkour parkour) {
        try {
            int courseId = getCourseId(parkour);
            if (courseId == 0)
                return;
            PreparedStatement ps = Main.getPlugin().getDatabase().openConnection().prepareStatement("DELETE FROM `time` WHERE `courseId`=?;");
            ps.setInt(1, courseId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Main.getPlugin().getDatabase().closeConnection();
        }
    }

    public static void deletePlayerCourseTimes(String playerName, Parkour courseName) {
        try {
            int courseId = getCourseId(courseName);
            if (courseId == 0)
                return;
            PreparedStatement ps = Main.getPlugin().getDatabase().openConnection().prepareStatement("DELETE FROM `time` WHERE `player`=? AND `courseId`=?;");
            ps.setString(1, playerName);
            ps.setInt(2, courseId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Main.getPlugin().getDatabase().closeConnection();
        }
    }

    public static boolean hasPlayerCompleted(String playerName, Parkour courseName) {
        boolean completed = true;
        try {
            int courseId = getCourseId(courseName);
            if (courseId == 0)
                return true;
            PreparedStatement ps = Main.getPlugin().getDatabase().openConnection().prepareStatement("SELECT 1 FROM time WHERE courseId=? AND player=? LIMIT 1;");
            ps.setInt(1, courseId);
            ps.setString(3, playerName);
            ResultSet rs = ps.executeQuery();
            if (!rs.next())
                completed = false;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Main.getPlugin().getDatabase().closeConnection();
        }
        return completed;
    }

    public static void recreateAllCourses(ParkourSet parkours) {
        Utils.logToFile("Started courses recreation.");
        Utils.log("Starting recreation of courses process...");
        int changes = 0;
        for (Parkour parkour : parkours.getParkours()) {
            if (getCourseId(parkour) == 0) {
                insertCourse(parkour, parkour.creator);
                changes++;
            }
        }
        Utils.log("Process complete. Courses recreated: " + changes);
    }

    private static Connection getConnection() {
        return Main.getPlugin().getDatabase().openConnection();
    }
}
