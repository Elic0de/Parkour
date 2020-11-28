package elicode.parkour.util.message;

import org.bukkit.entity.Player;

import elicode.parkour.util.text.Text;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class ActionBar implements MessageStyle {

	public static final ActionBar INSTANCE = new ActionBar();

	@Override
	public void sendTo(Player receiver, Text text) {
		receiver.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(text.toString()));
	}

}
