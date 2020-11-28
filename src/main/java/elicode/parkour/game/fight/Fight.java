package elicode.parkour.game.fight;

import elicode.parkour.game.GameState;
import elicode.parkour.game.fight.tasks.EndingTask;
import elicode.parkour.game.fight.tasks.StartingTask;
import elicode.parkour.parkour.Parkour;
import elicode.parkour.util.sound.SoundMetadata;
import elicode.parkour.util.text.BilingualText;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public abstract class Fight {

    protected Parkour parkour;
    protected List<Player> players = new ArrayList<>();
    protected long started;
    protected long ended;
    protected StartingTask startingTask;
    protected EndingTask endingTask;
    protected final SoundMetadata START_SE = new SoundMetadata(Sound.ENTITY_FIREWORK_BLAST, 1f, 1f);
    protected final SoundMetadata FINISH_SE = new SoundMetadata(Sound.ENTITY_BLAZE_SHOOT, 1f, 1f);
    protected GameState gameState;

    public abstract void onStart();

    public abstract void onStop();

    public abstract void leave(Player player);

    public void freeze() {

        players.forEach(player -> {
            player.setGameMode(GameMode.ADVENTURE);
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 23423525, -10, false, false));
            player.setWalkSpeed(0.0001F);
            player.setFoodLevel(1);
            player.setAllowFlight(false);
            player.setFlying(false);
            player.setInvulnerable(true);
        });

    }

    public void unFreeze() {
        players.forEach(player -> {
        player.removePotionEffect(PotionEffectType.JUMP);
        player.setFoodLevel(20);
        player.setWalkSpeed(0.2F);
        });
    }

    public void msgAll(String ja,String eng) {
        for (Player player : players) {

            if (player != null) {
                BilingualText.stream(ja, eng)
                        .color()
                        .setReceiver(player)
                        .sendChatMessage();
            }
        }
    }

    public Parkour getParkour() {
        return parkour;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setParkour(Parkour arena) {
        this.parkour = arena;
    }

    public long getStarted() {
        return started;
    }

    public long getEnded() {
        return ended;
    }

    public StartingTask getStartingTask() {
        return startingTask;
    }

    public EndingTask getEndingTask() {
        return endingTask;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public GameState getGameState() {
        return gameState;
    }

    public long getDuration() {
        if(started > System.currentTimeMillis()) {
            return 0;
        }
        //if(hasEnded()) return ended-started;
        return System.currentTimeMillis()-started;
    }

}
