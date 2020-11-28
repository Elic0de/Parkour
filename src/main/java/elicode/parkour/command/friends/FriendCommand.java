package elicode.parkour.command.friends;

import elicode.parkour.Main;
import elicode.parkour.command.Arguments;
import elicode.parkour.command.Command;
import elicode.parkour.command.Sender;
import elicode.parkour.util.databases.FriendDatabase;
import elicode.parkour.util.text.BilingualText;
import elicode.parkour.util.text.Text;
import elicode.parkour.util.text.TextStream;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class FriendCommand implements Command {

    private FriendDatabase friendDatabase = Main.getPlugin().getFriendDatabaseManager();

    @Override
    public void onCommand(Sender sender, Arguments args) {

        if (blockNonPlayer(sender)) return;

        //第1引数が無ければ戻る
        if (!args.hasNext()) {

            displayCommandUsage(sender);

            return;
        }
        Player player = sender.asPlayerCommandSender();

        switch (args.next()){

            case "add": {
                //第1引数が無ければ戻る
                if (!args.hasNext()) {

                    displayCommandUsage(sender);

                    return;
                }
                Player playerQuery = isOnline(args.next());

                if (isAFriendOf(player, playerQuery)) return;
                if (!hasNoRequestFrom(player, playerQuery)) return;
                if (hasRequestFrom(player,playerQuery)) {
                    BilingualText.stream("as","sada")
                            .setReceiver(playerQuery)
                            .sendChatMessage();
                    /*pPlayer.sendMessage(
                            (PREFIX + PLAYER_PATTERN.matcher(Main.getInstance().getMessages()
                                    .getString("Friends.Command.Add.FriendRequestFromReceiver")).replaceAll(Matcher.quoteReplacement(args[1]))));
                    pPlayer
                            .sendPacket(new TextComponent(ComponentSerializer.parse(("{\"text\":\"" + PREFIX
                                    + PLAYER_PATTERN.matcher(Main.getInstance().getMessages().getString("Friends.Command.Add.HowToAccept")).replaceAll(Matcher.quoteReplacement(args[1]))
                                    + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + "/"
                                    + Friends.getInstance().getName() + ACCEPT_COMMAND_NAME + args[1]
                                    + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\""
                                    + Main.getInstance().getMessages().getString("Friends.Command.Add.ClickHere")
                                    + "\"}]}}}"))));*/
                    return;
                }
                if (!allowsFriendRequests(player, playerQuery))
                    return;
                sendFriendRequest(player, playerQuery);
               /* if (Main.getInstance().getGeneralConfig().getInt("Commands.Friends.SubCommands.Add.FriendRequestTimeout") > 0) {
                    BukkitBungeeAdapter.getInstance().schedule(Main.getInstance(), () -> {
                        if (playerQuery.getRequests().contains(pPlayer)) {
                            playerQuery.denyRequest(pPlayer);
                            playerQuery.sendMessage((PREFIX + PLAYER_PATTERN.matcher(Main.getInstance()
                                    .getMessages().getString("Friends.Command.Add.FriendRequestTimedOut")).replaceAll(Matcher.quoteReplacement(pPlayer.getDisplayName()))));
                        }
                    }, Main.getInstance().getGeneralConfig().getInt("Commands.Friends.SubCommands.Add.FriendRequestTimeout"));
                }*/


                break;

            }case "deny": {
                if (!args.hasNext()) {

                    displayCommandUsage(sender);

                    return;
                }

                String arg = args.next();
                Player player1 = Bukkit.getPlayer(arg);
                if (player1 == null) {

                    BilingualText.stream("&c-[$playerName]の名前でプレーヤーを見つけることができません", "&c-Can't find a player by the name of '$playerName'")
                            .color()
                            .setAttribute("$playerName", arg)
                            .setReceiver(sender.asPlayerCommandSender())
                            .sendChatMessage();
                    return;
                }

                Player playerQuery = player1;
                if (!hasRequestFrom(player, playerQuery)) return;
                friendDatabase.denyRequest(friendDatabase.getPlayerID(player),friendDatabase.getPlayerID(playerQuery));

                break;
            }case "accept": {
                String arg = args.next();
                Player player1 = Bukkit.getPlayer(arg);
                if (player1 == null) {

                    BilingualText.stream("&c-[$playerName]の名前でプレーヤーを見つけることができません", "&c-Can't find a player by the name of '$playerName'")
                            .color()
                            .setAttribute("$playerName", arg)
                            .setReceiver(sender.asPlayerCommandSender())
                            .sendChatMessage();
                    return;
                }

                Player playerQuery = player1;

                if (player.getName().equals(playerQuery.getName())) {

                    BilingualText.stream("&c-自分を追加することはできません!", "&c-You can't add yourself as a friend!")
                            .color()
                            .setReceiver(sender.asPlayerCommandSender())
                            .sendChatMessage();

                    return;
                }
                if (!hasRequestFrom(player, playerQuery)) return;
                System.out.println(hasRequestFrom(player, playerQuery));

                friendDatabase.addFriend(friendDatabase.getPlayerID(player),friendDatabase.getPlayerID(playerQuery));
                friendDatabase.denyRequest(friendDatabase.getPlayerID(player),friendDatabase.getPlayerID(playerQuery));
                BilingualText.stream("narimasuta","be friend")
                        .color()
                        .setReceiver(player)
                        .sendChatMessage();

                if (!playerQuery.isOnline()) return;
                Player friend = playerQuery;
                BilingualText.stream("オンラインです","be friend")
                        .color()
                        .setReceiver(player)
                        .sendChatMessage();

                break;
            }
            case "remove": {
                if (!args.hasNext()) {

                    displayCommandUsage(sender);

                    return;
                }
                String arg = args.next();
                Player player1 = Bukkit.getPlayer(arg);
                if (player1 == null) {

                    BilingualText.stream("&c-[$playerName]の名前でプレーヤーを見つけることができません", "&c-Can't find a player by the name of '$playerName'")
                            .color()
                            .setAttribute("$playerName", arg)
                            .setReceiver(sender.asPlayerCommandSender())
                            .sendChatMessage();
                    return;
                }
                Player playerQuery = player1;
                if (!isAFriendOf(player, playerQuery)) return;
                friendDatabase.denyRequest(friendDatabase.getPlayerID(player),friendDatabase.getPlayerID(playerQuery));


                break;
            }
            case "list": {
                StringBuilder stringBuilder = new StringBuilder();

                for (int i : friendDatabase.getFriends(friendDatabase.getPlayerID(player.getUniqueId()))) {
                    String playerName = friendDatabase.getName(i);
                    Player player1 = Bukkit.getPlayer(playerName);
                    String format = null;
                    if(!(player1 == null)) {
                       format = BilingualText.stream("&eオンライン"," &eis online")
                                .color()
                                .textBy(player)
                                .toString();

                    }else {
                        format = BilingualText.stream("&cオフライン"," &cis currently offline")
                                .color()
                                .textBy(player)
                                .toString();
                    }

                    stringBuilder.append(playerName+ format + "\n" );
                }

                Text.stream("&aFriends\n" + stringBuilder)
                        .color()
                        .setReceiver(player)
                        .sendChatMessage();
                break;
            }

        }
    }

    private void sendFriendRequest(Player pSender, Player pReceiver) {

        friendDatabase.sendFriendRequest(friendDatabase.getPlayerID(pSender.getUniqueId()),friendDatabase.getPlayerID(pReceiver.getUniqueId()));
        sendRequest(pSender, pReceiver);
    }

    private void sendRequest(Player pPlayer, Player pPlayerQuery) {
        BilingualText.stream("受信しました", "sadsad")
                .color()
                .setReceiver(pPlayerQuery)
                .sendChatMessage();
    }
    private boolean hasRequestFrom(Player pPlayer, Player pQueryPlayer) {

        return friendDatabase.hasRequestFrom(friendDatabase.getPlayerID(pQueryPlayer),friendDatabase.getPlayerID(pPlayer.getUniqueId()));

    }

    private boolean hasNoRequestFrom(Player pPlayer, Player pQueryPlayer) {

        if (friendDatabase.hasRequestFrom(friendDatabase.getPlayerID(pQueryPlayer),friendDatabase.getPlayerID(pPlayer.getUniqueId()))) {
            BilingualText
                    .stream("alreadt","sd")
                    .color()
                    .setReceiver(pPlayer)
                    .sendChatMessage();

            return false;
        }
        return true;
    }

    protected boolean isAFriendOf(Player pPlayer, Player pGivenPlayer) {
        if (friendDatabase.isAFriendOf(friendDatabase.getPlayerID(pPlayer.getUniqueId()),friendDatabase.getPlayerID(pGivenPlayer.getUniqueId()))) {
          //  sendError(pPlayer, (new TextComponent(PREFIX + Main.getInstance().getMessages().getString("Friends.Command.Add.AlreadyFriends").replace("[PLAYER]", pGivenPlayer.getDisplayName()))));
            return true;
        }
        return false;
    }

    private boolean givenPlayerEqualsSender(Player pPlayer, String pGivenPlayer) {
        if (pPlayer.getName().equalsIgnoreCase(pGivenPlayer)) {
          //  sendError(pPlayer, "Friends.Command.Accept.ErrorSenderEqualsReceiver");
            return true;
        }
        return false;
    }

   /* private boolean doesPlayerExist(Player pPlayer, Player pGivenPlayer) {
        if (!pGivenPlayer.doesExist()) {
            sendError(pPlayer, "Friends.General.DoesNotExist");
            return false;
        }
        return true;
    }*/

    private boolean allowsFriendRequests(Player pPlayer, Player pGivenPlayer) {
       /* if (Main.getInstance().getGeneralConfig().getBoolean("Commands.Friends.SubCommands.Settings.Settings.FriendRequest.Enabled") && pGivenPlayer.getSettingsWorth(0) == 0) {
            sendError(pPlayer, new TextComponent(PREFIX + PLAYER_PATTERN.matcher(Main.getInstance().getMessages().getString("Friends.Command.Add.CanNotSendThisPlayer")).replaceFirst(pGivenPlayer.getName())));
            return false;
        }*/
        return true;
    }

    private Player isOnline(String playerName){
        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            return null;
        }
        return player;
    }

    private void displayCommandUsage(Sender sender){
        List<TextStream> texts = Arrays.asList(
                Text.stream("&6---------------------------------------------"),
                Text.stream("&aParty Commands:"),
                Text.stream("&e/p invite <player> &7- &bInvites the player to your party"),
                Text.stream("&e/p join <Name> &7- &bjoin a party"),
                Text.stream("&e/p leave &7- &bLeaves the current party"),
                Text.stream("&e/p kick <player>  &7- &bKick player form the party"),
                Text.stream("&e/p promote <player> &7- &bPromote the player to the party leader"),
                Text.stream("&e/p chat &7- &bSend all players in the party a message"),
                Text.stream("&e/p disband &7- &bdisband your party"),
                Text.stream("&e/p list &7- &bLists the members of your party"),
                Text.stream("&6---------------------------------------------")

        );

        texts.forEach(text -> text.color().setReceiver(sender.asPlayerCommandSender()).sendChatMessage());
    }
}
