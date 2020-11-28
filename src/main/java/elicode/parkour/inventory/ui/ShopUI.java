package elicode.parkour.inventory.ui;

import com.plotsquared.core.location.Location;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import elicode.parkour.creative.CreativeWorld;
import elicode.parkour.creative.CreativeWorldSet;
import elicode.parkour.inventory.ui.InventoryLine;
import elicode.parkour.inventory.ui.dsl.InventoryUI;
import elicode.parkour.inventory.ui.dsl.component.InventoryLayout;
import elicode.parkour.user.PurchasedCreative;
import elicode.parkour.user.User;
import elicode.parkour.util.sound.SoundMetadata;
import elicode.parkour.util.text.BilingualText;
import elicode.parkour.util.tuplet.Quadruple;
import com.google.common.collect.ImmutableList;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ShopUI implements InventoryUI {

    private static final SoundMetadata BUY_SE = new SoundMetadata(Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.75f);
    private static final SoundMetadata ERROR_SE = new SoundMetadata(Sound.BLOCK_ANVIL_PLACE, 1f, 1.75f);
    private final PurchasedCreative purchased;

    //ボタンの構造体を表す
    private static class Button extends Quadruple<Integer, Material, LocaleFunction, Consumer<User>> {

        public Button(Integer slotIndex, Material material, String japanise, String english, Consumer<User> processing) {
            super(slotIndex, material, new LocaleFunction(japanise, english), processing);
        }

    }

    private static final List<Button> BUTTONS;

    static{
        BUTTONS = ImmutableList.of(
             //   new Button(3, Material.GRASS_BLOCK, "プロットを購入する", "Buy Plot", user -> user.inventoryUserInterfaces.openCreativeWorldUI()),
                new Button(5, Material.LEATHER_HELMET, "帽子を購入する", "Buy Hats", user -> user.inventoryUserInterfaces.openBuyHatUI()),
                new Button(2, Material.SKULL    , "アイテムえを購入する", "Buy Item", user -> user.inventoryUserInterfaces.openItemUI())
        );

    }

    private final User user;

    public ShopUI(User user){
        this.user = user;
        this.purchased = user.creative;
    }

    @Override
    public Function<Player, InventoryLayout> layout() {
        Player player = user.asBukkitPlayer();

        List<CreativeWorld> hats = CreativeWorldSet.HATS.stream()
                .filter(hat -> !purchased.has(hat))
                .limit(1)
                .collect(Collectors.toList());

        return build(InventoryLine.x1, l -> {
            CreativeWorld creativeWorld = hats.get(0);
            int value = creativeWorld.value;

            l.put(s -> {
                if(purchased.canBuy(creativeWorld)){
                   /* s.onClick(e -> {
                        /purchased.buy(creativeWorld);
                        //BUY_SE.play(player);


                        //本番環境では変える
                        PlotPlayer plotPlayer = PlotPlayer.get(player.getName());
                        Location location = plotPlayer.getLocation();
                        final Plot plot1 = location.getPlotAbs();

                        if(plot1.canClaim(plotPlayer)) {
                            plot1.claim(plotPlayer, true, null, true);

                            BilingualText.stream("&b-Plotを購入しました", "&b-You bought a plot")
                                    .color()
                                    .setReceiver(player)
                                    .sendChatMessage();

                        }else{
                            BilingualText.stream("&c-Plotの上に立った状態で購入してください", "&c-Please purchase while standing on Plot")
                                    .color()
                                    .setReceiver(player)
                                    .sendChatMessage();
                        }
                        player.closeInventory();
                    });*/

                    s.icon(Material.STAINED_GLASS_PANE,i -> {

                        i.displayName = BilingualText.stream("&b-Plot &7-@ &b-$coinsコイン", "&b-Plot &7-@ &b-$coins Coins")
                                .textBy(player)
                                .setAttribute("$coins", value)
                                .color()
                                .toString();

                        String lore = BilingualText.stream("&7-クリックで購入します。", "&7-Click to buy.")
                                .textBy(player)
                                .color()
                                .toString();

                        i.lore(lore);
                    });
                }else{
                    s.onClick(e -> ERROR_SE.play(player));

                    s.icon(Material.STAINED_GLASS_PANE,i -> {

                        i.displayName = BilingualText.stream("&c-Plot &7-@ &c-$coinsコイン", "&c-Plot &7-@ &c-$coins Coins")
                                .textBy(player)
                                .setAttribute("$coins", value)
                                .color()
                                .toString();

                        String lore = BilingualText.stream("&7-コインが足りないため購入出来ません。", "&7-You cannot buy it because you don't have enough coins.")
                                .textBy(player)
                                .color()
                                .toString();

                        i.lore(lore);
                    });
                }

            }, 3);

            for(Button button : BUTTONS){
                l.put(s -> {

                    s.onClick(e -> button.fourth.accept(user));

                    s.icon(button.second, i -> {
                        i.displayName = "§b" + button.third.apply(player);
                        i.gleam();
                    });

                }, button.first);
            }

        });
    }


}
