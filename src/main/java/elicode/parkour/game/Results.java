package elicode.parkour.game;

import elicode.parkour.schedule.Async;
import elicode.parkour.util.format.TimeFormat;
import elicode.parkour.util.tuplet.Tuple;
import elicode.parkour.util.yaml.Yaml;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Results {

    //どうしようかなぁあ

    //全記録
    private final Map<UUID, Long> records;

    //上位10件の記録(非同期でリストを操作する為スレッドセーフなリストにしている)
    public final List<Tuple<UUID, String>> topTenRecords = new CopyOnWriteArrayList<>();

    //撤回された記録の保有者リスト
    private final List<UUID> holdersOfWithdrawnRecords = new ArrayList<>();

    public Results(){
            this.records = new HashMap<>();
    }

    //必要であれば記録する
    public boolean mightRecord(UUID uuid, long time){
        if(records.getOrDefault(uuid, Long.MAX_VALUE) <= time) return false;

        records.put(uuid, time);
        return true;
    }

    public boolean containsRecord(UUID uuid){
        return records.containsKey(uuid);
    }

    public long personalBest(UUID uuid){
        return records.getOrDefault(uuid, 0L);
    }

    public void withdrawRecord(UUID uuid){
        records.remove(uuid);
        holdersOfWithdrawnRecords.add(uuid);
        sortAsync();
    }

    public void sortAsync(){
        Async.define(() -> {
            List<Map.Entry<UUID, Long>> list = new ArrayList<>(records.entrySet());

            //記録を昇順にソートする
            list.sort(Map.Entry.comparingByValue());

            topTenRecords.clear();

            //最大で上位10件の記録をリストに追加する
            for(int index = 0; index < Math.min(10, records.size()); index++){
                //ソート済みリストから記録を取得する
                Map.Entry<UUID, Long> record = list.get(index);

                UUID uuid = record.getKey();

                //記録をフォーマットして追加する
                topTenRecords.add(new Tuple<>(uuid, TimeFormat.format(records.get(uuid))));
            }
        }).execute();
    }

}
