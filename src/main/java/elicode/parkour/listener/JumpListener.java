package elicode.parkour.listener;

import elicode.parkour.user.User;
import elicode.parkour.util.format.CountFormat;
import elicode.parkour.util.text.BilingualText;
import org.bukkit.Statistic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import elicode.parkour.events.PlayerJumpEvent;
import elicode.parkour.user.UserSet;

public class JumpListener implements Listener {

	private final UserSet users = UserSet.getInstnace();

	@EventHandler
	public void incrementJumps(PlayerJumpEvent event){
		User user = users.getUser(event.getPlayer());

		if(user.currentParkour == null || user.isPlayingParkour() && user.parkourPlayingNow.timeAttackEnable != true) {
			UserSet.getInstnace().getUser(event.getPlayer()).statusBoard.updateJumps();
			BilingualText.stream("&a-ジャンプ数 &7-: &e-$value",
					"&a-Jump count &7-: &e-$value")
					.setAttribute("$value", CountFormat.format(event.getPlayer().getStatistic(Statistic.JUMP)))
					.color()
					.setReceiver(event.getPlayer())
					.sendActionBarMessage();
		}else {

		}
	}

}
