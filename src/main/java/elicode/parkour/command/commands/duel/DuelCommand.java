package elicode.parkour.command.commands.duel;

import elicode.parkour.Main;
import elicode.parkour.command.BaseCommand;
import elicode.parkour.command.commands.Permissions;
import elicode.parkour.command.commands.parkours.subcommands.*;
import org.bukkit.command.CommandSender;

public class DuelCommand extends BaseCommand {

    public DuelCommand(final Main plugin) {
        super(plugin, "parkours", Permissions.ADMIN, false);
        child(
        );
    }

    @Override
    protected void execute(final CommandSender sender, final String label, final String[] args) {
        //sender.sendMessage("test");
    }
}