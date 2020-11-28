package elicode.parkour.listener.parkour;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import elicode.parkour.Main;
import elicode.parkour.game.GameManager;
import elicode.parkour.game.RankedColor;
import elicode.parkour.game.games.PartyFight;
import elicode.parkour.game.games.Solo;

import elicode.parkour.lobby.Lobby;
import elicode.parkour.lobby.LobbySet;
import elicode.parkour.user.TimeAttackProgress;
import elicode.parkour.user.UserSet;
import elicode.parkour.util.Utils;
import elicode.parkour.util.databases.DatabaseManager;
import elicode.parkour.util.databases.SQLQuery;
import elicode.parkour.util.format.TimeFormat;
import elicode.parkour.util.text.Text;
import elicode.parkour.util.tweet.Tweet;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import elicode.parkour.function.ImprintRank;
import elicode.parkour.parkour.Parkour;
import elicode.parkour.parkour.ParkourCategory;
import elicode.parkour.parkour.ParkourRegion;
import elicode.parkour.parkour.ParkourSet;
import elicode.parkour.parkour.RankColor;
import elicode.parkour.parkour.RankUpParkour;
import elicode.parkour.parkour.Records;
import elicode.parkour.util.sound.SoundMetadata;
import elicode.parkour.util.text.BilingualText;
import elicode.parkour.user.StatusBoard;
import elicode.parkour.user.User;

public class PassFinishLineListener extends PassRegionListener {

	private static final SoundMetadata RANK_UP_SE = new SoundMetadata(Sound.ENTITY_BLAZE_SHOOT, 0.5f, 1.2f);
	private static final SoundMetadata FINISH_SE = new SoundMetadata(Sound.ENTITY_BLAZE_SHOOT, 1f, 1f);
	private final ParkourSet parkours = ParkourSet.getInstance();
	private final LobbySet lobbies = LobbySet.getInstance();
	private final UserSet users = UserSet.getInstnace();

	public PassFinishLineListener() {
		super(ParkourSet.getInstance().chunksToFinishLinesMap);
	}

	@Override
	public void onMove(Player player, User user, Parkour parkour, ParkourRegion from, ParkourRegion to) {
		//アスレをゴールしたのでなければ戻る
		if (user.parkourPlayingNow == null || from != null || to == null) return;

		if(parkour == null) return;

		if(user.currentParkour == null) return;

		//アスレ名を取得する
		String parkourName = parkour.name;

		//タイムアタックが有効かどうか
		boolean enableTimeAttack = parkour.timeAttackEnable;

		//クリア済みのアスレ名リストを取得する
		Set<String> clearedParkourNames = user.clearedParkourNames;

		//クリアした事があるかどうか
		boolean haveCleared = clearedParkourNames.contains(parkourName);


		String prefixColor = parkour.prefixColor;
		String colorlessParkourName = parkour.colorlessName();
		String playerName = player.getDisplayName();
		String displayName = Text.stream("$ranked_color[$rank_color$rank-&r$ranked_color] $rank_color$player_name")
				.setAttribute("$rank_color", RankColor.values()[user.updateRank()].color)
				.setAttribute("$ranked_color", RankedColor.values()[user.rankedrank()].color)
				.setAttribute("$player_name", player.getName())
				.setAttribute("$rank", user.updateRank())
				.color()
				.toString();
		Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();


		if (Main.getPlugin().getPartyManager().getParty(player.getUniqueId()) != null) {
			PartyFight partyFight = Main.getPlugin().getPartyManager().getParty(player.getUniqueId()).getPartyFight();
			System.out.println("go-ru2");
			partyFight.onGoal(user);
			user.exitCurrentParkour();

			return;
		}
		if (GameManager.getInstnace().getGame(player) != null) {
			Solo solo = (Solo) GameManager.getInstnace().getGame(player);
			user.exitCurrentParkour();
			System.out.println("go-ru3");
			solo.onGoal(user);

			return;

		}

		//タイムアタックが有効の場合
		if (enableTimeAttack) {
			//ゴールタイムを計算する

			TimeAttackProgress timeAttackProgress  = user.getTimeAttackProgress();

			long time = timeAttackProgress.cancelTaskThatDisplaysElapsedTime();

			//タイムを削除する
			user.startTime = 0;

			Records records = parkour.records;
			UUID uuid = user.uuid;

			long personalBest = records.personalBest(uuid);

			//ゴールタイムを記録してみてその結果を取得する
			boolean recorded = records.mightRecord(uuid, time);

			//自己最高記録を超えた場合
			if (personalBest > 0 && recorded) {

				/*BilingualText.stream("$player&7-さんが$color$parkour&7-を&e-$time&7-でクリアすると同時に自己最高記録の&e-$best&7-を越えました！",
						"$player &7-cleared $color$parkour &7-in &e-$time &7-and best $player&7-'s personal best of &e-$best!")
						.setAttribute("$color", prefixColor)
						.setAttribute("$player", displayName)
						.setAttribute("$parkour", colorlessParkourName)
						.setAttribute("$time", TimeFormat.format(time))
						.setAttribute("$best", TimeFormat.format(personalBest))
						.color()
						.setReceivers(onlinePlayers)
						.sendChatMessage();*/

				DatabaseManager.get().executeStatement(SQLQuery.DELETE_PLAYER_COURSE_TIME,user.uuid, parkour.getId());
				DatabaseManager.get().executeStatement(SQLQuery.INSERT_COURSE_TIME,parkour.getId(),user.uuid,time,0);
				//DatabaseUtils.deletePlayerCourseTimes(player.getName(), parkour);
				//DatabaseUtils.insertTime(parkour, player.getUniqueId().toString(), time, 0);
			} else {

				Tweet.display(player, BilingualText.stream("$parkourを$timeでクリアしました！",
						"I have finished the $parkour in $time!")
						.setAttribute("$parkour", colorlessParkourName)
						.setAttribute("$time", TimeFormat.format(time))
						.textBy(player)
						.color()
						.toString());

				BilingualText.stream("$player&7-さんが$color$parkour&7-を&e-$time&7-でクリアしました！",
						"$player &7-has finished the $color$parkour &7-with time: &e-$time")
						.setAttribute("$color", prefixColor)
						.setAttribute("$player", displayName)
						.setAttribute("$parkour", colorlessParkourName)
						.setAttribute("$time", TimeFormat.format(time))
						.color()
						.setReceivers(onlinePlayers)
						.sendChatMessage();

				DatabaseManager.get().executeStatement(SQLQuery.DELETE_PLAYER_COURSE_TIME,user.uuid, parkour.getId());
				DatabaseManager.get().executeStatement(SQLQuery.INSERT_COURSE_TIME,parkour.getId(),user.uuid,time,0);
			}

			records.sortAsync();

			if(recorded) {
				BilingualText.stream("$player&7-が$parkour&7-を&e$time&7で&c$rank位&7-をとりました。", "$player &7-has &c$rank &7-finished the $parkour &7-with time:&e$time&7")
						.setAttribute("$player", displayName)
						.setAttribute("$parkour", parkourName)
						.setAttribute("$time", TimeFormat.format(time))
						.setAttribute("$rank", records.getRank(uuid))
						.color()
						.setReceivers(onlinePlayers)
						.sendChatMessage();
			}
		} else {

			if (haveCleared) {

				Tweet.display(player, BilingualText.stream("$parkourをクリアしました！",
						"I have finished the $parkour")
						.setAttribute("$parkour", colorlessParkourName)
						.textBy(player)
						.color()
						.toString());

				BilingualText.stream("$player&7-さんが$color$parkour&7-をクリアしました！",
						"$player &r-has finished the $color$parkour")
						.setAttribute("$color", prefixColor)
						.setAttribute("$player", displayName)
						.setAttribute("$parkour", parkourName)
						.color()
						.setReceivers(onlinePlayers)
						.sendChatMessage();
			} else {

				Tweet.display(player, BilingualText.stream("$parkourを初めてクリアしました！",
						"I have finished the $parkour for first")
						.setAttribute("$parkour", colorlessParkourName)
						.textBy(player)
						.color()
						.toString());

				BilingualText.stream("$player&7-さんが$color$parkour&7-を初めてクリアしました！",
						"$player &7-has finished the $color$parkour &7-for first")
						.setAttribute("$color", prefixColor)
						.setAttribute("$player", displayName)
						.setAttribute("$parkour", parkourName)
						.color()
						.setReceivers(onlinePlayers)
						.sendChatMessage();
			}


			if (lobbies.getLobby(parkour.linkedLobby) != null) {

				String linkedLobby1 = parkour.linkedLobby;
				Lobby lobby1 = lobbies.getLobby(linkedLobby1);

				//リンクされたロビーのスポーン地点にテレポートさせる
				lobby1.teleport(player);

				parkour.exit(user);

				BilingualText.stream("&a-$linkedlobby&r-にテレポートしました", "&r-You teleported to &a-$linkedlobby")
						.setAttribute("$linkedlobby", linkedLobby1)
						.color()
						.setReceiver(player)
						.sendActionBarMessage();


			} else if (parkours.getParkour(parkour.linkedCourse) != null) {


				Parkour linkedparkour = parkours.getParkour(parkour.linkedCourse);
				parkour.exit(user);
				//リンクされたアスレのスポーン地点にテレポートさせる


				//アスレに参加させる
				linkedparkour.entry(users.getUser(player));

				linkedparkour.teleport(user);

				BilingualText.stream("$color$parkour&r-にテレポートしました", "&r-You teleported to $color$parkour")
						.setAttribute("$parkour", parkour.linkedCourse)
						.setAttribute("$color", linkedparkour.prefixColor)
						.color()
						.setReceiver(player)
						.sendActionBarMessage();
			} else {
				user.parkourPlayingNow = null;
				user.progress = null;
				parkour.exit(user);
				user.exitCurrentParkour();
			}
		}


		//ランクアップアスレの場合
		if (parkour instanceof RankUpParkour) {
			ParkourCategory category = parkour.category;

			//アスレのランクを取得する
			int rank = ((RankUpParkour) parkour).rank;

			StatusBoard board = user.statusBoard;

			//各タイプで分岐する
			switch (category) {
				case UPDATE:
					//プレイヤーのランクの方が高ければ戻る
					if (user.updateRank() >= rank) return;

					//ランクを更新する
					user.incrementUpdateRank();

					//表示名を更新する
					ImprintRank.apply(user);

					board.updateUpdateRank();
					break;
				case EXTEND:
					//プレイヤーのランクの方が高ければ戻る
					if (user.extendRank() >= rank) return;

					//ランクを更新する
					user.incrementExtendRank();

					board.updateExtendRank();
					break;
				default:
					throw new NullPointerException("Ranked parkour type can not be null");
			}


			//RANK_UP_SE.play(player);

                    /*Tweet.display(player, BilingualText.stream("$typeランクが$rankに上がりました！", "$type rank went up to $rank!")
                            .setAttribute("$type", category.name)
                            .setAttribute("$rank", rank )
                            .textBy(player)
                            .color()
                            .toString());*/

			/*BilingualText.stream("$playerさんの&e$type&f-ランクが$color$rank&f-に上がりました！",
					"$player's &e$type &f-rank went up to $color$rank")
					.setAttribute("$color", RankColor.values()[rank].color)
					.setAttribute("$player", displayName)
					.setAttribute("$type", category.name)
					.setAttribute("$rank", rank)
					.color()
					.setReceivers(onlinePlayers)
					.sendChatMessage();*/

			//ツイートリンクを表示する
			//Tweet.～

		}

		//クリア回数に基づき報酬を取得する
		int coins = parkour.rewards.getReward(haveCleared ? 1 : 0);

		if (coins > 0) {
			//報酬のコインを与える
			user.depositCoins(coins);

			BilingualText.stream("報酬として&e-$coins&r-コインを与えました！", "Gave you &e-$coins &f-coins as reward!")
					.setAttribute("$coins", coins)
					.color()
					.setReceiver(player)
					.sendChatMessage();

		}

		if (haveCleared) {
			int count = 0;
			try {
				try (ResultSet rs = DatabaseManager.get().executeResultStatement(SQLQuery.SELECT_COURSE_COUNT, parkour.getId(),user.getId())) {
					if (rs.next()) {
						count = rs.getInt("count");
					} else {
						Utils.log("!! Not able to update ID of cleared_course! Please restart the server to resolve this issue!");
						Utils.log("!! Failed at: " + toString());
					}
				}
			} catch (SQLException ex) {
				Main.getPlugin().debugSqlException(ex);
			}
			System.out.println(count);
			DatabaseManager.get().executeStatement(SQLQuery.UPDATE_CLEARED_COURSE,count++,user.getId(), parkour.getId());
			//DatabaseUtils.updateClearedCourse(parkour, player);
		} else {
			DatabaseManager.get().executeStatement(SQLQuery.INSERT_CLEARED_COURSE, user.getId(), parkour.getId(),1);
			//DatabaseUtils.insertClearedCourse(parkour, player);
		}

		if (lobbies.getLobby(parkour.linkedLobby) != null) {

			String linkedLobby1 = parkour.linkedLobby;
			Lobby lobby1 = lobbies.getLobby(linkedLobby1);

			//リンクされたロビーのスポーン地点にテレポートさせる
			lobby1.teleport(player);

			parkour.exit(user);

			BilingualText.stream("&a-$linkedlobby&r-にテレポートしました", "&r-You teleported to &a-$linkedlobby")
					.setAttribute("$linkedlobby", linkedLobby1)
					.color()
					.setReceiver(player)
					.sendActionBarMessage();


		} else if (parkours.getParkour(parkour.linkedCourse) != null) {

			Parkour linkedparkour = parkours.getParkour(parkour.linkedCourse);

			parkour.exit(user);
			//リンクされたアスレのスポーン地点にテレポートさせる

			//アスレに参加させる
			linkedparkour.entry(users.getUser(player));

			linkedparkour.teleport(user);

			BilingualText.stream("$color$parkour&r-にテレポートしました", "&r-You teleported to $color$parkour")
					.setAttribute("$parkour", parkour.linkedCourse)
					.setAttribute("$color", linkedparkour.prefixColor)
					.color()
					.setReceiver(player)
					.sendActionBarMessage();
		} else {
			parkour.exit(user);
			user.exitCurrentParkour();
		}

		//クリア済みのアスレとして記録する(コレクションにはSetを用いているため要素の重複は起こらない)
		user.clearedParkourNames.add(parkourName);
	}
}
