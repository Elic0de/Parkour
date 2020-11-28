package elicode.parkour.util.message;

import org.bukkit.entity.Player;

import elicode.parkour.util.text.Text;

public class ChatMessage implements MessageStyle {

	public static final ChatMessage INSTANCE = new ChatMessage();

	@Override
	public void sendTo(Player receiver, Text text) {
		receiver.sendMessage(text.toString());
	}

}
