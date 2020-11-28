package elicode.parkour.listener.parkour;

import elicode.parkour.user.TimeAttackProgress;
import elicode.parkour.util.sound.SoundMetadata;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import elicode.parkour.parkour.ParkourRegion;
import elicode.parkour.parkour.Parkour;
import elicode.parkour.parkour.ParkourSet;
import elicode.parkour.util.text.BilingualText;
import elicode.parkour.user.ParkourChallengeProgress;
import elicode.parkour.user.User;

public class PassStartLineListener extends PassRegionListener {

	public PassStartLineListener() {
		super(ParkourSet.getInstance().chunksToStartLinesMap);
	}
	private static final SoundMetadata START_SE = new SoundMetadata(Sound.ENTITY_BLAZE_SHOOT	, 1f, 1f);

	@Override
	public void onMove(Player player, User user, Parkour parkour, ParkourRegion from, ParkourRegion to) {
		//スタートラインの領域から何もない領域に進もうとしたのでなければ戻る
		if(from != null || to == null) return;

		boolean timeAttackEnable = parkour.timeAttackEnable;

		//スポーン地点側に戻ってきた場合
		if(!user.isPlayingParkour()){

			//タイムアタックが有効でなければ戻る
			if(!timeAttackEnable) return;

			user.progress = new ParkourChallengeProgress();

			//プレイし始めた時間を記録する
			 user.startTime = System.currentTimeMillis();

			START_SE.play(player);

			BilingualText.stream("&c-タイマーをリセットしました", "&c-Reset your timer")
					.color()
					.setReceiver(player)
					.sendActionBarMessage();


		//アスレをプレイし始めた場合
		}else {
			//タイムアタックが有効でなければ戻る
			if (!timeAttackEnable) return;

			user.progress = new ParkourChallengeProgress();


			//タイムアタックが有効であればプレイし始めた時間を記録するF
			if(timeAttackEnable) {
				if(user.getTimeAttackProgress() != null) user.getTimeAttackProgress().cancelTaskThatDisplaysElapsedTime();
				user.setTimeAttackProgress(new TimeAttackProgress(user,parkour));
				user.getTimeAttackProgress().startMeasuringTime();
				user.getTimeAttackProgress().runTaskThatDisplaysElapsedTime();
				user.startTime = user.getTimeAttackProgress().getStartTime();
			}
			START_SE.play(player);

			BilingualText.stream("$color$parkourへの挑戦を始めました！",
					"$color$parkour challenge started!")
					.setAttribute("$color", parkour.prefixColor)
					.setAttribute("$parkour", parkour.colorlessName())
					.color()
					.setReceiver(player)
					.sendActionBarMessage();
		}
	}

}
