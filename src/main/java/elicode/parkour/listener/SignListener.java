package elicode.parkour.listener;

import elicode.parkour.parkour.ParkourSign;
import elicode.parkour.user.User;
import elicode.parkour.user.UserSet;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignListener implements Listener {

    private final ParkourSign sm = new ParkourSign();
    private final UserSet users = UserSet.getInstnace();

    @EventHandler(ignoreCancelled = true)
    public void onSignCreate(SignChangeEvent event) {
        if (event.getLine(0).equalsIgnoreCase("[CP]") || event.getLine(0).equalsIgnoreCase("c")) {
            Player player = event.getPlayer();
            User user = users.getUser(player);

            sm.createCheckpointSign(event, user, "[CP]");


        }
    }
}