package elicode.parkour.game.fight.tasks;

import elicode.parkour.Main;
import elicode.parkour.game.fight.Fight;

import org.bukkit.Bukkit;

public class EndingTask implements Runnable {

    private int timer;
    private final int id;
    private final Fight fight;

    public EndingTask(Fight fight) {
        this.timer = 10;
        this.fight = fight;

        this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), this, 0L, 20L);
    }

    @Override
    public void run() {
        timer--;
        if (timer <= 0) {
            stop();
            fight.onStop();
        }
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(id);
    }
}

