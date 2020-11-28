package elicode.parkour.util.tweet;

import elicode.parkour.util.message.HoverMessage;
import elicode.parkour.util.text.Text;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import elicode.parkour.util.message.ClickableMessage;
import elicode.parkour.util.message.ClickableMessage.ClickAction;
import elicode.parkour.util.sound.SoundMetadata;
import elicode.parkour.util.text.BilingualText;

public class Tweet {

	private static final SoundMetadata SE = new SoundMetadata(Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.75f);

	public static void display(Player player, String text){
		//テキストにアジ鯖のハッシュタグを追加してビルドする
		String tweet = new IntentTweetBuilder(text).addHashtag("アジ鯖").build();

		BilingualText.stream("&b-&l-#クリックして呟こう", "&b-&l-#Click to Tweet")
				.setAttribute("$text", text)
				.color()
				.setReceiver(player)
				.send(new ClickableMessage("&b-&l-#Click to Tweet",ClickAction.OPEN_URL, tweet));



		/*BilingualText.stream("&b-&l-#クリックして呟こう", "&b-&l-#Click to Tweet")
				.setAttribute("$text", text)
				.color()
				.setReceiver(player)
				.send(new HoverMessage(HoverAction.SHOW_TEXT, tweet));*/

		SE.play(player);
	}

}
