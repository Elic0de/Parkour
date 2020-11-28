package elicode.parkour.util.message;

import elicode.parkour.util.text.Text;
import elicode.parkour.util.text.TextStream;
import elicode.parkour.util.tuplet.Tuple;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class HoverMessage implements MessageStyle {

    public final HoverAction action;
    public final TextStream value;

    public HoverMessage(HoverAction action, String value){
        this(action, Text.stream(value));
    }

    public HoverMessage(HoverAction action, TextStream value){
        this.action = action;
        this.value = value;
    }

    @Override
    public void sendTo(Player receiver, Text text) {
        TextComponent component = new TextComponent(text.toString());
        component.setHoverEvent(new HoverEvent(action.action, new ComponentBuilder(value.textBy(receiver).toString()).create()));
        receiver.spigot().sendMessage(ChatMessageType.CHAT, component);
    }

    public static enum HoverAction {

        SHOW_TEXT(HoverEvent.Action.SHOW_TEXT),
        SHOW_ITEM(HoverEvent.Action.SHOW_ITEM),
        SHOW_ACHIEVEMENT(HoverEvent.Action.SHOW_ACHIEVEMENT),
        SHOW_ENTITY(HoverEvent.Action.SHOW_ENTITY);

        public final HoverEvent.Action action;

        private HoverAction(HoverEvent.Action action){
            this.action = action;
        }

    }
}
