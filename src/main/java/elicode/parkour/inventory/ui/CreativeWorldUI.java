package elicode.parkour.inventory.ui;

import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import elicode.parkour.inventory.ui.InventoryLine;
import elicode.parkour.inventory.ui.dsl.InventoryUI;
import elicode.parkour.inventory.ui.dsl.component.InventoryLayout;
import elicode.parkour.lobby.Lobby;
import elicode.parkour.lobby.LobbySet;
import elicode.parkour.util.format.CoinFormat;
import elicode.parkour.util.item.SkullCreator;
import elicode.parkour.util.text.BilingualText;
import elicode.parkour.util.text.Text;
import elicode.parkour.util.tuplet.Quadruple;
import elicode.parkour.util.tuplet.Triple;
import elicode.parkour.user.User;
import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CreativeWorldUI implements InventoryUI {

    //ボタンの構造体を表す
    private static class Button extends Quadruple<Integer, Material, LocaleFunction, Consumer<User>> {

        public Button(Integer slotIndex, Material material, String japanise, String english, Consumer<User> processing) {
            super(slotIndex, material, new LocaleFunction(japanise, english), processing);
        }

    }

    //説明文を表す
    private static class LoreBuilder extends Triple<LocaleFunction, String, Function<User, ?>> {

        public LoreBuilder(String japanise, String english, Function<User, ?> status) {
            this(japanise, english, "", status);
        }

        public LoreBuilder(String japanise, String english, String unit, Function<User, ?> status) {
            super(new LocaleFunction(japanise, english), unit, status);
        }

        public String buildBy(User user){
            //"&7-: &b-Updateランク &7-@ &b-$0
            return Text.stream("&7-$name: &e-$value$unit")
                    .setAttribute("$name", first.apply(user.asBukkitPlayer()))
                    .setAttribute("$value", third.apply(user))
                    .setAttribute("$unit", second)
                    .color()
                    .toString();
        }

    }

    private static final List<Button> BUTTONS;
    private static final List<LoreBuilder> LORE_BUILDERS;

    static{
        BUTTONS = ImmutableList.of(
                new Button(0, Material.ARROW, "クリエイティブワールド", "Creative World", user -> {

                    //アスレから退出させる
                    user.exitCurrentParkour();

                    Player player = user.asBukkitPlayer();

                    //本番環境では変える
                    LobbySet lobbies = LobbySet.getInstance();
                    //Chatcolorは応急処置
                    Lobby lobby = lobbies.getLobby(ChatColor.WHITE + "Creative");
                    //リンクされたロビーのスポーン地点にテレポートさせる
                    lobby.teleport(player);

                    BilingualText.stream("&b-CretiveWorldにテレポートしました", "&b-You teleported to CreativeWorld")
                            .color()
                            .setReceiver(player)
                            .sendActionBarMessage();

                }),
                new Button(1, Material.ARROW, "ホームにテレポートする", "Teleport to Home", user ->{

                    Player player = user.asBukkitPlayer();


                    //本番環境では変える
                    PlotPlayer plotPlayer = PlotPlayer.get(player.getName());

                    if (plotPlayer.getPlots().size()> 0 ) {

                        Plot plot = plotPlayer.getPlots().iterator().next();

                      //  plot.

                        BilingualText.stream("&e-所有しているplotにテレポートしました", "&e-You teleported to plot")
                                .color()
                                .setReceiver(player)
                                .sendChatMessage();

                    }else {
                        BilingualText.stream("&c-あなたはplotを所有していません", "&c-You haven't your plot")
                                .color()
                                .setReceiver(player)
                                .sendChatMessage();
                    }
                }),
                new Button(2, Material.COMPASS, "未実装", "not yet installed", user ->{}),
                new Button(3, Material.PAPER, "未実装", "not yet installed", user -> {}),
                new Button(4, Material.IRON_DOOR, "未実装", "not yet installed", user -> {}),
                new Button(5, Material.ARROW, "未実装", "not yet installed", user -> {})
        );

        LORE_BUILDERS = ImmutableList.of(
                new LoreBuilder("Updateランク", "Update Rank", user -> user.updateRank()),
                new LoreBuilder("Extendランク", "Extend Rank", user -> user.extendRank()),
                new LoreBuilder("ジャンプ数", "Jumps", user -> user.asBukkitPlayer().getStatistic(Statistic.JUMP)),
                new LoreBuilder("所持コイン数", "Coins", user -> CoinFormat.format(user.coins())),
                new LoreBuilder("総プレイ時間", "Time Played", user -> user.asBukkitPlayer().getStatistic(Statistic.PLAY_ONE_TICK))
        );
    }

    private final User user;

    public CreativeWorldUI(User user){
        this.user = user;
    }

    @Override
    public Function<Player, InventoryLayout> layout() {
        Player player = user.asBukkitPlayer();

        return build(InventoryLine.x1, l -> {
            l.title = BilingualText.stream("クリエイティブワールド メニュー", "CreativeWorld Menu")
                    .textBy(player)
                    .toString();


            //自分のステータス表示
            l.put((s) -> {
                //プレイヤーのスカルヘッドを作成する
                ItemStack skull = SkullCreator.fromPlayerUniqueId(user.uuid);

                s.icon(skull, i -> {
                    i.displayName = "§b" + player.getName();

                    List<String> lore = LORE_BUILDERS.stream().map(builder -> builder.buildBy(user)).collect(Collectors.toList());
                    lore.add(0, "");
                    lore.add(3, "");

                    i.lore = lore;
                });

            }, 8);

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
