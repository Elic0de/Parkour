package elicode.parkour.parkour;

import java.io.File;
import java.util.*;
import java.util.stream.Stream;

import elicode.parkour.Main;
import elicode.parkour.util.chunk.ChunksToObjectsMap;
import elicode.parkour.util.yaml.Yaml;
import net.md_5.bungee.api.ChatColor;

public class ParkourSet {

	private static ParkourSet instance;

	public static void load(){
		instance = new ParkourSet();
	}

	public static ParkourSet getInstance(){
		return instance;
	}

	private final Main plugin = Main.getPlugin();

	//アスレデータを保存するフォルダー
	public final File folder = new File(plugin.getDataFolder() + File.separator + "Parkours");

	//アスレのマップ
	private final Map<String, Parkour> parkours = new HashMap<>();

	//スタートラインのチャンクマップ
	public final ChunksToObjectsMap<ParkourRegion> chunksToStartLinesMap = new ChunksToObjectsMap<>();

	public final ChunksToObjectsMap<ParkourRegion> chunksToArenaMap = new ChunksToObjectsMap<>();

	//フィニッシュラインのチャンクマップ
	public final ChunksToObjectsMap<ParkourRegion> chunksToFinishLinesMap = new ChunksToObjectsMap<>();

	//チェックエリアのチャンクマップ
	public final ChunksToObjectsMap<ParkourRegion> chunksToPortalMap = new ChunksToObjectsMap<>();

	//チェックエリアのチャンクマップ
	public final ChunksToObjectsMap<ParkourRegion> chunksToCheckAreasMap = new ChunksToObjectsMap<>();

	private ParkourSet(){
		//フォルダーが存在しなければ作成する
		if(!folder.exists()) folder.mkdirs();

		//各アスレコンフィグ毎に処理をする
		for(File file : Optional.ofNullable(folder.listFiles()).orElse(new File[0])){
			String fileName = file.getName();

			//拡張子を削除してアスレ名を取得する
			String parkourName = fileName.substring(0, fileName.length() - 4);

			//アスレを登録する
			registerParkour(parkourName);
		}
	}

	public void saveAll(){
		parkours.values().forEach(Parkour::save);
	}

	public boolean existsFil(String parkourName){
		return new File(folder, parkourName + ".yml").exists();
	}

	public void registerParkour(Parkour parkour){
		parkours.put(parkour.name, parkour);

		//有効化されていなければ戻る
		if(!parkour.enable) return;

		//スタートラインを登録する
		registerStartLine(parkour.startLine);

		//フィニッシュラインを登録する
		registerFinishLine(parkour.finishLine);

		registerArena(parkour.arena);

		registerPortal(parkour.portal);

		//全チェックエリアを登録する
		parkour.checkAreas.registerAll();
	}

	public void registerParkour(String parkourName){
		File file = new File(folder, parkourName.replace('§', '&') + ".yml");

		//コンフィグが存在しなければ戻る
		if(!file.exists()) return;

		//コンフィグを取得する
		Yaml yaml = makeYaml(parkourName.replace('§', '&'));

		//コンフィグに基づきアスレを生成する
		Parkour parkour = RankUpParkour.isRankedParkour(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&',yaml.name))) ? new RankUpParkour(this, yaml) : new Parkour(this, yaml);

		registerParkour(parkour);
	}

	public void unregisterParkour(Parkour parkour){
		//スタートラインの登録を解除する
		if(parkour.startLine != null) unregisterStartLine(parkour.startLine);

		//フィニッシュラインの登録を解除する
		if(parkour.finishLine != null) unregisterFinishLine(parkour.finishLine);

		if (parkour.arena != null) unregisterPortal(parkour.arena);

		if (parkour.portal != null) unregisterPortal(parkour.portal);

		//全チェックエリアの登録を解除する
		parkour.checkAreas.unregisterAll();

		parkours.remove(parkour.name);
	}

	public void unregisterParkour(String parkourName){
		if(containsParkour(parkourName)) unregisterParkour(getParkour(parkourName));
	}

	public Collection<Parkour> getParkours(){
		return parkours.values();
	}

	public Stream<Parkour> getEnabledParkours(ParkourCategory category){
		return parkours.values().stream()
				.filter(parkour -> parkour.category == category)
				.filter(parkour -> parkour.enable);
	}

	public Parkour getParkour(String parkourName){
		return parkours.get(parkourName);
	}

	public Parkour getRandomParkour() {
		return parkours.get(new Random(parkours.size()));
	}

	public boolean containsParkour(Parkour parkour){
		return containsParkour(parkour.name);
	}

	public boolean containsParkour(String parkourName){
		return parkours.containsKey(parkourName);
	}

	public void registerStartLine(ParkourRegion startLine){
		registerParkourRegion(startLine, chunksToStartLinesMap);
	}

	public void unregisterStartLine(ParkourRegion startLine){
		unregisterParkourRegion(startLine, chunksToStartLinesMap);
	}

	public void registerArena(ParkourRegion arena) {
		registerParkourRegion(arena, chunksToArenaMap);
	}
	public void unregisterArena(ParkourRegion arena) {
		unregisterParkourRegion(arena,chunksToArenaMap);
	}

	public void registerFinishLine(ParkourRegion finishLine){
		registerParkourRegion(finishLine, chunksToFinishLinesMap);
	}

	public void unregisterFinishLine(ParkourRegion finishLine){
		unregisterParkourRegion(finishLine, chunksToFinishLinesMap);
	}

	public void registerPortal(ParkourRegion portal){
		registerParkourRegion(portal, chunksToPortalMap);
	}

	public void unregisterPortal(ParkourRegion portal){
		unregisterParkourRegion(portal, chunksToPortalMap);
	}

	public void registerCheckArea(ParkourRegion checkArea){
		registerParkourRegion(checkArea, chunksToCheckAreasMap);
	}

	public void unregisterCheckArea(ParkourRegion checkArea){
		unregisterParkourRegion(checkArea, chunksToCheckAreasMap);
	}

	public Yaml makeYaml(String parkourName){
		return new Yaml(plugin, new File(folder, parkourName + ".yml"), "parkour.yml");
	}

	private void registerParkourRegion(ParkourRegion region, ChunksToObjectsMap<ParkourRegion> chunksToRegionsMap){
		if(region == null) return;

		//領域を登録する
		chunksToRegionsMap.putAll(region.lesserBoundaryCorner,  region.greaterBoundaryCorner, region);

		//境界線の描画を始める
		region.displayBorders();
	}

	private void unregisterParkourRegion(ParkourRegion region, ChunksToObjectsMap<ParkourRegion> chunksToRegionsMap){
		if(region == null) return;

		//境界線の描画を止める
		region.undisplayBorders();

		//領域の登録を解除する
		chunksToRegionsMap.removeAll(region.lesserBoundaryCorner,  region.greaterBoundaryCorner, region);
	}

}
