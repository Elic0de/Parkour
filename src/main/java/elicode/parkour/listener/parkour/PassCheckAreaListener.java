package elicode.parkour.listener.parkour;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import elicode.parkour.parkour.Parkour;
import elicode.parkour.parkour.ParkourRegion;
import elicode.parkour.parkour.ParkourSet;
import elicode.parkour.util.sound.SoundMetadata;
import elicode.parkour.user.User;

public class PassCheckAreaListener extends PassRegionListener {

	private static final SoundMetadata IN_SE = new SoundMetadata(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2f, 2f);
	private static final SoundMetadata OUT_SE = new SoundMetadata(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2f, 0.5f);

	public PassCheckAreaListener() {
		super(ParkourSet.getInstance().chunksToCheckAreasMap);
	}

	@Override
	public void onMove(Player player, User user, Parkour parkour, ParkourRegion from, ParkourRegion to) {
		boolean existsFrom = from != null;
		boolean existsTo = to != null;

		//今いるアスレが無ければ戻る
		if(user.parkourPlayingNow == null) return;

		if(!user.isPlayingParkour()) return;

		//チェックエリアに入った場合
		if(!existsFrom && existsTo){
			user.currentCheckArea = to;

			user.parkourChallengeProgress().setPresentProcedure(progress -> {

			}).apply();
			if(!user.playerSettings.checkArenaNotification) IN_SE.play(player);


			//通知アイテムを輝かせる
		//	ControlFunctionalItem.updateSlot(player, ItemType.CHERCKPOINT_TELEPORTER);
			//チェックエリアから出た場合
		}else if(existsFrom && !existsTo){
			user.currentCheckArea = null;

			if(!user.playerSettings.checkArenaNotification) OUT_SE.play(player);

			//通知アイテムの輝きを失わせる
			//ControlFunctionalItem.updateSlot(player, ItemType.CHERCKPOINT_TELEPORTER);
		}
	}

	/*@EventHandler
	public void onTeleport(PlayerTeleportEvent event){
		Player player = event.getPlayer();

		//プラグインによるテレポートであれば通知アイテムを更新する
		ControlFunctionalItem.updateSlot(player, ItemType.CHERCKPOINT_TELEPORTER);
	}*/

}
