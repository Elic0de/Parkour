package elicode.parkour.command.commands.parkours.subcommands;

import elicode.parkour.Main;
import elicode.parkour.command.BaseCommand;
import elicode.parkour.parkour.Parkour;
import elicode.parkour.parkour.ParkourSet;
import elicode.parkour.util.databases.DatabaseManager;
import elicode.parkour.util.databases.SQLQuery;
import elicode.parkour.util.text.Text;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class RenameCommand extends BaseCommand {

    private ParkourSet parkours = ParkourSet.getInstance();

    public RenameCommand(final Main plugin) {
        super(plugin, "rename", "rename [parkour] [name]", "コースの名前を変更", 3, false);
    }

    @Override
    protected void execute(final CommandSender sender, final String label, final String[] args) {
        final String name = ChatColor.translateAlternateColorCodes('&', args[1]);
        final Parkour parkour = ParkourSet.getInstance().getParkour(name);
        final Player player = (Player) sender;

        if (parkour == null) {
            sender.sendMessage("mitukarimasenn");
            return;
        }

        String rename = ChatColor.translateAlternateColorCodes('&', args[2]);
        //対応したファイルが存在していれば戻る
        if(parkours.containsParkour(rename)){
            Text.stream("$parkour-&r-は既に存在しています。")
                    .setAttribute("$parkour", rename)
                    .color()
                    .setReceiver(player)
                    .sendChatMessage();
            return;
        }

        //アスレ名の先頭に装飾コードが存在しない場合
        if(!Parkour.PREFIX_PATTERN.matcher(rename).find()){
            //sender.warn("アスレ名の先頭には必ず装飾コードを置いて下さい。");
            return;
        }
        //Parkour parkour = parkours.getParkour(parkourName);

        //DatabaseUtils.updateCourse(parkour, ChatColor.stripColor(rename));

        //アスレが登録されていれば登録を解除する
        parkours.unregisterParkour(name);
        //表示名を変更する
        parkour.name = rename;

        //変更されたアスレをあたらしい表示名で登録
        parkours.registerParkour(parkour);

        //ファイルを削除する
        parkours.makeYaml(name.replace('§', '&')).file.delete();

        DatabaseManager.get().executeStatement(SQLQuery.UPDATE_COURSE, net.md_5.bungee.api.ChatColor.stripColor(rename),parkour.category.name, parkour.creator,parkour.timeAttackEnable,parkour.getId());


        Text.stream("$parkour-&r-の表示名を$rename&rに変更しました。")
                .setAttribute("$parkour", name)
                .setAttribute("$rename", rename)
                .color()
                .setReceiver(player)
                .sendChatMessage();
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        if (args.length == 2) {
            return handleTabCompletion(args[1], parkours.getParkours().stream().map(Parkour::getColorAndParkourName).collect(Collectors.toList()));
        }

        return null;
    }
}
