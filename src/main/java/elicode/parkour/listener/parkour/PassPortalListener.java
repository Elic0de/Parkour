package elicode.parkour.listener.parkour;

import elicode.parkour.parkour.*;
import elicode.parkour.util.sound.SoundMetadata;
import elicode.parkour.user.User;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PassPortalListener extends PassRegionListener {

    public PassPortalListener() {
        super(ParkourSet.getInstance().chunksToPortalMap);
    }
    private static final SoundMetadata TELEPORT_SE = new SoundMetadata(Sound.ENTITY_BLAZE_SHOOT, 1.5f, 1.2f);

    @Override
    public void onMove(Player player, User user, Parkour parkour, ParkourRegion from, ParkourRegion to) {
        //ポータルの領域から何もない領域に進もうとしたのでなければ戻る
        if (from != null || to == null) return;

        parkour.teleport(user);
        TELEPORT_SE.play(player);


        if (parkour instanceof RankUpParkour) {
            ParkourCategory category = parkour.category;

            //アスレのランクを取得する
            int rank = ((RankUpParkour) parkour).rank;

            //各タイプで分岐する
            switch (category) {
                case UPDATE:
                    //プレイヤーのランクの方が高ければ戻る
                    if (!(rank <= user.updateRank() + 1)) return;
                    //アスレに参加させる
                    parkour.entry(user);
                    break;
                case EXTEND:
                    //プレイヤーのランクの方が高ければ戻る
                    if (!(rank <= user.extendRank() + 1)) return;
                    //アスレに参加させる
                    parkour.entry(user);
                    break;

                default:
                    throw new NullPointerException("Ranked parkour type can not be null");
            }
        }else {
            //アスレに参加させる
            parkour.entry(user);
        }


    }

}
