package elicode.parkour.inventory.ui.parkour;

import elicode.parkour.inventory.ui.InventoryLine;
import elicode.parkour.inventory.ui.dsl.InventoryUI;
import elicode.parkour.inventory.ui.dsl.component.InventoryLayout;
import elicode.parkour.inventory.ui.menu.MenuCategory;
import elicode.parkour.parkour.Parkour;
import elicode.parkour.parkour.ParkourCategory;
import elicode.parkour.user.User;
import elicode.parkour.user.UserSet;
import elicode.parkour.util.item.SkullCreator;
import elicode.parkour.util.text.BilingualText;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class ParkourCategoryUI implements InventoryUI {

    private static final ParkourCategory[] CATEGORIES = ParkourCategory.values();

    private final UserSet users = UserSet.getInstnace();

    private final User user;


    public ParkourCategoryUI(User user){
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
                    .forEach(index -> l.put(s -> s.icon(Material.STAINED_GLASS_PANE, i -> {
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
                    .forEach(index -> l.put(s -> s.icon(Material.STAINED_GLASS_PANE, i -> {
                        i.displayName = " ";

                    }), index * 9 + 8));


            l.put((s) -> {

                s.onClick(e -> user.inventoryUserInterfaces.openParkourSelectionUI(ParkourCategory.NORMAL));

                s.icon(Material.STAINED_GLASS_PANE, i -> i.displayName =  "§b" + ParkourCategory.NORMAL.name);

            }, 13);
            l.put((s) -> {

                s.onClick(e -> user.inventoryUserInterfaces.openParkourSelectionUI(ParkourCategory.SEGMENT));

                s.icon(Material.FLOWER_POT, i -> i.displayName =  "§b" + ParkourCategory.SEGMENT.name);

            }, 29);
            l.put((s) -> {


                s.onClick(e -> user.inventoryUserInterfaces.openRankUpUI());

                s.icon(Material.ENDER_PEARL, i -> i.displayName =  "§b" + "Rank UP");

            }, 31);
            l.put((s) -> {


                s.onClick(e -> user.inventoryUserInterfaces.openParkourSelectionUI(ParkourCategory.BIOME));

                s.icon(Material.STAINED_GLASS_PANE, i -> i.displayName =  "§b" + ParkourCategory.BIOME.name);

            }, 33);
            /*l.put((s) -> {


                s.onClick(e -> user.inventoryUserInterfaces.openMyProfileUI(MenuCategory.MYPROFILE));

                s.icon(Material.WRITABLE_BOOK, i -> i.displayName = "§b" + ParkourCategory.SEGMENT.name);

            }, 45);
            l.put((s) -> {


                s.onClick(e -> user.inventoryUserInterfaces.openMyProfileUI(MenuCategory.MYPROFILE));

                s.icon(Material.FIREWORK_ROCKET, i -> i.displayName =  "§b" + ParkourCategory.SEGMENT.name);

            }, 48);

            l.put((s) -> {


                s.onClick(e -> user.inventoryUserInterfaces.openGameSelectorUI());

                s.icon(Material.BOW, i -> i.displayName =  "§b" + ParkourCategory.SEGMENT.name);

            }, 50);*/
        });


    }

}
