package elicode.parkour.game.fight.tasks;

import elicode.parkour.Main;
import elicode.parkour.game.GameState;
import elicode.parkour.game.fight.Fight;

import elicode.parkour.util.sound.SoundMetadata;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class StartingTask implements Runnable {

    private int timer;
    private final int id;
    private final Fight fight;
    private static final SoundMetadata COUNT_SE = new SoundMetadata(Sound.BLOCK_LEVER_CLICK, 1f, 1f);

    public StartingTask(Fight fight) {
        this.timer = 6;
        this.fight = fight;
        this.fight.setGameState(GameState.COUNTDOWN);
        this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), this, 0L, 20L);
    }

    @Override
    public void run() {
        timer--;
        if (timer <= 0) {
            stop();

            fight.unFreeze();
            fight.onStart();

        } else {

            for (Player player : fight.getPlayers()){
                COUNT_SE.play(player);
            }
            fight.msgAll("&e-あと&c-"+timer+"&e-秒で始まります!", "&e-The game starts in &c-"+timer +" &e-sec!");
        }
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(id);
    }
}
