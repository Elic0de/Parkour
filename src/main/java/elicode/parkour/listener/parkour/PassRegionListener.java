package elicode.parkour.listener.parkour;

import elicode.parkour.parkour.*;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import elicode.parkour.util.chunk.ChunksToObjectsMap;
import elicode.parkour.user.User;
import elicode.parkour.user.UserSet;

public abstract class PassRegionListener implements Listener {

	private final UserSet users = UserSet.getInstnace();
	private final ChunksToObjectsMap<ParkourRegion> chunksToRegionsMap;


	protected PassRegionListener(ChunksToObjectsMap<ParkourRegion> chunksToRegionsMap){
		this.chunksToRegionsMap = chunksToRegionsMap;
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		User user = users.getUser(player);

		HumanEntity human = event.getPlayer();

		//チェックエリアに入ったのがプレイヤーでなければ戻る
		if(!(human instanceof Player)) return;
		//今いるアスレが無ければ戻る
		if(user.isOnParkour()){
			Location from = event.getFrom();

			//元々いた地点に存在していた領域
			ParkourRegion fromRegion = null;

			for (ParkourRegion region : chunksToRegionsMap.get(from)) {
				if (!region.isIn(from))
					continue;

				fromRegion = region;
				break;
			}

			Location to = event.getTo();

			ParkourRegion toRegion = null;

			for (ParkourRegion region : chunksToRegionsMap.get(to)) {
				if (!region.isIn(to))
					continue;

				toRegion = region;
				break;
			}



			//アスレを取得する
			Parkour parkour = (fromRegion != null ? fromRegion.parkour : (toRegion != null ? toRegion.parkour : null));

			//アスレが存在しなければ戻る
			if (!(parkour == null)) {
				if (user.parkourPlayingNow == parkour) {
					onMove(player, user, parkour, fromRegion, toRegion);
				}
			}


			//アスレをゴールしたのでなければ戻る
			/*if (user.parkourPlayingNow == null || from != null || to == null) return;*/

		} else if(user.parkourPlayingNow == null){


			Location from = event.getFrom();

			//元々いた地点に存在していた領域
			ParkourRegion fromRegion = null;

			for (ParkourRegion region : chunksToRegionsMap.get(from)) {
				if (!region.isIn(from))
					continue;

				fromRegion = region;
				break;
			}

			Location to = event.getTo();

			ParkourRegion toRegion = null;

			for (ParkourRegion region : chunksToRegionsMap.get(to)) {
				if (!region.isIn(to))
					continue;

				toRegion = region;
				break;
			}

			//アスレを取得する
			Parkour parkour = (fromRegion != null ? fromRegion.parkour : (toRegion != null ? toRegion.parkour : null));

				onMove(player, user, parkour, fromRegion, toRegion);

				return;
		}
	}

	public abstract void onMove(Player player, User user, Parkour parkour, ParkourRegion from, ParkourRegion to);

}
