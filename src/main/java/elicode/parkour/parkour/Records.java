package elicode.parkour.parkour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import elicode.parkour.util.databases.DatabaseManager;
import elicode.parkour.util.databases.SQLQuery;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import elicode.parkour.schedule.Async;
import elicode.parkour.util.tuplet.Tuple;
import elicode.parkour.util.format.TimeFormat;
import elicode.parkour.util.yaml.Yaml;

public class Records {

	//全記録
	private final Map<UUID, Long> records;

	//上位10件の記録(非同期でリストを操作する為スレッドセーフなリストにしている)
	public final List<Tuple<UUID, String>> topTenRecords = new CopyOnWriteArrayList<>();

	private Map<UUID, Integer> rank = new HashMap<>();

	//撤回された記録の保有者リスト
	private final List<UUID> holdersOfWithdrawnRecords = new ArrayList<>();

	public Records(Yaml yaml){
		if(!yaml.isConfigurationSection("Records")){
			records = new HashMap<>();
			return;
		}

		ConfigurationSection recordsSection = yaml.getConfigurationSection("Records");

		Set<String> holdersOfRecords = recordsSection.getKeys(false);
		records = new HashMap<>(holdersOfRecords.size());

		for(String holder : holdersOfRecords){
			//UUIDに変換する
			UUID uuid = UUID.fromString(holder);

			//タイムに変換する
			long time = recordsSection.getLong(holder);

			records.put(uuid, time);
		}

		sortAsync();
	}
	public int getRank(UUID uuid) {
		if(rank.containsKey(uuid)) return rank.get(uuid);
				return -1;
	}

	//必要であれば記録する
	public boolean mightRecord(UUID uuid, long time){
		if(records.getOrDefault(uuid, Long.MAX_VALUE) <= time) return false;
		records.put(uuid, time);
		sortAsync();
		return true;
	}

	public boolean containsRecord(UUID uuid){
		return records.containsKey(uuid);
	}

	public long personalBest(UUID uuid){
		return records.getOrDefault(uuid, 0L);
	}

	public void withdrawRecord(UUID uuid){
		//DatabaseManager.get().executeStatement(SQLQuery.DELETE_PLAYER_COURSE_TIME);
		records.remove(uuid);
		holdersOfWithdrawnRecords.add(uuid);
		sortAsync();
	}
	public void removeAllRecord() {
		//すべてのレコードを削除する

		for(Entry<UUID,Long> recordEntory : records.entrySet()) {
			holdersOfWithdrawnRecords.add(recordEntory.getKey());
		}
		records.clear();
		sortAsync();

	}

	public void sortAsync(){
		/*Async.define(() -> {*/

			List<Entry<UUID, Long>> list = new ArrayList<>(records.entrySet());

			//記録を昇順にソートする
			list.sort(Entry.comparingByValue());

			topTenRecords.clear();
			//rank.clear();

			//最大で上位10件の記録をリストに追加する
			for(int index = 0; index < Math.min(10, records.size()); index++){
				//ソート済みリストから記録を取得する
				Entry<UUID, Long> record = list.get(index);

				UUID uuid = record.getKey();
				rank.put(uuid,index + 1);
				//記録をフォーマットして追加する
				topTenRecords.add(new Tuple<>(uuid, TimeFormat.format(records.get(uuid))));
			}
		/*}).execute();*/
	}

	public void save(Yaml yaml){
		//レコードを記録する
		for(Entry<UUID, Long> recordEntry : records.entrySet()) yaml.set("Records." + recordEntry.getKey() , recordEntry.getValue());

		//撤回された記録を削除する
		for(UUID holder : holdersOfWithdrawnRecords) yaml.set("Records." + holder, null);
	}

}
