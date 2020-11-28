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

public class GameSelectorUI implements InventoryUI {

    private final User user;


    public GameSelectorUI(User user){
        this.user = user;
    }

    @Override
    public Function<Player, InventoryLayout> layout() {

        Player player = user.asBukkitPlayer();

        return build(InventoryLine.x3, l -> {
            //カテゴリー名を取得する

            l.title = "Game Selector";

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

            l.put((s) -> {

                s.onClick(e -> {
                   // user.inventoryUserInterfaces.openParkourSelectionUI(category);
                });

                s.icon(Material.BOW, i -> i.displayName =  "§bGame");

            },4);

            l.put((s) -> {

                s.onClick(e -> {user.inventoryUserInterfaces.openParkourSelectionUI(ParkourCategory.UPDATE);
                });


                s.icon(Material.MUSHROOM_SOUP, i -> i.displayName = "Solo");

            }, 11);
            l.put((s) -> {

                s.onClick(e -> user.inventoryUserInterfaces.openParkourSelectionUI(ParkourCategory.EXTEND));

                s.icon(Material.MUSHROOM_SOUP, i -> i.displayName = "Doubles");

            }, 13);
            l.put((s) -> {

                s.onClick(e -> user.inventoryUserInterfaces.openParkourSelectionUI(ParkourCategory.EXTEND));

                s.icon(Material.MUSHROOM_SOUP, i -> i.displayName = "Ranked");

            }, 15);

            l.put((s) -> {


                s.onClick(e -> user.inventoryUserInterfaces.openParkourCategoryUI());

                s.icon(Material.BARRIER, i -> i.displayName =  "§b" + ParkourCategory.SEGMENT.name);

            }, 45);
        });
    }
}
