package elicode.parkour.command.parkour;

import elicode.parkour.command.Arguments;
import elicode.parkour.command.Command;
import elicode.parkour.command.Sender;
import org.bukkit.entity.Player;

import elicode.parkour.function.parkour.ControlFunctionalItem;
import elicode.parkour.util.text.BilingualText;

public class ItemCommand implements Command {

	@Override
	public void onCommand(Sender sender, Arguments args) {
		//プレイヤーでなければ戻る
		if(blockNonPlayer(sender)) return;

		Player player = sender.asPlayerCommandSender();

		//ホットバーのアイテムを再配置する
		ControlFunctionalItem.initializeSlots(player);

		BilingualText.stream("&b-ホットバー上のアイテムを再生成しました", "&b-Regenerated items on hotbar")
		.color()
		.setReceiver(player)
		.sendActionBarMessage();
	}

}
