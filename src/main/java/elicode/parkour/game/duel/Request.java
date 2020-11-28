package elicode.parkour.game.duel;

import elicode.parkour.parkour.Parkour;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Request {

    private final UUID sender;
    private final UUID target;
    private Parkour parkour;
    private final long creation;

    Request(final Player sender, final Player target) {
        this.sender = sender.getUniqueId();
        this.target = target.getUniqueId();
        this.creation = System.currentTimeMillis();
    }

    public UUID getSender() {
        return sender;
    }

    public UUID getTarget() {
        return target;
    }

    public Parkour getParkour() {
        return parkour;
    }

    public long getCreation() {
        return creation;
    }

    public void setParkour(Parkour parkour) {
        this.parkour = parkour;
    }
}
