package elicode.parkour.command.parkour;

import elicode.parkour.command.Arguments;
import elicode.parkour.command.Command;
import elicode.parkour.command.Sender;
import elicode.parkour.parkour.Parkour;
import elicode.parkour.parkour.ParkourSet;
import elicode.parkour.region.selection.RegionSelectionSet;
import elicode.parkour.util.text.Text;
import elicode.parkour.util.text.TextStream;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ParkourLinkCommand implements Command {

    private final ParkourSet parkours = ParkourSet.getInstance();
    private final RegionSelectionSet selections = RegionSelectionSet.getInstance();


    // TODO
    @Override
    public void onCommand(Sender sender, Arguments args) {
        //プレイヤーでなければ戻る
        if (blockNonPlayer(sender)) return;

        if(hasPermission(sender,this.getClass().getSimpleName())) return;

        //第1引数が無ければ戻る
        if (!args.hasNext()) {
            // displayCommandUsage(sender);
            return;
        }

        //送信者のUUIDを取得する
        Player player = sender.asPlayerCommandSender();
        UUID uuid = player.getUniqueId();

        //対象となるアスレの名前を取得する
        String parkourName = selections.hasSelection(uuid) ? selections.getSelectedParkourName(uuid) : ChatColor.translateAlternateColorCodes('&', args.next());

        //アスレが存在しなければ戻る
        if (!parkours.containsParkour(parkourName)) {
            sender.warn("指定されたアスレは存在しません。");
            return;
        }
        //修正必要
        Parkour parkour = parkours.getParkour(parkourName);

        if(!args.hasNext()){
            sender.warn("リンクするコースを指定してください");
            return;
        }

        String course = ChatColor.translateAlternateColorCodes('&', args.next());

        //アスレが存在しなければ戻る
        if (!parkours.containsParkour(course)) {
            sender.warn("指定されたリンクするアスレは存在しません。");
            return;
        }
        Text.stream("$parkour-&r-とリンクしました。")
                .setAttribute("$parkour", course)
                .color()
                .setReceiver(player)
                .sendChatMessage();

        parkour.linkedCourse = course;


        switch (args.next()) {
            case "info": {
                List<TextStream> texts = Arrays.asList(
                        Text.stream("&7-: &b-Name &7-@ &f-$parkour")
                                .setAttribute("$parkour", parkourName),
                        Text.stream("&7-: &b-Category &7-@ &f-$category")
                                .setAttribute("$category", parkour.category.name),
                        Text.stream("&7-: &b-Description &7-@ &f-$description")
                                .setAttribute("$description", parkour.description),
                        Text.stream("&7-: &b-Spawn &7-@ &f-$spawn")
                                .setAttribute("$spawn", parkour.spawn.serialize()),
                        Text.stream("&7-: &b-Color &7-@ &f-$color")
                                .setAttribute("$color", parkour.borderColor.serialize()),
                        Text.stream("&7-: &b-Time Attack &7-@ &f-$enable")
                                .setAttribute("$enable", parkour.timeAttackEnable ? "&b-有効" : "&7-無効"),
                        Text.stream("&7-: &b-linkedCourse &7-@ &f-$linkedcourse")
                                .setAttribute("$linkedcourse", parkour.linkedCourse),
                        Text.stream("&7-: &b-linkedLobby &7-@ &f-$linkedlobby")
                                .setAttribute("$linkedlobby", parkour.linkedLobby)
                );
                texts.forEach(text -> text.color().setReceiver(player).sendChatMessage());
                break;
            }
        }
    }
}