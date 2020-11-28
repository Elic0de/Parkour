package elicode.parkour.command.commands.parkours.subcommands;

import elicode.parkour.Main;
import elicode.parkour.command.BaseCommand;
import elicode.parkour.parkour.Parkour;
import elicode.parkour.parkour.ParkourSet;
import elicode.parkour.region.selection.RegionSelectionSet;
import elicode.parkour.util.text.Text;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class LinkCommand extends BaseCommand {

    private ParkourSet parkours = ParkourSet.getInstance();
    private final RegionSelectionSet selections = RegionSelectionSet.getInstance();

    public LinkCommand(final Main plugin) {
        super(plugin, "link", "link [name] [linkparkour]", "指定されたコースをリンクする", 3, true);
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

        UUID uuid = player.getUniqueId();

        //対象となるアスレの名前を取得する
        String parkourName = selections.hasSelection(uuid) ? selections.getSelectedParkourName(uuid) : ChatColor.translateAlternateColorCodes('&', args[1]);

        //アスレが存在しなければ戻る
        if (!parkours.containsParkour(parkourName)) {
            //sender.warn("指定されたアスレは存在しません。");
            return;
        }
        //修正必要
        //Parkour parkour = parkours.getParkour(parkourName);


        String course = ChatColor.translateAlternateColorCodes('&', args[2]);

        //アスレが存在しなければ戻る
        if (!parkours.containsParkour(course)) {
            //sender.warn("指定されたリンクするアスレは存在しません。");
            return;
        }
        Text.stream("$parkour-&r-とリンクしました。")
                .setAttribute("$parkour", course)
                .color()
                .setReceiver(player)
                .sendChatMessage();

        parkour.linkedCourse = course;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        if (args.length == 2) {
            return handleTabCompletion(args[1], parkours.getParkours().stream().map(Parkour::getColorAndParkourName).collect(Collectors.toList()));
        }

        return null;
    }
}
