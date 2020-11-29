package elicode.parkour.command.parkour;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import elicode.parkour.command.Arguments;
import elicode.parkour.command.Command;
import elicode.parkour.command.Sender;
import elicode.parkour.parkour.*;
import elicode.parkour.util.databases.DatabaseUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import elicode.location.ImmutableLocation;
import elicode.parkour.region.selection.RegionSelectionSet;
import elicode.parkour.util.text.Text;
import elicode.parkour.util.text.TextStream;
import elicode.parkour.util.Color;
import elicode.parkour.util.Splitter;

public class ParkourSettingCommand implements Command {

	private static final Pattern RGB_FORMAT = Pattern.compile("^((2[0-4]\\d|25[0-5]|1\\d{1,2}|[1-9]\\d|\\d)( ?, ?)){2}(2[0-4]\\d|25[0-5]|1\\d{1,2}|[1-9]\\d|\\d)");
	private static final Pattern REWARDS_FORMAT = Pattern.compile("[0-9]{1,8},[0-9]{1,8}");
	private static final Pattern DIFFICULTY_FORMAT = Pattern.compile("[0-9]{1,8},[0-9]{1,8}");

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

		//送信者のUUIDを取得する
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

		switch(args.next()){
		case "info":{
			List<TextStream> texts = Arrays.asList(
				Text.stream("&7-: &b-Name &7-@ &f-$parkour")
				.setAttribute("$parkour", parkourName),
				Text.stream("&7-: &b-Category &7-@ &f-$category")
				.setAttribute("$category", parkour.category.name),
				Text.stream("&7-: &b-Description &7-@ &f-$description")
				.setAttribute("$description", parkour.description),
				Text.stream("&7-: &b-Spawn &7-@ &f-$spawn")
				.setAttribute("$spawn", parkour.spawn.serialize()),
				Text.stream("&7-: &b-Color &7-@ &f-$color")
				.setAttribute("$color", parkour.borderColor.serialize()),
				Text.stream("&7-: &b-Time Attack &7-@ &f-$enable")
				.setAttribute("$enable", parkour.timeAttackEnable ? "&b-有効" : "&7-無効")
			);

			texts.forEach(text -> text.color().setReceiver(player).sendChatMessage());
			break;
		}case "category":{
			try{
				final ParkourCategory category = ParkourCategory.valueOf(args.next().toUpperCase());

				//同じカテゴリーであれば戻る
				if(category == parkour.category){
					sender.warn("既に同じカテゴリーに設定されています。");
					return;
				}

				//正しいカテゴリーであれば書き換える
				parkour.category = category;

				//DatabaseUtils.updateCourse(parkour);
			}catch(Exception e){
				sender.warn("指定されたカテゴリーは不正です。[Normal, Update, Extend, Segment, Biome]から選んで下さい。");
				return;
			}

			sender.info("カテゴリーを設定しました。");
			break;
		}case "description":{
			//説明文が入力されていなければ戻る
			if(!args.hasNext()){
				sender.warn("説明文を入力して下さい。");
				return;
			}

			String description = ChatColor.translateAlternateColorCodes('&', args.getRange(args.getIndex(), args.getLength()));

			parkour.description = description;

			Text.stream("$parkour-&r-の説明文を書き換えました。")
			.setAttribute("$parkour", parkourName)
			.color()
			.setReceiver(player)
			.sendChatMessage();
			break;
		}case "spawn": {
			//プレイヤーの座標を取得する
			Location location = sender.asPlayerCommandSender().getLocation();

			//イミュータブルな座標にしブロックの中央に調整した上でセットする
			parkour.spawn = new ImmutableLocation(location);

			Text.stream("$parkour-&r-のスポーン地点を現在地点に書き換えました。")
			.setAttribute("$parkour", parkourName)
			.color()
			.setReceiver(player)
			.sendChatMessage();
			break;
		}case "color":{
			String text = args.next();

			//RGB形式でなければ戻る
			if(!RGB_FORMAT.matcher(text).matches()){
				sender.warn("パーティクル色はRGB形式で指定して下さい。");
				return;
			}

			//各値に分割する
			int[] values = Splitter.splitToIntArguments(text);

			//各アスレのパーティクル色を更新する
			parkour.borderColor =  new Color(values[0], values[1], values[2]);
			parkour.startLine.recolorParticles2();
			parkour.finishLine.recolorParticles2();
			parkour.portal.recolorParticles2();
			parkour.checkAreas.recolorAll();

			Text.stream("$parkour-&r-のパーティクル色を書き換えました。")
			.setAttribute("$parkour", parkourName)
			.color()
			.setReceiver(player)
			.sendChatMessage();
			break;
		}case "rewards":{
			String text = args.next();

			if(!REWARDS_FORMAT.matcher(text).matches()){
				sender.warn("/parkour [parkour_name] [first,second_and_subsequent]");
				return;
			}

			//各値に分割する
			int[] coins = Splitter.splitToIntArguments(text);

			//報酬を更新する
			parkour.rewards = new Rewards(coins);

			Text.stream("$parkour-&r-の報酬を書き換えました。")
			.setAttribute("$parkour", parkourName)
			.color()
			.setReceiver(player)
			.sendChatMessage();
			return;
			}case "difficulty":{
				String text = args.next();

				if(!DIFFICULTY_FORMAT.matcher(text).matches()){
					sender.warn("/parkour [parkour_name] [first,second_and_subsequent]");
					return;
				}

				//各値に分割する
				int[] difficulty = Splitter.splitToIntArguments(text);

				//難易度を更新する
				parkour.difficulty = new Difficulty(difficulty);

				Text.stream("$parkour-&r-の難易度を書き換えました。")
						.setAttribute("$parkour", parkourName)
						.color()
						.setReceiver(player)
						.sendChatMessage();
				return;
		}case "timeattack":{
			if(!args.hasNextBoolean()){
				sender.warn("/parkour [parkour_name] [timeattack] [true/false]");
				return;
			}

			//タイムアタックを有効にするかどうか
			boolean enableTimeAttack = args.nextBoolean();

			String stateName = enableTimeAttack ? "有効" : "無効";

			//既に同じ設定であれば戻る
			if(enableTimeAttack == parkour.timeAttackEnable){
				Text.stream("$parkour-&r-のタイムアタックは既に$state化されています。")
				.setAttribute("$parkour", parkourName)
				.setAttribute("$state", stateName)
				.color()
				.setReceiver(player)
				.sendChatMessage();
				return;
			}

			//更新する
			parkour.timeAttackEnable = enableTimeAttack;

			DatabaseUtils.updateCourse(parkour);
			Text.stream("$parkour-&r-のタイムアタックを$stateにしました。")
			.setAttribute("$parkour", parkourName)
			.setAttribute("$state", stateName)
			.color()
			.setReceiver(player)
			.sendChatMessage();
			break;
		}default:
			displayCommandUsage(sender);
			break;
		}
	}

	private void displayCommandUsage(Sender sender){
		sender.warn("/parkoursetting [parkour] info @ 情報を表示する");
		sender.warn("/parkoursetting [parkour] category @ カテゴリーを設定する");
		sender.warn("/parkoursetting [parkour] spawn @ 現在地点をスポーン地点に設定する");
		sender.warn("/parkoursetting [parkour] color [R,G,B] @ パーティクル色を設定する");
		sender.warn("/parkoursetting [parkour] rewards [first,after] @ 報酬を設定する");
		sender.warn("/parkoursetting [parkour] difficulty [first,after] @ 難易度を設定する");
		sender.warn("/parkoursetting [parkour] timeattack [true/false] @ タイムアタックを有効にするかどうか設定する");
		sender.warn("アスレの範囲選択中であれば[parkour]は省略出来る");
	}

}
