package elicode.parkour.util.text;

import elicode.parkour.user.User;
import elicode.parkour.user.UserSet;
import elicode.parkour.util.message.MessageStyle;
import org.bukkit.entity.Player;

public class BilingualText implements TextStream {

	private final Text japanise, english;
	private final UserSet users = UserSet.getInstnace();

	public BilingualText(String japanise, String english){
		this.japanise = new Text(japanise);
		this.english = new Text(english);
	}

	public static BilingualText stream(String japanise, String english){
		return new BilingualText(japanise, english);
	}

	@Override
	public TextStream color(char alternateColorCode) {
		japanise.color(alternateColorCode);
		english.color(alternateColorCode);
		return this;
	}

	@Override
	public TextStream setAttribute(String name, Object value) {
		japanise.setAttribute(name, value);
		english.setAttribute(name, value);

		return this;
	}

	@Override
	public TextStream setAttribute(String name, MessageStyle messageStyle) {
		japanise.setAttribute(name, messageStyle);
		english.setAttribute(name, messageStyle);

		return this;
	}

	@Override
	public Text textBy(Player receiver) {
		User user = users.getUser(receiver);
		if(user.language != null) {
			return user.language.equals("ja_jp") ? japanise : english;
		}else {
			return receiver.getLocale().equals("ja_jp") ? japanise : english;
		}
	}

	//使用言語に対応したStringを返す
	public String toString(Player player){
		return textBy(player).toString();
	}

}
