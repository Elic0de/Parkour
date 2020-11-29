package elicode.parkour.user;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import elicode.location.ImmutableLocation;
import elicode.parkour.parkour.Parkour;
import elicode.parkour.parkour.ParkourSet;
import elicode.parkour.util.tuplet.Tuple;
import elicode.parkour.util.Optional;
import elicode.parkour.util.yaml.Yaml;
import org.bukkit.entity.Player;

public class CheckpointSet {

	private final ParkourSet parkours = ParkourSet.getInstance();

	private final Map<String, Map<Integer, ImmutableLocation>> checkpoints = new HashMap<>();
	private final Map<String, Integer> latestCheckpoints = new HashMap<>();

	public CheckpointSet(Yaml yaml){
		//セクションが存在しなければ戻る
		if(!yaml.isConfigurationSection("Check points")) return;

		//セクションを取得する
		ConfigurationSection parkourSection = yaml.getConfigurationSection("Check points");

		//各アスレ名毎に処理する
		for(String parkourName : parkourSection.getKeys(false)){
			//存在しないアスレであれば繰り返す
			if(!parkours.containsParkour(parkourName)) continue;

			//アスレ名と対応したアスレを取得する
			Parkour parkour = parkours.getParkour(parkourName);
			ImmutableLocation origin = parkour.originLocation();

			//このアスレのセクションを取得する
			ConfigurationSection checkAreaSection = parkourSection.getConfigurationSection(parkourName);

			//各チェックエリア番号毎に処理をする
			for(String checkAreaNumberText : checkAreaSection.getKeys(false)){
				if(checkAreaNumberText.equals("Latest")) continue;

				//チェックエリア番号を整数型に変換する
				int checkAreaNumber = Integer.parseInt(checkAreaNumberText);

				//チェックポイントのデータをデシリアライズする
				ImmutableLocation point = origin.add(ImmutableLocation.deserialize(checkAreaSection.getString(checkAreaNumberText)));

				//チェックポイントとして登録する
				setCheckpoint(parkour, checkAreaNumber, point);
			}

			//最新のチェックエリア番号を取得する
			int latestCheckAreaNumber = checkAreaSection.getInt("Latest");
			latestCheckpoints.put(parkourName, latestCheckAreaNumber);
		}
	}

	public boolean hasCheckpoint(Parkour parkour){
		return hasCheckpoint(parkour.name);
	}

	public boolean hasCheckpoint(String parkourName){
		if(checkpoints != null) {
			return checkpoints.containsKey(parkourName);
		}
		return false;
	}

	public List<Tuple<Integer, ImmutableLocation>> getCheckpoints(Parkour parkour){
		return getCheckpoints(parkour.name);
	}

	public List<Tuple<Integer, ImmutableLocation>> getCheckpoints(String parkourName){
		//チェックポイントが存在しなければ戻る
		if(!checkpoints.containsKey(parkourName)) return Collections.emptyList();

		return checkpoints.get(parkourName).entrySet().stream()
				.sorted(Entry.comparingByKey())
				.map(entry -> new Tuple<>(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());
	}

	public List<Tuple<Integer, ImmutableLocation>> getCheckpoints(Parkour parkour, int limit){
		return getCheckpoints(parkour.name, limit);
	}

	public List<Tuple<Integer, ImmutableLocation>> getCheckpoints(String parkourName, int limit){
		//チェックポイントが存在しなければ戻る
		if(!checkpoints.containsKey(parkourName)) return Collections.emptyList();

		return checkpoints.get(parkourName).entrySet().stream()
				.filter(entry -> entry.getKey() <= limit)
				.sorted(Entry.comparingByKey())
				.map(entry -> new Tuple<>(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());
	}

	public Optional<Tuple<Integer, ImmutableLocation>> getCheckpoint(Parkour parkour, Integer checkAreaNumber){
		return getCheckpoint(parkour.name, checkAreaNumber);
	}

	public Optional<Tuple<Integer, ImmutableLocation>> getCheckpoint(String parkourName, Integer checkAreaNumber){
		//チェックポイントが存在しなければ戻る
		if(!checkpoints.containsKey(parkourName)) return Optional.empty();

		Map<Integer, ImmutableLocation> checkpointMap = checkpoints.get(parkourName);

		//チェックポイントが存在しなければ戻る
		if(!checkpointMap.containsKey(checkAreaNumber)) return Optional.empty();

		ImmutableLocation checkpointLocation = checkpointMap.get(checkAreaNumber);

		return Optional.of(new Tuple<>(checkAreaNumber, checkpointLocation));
	}

	public Optional<Tuple<Integer, ImmutableLocation>> getLastCheckpoint(Parkour parkour){
		return getLastCheckpoint(parkour.name);
	}

	public Optional<Tuple<Integer, ImmutableLocation>> getLastCheckpoint(String parkourName){
		//チェックポイントが存在しなければ戻る
		if(!checkpoints.containsKey(parkourName)) Optional.empty();

		Map<Integer, ImmutableLocation> checkpointMap = checkpoints.get(parkourName);

		int lastCheckpointNumber = checkpointMap.keySet().stream()
				.mapToInt(Integer::intValue)
				.max()
				.orElse(-1);

		if(lastCheckpointNumber <= -1) return Optional.empty();

		ImmutableLocation lastCheckpointLocation = checkpointMap.get(lastCheckpointNumber);

		return Optional.of(new Tuple<>(lastCheckpointNumber, lastCheckpointLocation));
	}

	public Optional<Tuple<Integer, ImmutableLocation>> getLastCheckpoint(Parkour parkour, int limit){
		return getLastCheckpoint(parkour.name, limit);
	}

	public Optional<Tuple<Integer, ImmutableLocation>> getLastCheckpoint(String parkourName, int limit){
		//チェックポイントが存在しなければ戻る
		if(!checkpoints.containsKey(parkourName)) Optional.empty();

		Map<Integer, ImmutableLocation> checkpointMap = checkpoints.get(parkourName);

		int lastCheckpointNumber = checkpointMap.keySet().stream()
				.mapToInt(Integer::intValue)
				.filter(i -> i <= limit)
				.max()
				.orElse(-1);

		if(lastCheckpointNumber <= -1) return Optional.empty();

		ImmutableLocation lastCheckpointLocation = checkpointMap.get(lastCheckpointNumber);

		return Optional.of(new Tuple<>(lastCheckpointNumber, lastCheckpointLocation));
	}

	public Optional<Tuple<Integer, ImmutableLocation>> getLatestCheckpoint(Parkour parkour){
		return getLatestCheckpoint(parkour.name);
	}

	public Optional<Tuple<Integer, ImmutableLocation>> getLatestCheckpoint(String parkourName){
		//最新のチェックポイントが存在しなければ戻る
		if(!latestCheckpoints.containsKey(parkourName)) return Optional.empty();

		//最新のチェックエリア番号を取得する
		int latestCheckAreaNumber = latestCheckpoints.get(parkourName);

		//念の為にチェックポイントマップが存在しなければ戻る
		if(!checkpoints.containsKey(parkourName)) return Optional.empty();

		//アスレ内のチェックポイントマップを取得する
		Map<Integer, ImmutableLocation> points = checkpoints.get(parkourName);

		ImmutableLocation latestCheckpointLocation = points.get(latestCheckAreaNumber);

		return Optional.of(new Tuple<>(latestCheckAreaNumber, latestCheckpointLocation));
	}

	public Optional<Tuple<Integer, ImmutableLocation>> getLatestCheckpoint(Parkour parkour, int limit){
		return getLatestCheckpoint(parkour.name, limit);
	}

	public Optional<Tuple<Integer, ImmutableLocation>> getLatestCheckpoint(String parkourName, int limit){
		//最新のチェックポイントが存在しなければ戻る
		if(!latestCheckpoints.containsKey(parkourName)) return Optional.empty();

		//最新のチェックエリア番号を取得する
		int latestCheckAreaNumber = latestCheckpoints.get(parkourName);

		//念の為にチェックポイントマップが存在しなければ戻る
		if(latestCheckAreaNumber > limit || !checkpoints.containsKey(parkourName)) return Optional.empty();

		//アスレ内のチェックポイントマップを取得する
		Map<Integer, ImmutableLocation> points = checkpoints.get(parkourName);

		ImmutableLocation latestCheckpointLocation = points.get(latestCheckAreaNumber);

		return Optional.of(new Tuple<>(latestCheckAreaNumber, latestCheckpointLocation));
	}

	public void setCheckpoint(Parkour parkour, int checkAreaNumber, ImmutableLocation location){
		String parkourName = parkour.name;

		//パルクールに対応したチェックポイントリストを取得、存在しなければ新規作成する
		Map<Integer, ImmutableLocation> points = checkpoints.get(parkourName);
		if(points == null) checkpoints.put(parkourName, points = new HashMap<>());

		//対応した番号にチェックポイントをセットする
		points.put(checkAreaNumber, location);

		//最新のチェックポイントを更新する
		latestCheckpoints.put(parkourName, checkAreaNumber);
	}
	public void setCheckpoint(User user, int checkAreaNumber, ImmutableLocation location){
		String userName = user.asBukkitPlayer().getName();

		//パルクールに対応したチェックポイントリストを取得、存在しなければ新規作成する
		Map<Integer, ImmutableLocation> points = checkpoints.get(userName);
		if(points == null) checkpoints.put(userName, points = new HashMap<>());

		//対応した番号にチェックポイントをセットする
		points.put(checkAreaNumber,location);

		//最新のチェックポイントを更新する
		latestCheckpoints.put(userName, checkAreaNumber);
	}

	public void save(Yaml yaml){
		//各チェックポイントを記録する
		for(Entry<String, Map<Integer, ImmutableLocation>> parkourCheckpointsEntry : checkpoints.entrySet()){
			//アスレ名を取得する
			String parkourName = parkourCheckpointsEntry.getKey();

			//存在しないアスレであれば繰り返す
			if(!parkours.containsParkour(parkourName)) continue;

			ImmutableLocation origin = parkours.getParkour(parkourName).originLocation();

			for(Entry<Integer, ImmutableLocation> checkpointEntry : parkourCheckpointsEntry.getValue().entrySet()){
				//チェックエリア番号を取得する
				String checkAreaNumber = checkpointEntry.getKey().toString();

				//チェックポイントを取得し相対座標化する
				ImmutableLocation point = origin.relative(checkpointEntry.getValue());

				//対応したアスレ、チェックエリア番号にセットする
				yaml.set("Check points." + parkourName + "." + checkAreaNumber, point.serialize());
			}

			//最新のチェックポイントをセットする
			yaml.set("Check points." + parkourName + ".Latest", latestCheckpoints.get(parkourName));
		}
	}

}
