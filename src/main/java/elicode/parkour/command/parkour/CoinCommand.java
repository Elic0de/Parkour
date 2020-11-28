package elicode.parkour.command.parkour;

import java.util.UUID;

import elicode.parkour.command.Arguments;
import elicode.parkour.command.Command;
import elicode.parkour.command.Sender;
import elicode.parkour.util.format.CoinFormat;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import elicode.parkour.util.text.Text;
import elicode.parkour.user.User;
import elicode.parkour.user.UserSet;

public class CoinCommand implements Command {

	private final UserSet users = UserSet.getInstnace();

	@Override
	public void onCommand(Sender sender, Arguments args) {

		if(hasPermission(sender,this.getClass().getSimpleName())) return;

		//第1引数が無ければ戻る
		if(!args.hasNext()){
			displayCommandUsage(sender);
			return;
		}

		//第1引数をプレイヤー名として取得する
		String playerName = args.next();

		@SuppressWarnings("deprecation")
		OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
		UUID uuid = player.getUniqueId();

		//指定されたプレイヤーがサーバーに一度も参加した事がなければ戻る
		if(!users.containsUser(uuid)){
			Text.stream("&c-$playerはサーバーに参加した事がありません。")
			.setAttribute("$player", playerName)
			.color()
			.setReceiver(sender.asPlayerCommandSender())
			.sendChatMessage();
			return;
		}

		//ユーザーを取得する
		User user = users.getUser(uuid);

		//第2引数で分岐する
		switch(args.next()){
		case "deposit":{
			if(!args.hasNextInt()){
				sender.warn("与えるコイン数を指定して下さい。");
				return;
			}

			int coins = args.nextInt();

			user.depositCoins(coins);

			Text.stream("$playerに$coinsコインを与えました。")
			.setAttribute("$player", playerName)
			.setAttribute("$coins", coins)
			.color()
			.setReceiver(sender.asPlayerCommandSender())
			.sendChatMessage();
			return;
		}case "withdraw":{
			if(!args.hasNextInt()){
				sender.warn("奪うコイン数を指定して下さい。");
				return;
			}

			int coins = args.nextInt();

			user.withdrawCoins(coins);

			Text.stream("$playerから$coinsコインを奪いました。")
			.setAttribute("$player", playerName)
			.setAttribute("$coins", coins)
			.color()
			.setReceiver(sender.asPlayerCommandSender())
			.sendChatMessage();
			return;
		}case "see":{
			Text.stream("$playerは$coinsコイン持っています。")
			.setAttribute("$player", playerName)
			.setAttribute("$coins", CoinFormat.format(user.coins()))
			.color()
			.setReceiver(sender.asPlayerCommandSender())
			.sendChatMessage();
			return;
		}default:
			displayCommandUsage(sender);
			return;
		}
	}

	private void displayCommandUsage(Sender sender){
		sender.warn("/coin [player] deposit [coins] @ コインを与えます");
		sender.warn("/coin [player] withdraw [coins] @ コインを奪います");
		sender.warn("/coin [player] see @ 所有コイン数を表示します");
	}

}
