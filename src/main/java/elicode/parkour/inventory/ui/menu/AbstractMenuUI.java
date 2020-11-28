package elicode.parkour.inventory.ui.menu;

import elicode.parkour.inventory.ui.InventoryLine;
import elicode.parkour.inventory.ui.dsl.InventoryUI;
import elicode.parkour.inventory.ui.dsl.component.InventoryLayout;
import elicode.parkour.inventory.ui.AbstractUI;
import elicode.parkour.user.User;
import elicode.parkour.util.item.SkullCreator;
import elicode.parkour.util.sound.SoundMetadata;
import elicode.parkour.util.text.BilingualText;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract  class AbstractMenuUI implements InventoryUI  {

    private static final MenuCategory[] CATEGORIES = MenuCategory.values();

    private final User user;
    private final MenuCategory category;
    private final Consumer<InventoryLayout> raw ;

    private static final SoundMetadata ERROR_SE = new SoundMetadata(Sound.BLOCK_ANVIL_PLACE, 1f, 1.75f);

    public AbstractMenuUI(User user , MenuCategory category, Consumer<InventoryLayout>raw){
        this.user = user;
        this.category = category;
        this.raw = raw;

    }

    @Override
    public Function<Player, InventoryLayout> layout() {

        String categoryName = category.name;
        Player player = user.asBukkitPlayer();

        return build(InventoryLine.x6, l -> {
            //カテゴリー名を取得する

            l.title = categoryName;


            IntStream.range(0, 9)
                    .map(i -> i + 9)
                    .sorted()
                    .forEach(index -> {

                        l.put(s -> {

                            s.icon(category.line, i -> {
                                i.displayName = " ";

                            });

                        }, index);
                    });

            IntStream.range(0, 5)
                    .map(i -> i * 2)
                    .sorted()
                    .forEach(index -> {

                        l.put(s -> {

                            s.icon(category.line, i -> {
                                i.displayName = " ";

                            });

                        }, index);
                    });

            IntStream.range(0, 5)
                    .map(i -> i * 2)
                    .sorted()
                    .forEach(index -> {

                        l.put(s -> {

                            s.icon(category.line, i -> {
                                i.displayName = " ";

                            });

                        }, index + 8);
                    });

            /*IntStream.range(0, 9)
                    .map(i -> i + 45)
                    .sorted()
                    .forEach(index -> {

                        l.put(s -> {

                            s.icon(category.line, i -> {
                                i.displayName = " ";

                            });

                        }, index);
                    });*/

            l.put((s) -> {

                s.icon(category.line, i -> {
                    i.displayName = " ";

                });


            }, 1);

            l.put((s) -> {

                s.icon(category.line, i -> {
                    i.displayName = " ";

                });


            }, 7);


            l.put((s) -> {
                //プレイヤーのスカルヘッドを作成する
                ItemStack skull = SkullCreator.fromPlayerUniqueId(user.uuid);

                s.onClick(e -> user.inventoryUserInterfaces.openMyProfileUI(MenuCategory.MYPROFILE));

                s.icon(skull, i -> {
                    i.displayName = player.getDisplayName();

                });

            }, 2);

            l.put((s) -> {
                //プレイヤーのスカルヘッドを作成する
                ItemStack skull = SkullCreator.fromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzExM2MyMGJlODg2YzM4N2ZlNTU1NGZlZmI0ZTZiYzVkZDZjYzVhMzg5OWRjYzI2Y2JlMzQ0NzQ4MWNlNiJ9fX0=");

                //s.onClick(e -> user.inventoryUserInterfaces.openMyProfileUI(MenuCategory.FRIENDS));
                s.onClick(e -> ERROR_SE.play(player));

                s.icon(skull, i -> {
                    i.displayName = BilingualText.stream("§aフレンド", "§aFriend")
                            .textBy(player)
                            .toString();

                });


            }, 3);

            l.put((s) -> {
                //プレイヤーのスカルヘッドを作成する
                ItemStack skull = SkullCreator.fromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzU2Yzk2MzVkNzM0ODg1ZWM1YzNmMjAxMDg2ODYzMWFhY2FmNDVlNGI0N2Q3MTI4YTczMmEwODFmNThmZjMifX19");

                s.onClick(e -> user.inventoryUserInterfaces.openMyProfileUI(MenuCategory.PARTY));

                s.icon(skull, i -> {
                    i.displayName = BilingualText.stream("§aパーティー", "§aParty")
                            .textBy(player)
                            .toString();

                });

            }, 4);

            l.put((s) -> {
                //プレイヤーのスカルヘッドを作成する
                ItemStack skull = SkullCreator.fromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWZlNmE0ZjdmYzMxOTU0YWM3ZDE3ZjcwMmYyMjgzNWUzMjVmOGJiNTMyNmNmZjYyNzNjN2I5Y2MxOTIxY2ExIn19fQ==");

                s.onClick(e -> ERROR_SE.play(player));
                //s.onClick(e -> user.inventoryUserInterfaces.openMyProfileUI(MenuCategory.CHANNEL));

                s.icon(skull, i -> {
                    i.displayName = BilingualText.stream("§aチャンネル", "§aChannel")
                            .textBy(player)
                            .toString();;

                });

            }, 5);
            l.put((s) -> {
                //プレイヤーのスカルヘッドを作成する
                ItemStack skull = SkullCreator.fromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzVmNDVjODc1ZWZmNTM4ZmNlYzk4ZjZhY2MxZGYyYWVjYWUyOGY0ODYwYWVjZDI0ZTJkYmRmMTM5MjRiMzI3In19fQ==");

                //s.onClick(e -> user.inventoryUserInterfaces.openMyProfileUI(MenuCategory.RESENT));
                s.onClick(e -> ERROR_SE.play(player));

                s.icon(skull, i -> {
                    i.displayName = BilingualText.stream("§aレポート", "§aReport")
                            .textBy(player)
                            .toString();

                });

            }, 6);
            raw.accept(l);
    });


    }
}
