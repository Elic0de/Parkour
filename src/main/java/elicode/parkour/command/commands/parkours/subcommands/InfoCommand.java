package elicode.parkour.command.commands.parkours.subcommands;

import elicode.parkour.Main;
import elicode.parkour.command.BaseCommand;
import elicode.parkour.parkour.Parkour;
import elicode.parkour.parkour.ParkourSet;
import elicode.parkour.util.text.Text;
import elicode.parkour.util.text.TextStream;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InfoCommand extends BaseCommand {

    private ParkourSet parkours = ParkourSet.getInstance();

    public InfoCommand(final Main plugin) {
        super(plugin, "info", "info [name]", "指定されたコースの詳細を表示", 2, true);
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

        List<TextStream> texts = Arrays.asList(
                Text.stream("&7-: &b-State &7-@ &f-$enable")
                        .setAttribute("$enable", parkour.enable ? "&b-有効" : "&7-無効"),
                Text.stream("&7-: &b-Region &7-@ &f-$region")
                        .setAttribute("$region", parkour.region.serialize()),
                Text.stream("&7-: &b-Start Line &7-@ &f-$start_line")
                        .setAttribute("$start_line", parkour.startLine.serialize()),
                Text.stream("&7-: &b-Finish Line &7-@ &f-$finish_line")
                        .setAttribute("$finish_line", parkour.finishLine.serialize()),
                Text.stream("&7-: &b-Portal &7-@ &f-$portal")
                        .setAttribute("$portal", parkour.portal.serialize())

        );

        texts.forEach(text -> text.color().setReceiver(player).sendChatMessage());
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        if (args.length == 2) {
            return handleTabCompletion(args[1], parkours.getParkours().stream().map(Parkour::getColorAndParkourName).collect(Collectors.toList()));
        }

        return null;
    }
}
