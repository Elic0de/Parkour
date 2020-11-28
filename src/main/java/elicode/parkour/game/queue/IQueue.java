package elicode.parkour.game.queue;

import org.bukkit.scheduler.BukkitTask;

public interface IQueue {
    Object getPlayers();

    String getQueueType();

    BukkitTask getSearchingTask();

    boolean isRanked();

    long getStartingTime();

    void addToQueue();

    void removeFromQueue();

    void startSearching();

    void createGame(IQueue paramIQueue, boolean isRanked);
}