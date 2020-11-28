package elicode.parkour.command.commands.parkours.subcommands;

import elicode.parkour.Main;
import elicode.parkour.command.BaseCommand;
import elicode.parkour.parkour.Parkour;
import elicode.parkour.parkour.ParkourSet;
import elicode.parkour.parkour.Records;
import elicode.parkour.util.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RemoveTimeCommand extends BaseCommand {

    private ParkourSet parkours = ParkourSet.getInstance();

    public RemoveTimeCommand(final Main plugin) {
        super(plugin, "removetime", "removetime [parkour] [player]", "タイムを削除", 3, true);
    }

    @Override
    protected void execute(final CommandSender sender, final String label, final String[] args) {
        final String name = ChatColor.translateAlternateColorCodes('&', args[1]);
        @SuppressWarnings("deprecation")
        final OfflinePlayer player = Bukkit.getOfflinePlayer(args[2]);
        final Parkour parkour = ParkourSet.getInstance().getParkour(name);

        if (parkour == null) {
            sender.sendMessage("mitukarimasenn");
            return;
        }

        if(player == null) {
           return;
        }

        //アスレが登録されていれば登録を解除する
        Records records = parkour.records;

        if(!records.containsRecord(player.getUniqueId())){
            sender.sendMessage("mitukarimasenn");
            return;
        }

        records.withdrawRecord(player.getUniqueId());

        Text.stream("タイムを削除しました")
                .setAttribute("$parkour", name)
                .color()
                .setReceiver((Player) sender)
                .sendChatMessage();


    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        final String name = ChatColor.translateAlternateColorCodes('&', args[1]);
        final Parkour parkour = ParkourSet.getInstance().getParkour(name);


        if (args.length == 2) {
            return handleTabCompletion(args[1], parkours.getParkours().stream().filter(parkour1 -> parkour1.timeAttackEnable).map(Parkour::getColorAndParkourName).collect(Collectors.toList()));
        }

        if (args.length == 3 && parkour != null) {
            return handleTabCompletion(args[2], parkour.records.topTenRecords.stream().map(uuidStringTuple -> Bukkit.getOfflinePlayer(uuidStringTuple.first).getName()).collect(Collectors.toList()));
        }

        return null;
    }
}