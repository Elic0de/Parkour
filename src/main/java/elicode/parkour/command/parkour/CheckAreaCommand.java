package elicode.parkour.command.parkour;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import elicode.parkour.command.Arguments;
import elicode.parkour.command.Command;
import elicode.parkour.command.Sender;
import org.bukkit.ChatColor;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import elicode.parkour.parkour.CheckAreas;
import elicode.parkour.parkour.Parkour;
import elicode.parkour.parkour.ParkourRegion;
import elicode.parkour.parkour.ParkourSet;
import elicode.parkour.region.selection.RegionSelection;
import elicode.parkour.region.selection.RegionSelectionSet;
import elicode.parkour.util.text.Text;
import elicode.parkour.util.tuplet.Tuple;

public class CheckAreaCommand implements Command {

	private final ParkourSet parkours = ParkourSet.getInstance();
	private final RegionSelectionSet selections = RegionSelectionSet.getInstance();

	@Override
	public void onCommand(Sender sender, Arguments args) {
		//送信者がプレイヤーでなければ戻る
		if(blockNonPlayer(sender)) return;

		if(hasPermission(sender,this.getClass().getSimpleName())) return;

		//第1引数が無ければ戻る
		if(!args.hasNext()){
			displayCommandUsage(sender);
			return;
		}

		Player player = sender.asPlayerCommandSender();
		UUID uuid = player.getUniqueId();

		//対象となるアスレの名前を取得する
		String parkourName = selections.hasSelection(uuid) ? selections.getSelectedParkourName(uuid) : ChatColor.translateAlternateColorCodes('&', args.next());

		//アスレが存在しなければ戻る
		if(!parkours.containsParkour(parkourName)){
			sender.warn("指定されたアスレは存在しません。");
			return;
		}

		Parkour parkour = parkours.getParkour(parkourName);
		CheckAreas checkAreas = parkour.checkAreas;

		switch (args.next()) {
		case "add":{
			//範囲選択がされていなければ戻る
			if(blockNotSelected(sender)) return;

			int maxMajorCheckAreaNumber = checkAreas.getMaxMajorCheckAreaNumber();

			//メジャーチェックエリア番号を取得する
			int majorCheckAreaNumber = args.hasNextInt() ? args.nextInt() - 1 : maxMajorCheckAreaNumber + 1;

			//不正な番号が指定された場合
			if(majorCheckAreaNumber < 0 || majorCheckAreaNumber - 1 > maxMajorCheckAreaNumber){
				sender.warn("指定されたメジャーCA番号は正しくありません。");
				return;
			}

			//選択範囲を取得する
			RegionSelection selection = selections.getSelection(uuid);

			//新しくチェックエリアを生成する
			ParkourRegion newCheckArea = generateParkourRegion(parkour, selection);

			//バインドする
			Tuple<Integer, Integer> position = checkAreas.bindCheckArea(majorCheckAreaNumber, newCheckArea);

			Text.stream("$parkour-&r-にチェックエリア($major, $minor)を追加しました。")
			.setAttribute("$parkour", parkourName)
			.setAttribute("$major", position.first.intValue() + 1)
			.setAttribute("$minor", position.second.intValue() + 1)
			.color()
			.setReceiver(player)
			.sendChatMessage();
			break;
		}case "set":{
			//範囲選択がされていなければ戻る
			if(blockNotSelected(sender)) return;

			//メジャーチェックエリア番号が指定されていなければ戻る
			if(!args.hasNextInt()){
				sender.warn("メジャーCA番号を指定して下さい。");
				return;
			}

			//メジャーチェックエリア番号を取得する
			int majorCheckAreaNumber = args.nextInt() - 1;

			//不正なメジャーチェックエリア番号であれば戻る
			if(blockInvalidMajorCheckAreaNumber(sender, checkAreas, majorCheckAreaNumber)) return;

			//マイナーチェックエリア番号が指定されていなければ戻る
			if(!args.hasNextInt()){
				sender.warn("マイナーCA番号を指定して下さい。");
				return;
			}

			//マイナーチェックエリア番号を取得する
			int minorCheckAreaNumber = args.nextInt() - 1;

			//不正なマイナーチェックエリア番号であれば戻る
			if(blockInvalidMinorCheckAreaNumber(sender, checkAreas, majorCheckAreaNumber, minorCheckAreaNumber)) return;

			//選択範囲を取得する
			RegionSelection selection = selections.getSelection(uuid);

			//新しくチェックエリアを生成する
			ParkourRegion newCheckArea = generateParkourRegion(parkour, selection);

			checkAreas.setCheckArea(majorCheckAreaNumber, minorCheckAreaNumber, newCheckArea);

			Text.stream("$parkour-&r-のチェックエリア($major, $minor)を書き換えました。")
			.setAttribute("$parkour", parkourName)
			.setAttribute("$major", majorCheckAreaNumber + 1)
			.setAttribute("$minor", minorCheckAreaNumber + 1)
			.color()
			.setReceiver(player)
			.sendChatMessage();
			break;
		}case "insert":{
			//範囲選択がされていなければ戻る
			if(blockNotSelected(sender)) return;

			//メジャーチェックエリア番号が指定されていなければ戻る
			if(!args.hasNextInt()){
				sender.warn("メジャーCA番号を指定して下さい。");
				return;
			}

			//メジャーチェックエリア番号を取得する
			int majorCheckAreaNumber = args.nextInt() - 1;

			//不正なメジャーチェックエリア番号であれば戻る
			if(blockInvalidMajorCheckAreaNumber(sender, checkAreas, majorCheckAreaNumber)) return;

			//選択範囲を取得する
			RegionSelection selection = selections.getSelection(uuid);

			//新しくチェックエリアを生成する
			ParkourRegion newCheckArea = generateParkourRegion(parkour, selection);

			checkAreas.insertCheckArea(majorCheckAreaNumber, newCheckArea);

			Text.stream("$parkour-&r-の$majorにチェックエリアを挿入しました。")
			.setAttribute("$parkour", parkourName)
			.setAttribute("$major", majorCheckAreaNumber + 1)
			.color()
			.setReceiver(player)
			.sendChatMessage();
			break;
		}case "remove":{
			//メジャーチェックエリア番号が指定されていなければ戻る
			if(!args.hasNextInt()){
				sender.warn("メジャーCA番号を指定して下さい。");
				return;
			}

			//メジャーチェックエリア番号を取得する
			int majorCheckAreaNumber = args.nextInt() - 1;

			//不正なメジャーチェックエリア番号であれば戻る
			if(blockInvalidMajorCheckAreaNumber(sender, checkAreas, majorCheckAreaNumber)) return;

			//マイナーチェックエリア番号が指定されていなければ戻る
			if(!args.hasNextInt()){
				sender.warn("マイナーCA番号を指定して下さい。");
				return;
			}

			//マイナーチェックエリア番号を取得する
			int minorCheckAreaNumber = args.nextInt() - 1;

			//不正なマイナーチェックエリア番号であれば戻る
			if(blockInvalidMinorCheckAreaNumber(sender, checkAreas, majorCheckAreaNumber, minorCheckAreaNumber)) return;

			//指定された番号にバインドされたチェックエリアを削除する
			checkAreas.unbindCheckArea(majorCheckAreaNumber, minorCheckAreaNumber);

			Text.stream("$parkour-&r-のチェックエリア($major, $minor)を削除しました。")
			.setAttribute("$parkour", parkourName)
			.setAttribute("$major", majorCheckAreaNumber + 1)
			.setAttribute("$minor", minorCheckAreaNumber + 1)
			.color()
			.setReceiver(player)
			.sendChatMessage();
			break;
		}case "clear":{
			//メジャーチェックエリア番号が指定されていなければ戻る
			if(!args.hasNextInt()){
				sender.warn("メジャーCA番号を指定して下さい。");
				return;
			}

			//メジャーチェックエリア番号を取得する
			int majorCheckAreaNumber = args.nextInt() - 1;

			//不正なメジャーチェックエリア番号であれば戻る
			if(blockInvalidMajorCheckAreaNumber(sender, checkAreas, majorCheckAreaNumber)) return;

			//指定された番号にバインドされたチェックエリアを全て削除する
			checkAreas.unbindAllCheckAreas(majorCheckAreaNumber);

			Text.stream("$parkour-&r-のチェックエリア($major, All)を削除しました。")
			.setAttribute("$parkour", parkourName)
			.setAttribute("$major", majorCheckAreaNumber + 1)
			.color()
			.setReceiver(player)
			.sendChatMessage();
			break;
		}case "list":{
			Map<Integer, List<ParkourRegion>> areasMap = checkAreas.getCheckAreas();

			//空であればその趣旨のメッセージを表示して戻る
			if(areasMap.isEmpty()){
				sender.info("このアスレにチェックエリアは存在しません。");
				return;
			}

			//各メジャーチェックエリア番号毎に処理をする
			for(Entry<Integer, List<ParkourRegion>> areasEntry : areasMap.entrySet()){
				int majorCheckAreaNumber = areasEntry.getKey();

				//メジャーチェックエリア番号を表示する
				Text.stream("&7-: &b-$major")
				.setAttribute("$major", majorCheckAreaNumber + 1)
				.color()
				.setReceiver(player)
				.sendChatMessage();

				List<ParkourRegion> areas = areasEntry.getValue();

				//各チェックエリアの座標情報を表示する
				for(int minorCheckAreaNumber = 0; minorCheckAreaNumber < areas.size(); minorCheckAreaNumber++){
					ParkourRegion area = areas.get(minorCheckAreaNumber);

					Text.stream("  &7-: &f-$minor &7-- &f-$region")
					.setAttribute("$minor", minorCheckAreaNumber + 1)
					.setAttribute("$region", area.serialize().replace(",", "§7,§f"))
					.color()
					.setReceiver(player)
					.sendChatMessage();
				}
			}
			break;
		}default:
			displayCommandUsage(sender);
			break;
		}
	}

	private void displayCommandUsage(Sender sender){
		sender.warn("/checkarea add @ CAを追加する");
		sender.warn("/checkarea add [major] @ 指定メジャーCA番号にCAを追加する");
		sender.warn("/checkarea set [major] [minor] @ 指定メジャーCA番号、マイナーCA番号に設定されているCAを書き換える");
		sender.warn("/checkarea insert [major] @ 指定メジャーCA番号にCAを挿入する");
		sender.warn("/checkarea [parkour] remove [major] [minor] @ 指定メジャーCA番号、マイナーCA番号に設定されているCAを削除する");
		sender.warn("/checkarea [parkour] clear [major] @ 指定メジャーCA番号に設定されているCAを全て削除する");
		sender.warn("/checkarea [parkour] list @ CA一覧を表示する");
		sender.warn("アスレの範囲選択中であれば[parkour]は省略出来る");
	}

	private boolean blockNotSelected(Sender sender){
		if(selections.hasSelection(sender.asPlayerCommandSender().getUniqueId())) return false;

		sender.warn("範囲を指定して下さい。");
		return true;
	}

	private boolean blockInvalidMajorCheckAreaNumber(Sender sender, CheckAreas checkAreas, int majorCheckAreaNumber){
		if(majorCheckAreaNumber >= 0 && majorCheckAreaNumber <= checkAreas.getMaxMajorCheckAreaNumber()) return false;

		sender.warn("指定されたメジャーCA番号は不正です。");
		return true;
	}

	private boolean blockInvalidMinorCheckAreaNumber(Sender sender, CheckAreas checkAreas, int majorCheckAreaNumber, int minorCheckAreaNumber){
		List<ParkourRegion> areas = checkAreas.getCheckAreas(majorCheckAreaNumber);

		if(minorCheckAreaNumber >= 0 && minorCheckAreaNumber < areas.size()) return false;

		sender.warn("指定されたマイナーCA番号は不正です。");
		return true;
	}

	private ParkourRegion generateParkourRegion(Parkour parkour, RegionSelection selection){
		return new ParkourRegion(parkour, selection);
	}

}
