package elicode.parkour.game.queue;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QueueManager {

    private List<Queue> soloQueue = new ArrayList<>();

    /*private List<PartyQueue> partyQueue = new ArrayList<>();*/

    private HashMap<Player, IQueue> playerQueue = new HashMap<>();

    public List<Queue> getSoloQueue() {
        return this.soloQueue;
    }

    /*public List<PartyQueue> getPartyQueue() {
        return this.partyQueue;
    }*/

    public HashMap<Player, IQueue> getPlayerQueue() {
        return this.playerQueue;
    }

    public Queue getQueue(Player player) {
        return (Queue) getPlayerQueue().get(player);
    }

    public void quitQueue(Player player) {
        if(getPlayerQueue().get(player) != null)
        getPlayerQueue().get(player).removeFromQueue();
    }

    public void onDisable() {
        for (IQueue iQueue : this.playerQueue.values())
            iQueue.removeFromQueue();
    }
}
