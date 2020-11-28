package elicode.parkour.command;

import elicode.parkour.util.text.BilingualText;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.ArrayList;
import java.util.List;

public interface Command extends Listener {

	 List<String> aliases = new ArrayList<>();

	void onCommand(Sender sender, Arguments args);

	default boolean blockNonPlayer(Sender sender){
		if(sender.isPlayerCommandSender()) return false;

		sender.warn("ゲーム内から実行して下さい。");
		return true;
	}

	default boolean hasPermission(Sender sender, String commandName){

		if (sender.hasPermission("parkour." + commandName.toLowerCase())) return false;

		BilingualText.stream("&cこのコマンドを実行する権限がありません",
				"&cI'm sorry but you do not have permission to perform this command." +
						" Please contact the server administractors if you believe that this is in error.")
				.color()
				.setReceiver(sender.asPlayerCommandSender())
				.sendChatMessage();

		return true;
	}

	default List<String> get() {
		return aliases;
	}
}
