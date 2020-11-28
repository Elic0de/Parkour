package elicode.parkour.listener;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import elicode.parkour.Main;
import elicode.parkour.mysql.Database;
import elicode.parkour.user.*;
import elicode.parkour.util.Utils;
import elicode.parkour.util.databases.DatabaseManager;
import elicode.parkour.util.databases.DatabaseUtils;
import elicode.parkour.util.databases.SQLQuery;
import elicode.parkour.util.joor.Reflect;
import elicode.parkour.util.message.ClickableMessage;

import elicode.parkour.parkour.ParkourSet;
import elicode.parkour.util.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;


import elicode.parkour.function.ImprintRank;
import elicode.parkour.function.PlayerLocaleChange;
import elicode.parkour.function.ToggleHideMode;
import elicode.parkour.parkour.CheckAreas;
import elicode.parkour.parkour.Parkour;
import elicode.parkour.parkour.ParkourRegion;
import elicode.parkour.schedule.Sync;
import elicode.parkour.util.text.BilingualText;
import elicode.parkour.util.Optional;

import static elicode.parkour.util.Reflection.*;

public class UserJoinListener implements PlayerJoinListener {

	private static final Field collisionRule, playerNames, teamAction, teamName;
	private static final Constructor<?> newPacketPlayOutScoreboardTeam;


	static{
		//プライベートなフィールドを書き換える為にリフレクションを用いる

		Class<?> PacketPlayOutScoreboardTeam = getNMSClass("PacketPlayOutScoreboardTeam");


		newPacketPlayOutScoreboardTeam = getConstructor(PacketPlayOutScoreboardTeam);

		collisionRule = getField(PacketPlayOutScoreboardTeam, "f");
		playerNames = getField(PacketPlayOutScoreboardTeam, "h");
		teamAction = getField(PacketPlayOutScoreboardTeam, "i");
		teamName = getField(PacketPlayOutScoreboardTeam, "a");
	}
	private final UserSet users = UserSet.getInstnace();
	private final ParkourSet parkours = ParkourSet.getInstance();

	private final ToggleHideMode hideModeFunction = ToggleHideMode.getInstance();


	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		User user = users.getUser(player);

		//プレイヤー非表示機能にプレイヤーがログインした事を通知する
		hideModeFunction.onPlayerJoin(player);

		//プレイヤーの衝突を無効化するe
		disableCollision(event.getPlayer());

		//各UIを生成する
		user.inventoryUserInterfaces = new InventoryUISet(user);

		//ステータスボードを生成しロードする
		StatusBoard statusBoard = user.statusBoard = new StatusBoard(user);
		statusBoard.loadScoreboard();


		//もし5秒以内に言語設定に変更があればスコアボードの表示を更新する
		PlayerLocaleChange.applyIfLocaleChanged(user, 100, u -> u.statusBoard().setPresentProcedure(it -> it.updateAll()));

		//30秒後にPingの表示を更新する
		Sync.define(() -> user.statusBoard().setPresentProcedure(it -> it.updatePing())).executeLater(6000);

		//プレイヤー名にランクを表示させる
		ImprintRank.apply(user);

		/*if(doesExist(player)) {
			//Main.getPlugin().getFriendDatabaseManager().firstJoin(player);
		}*/
		if(doesPlayerDataExist(user)) {
			DatabaseManager.get().executeStatement(SQLQuery.INSERT_PLAYER_DATA, player.getName(),player.getUniqueId(),user.updateRank(),user.extendRank(),user.rankedrank());
			//DatabaseUtils.insertPlayerData(user);
		}

		//最終ログアウト時にどこかのアスレにいた場合

		Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();

		event.setJoinMessage("");

		Text.stream("$player &e-joined the game")
				.textBy(player)
				.setAttribute("$player", player.getDisplayName())
				.color()
				.setReceivers(onlinePlayers)
				.sendChatMessage();

		sendWelcomeMessageTitle(player);
		if(user.isOnParkour()){

			Parkour parkour = user.currentParkour;
			parkour.entry(user);

			if(!user.playerSettings.particle) {
				parkour.displayParticles(user);
			}
			Location location = player.getLocation();
			CheckAreas checkAreas = parkour.checkAreas;

			//どこかのチェックエリア内にいるか調べる
			label: for(List<ParkourRegion> areas : checkAreas.getCheckAreas().values()){
				for(ParkourRegion area : areas){
					if(!area.isIn(location)) continue;

					user.currentCheckArea = area;
					break label;
				}
			}

			BilingualText.stream("$color$parkourへの挑戦を再開しました！", "$color$parkour challenge restarted!")
					.setAttribute("$color", parkour.prefixColor)
					.setAttribute("$parkour", parkour.colorlessName())
					.color()
					.setReceiver(player)
					.sendActionBarMessage();


			//タイムアタックの途中であれば経過時間からスタート時のタイムを再計算しセットする
			if(user.isPlayingParkour() && user.timeElapsed > 0){
				user.startTime = user.timeElapsed;
				user.setTimeAttackProgress(new TimeAttackProgress(user,parkour));
				user.getTimeAttackProgress().setStartTime(System.currentTimeMillis() - user.startTime);
				user.getTimeAttackProgress().runTaskThatDisplaysElapsedTime();
				user.timeElapsed = 0;
			}

			//スコアボード上の接続プレイヤー数を更新する
			//if(users.getOnlineUsers() != null) users.getOnlineUsers().forEach(user1 -> user1.statusBoard.updateOnlinePlayers());

		}

		if(player.getGameMode() != GameMode.CREATIVE) user.asBukkitPlayer().setGameMode(GameMode.ADVENTURE);

		//クリアしていないアスレをメッセージとして送る
	//	sendMessageNotCompleteCourse(player, user);
	}

	public void sendMessageNotCompleteCourse(Player player, User user) {
		List<Parkour> notClearedParkours = new ArrayList<>();

		for(Parkour parkour : parkours.getParkours()) {
			String parkourName = parkour.name;

			if (!user.clearedParkourNames.contains(parkourName)) {
				notClearedParkours.add(parkour);
			}
		}

		Parkour randomParkour = notClearedParkours.get(new Random().nextInt(notClearedParkours.size()));

		BilingualText.stream("クリアしていないアスレに挑戦しよう！ &e-[クリックして挑戦]",
						"Let's challenge the athletic You haven't been cleared! &e-[Click to Challenge]")
				.color()
				.setReceiver(player)
				.send(new ClickableMessage(ClickableMessage.ClickAction.RUN_COMMAND, "/pa join " + randomParkour.name.replace("§","&")));


	}

	public void sendWelcomeMessageTitle(Player player){
		player.sendTitle(
				BilingualText.stream("&f-ようこそ！ &b-Rise&f-鯖へ！！", "&f-Welcome To &b-Rise&f-!!")
						.textBy(player)
						.color()
						.toString(),
				BilingualText.stream("&7-アスレチック / パルクールサーバー", "&7-Athletic / Parkour Server")
						.textBy(player)
						.color()
						.toString()
		);

	}
	public boolean doesPlayerDataExist(User user) {
		Player player = user.asBukkitPlayer();
		if (user.getId() != -1) {

			DatabaseManager.get().executeStatement(SQLQuery.UPDATE_PLAYER_DATA,player.getName(),user.updateRank(),user.extendRank(),user.rankedrank(), user.getId());

			return false;
		}
		return true;
	}

	public boolean doesExist(Player player) {
		return Main.getPlugin().getFriendDatabaseManager().getPlayerId(player) == 0;
	}


	public void disableCollision(Player player){
		//新しくパケットを作成する
		Object packet = newInstance(newPacketPlayOutScoreboardTeam);

		//対象となるプレイヤーを書き込む
		setFieldValue(playerNames, packet, Arrays.asList(player.getName()));

		//衝突はしない設定にする
		setFieldValue(collisionRule, packet, "never");


		//チームに参加するアクションとする
		setFieldValue(teamAction, packet, 0);

		//適当にチーム名を決める
		setFieldValue(teamName, packet, UUID.randomUUID().toString().substring(0, 15));

		Reflect.on(player).call("getHandle").field("playerConnection").call("sendPacket", packet);
	}

}
