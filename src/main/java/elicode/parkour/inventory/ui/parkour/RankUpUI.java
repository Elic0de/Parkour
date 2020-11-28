package elicode.parkour.inventory.ui.parkour;

import elicode.parkour.inventory.ui.InventoryLine;
import elicode.parkour.inventory.ui.dsl.InventoryUI;
import elicode.parkour.inventory.ui.dsl.component.InventoryLayout;
import elicode.parkour.parkour.ParkourCategory;
import elicode.parkour.user.User;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.function.Function;
import java.util.stream.IntStream;

public class RankUpUI implements InventoryUI {

    private final User user;
    private final boolean duel;


    public RankUpUI(User user, boolean duel){
        this.user = user;
        this.duel = duel;
    }

    @Override
    public Function<Player, InventoryLayout> layout() {

        Player player = user.asBukkitPlayer();

        return build(InventoryLine.x3, l -> {
            //カテゴリー名を取得する

            l.title = "RankUp Selector";



            /*l.put(s -> {
                s.icon(i -> {
                    //ベースアイテムを指定する
                    //スカルヘッドの生成はSkullを使うと楽出来る
                    i.material = Material.WHITE_STAINED_GLASS_PANE;
                });
                //続き番号を指定する場合はIntStreamを使うと良い
            }, IntStream.range(1, 4));*/


            IntStream.range(0, 9)
                    .sorted()
                    .forEach(index -> l.put(s -> s.icon(Material.STAINED_GLASS_PANE, i -> {
                        i.displayName = " ";

                    }), index));

            IntStream.range(0, 9)
                    .sorted()
                    .forEach(index -> l.put(s -> s.icon(Material.STAINED_GLASS_PANE, i -> {
                        i.displayName = " ";

                    }), index + 18));


            if(duel){
                l.put((s) -> {

                    s.onClick(e -> {user.inventoryUserInterfaces.openDuelSelectionUI(ParkourCategory.UPDATE);
                    });


                    s.icon(Material.DIAMOND, i -> i.displayName = ParkourCategory.UPDATE.name);

                }, 12);
                l.put((s) -> {

                    s.onClick(e -> user.inventoryUserInterfaces.openDuelSelectionUI(ParkourCategory.EXTEND));

                    s.icon(Material.GOLD_INGOT, i -> i.displayName = ParkourCategory.EXTEND.name);

                }, 14);
                return;
            }

            l.put((s) -> {

                s.onClick(e -> {user.inventoryUserInterfaces.openParkourSelectionUI(ParkourCategory.UPDATE);
                });


                s.icon(Material.DIAMOND, i -> i.displayName = ParkourCategory.UPDATE.name);

            }, 12);
            l.put((s) -> {

                s.onClick(e -> user.inventoryUserInterfaces.openParkourSelectionUI(ParkourCategory.EXTEND));

                s.icon(Material.GOLD_INGOT, i -> i.displayName = ParkourCategory.EXTEND.name);

            }, 14);
        });


    }

}
