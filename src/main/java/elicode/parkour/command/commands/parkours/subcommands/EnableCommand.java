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

public class EnableCommand extends BaseCommand {

    private ParkourSet parkours = ParkourSet.getInstance();

    public EnableCommand(final Main plugin) {
        super(plugin, "enable", "enable [name]", "指定されたコースを有効化", 2, true);
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

        if(parkour.enable){
            Text.stream("$parkour-&r-は既に有効化されています。")
                    .setAttribute("$parkour", name)
                    .color()
                    .setReceiver(player)
                    .sendChatMessage();
            return;
        }

        //アスレを有効化する
        parkour.update(it -> it.enable = true);

        Text.stream("$parkour-&r-を有効化しました。")
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
