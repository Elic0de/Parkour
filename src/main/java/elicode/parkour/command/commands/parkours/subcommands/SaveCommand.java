package elicode.parkour.command.commands.parkours.subcommands;

import elicode.parkour.Main;
import elicode.parkour.command.BaseCommand;
import elicode.parkour.lobby.LobbySet;
import elicode.parkour.parkour.Parkour;
import elicode.parkour.parkour.ParkourSet;
import elicode.parkour.user.UserSet;
import elicode.parkour.util.text.Text;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class SaveCommand extends BaseCommand {

    private ParkourSet parkours = ParkourSet.getInstance();

    public SaveCommand(final Main plugin) {
        super(plugin, "save", "save", "すべてのデータを保存", 1, true);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        final Player player = (Player) sender;

        LobbySet.getInstance().saveAll();
        UserSet.getInstnace().saveAll();
        ParkourSet.getInstance().saveAll();

        Text.stream("すべてのデータをセーブしました。")
                .color()
                .setReceiver(player)
                .sendChatMessage();
    }

}
