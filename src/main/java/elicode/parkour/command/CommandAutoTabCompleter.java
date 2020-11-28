package elicode.parkour.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandAutoTabCompleter implements TabCompleter, elicode.parkour.command.Command {

    private static final Set<String> BASIC_CMDS = new HashSet<>(
            Arrays.asList("party", "friend", "stats", "tweet", "item", "duel"));

    @Override
    public List<String> onTabComplete(CommandSender sender,Command command,  String alias, String[] args) {

        if(blockNonPlayer((Sender) sender)) return null;
        return null;
    }

    @Override
    public void onCommand(Sender sender, Arguments args) {

    }
}
