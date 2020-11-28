package elicode.parkour.command.commands.parkour;

import elicode.parkour.Main;
import elicode.parkour.command.BaseCommand;
import elicode.parkour.command.commands.Permissions;
import elicode.parkour.command.commands.parkours.subcommands.*;
import org.bukkit.command.CommandSender;

public class ParkourCommand extends BaseCommand {

    public ParkourCommand(final Main plugin) {
        super(plugin, "parkour",Permissions.DUEL, false);
        child(

        );
    }

    @Override
    protected void execute(final CommandSender sender, final String label, final String[] args) {
        //sender.sendMessage("test");
    }

}
