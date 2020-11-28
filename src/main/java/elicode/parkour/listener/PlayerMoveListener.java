package elicode.parkour.listener;

import elicode.beta.parkour.location.ImmutableLocation;
import elicode.parkour.Main;
import elicode.parkour.parkour.Parkour;
import elicode.parkour.user.CheckpointSet;
import elicode.parkour.user.User;
import elicode.parkour.user.UserSet;
import elicode.parkour.util.Optional;
import elicode.parkour.util.text.BilingualText;
import elicode.parkour.util.tuplet.Tuple;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    private final UserSet users = UserSet.getInstnace();

    @EventHandler
    public void PlayerMove(PlayerMoveEvent event) {


        Player player = event.getPlayer();
        User user = users.getUser(player);
        Parkour parkour = user.currentParkour;

        //今いるアスレが無ければ戻る

        if (!user.isOnParkour()) return;

        if (!user.isPlayingParkour()) return;

        if (!(player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.SKULL)) return;

        if(!player.isOnGround()) return;


        switch (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType()) {
            case SKULL:
                CheckpointSet checkpoints = user.checkpoints;

                Optional<Tuple<Integer, ImmutableLocation>> wrappedCheckpoint = checkpoints.getLatestCheckpoint(parkour);

                if(!wrappedCheckpoint.isPresent()){

                    user.asBukkitPlayer().teleport(parkour.spawn.asBukkit());

                    return;
                }

                Tuple<Integer, ImmutableLocation> checkpoint = wrappedCheckpoint.forcedUnwrapping();

                //チェックポイントにテレポートさせる
                player.teleport(checkpoint.second.asBukkit());

                break;
        }
    }

}
