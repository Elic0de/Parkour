package elicode.parkour.game.queue;

import elicode.parkour.Main;
import elicode.parkour.game.GameManager;
import elicode.parkour.parkour.Parkour;
import elicode.parkour.parkour.ParkourSet;
import elicode.parkour.user.User;
import elicode.parkour.user.UserSet;
import elicode.parkour.util.text.BilingualText;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Queue implements IQueue {

    private Player player;

    private int range;

    private BukkitTask task;

    private QueueManager queueManager = Main.getPlugin().getQueueManager();

    private GameManager gameManager = GameManager.getInstnace();

    private boolean ranked;

    private final UserSet users = UserSet.getInstnace();
    private final ParkourSet parkours = ParkourSet.getInstance();

    private int rating;

    private Parkour parkour;

    private long startingTime;

    public Queue(Player paramPlayer, boolean paramBoolean) {
        this.player = paramPlayer;
        this.ranked = paramBoolean;
        this.startingTime = System.nanoTime();
        this.range = 50;

    }
    //改修予定

    public int getRange() {
        return range;
    }

    public int getRating() {
        return rating;
    }

    public Player getPlayers() {
        return this.player;
    }

    public String getQueueType() {
        return this.ranked ? "RANKED-1V1" : "UNRANKED-1V1";
    }

    public String getDisplayQueue() {
        return this.ranked ? "RANKED-1V1" : "UNRANKED-1V1";
    }

    public BukkitTask getSearchingTask() {
        return this.task;
    }

    public boolean isRanked() {
        return this.ranked;
    }

    public long getStartingTime() {
        return this.startingTime;
    }

    public void addToQueue() {
        queueManager.getSoloQueue().add(this);
        queueManager.getPlayerQueue().put(this.player, this);
        BilingualText.stream("キューに参加しました","キューに参加しました")
                .color()
                .setReceiver(player)
                .sendChatMessage();

        startSearching();
    }


    public void removeFromQueue() {
        this.player.getInventory().clear();
        this.task.cancel();

        queueManager.getPlayerQueue().remove(this.player);
        queueManager.getSoloQueue().remove(this);
    }

    public void startSearching() {
        User user = users.getUser(player);
        if (this.ranked) {
            this.rating = user.getRate();
            BukkitTask bukkitTask1 = (new BukkitRunnable() {

                int time = 0;

                boolean anyRange = false;

                boolean hasSent = false;

                boolean isSearchingRange = true;

                public void run() {
                    List<Queue> list1 = queueManager.getSoloQueue();
                    boolean bool = (this.time % 300 == 0) ? true : false;
                    if (!queueManager.getPlayerQueue().containsKey(Queue.this.player)) {
                        cancel();
                        return;
                    }
                    if (Queue.this.player == null) {
                        cancel();
                        return;
                    }
                    if (bool &&  !this.hasSent && this.isSearchingRange) {
                        this.hasSent = true;
                    }
                    if (bool && !this.anyRange && this.isSearchingRange) {
                        Queue.this.range += 50;

                        BilingualText.stream("&cSearching in range [ $range ]", "&cSearching in range [ $range ]")
                                .color()
                                .setAttribute("$range", getRange())
                                .setReceiver(player)
                                .sendChatMessage();
                    }

                   if (list1.isEmpty())
                        return;
                    Queue soloQueue = list1.iterator().next();
                    if (!soloQueue.isRanked()) return;

                    if (soloQueue.getPlayers().getName().equals(Queue.this.getPlayers().getName())) return;

                    for(Queue a: list1) {
                        System.out.println(a.rating +" " + Queue.this.range);
                        System.out.println(Math.abs(a.rating-Queue.this.rating));
                        if(!(Math.abs(a.rating-Queue.this.rating) < a.getRange() && Math.abs(a.rating-Queue.this.rating) < a.getRange())) return;
                    }
                    createGame(soloQueue, true);
                    queueManager.getSoloQueue().remove(this);
                    queueManager.getSoloQueue().remove(soloQueue);
                    removeFromQueue();
                    cancel();
                    this.time++;
                }
            }).runTaskTimer(Main.getPlugin(), 0L, 100L);
            this.task = bukkitTask1;
            return;
        }
        BukkitTask bukkitTask = (new BukkitRunnable() {
            public void run() {
                List<Queue> list1 = queueManager.getSoloQueue();
                if (!queueManager.getPlayerQueue().containsKey(Queue.this.player)) {
                    cancel();
                    return;
                }
                if (Queue.this.player == null) {
                    cancel();
                    return;
                }
                if (list1.isEmpty())
                    return;
                Queue soloQueue = list1.iterator().next();
                if (soloQueue.isRanked())
                    return;
                if (soloQueue.getPlayers().getName().equals(Queue.this.getPlayers().getName()))
                    return;
                createGame(soloQueue,false);
                queueManager.getSoloQueue().remove(this);
                queueManager.getSoloQueue().remove(soloQueue);
                removeFromQueue();
                cancel();
            }
        }).runTaskTimer(Main.getPlugin(), 0L, 5L);
        this.task = bukkitTask;
    }

    @Override
    public void createGame(IQueue paramIQueue, boolean isRanked) {
        Queue soloQueue = (Queue)paramIQueue;
        Player player1 = getPlayers();
        Player player2 = soloQueue.getPlayers();


        BilingualText.stream("&8-----------------------------------\n&eFound a ranked match vs $playerName\n&8-----------------------------------",
                "&8-----------------------------------\n&eFound a ranked match vs $playerName\n&8-----------------------------------")
                .color()
                .setAttribute("$playerName", player2.getDisplayName())
                .setReceiver(player1)
                .sendChatMessage();

        BilingualText.stream("&8-----------------------------------\n&eFound a ranked match vs $playerName\n&8-----------------------------------",
                "&8-----------------------------------\n&eFound a ranked match vs $playerName\n&8-----------------------------------")
                .color()
                .setAttribute("$playerName", player1.getDisplayName())
                .setReceiver(player2)
                .sendChatMessage();


        List<Parkour> notClearedParkours = new ArrayList<>();

        for(Parkour parkour : parkours.getParkours()) {

            notClearedParkours.add(parkour);
        }

        Parkour randomParkour = notClearedParkours.get(new Random().nextInt(notClearedParkours.size()));

        this.parkour = randomParkour;

        gameManager.createSoloMatch(player1, player2, parkour,isRanked);

        queueManager.getPlayerQueue().remove(player1);
        queueManager.getPlayerQueue().remove(player2);
    }
}
