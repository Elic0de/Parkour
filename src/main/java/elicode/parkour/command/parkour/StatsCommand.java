package elicode.parkour.command.parkour;

import elicode.parkour.Main;
import elicode.parkour.command.Arguments;
import elicode.parkour.command.Command;
import elicode.parkour.command.Sender;
import elicode.parkour.function.parkour.ControlFunctionalItem;
import elicode.parkour.parkour.Parkour;
import elicode.parkour.parkour.ParkourSet;
import elicode.parkour.user.User;
import elicode.parkour.user.UserSet;
import elicode.parkour.util.text.BilingualText;
import elicode.parkour.util.text.Text;
import elicode.parkour.util.text.TextStream;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class StatsCommand implements Command {

    private final ParkourSet parkours = ParkourSet.getInstance();
    private UserSet users = UserSet.getInstnace();

    @Override
    public void onCommand(Sender sender, Arguments args) {
        //プレイヤーでなければ戻る
        if(blockNonPlayer(sender)) return;

        if(!args.hasNext()) return;


        //第1引数をプレイヤー名として取得する
        String playerName = args.next();

        @SuppressWarnings("deprecation")
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        UUID uuid = player.getUniqueId();

        //指定されたプレイヤーがサーバーに一度も参加した事がなければ戻る
        if(!users.containsUser(uuid)){
            Text.stream("&c-$playerはサーバーに参加した事がありません。")
                    .setAttribute("$player", playerName)
                    .color()
                    .setReceiver(sender.asPlayerCommandSender())
                    .sendChatMessage();
            return;
        }


        //ユーザーを取得する
        User user = users.getUser(uuid);

        List<TextStream> texts = Arrays.asList(
                Text.stream("&7-: &b-UserName &7-@ &f-$mcid")
                        .setAttribute("$mcid", player.getName()),
                Text.stream("&7-: &b-Update &7-@ &f-$update")
                        .setAttribute("$update", user.updateRank()),
                Text.stream("&7-: &b-Extend &7-@ &f-$extend")
                        .setAttribute("$extend", user.extendRank())
                /*Text.stream("&7-: &b-Level &7-@ &f-$finish_line")
                        .setAttribute("$finish_line", Main.getPlugin().getLevel().getLevel((Player) player)),*/
                /*Text.stream("&7-: &b-Karma &7-@ &f-$portal")
                        .setAttribute("$portal", Main.getPlugin().getKarmaPlugin().getPlayerKarma((Player) player))*/

        );
        texts.forEach(text -> text.color().setReceiver(sender.asPlayerCommandSender()).sendChatMessage());
    }

}
