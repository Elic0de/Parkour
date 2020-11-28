package elicode.parkour.parkour;

import elicode.parkour.util.yaml.Yaml;

public class RankUpParkour extends Parkour {

	//指定されたアスレ名がランクアップアスレであるかどうか
	public static boolean isRankedParkour(Parkour parkour){
		return parkour instanceof RankUpParkour;
	}

	public static boolean isRankedParkour(String parkourName){
		return parkourName.startsWith("Update") || parkourName.startsWith("Extend");
	}

	public final int rank;

	public RankUpParkour(ParkourSet parkours, Yaml yaml) {
		super(parkours, yaml);

		//装飾コードを除いたアスレ名を取得する
		String parkourName = colorlessName();
		System.out.println(parkourName + "大丈夫かぁ？");
		//ランクアップアスレでなければ戻る
		if(!isRankedParkour(parkourName)) throw new IllegalArgumentException("Parkour must be ranked");

		//接頭辞を取得する
		String prefix = parkourName.substring(0, 6);

		//カテゴリーを設定する
		category = ParkourCategory.valueOf(prefix.toUpperCase());

		//アスレ名からランクを取得する
		rank = Integer.parseInt(parkourName.replace(prefix, ""));
	}

}
