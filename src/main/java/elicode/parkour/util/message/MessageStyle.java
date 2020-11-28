package elicode.parkour.util.message;

import org.bukkit.entity.Player;

import elicode.parkour.util.text.Text;
import elicode.parkour.util.tuplet.Tuple;

public interface MessageStyle {

	default void sendTo(Tuple<Player, Text> map){
		sendTo(map.first, map.second);
	}

	void sendTo(Player receiver, Text text);
}
