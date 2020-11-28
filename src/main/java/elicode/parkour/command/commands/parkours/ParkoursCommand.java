package elicode.parkour.command.commands.parkours;

import elicode.parkour.Main;
import elicode.parkour.command.BaseCommand;
import elicode.parkour.command.commands.Permissions;
import elicode.parkour.command.commands.parkours.subcommands.*;
import org.bukkit.command.CommandSender;

public class ParkoursCommand extends BaseCommand {

    public ParkoursCommand(final Main plugin) {
        super(plugin, "parkours", Permissions.ADMIN, false);
        child(
                new CreateCommand(plugin),
                new DeleteCommand(plugin),
                new EnableCommand(plugin),
                new DisableCommand(plugin),
                new InfoCommand(plugin),
                new RenameCommand(plugin),
                new CategoryCommand(plugin),
                new DifficultyCommand(plugin),
                new RewardsCommand(plugin),
                new EditCommand(plugin),
                new ItemCommand(plugin),
                new SaveCommand(plugin),
                new SetCoinCommand(plugin),
                new RemoveTimeCommand(plugin)
        );
    }

    @Override
    protected void execute(final CommandSender sender, final String label, final String[] args) {
        //sender.sendMessage("test");
    }
}
