package elicode.parkour.util.databases;

public enum SQLQuery {
    CREATE_TABLE_PARKOUR(
            /*"CREATE TABLE IF NOT EXISTS players ("+
                    "player_id INTEGER PRIMARY KEY,"+
                    "player_name VARCHAR(16) NOT NULL,"+
                    "player_uuid CHAR(38) NOT NULL,"+
                    "'update' INTEGER NOT NULL,"+
                    "'extend' INTEGER NOT NULL,"+
                    "'ranked' INTEGER NOT NULL);"*/
            "CREATE TABLE IF NOT EXISTS players (" +
                    "player_id INTEGER PRIMARY KEY," +
                    "player_name VARCHAR(16) NOT NULL," +
                    "player_uuid CHAR(38) NOT NULL," +
                    "'update' INTEGER NOT NULL," +
                    "'extend' INTEGER NOT NULL," +
                    "'ranked' INTEGER NOT NULL)" +

                    ";CREATE TABLE IF NOT EXISTS course (" +
                    "courseId INTEGER PRIMARY KEY," +
                    "name VARCHAR(15) NOT NULL UNIQUE," +
                    "category VARCHAR(15) NOT NULL," +
                    "author VARCHAR(20) NOT NULL," +
                    "timeattack BOOLEN(1) NOT NULL," +
                    "created TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL);" +

                    "CREATE TABLE IF NOT EXISTS time (" +
                    "timeId INTEGER PRIMARY KEY," +
                    "courseId INTEGER NOT NULL," +
                    "player VARCHAR(20) NOT NULL," +
                    "time DECIMAL(13,0) NOT NULL," +
                    "deaths INT(5) NOT NULL," +
                    "cleared_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL," +
                    "FOREIGN KEY (courseId) REFERENCES course(courseId) ON DELETE CASCADE ON UPDATE CASCADE); " +
                    "" +
                    "CREATE TABLE IF NOT EXISTS vote " +
                    "(courseId INTEGER NOT NULL," +
                    "player VARCHAR(20) NOT NULL," +
                    "liked BIT NOT NULL," +
                    "PRIMARY KEY (courseId, player)," +
                    "FOREIGN KEY (courseId) REFERENCES course(courseId) ON DELETE CASCADE ON UPDATE CASCADE); "
    ),
    CREATE_TABLE_CLEARED_COURSE("CREATE TABLE IF NOT EXISTS cleared_course (player_id INTEGER NOT NULL, course_id INTEGER NOT NULL, count INT(5), first_cleared_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL);"),
    CREATE_TABLE_VOTE("CREATE TABLE IF NOT EXISTS vote ("+
            "courseId INTEGER NOT NULL,"+
            "player VARCHAR(20) NOT NULL,"+
            "liked BIT NOT NULL,"+
            "PRIMARY KEY (courseId, player),"+
            "FOREIGN KEY (courseId) REFERENCES course(courseId) ON DELETE CASCADE ON UPDATE CASCADE); "),
    CREATE_TABLE_TIME("CREATE TABLE IF NOT EXISTS time ("+
            "timeId INTEGER PRIMARY KEY,"+
            "courseId INTEGER NOT NULL,"+
            "player VARCHAR(20) NOT NULL,"+
            "time DECIMAL(13,0) NOT NULL,"+
            "deaths INT(5) NOT NULL,"+
            "cleared_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,"+
            "FOREIGN KEY (courseId) REFERENCES course(courseId) ON DELETE CASCADE ON UPDATE CASCADE);"),
    CREATE_TABLE_COURSE("CREATE TABLE IF NOT EXISTS course ("+
            "courseId INTEGER PRIMARY KEY,"+
            "name VARCHAR(15) NOT NULL UNIQUE,"+
            "category VARCHAR(15) NOT NULL,"+
            "author VARCHAR(20) NOT NULL,"+
            "timeattack BOOLEN(1) NOT NULL,"+
            "created TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL);"),
    INSERT_COURSE(
            "INSERT INTO `course` "+
                    "(`name`,category, `author`,`timeattack`) " +
                    "VALUES (?,?,?,?);"
    ),
    INSERT_CLEARED_COURSE(
            "INSERT INTO `cleared_course` "+"" +
                    "(`player_id`, `course_id`, `count` ) " +
                    "VALUES (?, ?, ?);"
    ),
    INSERT_PLAYER_DATA(
            "INSERT INTO players "+
                    "(`player_name`,`player_uuid`,`update`, `extend`, `ranked`) "+
                    "VALUES (?, ?, ?, ?, ?)"
    ),
    INSERT_COURSE_TIME(
            "INSERT INTO `time` "+
                    "(`courseId`, `player`, `time`, `deaths`) "+
                    "VALUES (?, ?, ?, ?);"
    ),
    INSERT_COURSE_VOTE(
            "INSERT INTO `vote` "+
                    "(courseId, player, liked) "+
                    "VALUES (?, ?, ?);"
    ),
    UPDATE_COURSE(
            "UPDATE course SET name =?, category =?, author =?, timeattack =? WHERE courseId =?"
    ),
    UPDATE_CLEARED_COURSE(
            "UPDATE cleared_course SET count =? WHERE player_id =? AND course_id=?;"
    ),
    UPDATE_PLAYER_DATA(
            "UPDATE players SET `player_name` =?, `update`=?, `extend`=?, `ranked`=? WHERE player_id =?"
    ),
    SELECT_COURSE_COUNT(
            "SELECT count FROM cleared_course WHERE course_id=? AND player_id=? LIMIT 1;"
    ),
    SELECT_VOTE_PERCENT(
            "SELECT count(*) AS votes, (SELECT count(*) FROM vote WHERE liked = 1 AND courseId=?) AS likes FROM vote WHERE courseId=?;"
    ),
    SELECT_COURSE_ID(
            "SELECT courseId FROM course WHERE name = ?;"
    ),
    SELECT_PLAYER_ID(
            "SELECT player_id, player_name from players WHERE player_uuid=? LIMIT 1"
    ),
    DELETE_PLAYER_COURSE_TIME(
            "DELETE FROM `time` WHERE `player`=? AND `courseId`=?;"
    );


    private String mysql;

    SQLQuery(String mysql) {
        this.mysql = mysql;
    }
    @Override
    public String toString() {
        return mysql;
    }
}
