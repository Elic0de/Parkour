package elicode.parkour.inventory.ui;

import java.util.function.Function;

import elicode.parkour.user.User;
import elicode.parkour.user.UserSet;
import org.bukkit.entity.Player;

public class LocaleFunction implements Function<Player, String> {

	private final String japanise, english;
	private final UserSet users = UserSet.getInstnace();

	public LocaleFunction(String japanise, String english){
		this.japanise = japanise;
		this.english = english;
	}

	public String apply(Player player){
		User user = users.getUser(player);
		if(user.language != null){
			return user.language.equals("ja_jp") ? japanise : english;
		}else {
			return player.getLocale().equals("ja_jp") ? japanise : english;
		}
	}

}
