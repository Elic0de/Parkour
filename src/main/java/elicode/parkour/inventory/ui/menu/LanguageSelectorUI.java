package elicode.parkour.inventory.ui.menu;

import elicode.parkour.function.parkour.ControlFunctionalItem;
import elicode.parkour.inventory.ui.InventoryLine;
import elicode.parkour.inventory.ui.dsl.InventoryUI;
import elicode.parkour.inventory.ui.dsl.component.InventoryLayout;
import elicode.parkour.parkour.ParkourCategory;
import elicode.parkour.user.User;
import elicode.parkour.util.item.SkullCreator;
import elicode.parkour.util.text.BilingualText;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;
import java.util.stream.IntStream;

public class LanguageSelectorUI implements InventoryUI {

    private final User user;


    public LanguageSelectorUI(User user){
        this.user = user;
    }

    @Override
    public Function<Player, InventoryLayout> layout() {

        Player player = user.asBukkitPlayer();

        return build(InventoryLine.x3, l -> {
            //カテゴリー名を取得する

            l.title = "Select Language";

            IntStream.range(0, 9)
                    .sorted()
                    .forEach(index -> l.put(s -> s.icon(Material.STAINED_GLASS_PANE, i -> {
                        i.displayName = " ";

                    }), index));

            IntStream.range(0, 9)
                    .sorted()
                    .forEach(index -> l.put(s -> s.icon(Material.STAINED_GLASS_PANE , i -> {
                        i.displayName = " ";

                    }), index + 18));

            l.put((s) -> {

                s.onClick(e -> {
                    // user.inventoryUserInterfaces.openParkourSelectionUI(category);
                });

                ItemStack skull = SkullCreator.fromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOThkYWExZTNlZDk0ZmYzZTMzZTFkNGM2ZTQzZjAyNGM0N2Q3OGE1N2JhNGQzOGU3NWU3YzkyNjQxMDYifX19");

                s.icon(skull, i -> i.displayName = BilingualText.stream("§a言語", "§aLanguage")
                        .textBy(player)
                        .toString());

            },4);

            l.put((s) -> {

                s.onClick(e -> {
                    user.language = "ja_jp";
                    ControlFunctionalItem.updateAllSlots(player);
                    user.statusBoard.updateAll();
                });
                ItemStack skull = SkullCreator.fromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDY0MGFlNDY2MTYyYTQ3ZDNlZTMzYzQwNzZkZjFjYWI5NmYxMTg2MGYwN2VkYjFmMDgzMmM1MjVhOWUzMzMyMyJ9fX0=");

                s.icon(skull, i -> i.displayName = "Japanese");

            }, 12);
            l.put((s) -> {

                s.onClick(e -> {
                    user.language = "us_en";
                    ControlFunctionalItem.updateAllSlots(player);
                    user.statusBoard.updateAll();
                });
                ItemStack skull = SkullCreator.fromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGNhYzk3NzRkYTEyMTcyNDg1MzJjZTE0N2Y3ODMxZjY3YTEyZmRjY2ExY2YwY2I0YjM4NDhkZTZiYzk0YjQifX19");

                s.icon(skull, i -> i.displayName = "English");

            }, 14);

            l.put((s) -> {


                s.onClick(e -> user.inventoryUserInterfaces.openParkourCategoryUI());

                s.icon(Material.BARRIER, i -> i.displayName =  "§b" + ParkourCategory.SEGMENT.name);

            }, 45);
        });
    }
}
