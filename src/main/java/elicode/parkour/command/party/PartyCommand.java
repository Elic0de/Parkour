package elicode.parkour.command.party;

import elicode.parkour.Main;
import elicode.parkour.command.Arguments;
import elicode.parkour.command.Command;
import elicode.parkour.command.Sender;
import elicode.parkour.parkour.ParkourSet;
import elicode.parkour.party.Party;
import elicode.parkour.party.PartyManager;
import elicode.parkour.user.UserSet;
import elicode.parkour.util.message.ClickableMessage;
import elicode.parkour.util.text.BilingualText;
import elicode.parkour.util.text.Text;
import elicode.parkour.util.text.TextStream;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class PartyCommand implements Command {

    private final ParkourSet parkours = ParkourSet.getInstance();
    private final UserSet users = UserSet.getInstnace();
    private final PartyManager partyManager = Main.getPlugin().getPartyManager();

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

        switch (args.next()) {
            case "invite": {
                String arg = args.next();
                Player player1 = Bukkit.getPlayerExact(arg);
                if (player1 == null) {

                    BilingualText.stream("&c-[$playerName]の名前でプレーヤーを見つけることができません", "&c-Can't find a player by the name of '$playerName'")
                            .color()
                            .setAttribute("$playerName", arg)
                            .setReceiver(sender.asPlayerCommandSender())
                            .sendChatMessage();
                    return;
                }

                Player playerQuery = player1;

                Player toInvite = playerQuery;
                if (player.getName().equals(toInvite.getName())) {

                    BilingualText.stream("&c-パーティーに自分を招待することはできません!", "&c-You cannot invite yourself to a party!.")
                            .color()
                            .setReceiver(sender.asPlayerCommandSender())
                            .sendChatMessage();

                    return;
                }
                boolean justCreated = false;
                Party party1 = Main.getPlugin().getPartyManager().getParty(player.getUniqueId());
                if (party1 == null) {
                    Main.getPlugin().getPartyManager().createParty(player);
                    justCreated = true;
                }
                Party party = Main.getPlugin().getPartyManager().getParty(player.getUniqueId());
                if (!party.isOwner(player))
                    return;
                if (!party.allowsInvitation(player, toInvite))
                    return;
                if (party.isAlreadyInAParty(player, toInvite)) {
                    if (justCreated)
                        partyManager.deleteParty(party);
                    return;
                }
                if (party.isAlreadyInvited(player, toInvite))
                    return;
                if (!party.canInvite(player))
                    return;

                party.invite(toInvite);

                BilingualText.stream("$player1&eが$player2&eをパーティーに招待しました！",
                        "$player1 &einvited $player2 &eto the party!\n They have 60 seconds to accept.")
                        .color()
                        .setAttribute("$player1", player.getDisplayName())
                        .setAttribute("$player2", toInvite.getDisplayName())
                        .setReceivers(party.getPlayers())
                        .sendChatMessage();

                BilingualText.stream("$player&eがパーティーに招待しています！\n&6クリックして参加 &e60秒以内にアクセスできます。", "$player &ehas invited you to join their party!\n" +
                        "&6Click here &eto join You have 60 seconds to accept")
                        .color()
                        .setAttribute("$player", player.getDisplayName())
                        .setReceiver(toInvite)
                        .send(new ClickableMessage(ClickableMessage.ClickAction.RUN_COMMAND, "/party join " + player.getName()));


               break;
            }
            case "join": {
                String arg = args.next();
                Player player1 = Bukkit.getPlayer(arg);
                if (player1 == null) {

                    BilingualText.stream("&c-[$ playerName]の名前でプレーヤーを見つけることができません", "&c-Can't find a player by the name of '$playerName'")
                            .color()
                            .setAttribute("$playerName", arg)
                            .setReceiver(sender.asPlayerCommandSender())
                            .sendChatMessage();
                    return;
                }

                if(!(Main.getPlugin().getPartyManager().getParty(player1.getUniqueId()) == null)) {
                    Party party = Main.getPlugin().getPartyManager().getParty(player1.getUniqueId());
                    if (party.isInParty(player)) return;
                }
                Party party = Main.getPlugin().getPartyManager().getParty(player1.getUniqueId());
                if (party == null) return;
                if (party.addPlayer(player)) {
                    BilingualText.stream("$player&aさんがパーティーに参加しました!","$player &ajoined the party!")
                            .color()
                            .setAttribute("$player", player.getDisplayName())
                            .setReceivers(party.getPlayers())
                            .sendChatMessage();
                } else {
                    BilingualText.stream("プライベート","error contact to developer")
                            .color()
                            .setReceiver(player)
                            .sendChatMessage();
                }
                break;
            }
            case "leave": {
                Party party1 = Main.getPlugin().getPartyManager().getParty(player.getUniqueId());
                if (party1 == null) {
                    return;
                }
                Party party = Main.getPlugin().getPartyManager().getParty(player.getUniqueId());
                if (!party.isInParty(player))
                    return;
                party.leaveParty(player);

                break;
            }
            case "kick": {
                Party party1 = Main.getPlugin().getPartyManager().getParty(player.getUniqueId());
                if (party1 == null) {
                    Main.getPlugin().getPartyManager().createParty(player);

                }
                Party party = Main.getPlugin().getPartyManager().getParty(player.getUniqueId());
                if (!standardCheck(player, party, args))
                    return;
                Player player1 = Bukkit.getPlayer(args.next());
                Player toKick = party.getPlayer(player1);
                if (!checkIsInParty(player, toKick, party, args))
                    return;
                party.kickPlayer(toKick);
                break;
            }
            case "disband": {
                Party party = Main.getPlugin().getPartyManager().getParty(player.getUniqueId());
                if (party == null) {
                    BilingualText.stream("&c-このコマンドを使用するにはパーティーに参加している必要があります", "&c-You must be in a party to use this command.")
                            .color()
                            .setReceiver(player)
                            .sendChatMessage();
                    return;

                }
                if(!party.getOwner().equals(player)) {
                    BilingualText.stream("&c-このコマンドを使用するには、パーティーリーダーである必要があります", "&c-You must be the Party Leader to use that command!")
                            .color()
                            .setReceiver(player)
                            .sendChatMessage();
                    return;
                }
                party.disbandParty();

                break;
            }
            case "list" : {

                Party party = Main.getPlugin().getPartyManager().getParty(player.getUniqueId());
                if (party == null) {
                    BilingualText.stream("&c-このコマンドを使用するにはパーティーに参加している必要があります", "&c-You must be in a party to use this command.")
                            .color()
                            .setReceiver(player)
                            .sendChatMessage();
                    return ;

                }
                if (!party.isInParty(player))
                    return;
                String leader = party.getOwner().getDisplayName();
                StringBuilder stringBuilder = new StringBuilder();


                    for (Player pp : party.getPlayers()) {
                        stringBuilder.append(pp.getDisplayName() + " ");
                    }

                Text.stream("&aParty members ($count): " + stringBuilder)
                        .color()
                        .setAttribute("$count", party.getPlayers().size())
                        .setReceiver(player)
                        .sendChatMessage();
                break;
            }
            case "promote": {

                Party party = Main.getPlugin().getPartyManager().getParty(player.getUniqueId());
                if (!checkIsInParty(player, player, party, args))
                    return;
                if (!standardCheck(player, party, args))
                    return;

                party.setOwner(player);
                BilingualText.stream("$player1&eが$player2&eをパーティーリーダーに昇格させました！","$player1 &ehas promoted $player2 &eto party leader!")
                        .color()
                        .setAttribute("$player1",player.getDisplayName())
                        .setAttribute("$player2",player.getDisplayName())
                        .setReceivers(party.getPlayers())
                        .sendChatMessage();

                break;
            }

            case "chat": {
                break;
            }
        }

    }

    public boolean hasAccess(int pPermissionHeight) {
        return (pPermissionHeight == 2);
    }

    protected boolean checkIsInParty(Player pPlayer, Player pSearched, Party pParty, Arguments args) {

        if (pParty == null) {
            BilingualText.stream("&c-このコマンドを使用するにはパーティーに参加している必要があります", "&c-You must be in a party to use this command.")
                    .color()
                    .setReceiver(pPlayer)
                    .sendChatMessage();
            return false;

        }

        if (pSearched == null) {

            BilingualText.stream("&c-プレイヤーが見つかりません", "&c-Can't find a player")
                    .color()
                    .setReceiver(pPlayer)
                    .sendChatMessage();
            return false;
        }
        if (!pParty.isInParty(pSearched)) {
            BilingualText.stream("&c-このコマンドを使用するにはパーティーに参加している必要があります", "&c-You must be in a party to use this command.")
                    .color()
                    .setReceiver(pPlayer)
                    .sendChatMessage();
            return false;
        }
        if (pSearched.equals(pPlayer)) {
            BilingualText.stream("&c-パーティーに自分を招待することはできません!", "&c-You cannot invite yourself to a party!.")
                    .color()
                    .setReceiver(pPlayer)
                    .sendChatMessage();
            return false;
        }
        return true;
    }

    protected boolean standardCheck(Player pPlayer, Party pParty, Arguments args) {
        String arg = args.next();
        Player player1 = Bukkit.getPlayer(arg);

        if (pParty == null) {
            BilingualText.stream("&c-このコマンドを使用するにはパーティーに参加している必要があります", "&c-You must be in a party to use this command.")
                    .color()
                    .setReceiver(pPlayer)
                    .sendChatMessage();
            return false;

        }

        if (player1 == null) {

            BilingualText.stream("&c-[$ playerName]の名前でプレーヤーを見つけることができません", "&c-Can't find a player by the name of '$playerName'")
                    .color()
                    .setAttribute("$playerName", arg)
                    .setReceiver(pPlayer)
                    .sendChatMessage();
            return false;
        }
        if (!pParty.isInParty(pPlayer))
            return false;
        if (!pParty.isLeader(pPlayer)) {
           /* pPlayer.sendMessage(this.PREFIX +
                    Main.getInstance().getMessages().getString("Party.Command.General.ErrorNotPartyLeader"));*/
            return false;
        }
        return true;
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
