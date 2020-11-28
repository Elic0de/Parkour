package elicode.parkour.listener;

import java.util.Collection;
import java.util.UUID;

import elicode.parkour.util.message.ClickableMessage;
import elicode.parkour.util.text.Text;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;



import elicode.parkour.user.User;
import elicode.parkour.user.UserSet;

public class VoteListener implements Listener {

	private final UserSet users = UserSet.getInstnace();

	@EventHandler
	public void onVote(VotifierEvent event){
		Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
		Vote vote = event.getVote();
		String serviceName = vote.getServiceName();
		String playerName = vote.getUsername();

		@SuppressWarnings("deprecation")
		OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);

		//サーバーに接続した事のないプレイヤーであれば戻る
		if(player == null || !player.hasPlayedBefore()) return;

		UUID uuid = player.getUniqueId();

		//ユーザーデータが存在しなければ戻る
		if(!users.containsUser(uuid)) return;

		//ユーザーデータを取得する
		User user = users.getUser(uuid);

		user.depositCoins(150);

		for (User user1:users.getOnlineUsers()) {
			user1.depositCoins(15);

		}
		System.out.println(serviceName);
		System.out.println(vote.getAddress());
		String url = serviceName.contains("minecraft.jp")  ? "https://minecraft.jp/servers/azisaba.net/vote" : "https://monocraft.net/servers/xWBVrf1nqB2P0LxlMm2v";

		Text.stream(
				"\n&e-=============================================\n&a$player&7-さんが$serviceNameで投票したことによりコインが&c-15&7-枚もらえました！\n$url\n&e-=============================================")
				.setAttribute("$player", playerName)
				.setAttribute("$serviceName", serviceName)
				.setAttribute("$url", url)
				.color()
				.setReceivers(onlinePlayers)
				.sendChatMessage();
	}

}
