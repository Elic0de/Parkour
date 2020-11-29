package elicode.parkour.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import elicode.parkour.Main;
import elicode.parkour.game.fight.Fight;
import elicode.parkour.util.Utils;
import elicode.parkour.util.databases.DatabaseManager;
import elicode.parkour.util.databases.SQLQuery;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import elicode.location.ImmutableLocation;
import elicode.parkour.function.parkour.ControlFunctionalItem;
import elicode.parkour.function.parkour.ItemType;
import elicode.parkour.parkour.Parkour;
import elicode.parkour.parkour.ParkourRegion;
import elicode.parkour.parkour.ParkourSet;
import elicode.parkour.util.Optional;
import elicode.parkour.util.yaml.Yaml;

public class User {

	//UUID
	public final UUID uuid;

	private int id;

	//Updateのランク
	private int updateRank;

	//Extendのランク
	private int extendRank;

	private int ranked;

	//コイン
	private int coins;

	private TimeAttackProgress  timeAttackProgress;
	//Lists
	private Fight playingGame;

	//レート
	private int rate;

	//今いるアスレ
	public Parkour currentParkour;

	//今プレイ中のアスレ
	public Parkour parkourPlayingNow;

	public String teleporterItem;

	//今いるチェックエリア
	public ParkourRegion currentCheckArea;

	//プレイし始めた時間(ミリ秒)
	public long startTime;

	//アスレのプレイ開始からログアウトまでの経過時間(ミリ秒)
	public long timeElapsed;

	//各アスレのチェックポイント
	public final CheckpointSet checkpoints;

	//ステータスボードの設定
	public final StatusBoardSetting setting;

	public final PlayerSettings playerSettings;

	//クリア済みのアスレの名前リスト
	public final Set<String> clearedParkourNames;

	//クリエイティブワールドでのチェックポイント
	public ImmutableLocation creativeWorldCheckpoint;

	//プレイヤー非表示モードかどうか
	public boolean hideMode;

	public boolean nightVisionMode;

	public boolean particle;

	//購入したヘッドのセット
	public final PurchasedHatCollection hats;

	//購入したヘッドのセット
	public final PurchasedCreative creative;

	public final PurchasedTeleportItem items;

	//ステータスボード
	public StatusBoard statusBoard;

	//各UIを保持している
	public InventoryUISet inventoryUserInterfaces;

	public InventoryUISet inventoryUserDuelInterfaces;

	//アスレの進捗度
	public ParkourChallengeProgress progress;

	public String language;

	public User(Yaml yaml){
		//ファイル名に基づきUUIDを生成し代入する
		this.uuid = UUID.fromString(yaml.name);

		//Updateランクを取得する
		updateRank = yaml.getInt("Update rank");

		ranked = yaml.getInt("Ranked rank");

		//Extendランクを取得する
		extendRank = yaml.getInt("Extend rank");

		//コイン数を取得する
		coins = yaml.getInt("Coins");

		//レートを取得する
		rate = yaml.getInt("Rate");

		ParkourSet parkours = ParkourSet.getInstance();

		//最後にいたアスレを取得する
		currentParkour = parkours.getParkour(yaml.getString("Last parkour"));
		//最後に遊んでいたアスレを取得する
		parkourPlayingNow = parkours.getParkour(yaml.getString("Last played parkour"));

		//タイムアタックを始めてからの経過時間を取得する
		timeElapsed = yaml.getLong("Time elapsed");

		teleporterItem = yaml.getString("TeleporterItem");

		checkpoints = new CheckpointSet(yaml);

		//個人設定はYamlに基づき生成する
		setting = new StatusBoardSetting(yaml);

		playerSettings = new PlayerSettings(yaml);

		//クリア済みのアスレ名リストを取得してセットでラップする
		clearedParkourNames = new HashSet<>(yaml.getStringList("Cleared parkour names"));

		//データを基に座標を作成する
		creativeWorldCheckpoint = ImmutableLocation.deserialize(yaml.getString("Creative world checkpoint"));

		hideMode = yaml.getBoolean("Hide mode");

		particle = yaml.getBoolean("Particle");

		nightVisionMode = yaml.getBoolean("NightVision mode");

		language = yaml.getString("language");

		//購入済みのスカルのIDをUUIDに変換したリストを作成する
		hats = new PurchasedHatCollection(this, yaml);

		creative = new PurchasedCreative(this, yaml);

		items = new PurchasedTeleportItem(this, yaml);

		int currentCheckAreaNumber = yaml.getInt("Parkour challenge progress");

		if(currentCheckAreaNumber > 0) progress = new ParkourChallengeProgress(currentCheckAreaNumber);

		/*try {
			//DatabaseManager.get().executeStatement(SQLQuery.INSERT_COURSE, getName(), getUuid(), getReason(), getOperator(), getType().name(), getStart(), getEnd(), getCalculation());
			try (ResultSet rs = DatabaseManager.get().executeResultStatement(SQLQuery.SELECT_PLAYER_ID,uuid)) {
				if (rs.next()) {
					this.id = rs.getInt("player_id");
				} else {
					Utils.log("!! Not able to update ID of user! Please restart the server to resolve this issue!");
					Utils.log("!! Failed at: " + toString());
				}
			}
		} catch (SQLException ex) {
			Main.getPlugin().debugSqlException(ex);
		}
		System.out.println(id + "user");*/
	}

	//このユーザーに対応したプレイヤーを取得する
	public Player asBukkitPlayer(){
		return Bukkit.getPlayer(uuid);
	}

	public int getId() {
		return id;
	}

	public int updateRank(){
		return updateRank;
	}

	public void incrementUpdateRank(){
		updateRank++;
	}

	public int rankedrank() {
		return ranked;
	}

	public void incrementRanked(int rank){
		ranked = rank;
	}

	public int extendRank(){
		return extendRank;
	}

	public void incrementExtendRank(){
		extendRank++;
	}

	public int coins(){
		return coins;
	}

	public int getRate() {
		return rate;
	}

	public void addElo(int elo) {
		this.rate += elo;
	}

	public void removeElo(int elo){
		this.rate = Math.max(this.rate - elo, 0);

	}

	public TimeAttackProgress getTimeAttackProgress() {
		return timeAttackProgress;
	}

	public void setTimeAttackProgress(TimeAttackProgress timeAttackProgress) {
		this.timeAttackProgress = timeAttackProgress;
	}

	//指定数だけ所持コイン数を増やす
	public void depositCoins(int coins){
		this.coins += coins;

		if(statusBoard != null) statusBoard.updateCoins();
	}

	//指定数だけ所持コイン数を減らす
	public void withdrawCoins(int coins){
		this.coins = Math.max(this.coins - coins, 0);

		if(statusBoard != null) statusBoard.updateCoins();
	}

	//今アスレ内にいるかどうか
	public boolean isOnParkour(){
		return currentParkour != null;
	}

	//アスレから退出する
	public void exitCurrentParkour(){
		if(!isOnParkour()) return;

		if( currentParkour != null && currentParkour.timeAttackEnable && getTimeAttackProgress() != null ) getTimeAttackProgress().cancelTaskThatDisplaysElapsedTime();

		//今いるアスレから退出する
		currentParkour.exit(this);

		timeAttackProgress = null;

		parkourPlayingNow = null;

		progress = null;

		currentParkour = parkourPlayingNow = null;

		//通知アイテムを更新する
		//ControlFunctionalItem.updateSlot(asBukkitPlayer(), ItemType.CHERCKPOINT_TELEPORTER);

	}
	public void exitCurrentGame(){

		playingGame = null;
	}

	//今アスレをプレイ中かどうか
	public boolean isPlayingParkour(){
		return parkourPlayingNow != null;
	}

	public boolean isPlayingGame(){
		return playingGame != null;
	}

	//今チェックエリア内にいるかどうか
	public boolean isOnCheckArea(){
		return currentCheckArea != null;
	}

	public Optional<StatusBoard> statusBoard(){
		return Optional.of(statusBoard);
	}

	public boolean isChallenging(){
		return progress != null;
	}

	public Optional<ParkourChallengeProgress> parkourChallengeProgress(){
		return Optional.of(progress);
	}

	public void setPlayingGame(Fight playingGame) {
		this.playingGame = playingGame;
	}

	public Fight getPlayingGame() {
		return playingGame;
	}

	public static boolean isOnline(String playerName) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getName().equalsIgnoreCase(playerName)) {
				return true;
			}
		}
		return false;
	}

	public void save(){
		Yaml yaml = UserSet.getInstnace().makeYaml(uuid);

		//Updateランクを記録する
		yaml.set("Update rank", updateRank);

		//Extendランクを取得する
		yaml.set("Extend rank", extendRank);

		//Rankedランクを取得する
		yaml.set("Ranked rank", ranked);

		//コイン数を記録する
		yaml.set("Coins", coins);

		//レートを記録する
		yaml.set("Rate", rate);

		//最後にいたアスレの名前を記録する
		yaml.set("Last parkour", currentParkour != null ? currentParkour.name : null);

		//最後にプレイしていたアスレの名前を記録する
		yaml.set("Last played parkour", parkourPlayingNow != null ? parkourPlayingNow.name : null);

		//タイムアタック中であれば経過時間を記録し、そうでなければ削除する
		yaml.set("Time elapsed", timeElapsed > 0 ? timeElapsed : null);

		//クリア済みのアスレの名前リストを記録する
		yaml.set("Cleared parkour names", clearedParkourNames.stream().collect(Collectors.toList()));

		//クリエイティブワールドのチェックポイントを記録する
		yaml.set("Creative world checkpoint", creativeWorldCheckpoint.serialize());

		yaml.set("Hide mode", hideMode);

		yaml.set("TeleporterItem", teleporterItem);

		yaml.set("Particle", particle);

		yaml.set("NightVision mode", nightVisionMode);

		yaml.set("language", language);

		if(progress != null) yaml.set("Parkour challenge progress", progress.currentCheckAreaNumber());

		hats.save(yaml);
		items.save(yaml);
		creative.save(yaml);
		checkpoints.save(yaml);
		setting.save(yaml);
		playerSettings.save(yaml);

		//セーブする
		yaml.save();
	}

}
