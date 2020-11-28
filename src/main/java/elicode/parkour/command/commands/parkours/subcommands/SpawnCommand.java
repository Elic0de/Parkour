package elicode.parkour.command.commands.parkours.subcommands;

import elicode.beta.parkour.location.ImmutableLocation;
import elicode.parkour.Main;
import elicode.parkour.command.BaseCommand;
import elicode.parkour.parkour.Parkour;
import elicode.parkour.parkour.ParkourSet;
import elicode.parkour.util.text.Text;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class SpawnCommand extends BaseCommand {

    private ParkourSet parkours = ParkourSet.getInstance();

    public SpawnCommand(final Main plugin) {
        super(plugin, "spawn", "spawn [name]", "スポーン地点を設定", 2, true);
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

        //プレイヤーの座標を取得する
        Location location = player.getLocation();

        //イミュータブルな座標にしブロックの中央に調整した上でセットする
        parkour.spawn = new ImmutableLocation(location);

        Text.stream("$parkour-&r-のスポーン地点を現在地点に書き換えました。")
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
