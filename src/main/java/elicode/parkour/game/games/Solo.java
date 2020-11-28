package elicode.parkour.game.games;

import elicode.parkour.function.ImprintRank;
import elicode.parkour.game.GameState;
import elicode.parkour.game.fight.Fight;
import elicode.parkour.game.fight.tasks.EndingTask;
import elicode.parkour.game.fight.tasks.StartingTask;
import elicode.parkour.game.RankedColor;
import elicode.parkour.parkour.Parkour;
import elicode.parkour.user.User;
import elicode.parkour.user.UserSet;
import elicode.parkour.util.format.TimeFormat;
import elicode.parkour.util.text.BilingualText;
import elicode.parkour.util.text.Text;
import elicode.parkour.util.tuplet.Tuple;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Solo extends Fight {

    private Parkour parkour;
    private boolean isRanked;
    private final UserSet users = UserSet.getInstnace();
    private final List<Tuple<Player, String>> goalplayers = new CopyOnWriteArrayList<>();
    private Player winner;

    public Solo(Player paramPlayer1, Player paramPlayer2, Parkour parkour, boolean paramBoolean) {

        this.parkour = parkour;
        this.isRanked = paramBoolean;

        players.add(paramPlayer1);
        players.add(paramPlayer2);
    }

    public GameState getStatus() {
        return this.gameState;
    }

    public void preStart() {

        getPlayers().forEach(player -> {
            User user = users.getUser(player);
            parkour.entry(user);
            parkour.teleport(user);
            user.setPlayingGame(this);

        });
        freeze();
        startCountdown();
    }
    public void startCountdown() {
            setGameState(GameState.COUNTDOWN);
            startingTask = new StartingTask(this);
    }

    public Player getEnemy(Player paramPlayer) {
        return this.players.get(0).equals(paramPlayer) ? players.get(1) : players.get(0);
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
        setGameState(GameState.STOPPED);
        doDisplayResult();
        cancelTasks();

        getPlayers().forEach(player -> {
            User user = users.getUser(player);

            user.exitCurrentParkour();
            user.exitCurrentGame();
            RankedColor rankedColor = getRank(user);
            user.incrementRanked(rankedColor.ordinal());
            ImprintRank.apply(user);
            parkour.teleport(user);
        });
        players.clear();

    }

    @Override
    public void leave(Player player) {
        if(isGameOver()) {
            players.remove(player);
            onStop();
        }
    }

    public void onGoal(User user) {
        if (!gameState.equals(GameState.RUNNING)) return;
        //ゴールタイムを計算する
        long time = System.currentTimeMillis() - user.startTime;

        FINISH_SE.play(user.asBukkitPlayer());

        //タイムを削除する
        user.startTime = 0;

        if(isGameOver()){

            this.winner = user.asBukkitPlayer();
            this.endingTask = new EndingTask(this);

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

        goalplayers.add(new Tuple<>(user.asBukkitPlayer(), TimeFormat.format(time)));
    }

    private boolean isGameOver() {
        if (goalplayers.size() <= 1) return true;
        return false;
    }

    public void cancelTasks() {
        if (this.startingTask != null) this.startingTask.stop();
        if (this.endingTask != null) this.endingTask.stop();
    }

    public void doDisplayResult() {
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
        System.out.println(players.size() + 1);
        lore.add(0, "---------------------------------------------");
        //lore.add(players.size() +1, "---------------------------------------------");

        lore.forEach(text -> Text.stream(text).setReceivers(players).sendChatMessage());

        if (isRanked) {

            User user = users.getUser(this.winner);

            if (user.getRate() >= 0 && user.getRate() <= 500) {
                int i = user.getRate(); System.out.println(user.getRate());
                int j = users.getUser(getEnemy(user.asBukkitPlayer())).getRate(); System.out.println(users.getUser(getEnemy(user.asBukkitPlayer())).getRate());
                double d = 1.0D / (1.0D + Math.pow(10.0D, (i - j) / 400.0D));
                int k = (int) Math.round(5.0D * (3.0D - d));

                if(user.getRate() <= 500 && user.getRate() + k  <= 500) {
                    user.addElo(k);

                    BilingualText.stream("&a$k &7Rating (&a▲$rate&7)",
                            "&a$k &7Rating (&a$rate&7)")
                            .setAttribute("$k", k)
                            .setAttribute("$rate", user.getRate())
                            .color()
                            .setReceiver(user.asBukkitPlayer())
                            .sendChatMessage();
                }else {

                    BilingualText.stream("&a$k &7Rating (&a▲$rate&7)",
                            "&a$k &7Rating (&a$rate&7)")
                            .setAttribute("$k", 0)
                            .setAttribute("$rate", user.getRate())
                            .color()
                            .setReceiver(user.asBukkitPlayer())
                            .sendChatMessage();
                }

            }

            if (users.getUser(getEnemy(user.asBukkitPlayer())).getRate() >= 0 && users.getUser(getEnemy(user.asBukkitPlayer())).getRate() <= 500) {
                int i = user.getRate();
                int j = users.getUser(getEnemy(user.asBukkitPlayer())).getRate();
                double d = 1.0D / (1.0D + Math.pow(10.0D, (i - j) / 400.0D));
                int k = (int) Math.round(5.0D * (3.0D - d));
                if(user.getRate() >= 0 && user.getRate() - k  >= 0) {
                    users.getUser(getEnemy(user.asBukkitPlayer())).removeElo(k);

                    BilingualText.stream("&e--$k &7Rating (&e$rate&7)",
                            "&e--$k &7Rating (&e$rate&7)")
                            .setAttribute("$k", k)
                            .setAttribute("$rate", users.getUser(getEnemy(user.asBukkitPlayer())).getRate())
                            .color()
                            .setReceiver(getEnemy(user.asBukkitPlayer()))
                            .sendChatMessage();
                }else {

                    BilingualText.stream("&e--$k &7Rating (&e$rate&7)",
                            "&e--$k &7Rating (&e$rate&7)")
                            .setAttribute("$k", k)
                            .setAttribute("$rate", users.getUser(getEnemy(user.asBukkitPlayer())).getRate())
                            .color()
                            .setReceiver(getEnemy(user.asBukkitPlayer()))
                            .sendChatMessage();
                }
            }
        }
    }

    /*public void leave(Player player) {
        players.remove(player.getUniqueId());

        if (death) {
            if (this.getStatus() == Status.RUNNING)
                bar.removePlayer(player);
            heal(player);
            playerManager.getPlayerData(uuid).restore(player);
            playerManager.removePlayerData(player);
            exit(player);
            sb.restoreSB(player);
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 5, 1);
            if (spectate && spectateOnDeath && !isGameOver()) {
                spectate(player);
                player.sendTitle(getName(), "You are now spectating!", 10, 100, 10); //TODO this a temp test
            }
        } else {
            heal(player);
            playerManager.getPlayerData(uuid).restore(player);
            playerManager.removePlayerData(player);
            exit(player);
            sb.restoreSB(player);
        }
        updateAfterDeath(player, death);
    }*/

    public RankedColor getRank(User user){
        int elo = user.getRate();
        for (RankedColor rank : RankedColor.values()) {
            if (elo >= rank.elo)
                continue;
            return getPointsMax(rank.elo);

        }
        return RankedColor.Default;
    }

    public static RankedColor getPointsMax(int i) {
        int pos = i;

        for (RankedColor rank : RankedColor.values()) {
            if (pos == rank.elo)
                return rank;
        }
        return RankedColor.Default;

    }

}
