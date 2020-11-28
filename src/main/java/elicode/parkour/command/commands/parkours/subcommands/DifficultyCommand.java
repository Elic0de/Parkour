package elicode.parkour.command.commands.parkours.subcommands;

import elicode.parkour.Main;
import elicode.parkour.command.BaseCommand;
import elicode.parkour.parkour.Difficulty;
import elicode.parkour.parkour.Parkour;
import elicode.parkour.parkour.ParkourSet;
import elicode.parkour.util.Splitter;
import elicode.parkour.util.text.Text;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DifficultyCommand extends BaseCommand {

    private ParkourSet parkours = ParkourSet.getInstance();
    private static final Pattern DIFFICULTY_FORMAT = Pattern.compile("[0-9]{1,8},[0-9]{1,8}");

    public DifficultyCommand(final Main plugin) {
        super(plugin, "difficulty", "difficulty [0,0]", "難易度を設定", 2, true);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        final String name = ChatColor.translateAlternateColorCodes('&', args[1]);
        final Player player = (Player) sender;
        final Parkour parkour = ParkourSet.getInstance().getParkour(name);

        if (parkour == null) {
            sender.sendMessage("mitukarimasenn");
            return;
        }

        String text = args[2];

        if(!DIFFICULTY_FORMAT.matcher(text).matches()){
            //sender.warn("/parkour [parkour_name] [first,second_and_subsequent]");
            return;
        }

        //各値に分割する
        int[] difficulty = Splitter.splitToIntArguments(text);

        //難易度を更新する
        parkour.difficulty = new Difficulty(difficulty);

        Text.stream("$parkour-&r-の難易度を書き換えました。")
                .setAttribute("$parkour", name)
                .color()
                .setReceiver(player)
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
