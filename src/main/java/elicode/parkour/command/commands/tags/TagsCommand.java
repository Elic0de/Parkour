package elicode.parkour.command.commands.tags;

import elicode.parkour.Main;
import elicode.parkour.command.BaseCommand;
import elicode.parkour.command.Sender;
import elicode.parkour.command.commands.Permissions;
import elicode.parkour.command.commands.tags.subcommands.HelpCommand;
import org.bukkit.command.CommandSender;

public class TagsCommand extends BaseCommand {


    public TagsCommand(final Main plugin) {
        super(plugin, "tags", Permissions.ADMIN, false);
        child(
               new HelpCommand(plugin)
                );
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        //lang.sendMessage(sender, "COMMAND.duels.usage", "command", label);
    }
}
