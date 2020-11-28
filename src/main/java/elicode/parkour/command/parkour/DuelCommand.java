package elicode.parkour.command.parkour;

import elicode.parkour.Main;
import elicode.parkour.command.Arguments;
import elicode.parkour.command.Command;
import elicode.parkour.command.Sender;
import elicode.parkour.game.GameManager;
import elicode.parkour.game.duel.Request;
import elicode.parkour.parkour.ParkourCategory;
import elicode.parkour.parkour.ParkourSet;
import elicode.parkour.user.User;
import elicode.parkour.user.UserSet;
import elicode.parkour.util.text.BilingualText;
import elicode.parkour.util.text.Text;
import elicode.parkour.util.text.TextStream;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class DuelCommand implements Command {

    private final ParkourSet parkours = ParkourSet.getInstance();
    private final UserSet users = UserSet.getInstnace();
    private static final ParkourCategory[] COMMON_CATEGORIES = new ParkourCategory[]{ParkourCategory.NORMAL, ParkourCategory.SEGMENT, ParkourCategory.BIOME};


    @Override
    public void onCommand(Sender sender, Arguments args) {
        //送信者がプレイヤーでなければ戻る
        if (blockNonPlayer(sender)) return;

        //第1引数が無ければ戻る
        if (!args.hasNext()) {

            displayCommandUsage(sender);

            return;
        }

        Player player = sender.asPlayerCommandSender();
        User user = users.getUser(player);

        String arg = args.next();

        switch (arg){
            case "cancel":
                if(user.isPlayingGame()){
                    user.getPlayingGame().leave(player);
                }else {
                    BilingualText.stream("&c-このコマンドを使用するにはゲームに参加している必要があります", "&c-You must be in a game to use this command.")
                            .color()
                            .setReceiver(player)
                            .sendChatMessage();
                }
            break;

        }

        if(arg.equals("accept")) {

            if (!args.hasNext()) {

                displayCommandUsage(sender);

                return;
            }

            Player target = Bukkit.getPlayerExact(args.next());

            if (target == null) {

                BilingualText.stream("&c-プレイヤーが見つかりません", "&c-This player is offline")
                        .color()
                        .setAttribute("$playerName", player.getDisplayName())
                        .setReceiver(sender.asPlayerCommandSender())
                        .sendChatMessage();
                return;
            }

            final User targetUser = users.getUser(target);
            final Request request = Main.getPlugin().getRequestManager().get(target, player);

            if (request == null) {

                BilingualText.stream("&c-Duelに招待されていないか、招待の有効期限が切れています","&c--You haven't been invite to duel or the invitation has expired")
                        .color()
                        .setReceiver(player)
                        .sendChatMessage();
                return;
            }

            if(targetUser.isPlayingGame() && request.getParkour() == null) {

                BilingualText.stream("&c-取り込み中です", "&c-This player is currently busy.")
                        .color()
                        .setReceiver(sender.asPlayerCommandSender())
                        .sendChatMessage();
                return;
            }

            //ほかのゲームに参加していたら退出処理をさせる
            if(user.isPlayingGame()) {
                user.getPlayingGame().leave(player);
            }

            GameManager.getInstnace().createSoloMatch(target, player, request.getParkour(),false);

            Main.getPlugin().getRequestManager().remove(target, player);

            return;
        }

        Player player1 = Bukkit.getPlayerExact(arg);

        if (player1 == null) {

            BilingualText.stream("&c-プレイヤーが見つかりません", "&c-This player is offline")
                    .color()
                    .setAttribute("$playerName", player.getDisplayName())
                    .setReceiver(sender.asPlayerCommandSender())
                    .sendChatMessage();
            return;
        }

        User target = users.getUser(player1);
        if (player1.getName().equals(sender.asPlayerCommandSender().getName())) {

            BilingualText.stream("&c-自分自身にDuelリクエストを申し込むことはできません。", "&c-You can't invite yourself to Duel!.")
                    .color()
                    .setReceiver(sender.asPlayerCommandSender())
                    .sendChatMessage();

            return;
        }

        /*if (Main.getPlugin().getRequestManager().has(player, player1) && Main.getPlugin().getRequestManager().get(player, false).get(player.getUniqueId()).getParkour() == null) {
            //lang.sendMessage(sender, "ERROR.duel.already-has-request", "name", target.getName());
            return;
        }*/

        if (target.isPlayingGame()) {

            BilingualText.stream("&c-取り込み中です", "&c-This player is currently busy.")
                    .color()
                    .setReceiver(sender.asPlayerCommandSender())
                    .sendChatMessage();

            return;
        }
        //改修予定
        Main.getPlugin().getRequestManager().make(player, player1);
        user.inventoryUserInterfaces.openDuelSelectorUI();

        return;

    }


    private void displayCommandUsage(Sender sender){

        List<TextStream> texts = Arrays.asList(
                Text.stream("&6---------------------------------------------"),
                Text.stream("&aDuel Commands:"),
                Text.stream("&e-/duel <player> - Opens a GUI to invite a player to a Duel."),
                Text.stream("&e-/duel accept - Accept a Duel invit from the player"),
                Text.stream("&e-/duel cancel - cancel"),
                Text.stream("&6---------------------------------------------")

        );

        texts.forEach(text -> text.color().setReceiver(sender.asPlayerCommandSender()).sendChatMessage());
    }
}
