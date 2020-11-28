package elicode.parkour.game.games;

import elicode.parkour.game.GameState;
import elicode.parkour.game.fight.Fight;
import elicode.parkour.game.fight.tasks.EndingTask;
import elicode.parkour.game.fight.tasks.StartingTask;
import elicode.parkour.parkour.Parkour;
import elicode.parkour.party.Party;
import elicode.parkour.user.User;
import elicode.parkour.user.UserSet;
import elicode.parkour.util.format.TimeFormat;
import elicode.parkour.util.text.BilingualText;
import elicode.parkour.util.text.Text;
import elicode.parkour.util.text.TextStream;
import elicode.parkour.util.tuplet.Tuple;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class PartyFight extends Fight {

    private Parkour parkour;
    private Party party;
    private final UserSet users = UserSet.getInstnace();
    private Player winner;
    private final List<Tuple<Player, String>> goalplayers = new CopyOnWriteArrayList<>();


    public PartyFight(Party party, Parkour parkour) {
        this.party = party;
        this.parkour = parkour;
        this.players = this.party.getPlayers();
        startingTask = new StartingTask(this);
        party.setPartyFight(this);

        getPlayers().forEach(player -> {
            User user = users.getUser(player);
            user.exitCurrentParkour();
            user.setPlayingGame(this);
            parkour.teleport(user);
            parkour.entry(user);
            user.parkourPlayingNow = parkour;
        });
        freeze();
    }

    @Override
    public void onStart() {
        setGameState(GameState.RUNNING);
        getPlayers().forEach(player -> {
            User user = users.getUser(player);
            user.startTime = System.currentTimeMillis();

            START_SE.play(player);
        });
        msgAll("&e-スタート！！", "&e-Start!!");
    }

    @Override
    public void onStop() {
        doDisplayResult();

        getPlayers().forEach(player -> {
            User user = users.getUser(player);
            user.exitCurrentParkour();
            user.exitCurrentGame();
            parkour.teleport(user);
        });

        this.party.setPartyFight(null);
        this.gameState = GameState.STOPPED;
        this.players.clear();
    }

    @Override
    public void leave(Player player) {

    }

    public void onGoal(User user) {
        if (!this.gameState.equals(GameState.RUNNING)) return;

        //ゴールタイムを計算する
        long time = System.currentTimeMillis() - user.startTime;

        //タイムを削除する
        user.startTime = 0;

        goalplayers.add(new Tuple<>(user.asBukkitPlayer(), TimeFormat.format(time)));

        FINISH_SE.play(user.asBukkitPlayer());

        if(this.goalplayers.size() <= 1){
            endingTask = new EndingTask(this);

            this.winner = user.asBukkitPlayer();

            BilingualText.stream("&3-$player&7-さんが&e-$time&7-でクリア！",
                    "&3$player &7-has finished the parkour &f-with time: &e-$time")
                    .setAttribute("$player" , user.asBukkitPlayer().getDisplayName())
                    .setAttribute("$time", TimeFormat.format(time))
                    .color()
                    .setReceivers(players)
                    .sendChatMessage();

        }else {

            BilingualText.stream("&3$player&7さんが&e-$time&7-でクリア！",
                    "$player has finished the parkour &f-with time: &e-$time")
                    .setAttribute("$player" , user.asBukkitPlayer().getDisplayName())
                    .setAttribute("$time", TimeFormat.format(time))
                    .color()
                    .setReceivers(players)
                    .sendChatMessage();
        }
    }

    public void doDisplayResult() {
        User user = users.getUser(this.winner);

        AtomicInteger rank = new AtomicInteger(1);

        List<String> lore = goalplayers.stream().map(record ->
                BilingualText.stream("&b-$rank-&7-位 &b-$name &7-@ &b-$time", "&b-$rank-&7-. &b-$name &7-@ &b-$time")
                        .textBy(record.first)
                        .setAttribute("$rank", rank.getAndIncrement())
                        .setAttribute("$name", record.first.getName())
                        .setAttribute("$time", record.second)
                        .color()
                        .toString()
        ).collect(Collectors.toList());

        if(players.size() != goalplayers.size()) {
            players.stream().filter(player -> goalplayers.stream().anyMatch(playerStringTuple -> playerStringTuple.first != player)).map(player -> BilingualText.stream("&7---&7-位 &b-$name &7-@ &7---:--:--", "&b-$rank-&7-. &b-$name &7-@ &b---:--:--")
                    .textBy(player)
                    .setAttribute("$name", player.getName())
                    .color()
                    .toString()
            ).collect(Collectors.toList()).forEach(lore::add);
        }

        lore.add(0, "---------------------------------------------");
        lore.add(players.size()  + 1, "---------------------------------------------");

        lore.forEach(text -> Text.stream(text).setReceivers(players).sendChatMessage());

    }
}
