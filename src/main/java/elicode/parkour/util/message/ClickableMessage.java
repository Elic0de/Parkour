package elicode.parkour.util.message;

import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import elicode.parkour.util.text.Text;
import elicode.parkour.util.text.TextStream;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;

public class ClickableMessage implements MessageStyle {

	private TextStream text;
	private Object object;
	public final ClickAction action;
	public final TextStream value;

	public ClickableMessage(ClickAction action, String value){

		this(action, Text.stream(value));
	}

	public ClickableMessage(ClickAction action, String text,String name, Object value) {
		this(text,action, text.replace(name, value.toString()));
	}

	public ClickableMessage(ClickAction action, TextStream value){
		this.action = action;
		this.value = value;
	}

	public ClickableMessage(String text,ClickAction action, String value){
		this.text = Text.stream(text);
		this.action = action;
		this.value = Text.stream(value);
	}

	@Override
	public void sendTo(Player receiver, Text text) {
		TextComponent component = new TextComponent(text.toString());
		if(text == null) {
			component.setClickEvent(new ClickEvent(action.action, value.textBy(receiver).toString()));
		}else {
			System.out.println(this.text +" " + text.toString() + " 11111111" + value);
			component.setClickEvent(new ClickEvent(action.action, this.text.textBy(receiver).toString()));
		}

		receiver.spigot().sendMessage(ChatMessageType.CHAT, component);
	}

	public static enum ClickAction {

		OPEN_FILE(Action.OPEN_FILE),
		OPEN_URL(Action.OPEN_URL),
		RUN_COMMAND(Action.RUN_COMMAND),
		SUGGEST_COMMAND(Action.SUGGEST_COMMAND);

		public final Action action;

		private ClickAction(Action action){
			this.action = action;
		}

	}

}
