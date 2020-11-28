package elicode.parkour.command.commands.parkours.subcommands;

import elicode.parkour.Main;
import elicode.parkour.command.BaseCommand;
import elicode.parkour.parkour.Parkour;
import elicode.parkour.parkour.ParkourSet;
import elicode.parkour.util.text.Text;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


import java.util.List;
import java.util.stream.Collectors;

public class DeleteCommand extends BaseCommand {

    private ParkourSet parkours = ParkourSet.getInstance();

    public DeleteCommand(final Main plugin) {
        super(plugin, "delete", "delete [name]", "コースを削除", 2, false);
    }

    @Override
    protected void execute(final CommandSender sender, final String label, final String[] args) {
        final String name = ChatColor.translateAlternateColorCodes('&', args[1]);
        final Parkour parkour = ParkourSet.getInstance().getParkour(name);

        if (parkour == null) {
            sender.sendMessage("mitukarimasenn");
            return;
        }

        //アスレが登録されていれば登録を解除する
        parkours.unregisterParkour(name);

        //ファイルを削除する
        parkours.makeYaml(name.replace('§', '&')).file.delete();

        //DatabaseManager.get().executeStatement(SQLQuery.DELETE_PLAYER_COURSE_TIME,);
        //DatabaseUtils.deletePlayerCourseTimes();

        Text.stream("$parkour-&r-を削除しました。")
                .setAttribute("$parkour", name)
                .color()
                .setReceiver((Player)sender)
                .sendChatMessage();
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        if (args.length == 2) {
            return handleTabCompletion(args[1], parkours.getParkours().stream().map(Parkour::getColorAndParkourName).collect(Collectors.toList()));
        }

        return null;
    }
}