package elicode.parkour.listener;

import elicode.parkour.Main;


import elicode.parkour.function.ToggleHideMode;
import elicode.parkour.game.fight.Fight;
import elicode.parkour.game.queue.QueueManager;
import elicode.parkour.user.StatusBoard;
import elicode.parkour.util.Optional;
import elicode.parkour.util.databases.DatabaseManager;
import elicode.parkour.util.databases.DatabaseUtils;
import elicode.parkour.util.databases.SQLQuery;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import elicode.parkour.user.User;
import elicode.parkour.user.UserSet;

public class UserQuitListener implements PlayerQuitListener {

	private final ToggleHideMode hideModeFunction = ToggleHideMode.getInstance();

	@Override
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onQuit(PlayerQuitEvent event) {
		User user = UserSet.getInstnace().getUser(event.getPlayer());
		Player player = event.getPlayer();

		//タイムアタックの途中であれば経過時間を記録する
		if(user.isPlayingParkour() && user.parkourPlayingNow.timeAttackEnable) {
			user.timeElapsed = user.getTimeAttackProgress().cancelTaskThatDisplaysElapsedTime();
		}

		hideModeFunction.onPlayerQuit(player);

		//今いるアスレから退出させる
		if(user.isOnParkour() && !user.playerSettings.particle) user.currentParkour.undisplayParticles(user);

		user.inventoryUserInterfaces = null;

		user.statusBoard.clearScoreboard();
		user.statusBoard = null;

		user.save();

		DatabaseManager.get().executeStatement(SQLQuery.UPDATE_PLAYER_DATA,player.getName(),user.updateRank(),user.extendRank(),user.rankedrank(), user.getId());
		//DatabaseUtils.updatePlayerData(user);

		event.setQuitMessage("");

		if(Main.getPlugin().getQueueManager().getQueue(user.asBukkitPlayer()) != null) {
			Main.getPlugin().getQueueManager().quitQueue(user.asBukkitPlayer());
		}

		if(user.isPlayingGame()) {
			user.getPlayingGame().leave(user.asBukkitPlayer());
		}

	}

}
