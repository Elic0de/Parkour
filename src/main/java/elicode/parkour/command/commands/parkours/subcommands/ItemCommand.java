package elicode.parkour.command.commands.parkours.subcommands;

import elicode.parkour.Main;
import elicode.parkour.command.BaseCommand;
import elicode.parkour.function.creative.CheckPointItem;
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

public class ItemCommand extends BaseCommand {

    private ParkourSet parkours = ParkourSet.getInstance();

    public ItemCommand(final Main plugin) {
        super(plugin, "item", "item", "parkour item", 1, true);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        final Player player = (Player) sender;

        player.getInventory().setItem(0,new CheckPointItem().build(UserSet.getInstnace().getUser(player)));
    }

}
