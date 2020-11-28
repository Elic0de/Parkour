package elicode.parkour.game;

import elicode.parkour.game.fight.Fight;
import elicode.parkour.game.games.Solo;
import elicode.parkour.parkour.Parkour;
import elicode.parkour.user.UserSet;
import org.bukkit.entity.Player;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GameManager {

    private static final Set<Fight> Matches = new HashSet<>();
    private static GameManager instance;
    private final UserSet users = UserSet.getInstnace();

    public static void load(){
        instance = new GameManager();
    }

    public static GameManager getInstnace(){
        return instance;
    }

    public Fight createSoloMatch(Player firstPlayer, Player secoundPlayer, Parkour parkour, boolean paramBoolean) {
        Solo solo = new Solo(firstPlayer, secoundPlayer, parkour, paramBoolean);
        solo.preStart();

        this.Matches.add(solo);

        return solo;
    }

    public void removeGame(Solo solo) {
        Matches.remove(solo);
    }

    public Fight getGame(Player player) {
        for (Fight g : Matches) {
            if (g.getPlayers().contains(player)) {
                return g;
            }
        }
        return null;
    }

    public Fight getGame(Fight fight) {
        for (Fight g : Matches) {
            if (g.equals(fight)) {
                return g;
            }
        }
        return null;
    }
    public List<Fight> getGames(){
        return Matches.stream().map(this::getGame).collect(Collectors.toList());
    }
}
