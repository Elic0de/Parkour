package elicode.parkour.command;

import elicode.parkour.Main;
import elicode.parkour.config.Lang;
import elicode.parkour.util.command.AbstractCommand;
import elicode.parkour.util.text.Text;
import elicode.parkour.util.text.TextStream;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseCommand extends AbstractCommand {

    protected final Lang lang;

    protected BaseCommand(final Main plugin, final String name, final String usage, final String description, final String permission, final int length,
                          final boolean playerOnly, final String... aliases) {
        super(plugin, name, usage, description, permission, length, playerOnly, aliases);
        this.plugin = plugin;
        this.lang = plugin.getLang();
    }

    /**
     * Constructor for a sub command, inherits parent permission
     */
    protected BaseCommand(final Main plugin, final String name, final String usage, final String description, final int length, final boolean playerOnly,
                          final String... aliases) {
        this(plugin, name, usage, description, null, length, playerOnly, aliases);
    }

    /**
     * Constructor for a parent command
     */
    protected BaseCommand(final Main plugin, final String name, final String permission, final boolean playerOnly) {
        this(plugin, name, null, null, permission, -1, playerOnly);
    }

    @Override
    protected void handleMessage(final CommandSender sender, final MessageType type, final String... args) {
        switch (type) {
            case PLAYER_ONLY:
                super.handleMessage(sender, type, args);
                break;
            case NO_PERMISSION:
                Text.stream("&cNo！ 権限がありません: $permission")
                        .setAttribute("$permission", args[0])
                        .color()
                        .setReceiver((Player) sender)
                        .sendChatMessage();
                //lang.sendMessage(sender, "ERROR.no-permission", "permission", args[0]);
                break;
            case SUB_COMMAND_INVALID:
                Text.stream("&c'$argument'は有効なサブコマンドではありません。ヘルプを表示するには、/$commandと入力してください")
                        .setAttribute("$argument", args[1])
                        .setAttribute("$command", args[0])
                        .color()
                        .setReceiver((Player) sender)
                        .sendChatMessage();
                //lang.sendMessage(sender, "ERROR.command.invalid-sub-command", "command", args[0], "argument", args[1]);
                break;
            case SUB_COMMAND_USAGE:
                Text.stream("&f/$command $usage &e-- &7$description")
                        .setAttribute("$command", args[0])
                        .setAttribute("$usage", args[1])
                        .setAttribute("$description", args[2])
                        .color()
                        .setReceiver((Player) sender)
                        .sendChatMessage();

                //sender.sendMessage(args[0] + " " + args[1] + " " + args[2]);
                //lang.sendMessage(sender, "COMMAND.sub-command-usage", "command", args[0], "usage", args[1], "description", args[2]);
                break;
        }
    }

    protected List<String> handleTabCompletion(final String argument, final Collection<String> collection) {
        return collection.stream()
                .filter(value ->value.toLowerCase().startsWith(argument.toLowerCase()))
                .map(value -> value.replace(" ", "-"))
                .collect(Collectors.toList());
    }

}
