package elicode.parkour.function;

import elicode.parkour.game.RankedColor;
import org.bukkit.entity.Player;

import elicode.parkour.parkour.RankColor;
import elicode.parkour.util.text.Text;
import elicode.parkour.user.User;

public class ImprintRank {

	public static void apply(User user){
		Player player = user.asBukkitPlayer();
		int rank = user.updateRank();
		int ranked = user.rankedrank();

		//表示例: [1] elicode
		String displayName = Text.stream("&c-$ranked_color[$rank_color$rank-&r$ranked_color] $rank_color$player_name")
				.setAttribute("$rank_color", RankColor.values()[rank].color)
				.setAttribute("$ranked_color", RankedColor.values()[ranked].color)
				.setAttribute("$player_name", player.getName())
				.setAttribute("$rank", rank)
				.color()
				.toString();

		player.setDisplayName(displayName);
		player.setPlayerListName(displayName);
	}

}
