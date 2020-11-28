package elicode.parkour.command.commands.tags.subcommands;

import elicode.parkour.Main;
import elicode.parkour.command.BaseCommand;
import elicode.parkour.command.Sender;
import elicode.parkour.parkour.ParkourSet;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HelpCommand extends BaseCommand {

    public HelpCommand(final Main plugin) {
        super(plugin, "delete", "delete [name]", "Deletes an arena.", 2, false);
    }

    @Override
    protected void execute(final CommandSender sender, final String label, final String[] args) {
        final Player player = (Player) sender;
        //final Sign sign = BlockUtil.getTargetBlock(player, Sign.class, 6);

        /*if (sign == null) {
            lang.sendMessage(sender, "ERROR.sign.not-a-sign");
            return;
        }

        final int bet = NumberUtil.parseInt(args[1]).orElse(0);
        Kit kit = null;

        if (!args[2].equals("-")) {
            String name = StringUtils.join(args, " ", 2, args.length).replace("-", " ");
            kit = kitManager.get(name);

            if (kit == null) {
                lang.sendMessage(sender, "ERROR.kit.not-found", "name", name);
                return;
            }
        }

        final String kitName = kit != null ? kit.getName() : lang.getMessage("GENERAL.none");
        final Queue queue = queueManager.get(kit, bet);

        if (queue == null) {
            lang.sendMessage(sender, "ERROR.queue.not-found", "bet_amount", bet, "kit", kitName);
            return;
        }

        if (!queueSignManager.create(player, sign.getLocation(), queue)) {
            lang.sendMessage(sender, "ERROR.sign.already-exists");
            return;
        }
        se
        final Location location = sign.getLocation();
        lang.sendMessage(sender, "COMMAND.duels.add-sign", "location", StringUtil.parse(location), "kit", kitName, "bet_amount", bet);*/
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        if (args.length == 2) {
            return Arrays.asList("0", "10", "50", "100", "500", "1000");
        }

        if (args.length > 2) {
            return handleTabCompletion(args[2], ParkourSet.getInstance().getParkours().stream().map(parkour -> parkour.name).collect(Collectors.toList()));
        }

        return null;
    }
}
