package elicode.parkour.command.parkour;

import java.util.UUID;

import elicode.parkour.command.Arguments;
import elicode.parkour.command.Command;
import elicode.parkour.command.Sender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import elicode.parkour.parkour.ParkourSet;
import elicode.parkour.region.selection.RegionSelectionSet;
import elicode.parkour.util.text.Text;
import net.md_5.bungee.api.ChatColor;

public class ParkourEditCommand implements Command {

	private final RegionSelectionSet selections = RegionSelectionSet.getInstance();

	@Override
	public void onCommand(Sender sender, Arguments args) {
		//プレイヤーでなければ戻る
		if(blockNonPlayer(sender)) return;

		if(hasPermission(sender,this.getClass().getSimpleName())) return;

		//アスレ名が指定されていなければ戻る
		if(!args.hasNext()){
			sender.warn("範囲選択をするアスレの名前を指定して下さい。");
			return;
		}

		//第1引数をアスレ名として取得する
		String parkourName = ChatColor.translateAlternateColorCodes('&', args.next());

		//送信者をプレイヤーとして取得する
		Player player = sender.asPlayerCommandSender();
		UUID uuid = player.getUniqueId();

		//finishと入力された場合は範囲選択を終了する
		if(parkourName.equals("finish")){
			//範囲選択中でなければ戻る
			if(!selections.hasSelection(uuid)){
				sender.warn("範囲選択中ではありません。");
				return;
			}

			Inventory inventory = player.getInventory();

			//インベントリ内から範囲選択ツールを削除する
			for(ItemStack item : inventory.getContents()) if(selections.isSelectionTool(item))
				inventory.remove(item);

			//セレクションをクリアする
			selections.clearSelection(player);

			sender.info("範囲選択を終了しました。");
			return;
		}

		//アスレが存在しなければ戻る
		if(!ParkourSet.getInstance().containsParkour(parkourName)){
			Text.stream("$parkour-&r-は存在しません。")
			.setAttribute("$parkour", parkourName)
			.color()
			.setReceiver(player)
			.sendChatMessage();
			return;
		}

		//新しいセレクションを作成する
		selections.setNewSelection(uuid, parkourName);

		//対応した範囲選択ツールを作成する
		ItemStack selectionTool = selections.makeNewSelectionTool(uuid);

		player.getInventory().addItem(selectionTool);

		Text.stream("$parkour-&r-用の範囲選択ツールを与えました。")
		.setAttribute("$parkour", parkourName)
		.color()
		.setReceiver(player)
		.sendChatMessage();
		return;
	}

}
