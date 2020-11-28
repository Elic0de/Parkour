package elicode.parkour.cosmetics.item;

import elicode.parkour.cosmetics.hat.Hat;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Items {

    public final static List<Item> ITEMS = new ArrayList<>(60);

    static {
        initialize(
                "0,500,空色の染料,LIGHT_BLUE_SHULKER_BOX",
                "1,500,黄色の染料,DANDELION_YELLOW",
                "2,500,赤色の染料,ROSE_RED",
                "3,500,緑色の染料,CACTUS_GREEN",
                "4,500,カカオ豆,COCOA_BEANS",
                "5,500,ラピスラズリ,LAPIS_LAZULI",
                "6,500,紫色の染料,PURPLE_DYE",
                "7,500,薄灰色の染料,LIGHT_GRAY_DYE",
                "8,500,桃色の染料,PINK_DYE",
                "9,500,赤紫色の染料,MAGENTA_DYE",
                "10,500,橙色の染料,ORANGE_DYE",
                "11,500,火薬,GUNPOWDER",
                "12,500,イカスミ,INK_SAC",
                "13,1000,ファイヤーチャージ,FIRE_CHARGE",
                "14,1000,石炭,COAL",
                "15,1000,レッドストーン,REDSTONE",
                "16,1000,ネザーレンガ,NETHER_BRICK",
                "17,1000,レンガ,BRICK",
                "18,1000,金インゴット,GOLD_INGOT",
                "19,1000,鉄インゴット,IRON_INGOT",
                "20,1000,ダイヤモンド,DIAMOND",
                "21,1000,エメラルド,EMERALD",
                "22,1000,きらめくスイカ,GLISTERING_MELON_SLICE",
                "23,1000,花火の星,FIREWORK_STAR",
                "24,1000,金のニンジン,GOLDEN_CARROT",
                "25,1000,ヒマワリ,SUNFLOWER",
                "26,10000,羽,FEATHER",
                "27,10000,カメのウロコ,SCUTE",
                "28,10000,シープクルス,SEA_PICKLE",
                "39,10000,乾燥した昆布,DRIED_KELP",
                "40,10000,マグマクリーム,MAGMA_CREAM",
                "41,10000,うさぎの足,RABBIT_FOOT",
                "42,10000,鉄の馬鎧,IRON_HORSE_ARMOR",
                "43,10000,金の馬鎧,GOLDEN_HORSE_ARMOR",
                "44,10000,ダイヤモンドの馬鎧,DIAMOND_HORSE_ARMOR",
                "45,10000,ガストの涙,GHAST_TEAR",
                "46,10000,昆布,KELP",
                "47,10000,ネザークォーツ,QUARTZ",
                "48,10000,オウムガイの殻,NAUTILUS_SHELL",
                "49,10000,ファントムの皮膜,PHANTOM_MEMBRANE",
                "50,10000,ブレイズパウダー,BLAZE_POWDER",
                "51,10000,クモの目,FERMENTED_SPIDER_EYE",
                "52,50000,ドラゴンの頭,DRAGON_HEAD",
                "53,50000,ドラゴンの卵,DRAGON_EGG",
                "54,50000,不死のトーテム,TOTEM_OF_UNDYING"
        );}

    private static void initialize(String... texts){
        Arrays.stream(texts)
                .map(text -> text.split(","))
                .map(data -> new Item(Integer.parseInt(data[0]), Integer.parseInt(data[1]), data[2], Material.CARROT/*Material.valueOf(data[3])*/))
                .forEach(ITEMS::add);
    }

}
