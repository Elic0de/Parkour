package elicode.parkour.parkour;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import elicode.parkour.Main;
import elicode.parkour.user.StatusBoard;
import elicode.parkour.user.TimeAttackProgress;
import elicode.parkour.util.Utils;
import elicode.parkour.util.databases.DatabaseManager;
import elicode.parkour.util.databases.SQLQuery;
import elicode.parkour.util.text.BilingualText;
import elicode.parkour.user.ParkourChallengeProgress;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Consumer;


import elicode.beta.parkour.location.ImmutableLocation;
import elicode.parkour.region.Region;
import elicode.parkour.user.User;
import elicode.parkour.util.Color;
import elicode.parkour.util.Splitter;
import elicode.parkour.util.yaml.Yaml;

public class Parkour {

	public static final Pattern PREFIX_PATTERN = Pattern.compile("(?i)§[0-9a-fA-F]");

	private final ParkourSet parkours;
	private int id;

	public String name;
	public final String prefixColor;
	public boolean enable;
	public ParkourCategory category;
	public Color borderColor;
	public Region region;
	public ImmutableLocation spawn;
	public ParkourRegion startLine, finishLine,portal, arena;
	public CheckAreas checkAreas;
	public Rewards rewards;
	public Difficulty difficulty;
	public boolean timeAttackEnable;
	public Records records;
	public String description;
	public String linkedCourse;
	public String linkedLobby;
	public String creator;
	public PlayerConnections connections = new PlayerConnections();

	public Parkour(ParkourSet parkours, Yaml yaml){
		this.parkours = parkours;

		//yaml.nameは拡張子を取り除いたファイル名を返すのでアスレ名としてそのまま設定する
		name = ChatColor.translateAlternateColorCodes('&', yaml.name);

		Matcher prefixColorMatcher = PREFIX_PATTERN.matcher(name);
		prefixColor = prefixColorMatcher.find() ? prefixColorMatcher.group() : "§f";

		enable = yaml.getBoolean("Enable");
		category = ParkourCategory.valueOf(yaml.getString("Category"));
		linkedCourse = yaml.getString("linkedCourse");
		creator = yaml.getString("Creator");

		//全座標の基準点
		ImmutableLocation origin = ImmutableLocation.deserialize(yaml.getString("Origin"));

		//座標を主とするこれらのデータは基準点の相対座標として保存されているため絶対座標に変換して設定する

		//領域を設定する
		Region relativeRegion = Region.deserialize(yaml.getString("Region"));
		region = relativeRegion.add(origin);

		//スポーン地点を設定する
		ImmutableLocation relativeSpawn = ImmutableLocation.deserialize(yaml.getString("Spawn"));
		spawn = origin.add(relativeSpawn);

		//ボーダーカラーはParkourRegionより先に読み込む
		borderColor = Color.deserialize(yaml.getString("Border color"));

		//スタートラインを設定する
		Region relativeStartLine = Region.deserialize(yaml.getString("Start line"));
		startLine = new ParkourRegion(this, relativeStartLine.add(origin));

		//を設定する
		Region relativeArena = Region.deserialize(yaml.getString("Region"));
		arena = new ParkourRegion(this, relativeArena.add(origin));

		//フィニッシュラインを設定する
		Region relativeFinishLine = Region.deserialize(yaml.getString("Finish line"));
		finishLine =  new ParkourRegion(this, relativeFinishLine.add(origin));

		Region relativePortal = Region.deserialize(yaml.getString("Portal"));
		portal =  new ParkourRegion(this, relativePortal.add(origin));

		//チェックエリアを設定する
		checkAreas = new CheckAreas(parkours, yaml, this, origin);

		timeAttackEnable = yaml.getBoolean("Time attack");
		rewards = new Rewards(Splitter.splitToIntArguments(yaml.getString("Rewards")));
		difficulty = new Difficulty(Splitter.splitToIntArguments(yaml.getString("Difficulty")));
		description = yaml.getString("Description");
		records = new Records(yaml);

		/*try {
			//DatabaseManager.get().executeStatement(SQLQuery.INSERT_COURSE,colorlessName(),category,creator,timeAttackEnable);
			try (ResultSet rs = DatabaseManager.get().executeResultStatement(SQLQuery.SELECT_COURSE_ID, colorlessName())) {
				if (rs.next()) {
					this.id = rs.getInt("courseId");
				} else {
					Utils.log("!! Not able to update ID of parkour! Please restart the server to resolve this issue!");
					Utils.log("!! Failed at: " + toString());
				}
			}
		} catch (SQLException ex) {
			Main.getPlugin().debugSqlException(ex);
		}*/
		//System.out.println(id);
	}

	public String colorlessName(){
		return ChatColor.stripColor(name);
	}

	public World world(){
		return region.world;
	}

	public ImmutableLocation originLocation(){
		return region.lesserBoundaryCorner;
	}

	public void teleport(User user){
		//念のため
		undisplayParticles(user);

		if (this instanceof RankUpParkour) {
			ParkourCategory category = this.category;

			//アスレのランクを取得する
			int rank = ((RankUpParkour) this).rank;

			//各タイプで分岐する
			switch (category) {
				case UPDATE:
					//プレイヤーのランクの方が高ければ戻る
					if (!(rank <= user.updateRank() + 1)) return;
					user.asBukkitPlayer().teleport(spawn.asBukkit());
					break;
				case EXTEND:
					//プレイヤーのランクの方が高ければ戻る
					if (!(rank <= user.extendRank() + 1)) return;
					user.asBukkitPlayer().teleport(spawn.asBukkit());
					break;

				default:
					throw new NullPointerException("Ranked parkour type can not be null");

			}
		}else {
			user.asBukkitPlayer().teleport(spawn.asBukkit());
		}
	}

	public void entry(User user){
		//このアスレ以外のアスレに参加していれば退出させる
		if(user.isOnParkour() && !name.equals(user.currentParkour.name)) user.currentParkour.exit(user);
		user.currentParkour = this;
		user.parkourPlayingNow = this;

		if(user.parkourPlayingNow.timeAttackEnable) {
			if(user.getTimeAttackProgress() != null) user.getTimeAttackProgress().cancelTaskThatDisplaysElapsedTime();
			user.setTimeAttackProgress(new TimeAttackProgress(user,user.currentParkour));
			user.getTimeAttackProgress().startMeasuringTime();
			user.startTime = user.getTimeAttackProgress().getStartTime();
		}

		if(!user.playerSettings.particle) {
			displayParticles(user);
		}

		startParkour(user.currentParkour, user);
	}

	public void displayParticles(User user){
		//パケット送信用のコネクションリストに追加する
		connections.add(user.asBukkitPlayer());

		//全境界線を表示する
		if (timeAttackEnable) {
			startLine.displayBorders();
			finishLine.displayBorders();

		}
		portal.undisplayBorders();
		checkAreas.displayAll();
	}
	public void displayPortalBorder(User user) {

		//パケット送信用のコネクションリストに追加する
		connections.add(user.asBukkitPlayer());

		portal.displayBorders();

	}

	public void exit(User user){
		user.progress = null;
		user.parkourPlayingNow = null;
		if(user.currentParkour != null && user.currentParkour.timeAttackEnable && user.getTimeAttackProgress() != null )user.getTimeAttackProgress().cancelTaskThatDisplaysElapsedTime();

		user.setTimeAttackProgress(null);
		displayPortalBorder(user);
		undisplayParticles(user);
	}

	public void undisplayParticles(User user){
		connections.remove(user.asBukkitPlayer());

		//プレイヤーがいれば戻る
		if(!connections.isEmpty()) return;

		//全境界線を非表示にする
		startLine.undisplayBorders();
		finishLine.undisplayBorders();
		checkAreas.undisplayAll();
	}


	public  void startParkour(Parkour parkour, User user){

		boolean timeAttackEnable = parkour.timeAttackEnable;

		if(user.isPlayingParkour()){

		//タイムアタックが有効なら戻る
		if(!timeAttackEnable) return;

			//通常アスレをプレイし始めた場合
		}else {
			//タイムアタックが有効でなければ戻る
			if (timeAttackEnable) return;

			user.progress = new ParkourChallengeProgress();
			user.parkourPlayingNow = parkour;
		}
	}

	public void update(Consumer<Parkour> applier){
		parkours.unregisterParkour(this);
		applier.accept(this);
		parkours.registerParkour(this);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getColorAndParkourName() {
		return name.replace('§', '&');
	}

	public void save(){
		Yaml yaml = ParkourSet.getInstance().makeYaml(name.replace('§', '&'));
		yaml.set("Enable", enable);
		yaml.set("Category", category.toString());
		yaml.set("Creator", creator);

		//アスレの領域の基準点を取得する
		ImmutableLocation origin = originLocation();


		yaml.set("Origin", origin.serialize());
		yaml.set("Region", region.relative(origin).serialize());

		yaml.set("Spawn", origin.relative(spawn).serialize());

		yaml.set("Border color", borderColor.serialize());
		yaml.set("Start line", startLine.relative(origin).serialize());
		yaml.set("Finish line", finishLine.relative(origin).serialize());
		yaml.set("Portal", portal.relative(origin).serialize());

		checkAreas.save(yaml, origin);

		yaml.set("Rewards", rewards.serialize());
		yaml.set("Difficulty", difficulty.serialize());
		yaml.set("Time attack", timeAttackEnable);
		yaml.set("Description", description);
		yaml.set("linkedCourse", linkedCourse);
		yaml.set("linkedLobby", linkedLobby);

		records.save(yaml);

		yaml.save();
	}

}
