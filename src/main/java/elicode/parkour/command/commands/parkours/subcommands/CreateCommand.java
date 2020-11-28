package elicode.parkour.command.commands.parkours.subcommands;

import elicode.parkour.Main;
import elicode.parkour.command.BaseCommand;
import elicode.parkour.parkour.Parkour;
import elicode.parkour.parkour.ParkourSet;
import elicode.parkour.util.databases.DatabaseManager;
import elicode.parkour.util.databases.SQLQuery;
import elicode.parkour.util.text.Text;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateCommand extends BaseCommand {

    private ParkourSet parkours = ParkourSet.getInstance();

    public CreateCommand(final Main plugin) {
        super(plugin, "create", "create [name]", "指定された名前でコースを作成します", 2, true);
    }

    @Override
    protected void execute(final CommandSender sender, final String label, final String[] args) {
        final String parkourName = ChatColor.translateAlternateColorCodes('&',args[1]);
        Player player = (Player) sender;

        //対応したファイルが存在していれば戻る
        if(parkours.containsParkour(parkourName)){
            Text.stream("$parkour-&r-は既に存在しています。")
                    .setAttribute("$parkour", parkourName)
                    .color()
                    .setReceiver(player)
                    .sendChatMessage();
            return;
        }

        //アスレ名の先頭に装飾コードが存在しない場合
        if(!Parkour.PREFIX_PATTERN.matcher(parkourName).find()){
            //sender.warn("アスレ名の先頭には必ず装飾コードを置いて下さい。");
            sender.sendMessage("アスレ名の先頭には必ず装飾コードを置いて下さい。");
            return;
        }

        //ファイルを作成する
        parkours.makeYaml(parkourName.replace('§', '&'));
        //無効化された状態で登録する
        parkours.registerParkour(parkourName);

        Parkour parkour = parkours.getParkour(parkourName);
        parkour.creator = player.getName();

        DatabaseManager.get().executeStatement(SQLQuery.INSERT_COURSE,parkour.colorlessName(),parkour.category,parkour.creator,parkour.timeAttackEnable);
        //DatabaseUtils.insertCourse(parkour,parkour.creator);

        Text.stream("$parkour-&r-のデータを新規作成しました。")
                .setAttribute("$parkour", parkourName)
                .color()
                .setReceiver(player)
                .sendChatMessage();




    }
}