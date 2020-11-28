package elicode.parkour.command.commands.parkours.subcommands;

import elicode.parkour.Main;
import elicode.parkour.command.BaseCommand;
import elicode.parkour.parkour.ParkourSet;
import elicode.parkour.parkour.RecordIds;
import elicode.parkour.util.databases.DatabaseManager;
import elicode.parkour.util.databases.SQLQuery;
import org.bukkit.command.CommandSender;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class RecordIdsCommand extends BaseCommand {

    private ParkourSet parkours = ParkourSet.getInstance();

    public RecordIdsCommand(final Main plugin) {
        super(plugin, "recordIds", "recordIds [parkour] [player]", "コースを削除", 3, false);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {

    }

    /*private final Map<UUID, Long> records;

    public Map<UUID, Long> getRecords(SQLQuery sqlQuery, Object... parameters) {
        Map<UUID, Long> ptList = new ArrayList<>();

        ResultSet rs = DatabaseManager.get().executeResultStatement(sqlQuery, parameters);
        try {
            while (rs.next()) {
                Punishment punishment = getPunishmentFromResultSet(rs);
                ptList.add(punishment);
            }
            rs.close();
        } catch (SQLException ex) {
            universal.log("An error has occurred executing a query in the database.");
            universal.debug("Query: \n" + sqlQuery);
            universal.debugSqlException(ex);
        }
        return ptList;
    }

    public Punishment getPunishmentFromResultSet(ResultSet rs) throws SQLException {
        return new RecordIds(
                rs.getString("timeId"),
                rs.getString("uuid"), rs.getString("reason"),
                rs.getString("operator"),
                PunishmentType.valueOf(rs.getString("punishmentType")),
                rs.getLong("start"),
                rs.getLong("end"),
                rs.getString("calculation"),
                rs.getInt("id"));
    }*/
}
