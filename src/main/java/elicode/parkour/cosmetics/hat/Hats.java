package elicode.parkour.cosmetics.hat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;

public class Hats {

	public final static List<Hat> HATS = new ArrayList<>(60);

	static {
		initialize(
				"0,500,ホワイトステンドガラス,WHITE_STAINED_GLASS",
				"1,500,レッドステンドガラス,RED_STAINED_GLASS",
				"2,500,ブルーステンドガラス,BLUE_STAINED_GLASS",
				"3,500,イエローステンドガラス,YELLOW_STAINED_GLASS",
				"4,500,ライムステンドガラス,LIME_STAINED_GLASS",
				"5,500,グレーンステンドガラス,GREEN_STAINED_GLASS",
				"6,500,ライトブルーステンドガラス,LIGHT_BLUE_STAINED_GLASS",
				"7,500,イエローステンドガラス,YELLOW_STAINED_GLASS",
				"8,500,シアンステンドガラス,CYAN_STAINED_GLASS",
				"9,500,オレンジステンドガラス,ORANGE_STAINED_GLASS",
				"10,500,ピンクステンドガラス,PINK_STAINED_GLASS",
				"11,500,シアンステンドガラス,CYAN_STAINED_GLASS",
				"12,500,マゼンタステンドガラス,MAGENTA_STAINED_GLASS",
				"13,500,パープルステンドガラス,PURPLE_STAINED_GLASS",
				"14,500,ライトグレイステンドガラス,LIGHT_GRAY_STAINED_GLASS",
				"15,500,グレイステンドガラス,GRAY_STAINED_GLASS",
				"16,500,ブラックステンドガラス,BLACK_STAINED_GLASS",
				"17,500,ブラウンステンドガラス,BROWN_STAINED_GLASS",
				"18,1000,ビーコン,BEACON",
				"19,1000,オークリーブズ,OAK_LEAVES",
				"20,1000,チェスト,CHEST",
				"21,1000,エンダーチェスト,ENDER_CHEST",
				"22,5000,エンドロッド,END_ROD",
				"23,5000,ボーン,BONE",
				"24,5000,アンビル,ANVIL",
				"25,1000,スライムブロック,SLIME_BLOCK",
				"26,5000,ＴＮＴ,TNT",
				"27,1000,ジャック・オ・ランタン,JACK_O_LANTERN",
				"28,1000,サボテン,CACTUS",
				"39,5000,レッドベッド,RED_BED",
				"40,5000,ブルーベッド,BLUE_BED",
				"41,5000,イエローベッド,YELLOW_BED",
				"42,5000,ライムベッド,LIME_BED",
				"43,5000,グリーンベッド,GREEN_BED",
				"44,5000,ライトブルーベッド,LIGHT_BLUE_BED",
				"45,5000,シアンベッド,CYAN_BED",
				"46,5000,オレンジベッド,ORANGE_BED",
				"47,5000,ピンクベッド,PINK_BED",
				"48,5000,マゼンタベッド,MAGENTA_BED",
				"49,5000,ライトグレイベッド,LIGHT_GRAY_BED",
				"50,5000,グレイベッド,GRAY_BED",
				"51,5000,ホワイトベッド,WHITE_BED",
				"52,2500,ディスペンサー,DISPENSER",
				"53,2500,オブザーバー,OBSERVER",
				"54,2500,ドロッパー,DROPPER",
				"55,2500,オークフェンスゲート,OAK_FENCE_GATE",
				"56,2500,スプルスフェンスゲート,SPRUCE_FENCE_GATE",
				"57,2500,バージフェンスゲート,BIRCH_FENCE_GATE",
				"58,2500,ジャングルフェンスゲート,JUNGLE_FENCE_GATE",
				"59,2500,アカシアフェンスゲート,ACACIA_FENCE_GATE",
				"60,2500,ダークオークフェンスゲート,DARK_OAK_FENCE_GATE"
		);
	}

	private static void initialize(String... texts){
		Arrays.stream(texts)
		.map(text -> text.split(","))
		.map(data -> new Hat(Integer.parseInt(data[0]), Integer.parseInt(data[1]), data[2], Material.valueOf(data[3])))
		.forEach(HATS::add);
	}

}
