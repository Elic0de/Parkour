package elicode.parkour.inventory.ui.parkour;

import elicode.parkour.inventory.ui.InventoryLine;
import elicode.parkour.inventory.ui.dsl.InventoryUI;
import elicode.parkour.inventory.ui.dsl.component.InventoryLayout;
import elicode.parkour.parkour.ParkourCategory;
import elicode.parkour.user.User;
import elicode.parkour.user.UserSet;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.function.Function;
import java.util.stream.IntStream;

public class DuelCategoryUI implements InventoryUI {

    private static final ParkourCategory[] CATEGORIES = ParkourCategory.values();

    private final User user;


    public DuelCategoryUI(User user){
        this.user = user;
    }

    @Override
    public Function<Player, InventoryLayout> layout() {

        Player player = user.asBukkitPlayer();

        return build(InventoryLine.x6, l -> {
            //カテゴリー名を取得する

            l.title = "Parkour Category Selector";


            IntStream.range(0, 9)
                    .sorted()
                    .forEach(index -> l.put(s -> s.icon(Material.STAINED_GLASS_PANE , i -> {
                        i.displayName = " ";

                    }), index));

            IntStream.range(0, 9)
                    .sorted()
                    .forEach(index -> l.put(s -> s.icon(Material.STAINED_GLASS_PANE, i -> {
                        i.displayName = " ";

                    }), index + 45));

            IntStream.range(0, 6)
                    .sorted()
                    .forEach(index -> l.put(s -> s.icon(Material.STAINED_GLASS_PANE, i -> {
                        i.displayName = " ";

                    }), index * 9));

            IntStream.range(0, 6)
                    .sorted()
                    .forEach(index -> l.put(s -> s.icon(Material.STAINED_GLASS_PANE , i -> {
                        i.displayName = " ";

                    }), index * 9 + 8));


            l.put((s) -> {

                s.onClick(e -> user.inventoryUserInterfaces.openDuelSelectionUI(ParkourCategory.NORMAL));

                s.icon(Material.STAINED_GLASS_PANE  , i -> i.displayName = "§b" + ParkourCategory.NORMAL.name);

            }, 13);
            l.put((s) -> {

                s.onClick(e -> user.inventoryUserInterfaces.openDuelSelectionUI(ParkourCategory.SEGMENT));

                s.icon(Material.FLOWER_POT, i -> i.displayName = "§b" + ParkourCategory.SEGMENT.name);

            }, 29);
            l.put((s) -> {


                s.onClick(e -> user.inventoryUserInterfaces.openDuelRankUpUI());

                s.icon(Material.ENDER_PEARL, i -> i.displayName = "§b" + "Rank UP");

            }, 31);
            l.put((s) -> {


                s.onClick(e -> user.inventoryUserInterfaces.openDuelSelectionUI(ParkourCategory.BIOME));

                s.icon(Material.GREEN_SHULKER_BOX, i -> i.displayName = "§b" + ParkourCategory.BIOME.name);

            }, 33);
        });
    }
}
