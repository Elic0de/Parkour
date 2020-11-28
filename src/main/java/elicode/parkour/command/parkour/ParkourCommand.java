package elicode.parkour.command.parkour;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import elicode.parkour.Main;
import elicode.parkour.command.*;
import elicode.parkour.function.creative.CheckPointItem;
import elicode.parkour.lobby.LobbySet;
import elicode.parkour.mysql.Database;
import elicode.parkour.parkour.ParkourCategory;
import elicode.parkour.user.User;
import elicode.parkour.user.UserSet;
import elicode.parkour.util.Utils;
import elicode.parkour.util.databases.DatabaseManager;
import elicode.parkour.util.databases.DatabaseUtils;
import elicode.parkour.util.databases.SQLQuery;
import elicode.parkour.util.tuplet.Tuple;
import org.bukkit.entity.Player;

import elicode.parkour.parkour.Parkour;
import elicode.parkour.parkour.ParkourSet;
import elicode.parkour.util.text.Text;
import elicode.parkour.util.text.TextStream;
import net.md_5.bungee.api.ChatColor;

public class ParkourCommand implements Command {

	private final ParkourSet parkours = ParkourSet.getInstance();
	private final UserSet users = UserSet.getInstnace();
	private static final ParkourCategory[] COMMON_CATEGORIES = new ParkourCategory[]{ParkourCategory.NORMAL, ParkourCategory.SEGMENT, ParkourCategory.BIOME};
	/*
	 * parkour command
	 *
	 * [parkour] create
	 * [parkour] delete
	 * [parkour] rename
	 * [parkour] enable
	 * [parkour] disable
	 *
	 * setparkourregion command
	 *
	 * [parkour?] setregion
	 * [parkour?] setstartline
	 * [parkour?] setfinishline
	 *
	 * parkoursetting command
	 *
	 * [parkour?] category
	 * [parkour?] spawn
	 * [parkour?] color [R,G,B]
	 * [parkour?] rewards [F,S]
	 * [parkour?] timeattack [true/false]
	 *
	 * [parkour?] - アスレの編集中であれば入力する必要が無くなる引数
	 */
	/*public ParkourCommand {

		this.requirements = new CommandRequirements.Builder(Permission.PARKOUR)
				.playerOnly()
				.build();

	}*/

	public ParkourCommand(){
		aliases.add("test");
		aliases.add("eri");
		aliases.add("yuka");
		aliases.add("sora");
	}

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
		User user = users.getUser(player);

		//第1引数をアスレ名として取得する
		String parkourName = ChatColor.translateAlternateColorCodes('&', args.next());

		//第1引数がlistであればアスレ名を全て表示する
		if(parkourName.equals("list")){
			for(Parkour parkour : parkours.getParkours()){
				Text.stream("&7-: &r-$parkour-&r &7-@ $enable")
				.setAttribute("$parkour", parkour.name)
				.setAttribute("$enable", parkour.enable ? "&b-有効" : "&7-無効")
				.color()
				.setReceiver(player)
				.sendChatMessage();
			}
			return;
		}
		//第1引数がJoinであればアスレに参加させる
		if(parkourName.equals("join")) {

			String argsParkourName = ChatColor.translateAlternateColorCodes('&', args.next());
			//指定されたアスレが存在しなければ戻る
			if(blockNotExistParkour(player, argsParkourName)) return;

				Parkour parkour = parkours.getParkour(argsParkourName);

				parkour.entry(user);
				parkour.teleport(user);

				return;
		}
		if(parkourName.equals("recreate")) {

			//DatabaseUtils.recreateAllCourses(parkours);

			Text.stream("すべてのデータを更新しました。")
					.color()
					.setReceiver(player)
					.sendChatMessage();

			return;
		}

		if(parkourName.equals("item")) {
			player.getInventory().setItem(0,new CheckPointItem().build(toUser(player)));

			return;
		}
		if(parkourName.equals("save")) {

			LobbySet.getInstance().saveAll();
			UserSet.getInstnace().saveAll();
			ParkourSet.getInstance().saveAll();

			Text.stream("すべてのデータをセーブしました。")
					.color()
					.setReceiver(player)
					.sendChatMessage();

			return;
		}

		//第2引数で分岐する
		switch(args.next()){
		case "create":{
				//対応したファイルが存在していれば戻る
				if(parkours.containsParkour(parkourName)){
					Text.stream("$parkour-&r-は既に存在しています。")
							.setAttribute("$parkour", parkourName)
							.color()
							.setReceiver(player)
							.sendChatMessage();
					return;
				}

				//アスレ名の先頭に装飾コードが存在しない場合
				if(!Parkour.PREFIX_PATTERN.matcher(parkourName).find()){
					sender.warn("アスレ名の先頭には必ず装飾コードを置いて下さい。");
					return;
				}

				//ファイルを作成する
				parkours.makeYaml(parkourName.replace('§', '&'));
				//無効化された状態で登録する
				parkours.registerParkour(parkourName);

				Parkour parkour = parkours.getParkour(parkourName);
				parkour.creator = player.getName();

				DatabaseManager.get().executeStatement(SQLQuery.INSERT_COURSE,parkour.colorlessName(),parkour.category,parkour.creator,parkour.timeAttackEnable);
				//DatabaseUtils.insertCourse(parkour,parkour.creator);

				Text.stream("$parkour-&r-のデータを新規作成しました。")
						.setAttribute("$parkour", parkourName)
						.color()
						.setReceiver(player)
						.sendChatMessage();
				break;
			}case "rename":{
				//指定されたアスレが存在しなければ戻る
				if(blockNotExistParkour(player, parkourName)) return;

				String rename = ChatColor.translateAlternateColorCodes('&', args.next());
				//対応したファイルが存在していれば戻る
				if(parkours.containsParkour(rename)){
					Text.stream("$parkour-&r-は既に存在しています。")
							.setAttribute("$parkour", rename)
							.color()
							.setReceiver(player)
							.sendChatMessage();
					return;
				}

				//アスレ名の先頭に装飾コードが存在しない場合
				if(!Parkour.PREFIX_PATTERN.matcher(rename).find()){
					sender.warn("アスレ名の先頭には必ず装飾コードを置いて下さい。");
					return;
				}
				Parkour parkour = parkours.getParkour(parkourName);

				//DatabaseUtils.updateCourse(parkour, ChatColor.stripColor(rename));

				//アスレが登録されていれば登録を解除する
				parkours.unregisterParkour(parkourName);
				//表示名を変更する
				parkour.name = rename;

				//変更されたアスレをあたらしい表示名で登録
				parkours.registerParkour(parkour);

				//ファイルを削除する
				parkours.makeYaml(parkourName.replace('§', '&')).file.delete();

				DatabaseManager.get().executeStatement(SQLQuery.UPDATE_COURSE,ChatColor.stripColor(rename),parkour.category.name, parkour.creator,parkour.timeAttackEnable,parkour.getId());


				Text.stream("$parkour-&r-の表示名を$rename&rに変更しました。")
						.setAttribute("$parkour", parkourName)
						.setAttribute("$rename", rename)
						.color()
						.setReceiver(player)
						.sendChatMessage();
				break;
			}
			case "delete": {
				//指定されたアスレが存在しなければ戻る
				if (blockNotExistParkour(player, parkourName)) return;

				//アスレが登録されていれば登録を解除する
				parkours.unregisterParkour(parkourName);

				//ファイルを削除する
				parkours.makeYaml(parkourName.replace('§', '&')).file.delete();

				//DatabaseManager.get().executeStatement(SQLQuery.DELETE_PLAYER_COURSE_TIME,);
				//DatabaseUtils.deletePlayerCourseTimes();

				Text.stream("$parkour-&r-を削除しました。")
						.setAttribute("$parkour", parkourName)
						.color()
						.setReceiver(player)
						.sendChatMessage();
				break;
			}
			case "removeAllRecords":{
				Parkour parkour = getParkour(parkourName);
				parkour.records.removeAllRecord();
				//DatabaseUtils.deleteCourseTimes(parkour);

				Text.stream("$parkour-&r-のすべてのレコードを削除しました。")
						.setAttribute("$parkour", parkourName)
						.color()
						.setReceiver(player)
						.sendChatMessage();
				break;
		}case "enable":{
			//指定されたアスレが存在しなければ戻る
			if(blockNotExistParkour(player, parkourName)) return;

			Parkour parkour = getParkour(parkourName);

			if(parkour.enable){
				Text.stream("$parkour-&r-は既に有効化されています。")
				.setAttribute("$parkour", parkourName)
				.color()
				.setReceiver(player)
				.sendChatMessage();
				return;
			}

			//アスレを有効化する
			parkour.update(it -> it.enable = true);

			Text.stream("$parkour-&r-を有効化しました。")
			.setAttribute("$parkour", parkourName)
			.color()
			.setReceiver(player)
			.sendChatMessage();
			break;
		}case "disable":{
			//指定されたアスレが存在しなければ戻る
			if(blockNotExistParkour(player, parkourName)) return;

			Parkour parkour = getParkour(parkourName);

			if(!parkour.enable){
				Text.stream("$parkour-&r-は既に無効化されています。")
				.setAttribute("$parkour", parkourName)
				.color()
				.setReceiver(player)
				.sendChatMessage();
				return;
			}

			//アスレを無効化する
			parkour.update(it -> it.enable = false);

			Text.stream("$parkour-&r-を無効化しました。")
			.setAttribute("$parkour", parkourName)
			.color()
			.setReceiver(player)
			.sendChatMessage();
			break;

		} case "setcreator": {

				if (blockNotExistParkour(player, parkourName)) return;

				if (!args.hasNext()) {

					Text.stream("&c引数を指定してください。")
							.color()
							.setReceiver(player)
							.sendChatMessage();

					return;
				}

				Parkour parkour = getParkour(parkourName);

				parkour.creator = args.next();

				DatabaseManager.get().executeStatement(SQLQuery.UPDATE_COURSE, parkour.colorlessName(), parkour.category.name, parkour.creator, parkour.timeAttackEnable, parkour.getId());

				Text.stream("$parkour&r-のクリエイターを$creatorに変更しました。")
						.setAttribute("$parkour", parkourName)
						.setAttribute("$parkour", parkour.creator)
						.color()
						.setReceiver(player)
						.sendChatMessage();
				break;

			}case "removeTime":{

				if(blockNotExistParkour(player, parkourName)) return;

				if (!args.hasNext()) {

					Text.stream("&c引数を指定してください。")
							.color()
							.setReceiver(player)
							.sendChatMessage();

					return;
				}

				break;
		} case "removetime": {
				//指定されたアスレが存在しなければ戻る
				if (blockNotExistParkour(player, parkourName)) return;

				if (!args.hasNext()) {

					Text.stream("&c引数を指定してください。")
							.color()
							.setReceiver(player)
							.sendChatMessage();

					return;
					}



					if (!args.hasNext()) {

						Text.stream("&c引数を指定してください。")
								.color()
								.setReceiver(player)
								.sendChatMessage();

						for(Parkour parkour : parkours.getParkours()){
							Text.stream("&7-: &r-$parkour-&r &7-@ $enable")
									.setAttribute("$parkour", parkour.name)
									.setAttribute("$enable", parkour.enable ? "&b-有効" : "&7-無効")
									.color()
									.setReceiver(player)
									.sendChatMessage();
						return;
					}
				}

		} case "info": {
				//指定されたアスレが存在しなければ戻る
				if (blockNotExistParkour(player, parkourName)) return;

				Parkour parkour = getParkour(parkourName);

				List<TextStream> texts = Arrays.asList(
						Text.stream("&7-: &b-State &7-@ &f-$enable")
								.setAttribute("$enable", parkour.enable ? "&b-有効" : "&7-無効"),
						Text.stream("&7-: &b-Region &7-@ &f-$region")
								.setAttribute("$region", parkour.region.serialize()),
						Text.stream("&7-: &b-Start Line &7-@ &f-$start_line")
								.setAttribute("$start_line", parkour.startLine.serialize()),
						Text.stream("&7-: &b-Finish Line &7-@ &f-$finish_line")
								.setAttribute("$finish_line", parkour.finishLine.serialize()),
						Text.stream("&7-: &b-Portal &7-@ &f-$portal")
								.setAttribute("$portal", parkour.portal.serialize())

				);

				texts.forEach(text -> text.color().setReceiver(player).sendChatMessage());
				break;

			}
			default:
				displayCommandUsage(sender);
			return;
		}
	}

	private void displayCommandUsage(Sender sender){
		sender.warn("/parkour [parkour] create @ 指定された名前でアスレを作成します。アスレ名の先頭には必ず装飾コードを置いて下さい。");
		sender.warn("/parkour [parkour] delete @ アスレを削除します。");
		sender.warn("/parkour [parkour] rename [rename] @ アスレの名前を変更します。");
		sender.warn("/parkour [parkour] enable @ 有効化し選択画面に表示します。");
		sender.warn("/parkour [parkour] disable @ 無効化し選択画面から非表示にします。");
		sender.warn("/parkour [parkour] info @ アスレの情報を表示します。");
		sender.warn("/parkour [parkour] removeTime [player] @ ランキングのプレイヤーのアスレタイムを削除します。");
		sender.warn("アスレ名の装飾コードはアンパサンドを使用して下さい。");
	}

	private Parkour getParkour(String parkourName){
		return parkours.getParkour(parkourName);
	}

	public List<Tuple<String, String>> getTimes(SQLQuery sqlQuery, Object... parameters) {
		List<Tuple<String, String>> ptList = new ArrayList<>();

				ResultSet rs = DatabaseManager.get().executeResultStatement(sqlQuery, parameters);
		try {
			while (rs.next()) {
				ptList.add(new Tuple<>((rs.getString("timeId")), rs.getString("timeId")));
			}
			rs.close();
		} catch (SQLException ex) {
			Utils.log("An error has occurred executing a query in the database.");
			Main.getPlugin().debug("Query: \n" + sqlQuery);
			Main.getPlugin().debugSqlException(ex);
		}
		return ptList;
	}

	/*public Punishment getPunishmentFromResultSet(ResultSet rs) throws SQLException {
		return new Punishment(
				rs.getString("name"),
				rs.getString("uuid"), rs.getString("reason"),
				rs.getString("operator"),
				PunishmentType.valueOf(rs.getString("punishmentType")),
				rs.getLong("start"),
				rs.getLong("end"),
				rs.getString("calculation"),
				rs.getInt("id"));
	}*/

	private boolean blockNotExistParkour(Player player, String parkourName){
		if(parkours.containsParkour(parkourName)) return false;

		Text.stream("$parkour-&r-は存在しません。")
		.setAttribute("$parkour", parkourName)
		.color()
		.setReceiver(player)
		.sendChatMessage();
		return true;
	}
	private static User toUser(Player player){
		return UserSet.getInstnace().getUser(player);
	}

}
