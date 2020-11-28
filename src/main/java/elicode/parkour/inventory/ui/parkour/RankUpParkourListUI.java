package elicode.parkour.inventory.ui.parkour;

import java.util.Comparator;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import elicode.parkour.lobby.Lobby;
import elicode.parkour.lobby.LobbySet;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import elicode.parkour.inventory.ui.InventoryLine;
import elicode.parkour.parkour.ParkourCategory;
import elicode.parkour.parkour.ParkourSet;
import elicode.parkour.parkour.RankUpParkour;
import elicode.parkour.util.text.BilingualText;
import elicode.parkour.user.User;

public class RankUpParkourListUI extends AbstractParkourListUI<RankUpParkour> {

    public RankUpParkourListUI(User user, ParkourCategory category,  Supplier<Integer> rank,boolean duel) {

        super(
                user,
                category,

                //カテゴリーに対応したアスレリストを返す関数を作成する
                () -> ParkourSet.getInstance().getEnabledParkours(category)
                        .map(parkour -> (RankUpParkour) parkour)
                        .sorted(Comparator.comparingInt(parkour -> parkour.rank))
                        .collect(Collectors.toList()),

                //必要な段数を計算する関数を作成する
                parkours -> InventoryLine.necessaryInventoryLine(54),

                //ロビーへの移動ボタンをセットする
                layout -> layout.put(s -> {
                    Player player = layout.player;

                    s.onClick(e -> {
                        //本番環境では変える
                        LobbySet lobbies = LobbySet.getInstance();
                        //Chatcolorは緊急処置
                        Lobby lobby = lobbies.getLobby(ChatColor.WHITE + category.name);
                        //リンクされたロビーのスポーン地点にテレポートさせる
                        lobby.teleport(player);


                        user.exitCurrentParkour();

                        BilingualText.stream("&b-$categoryロビーにテレポートしました", "&b-You teleported to $category lobby")
                                .setAttribute("$category", category.name)
                                .color()
                                .setReceiver(player)
                                .sendActionBarMessage();
                    });

                    String displayName = BilingualText.stream("&b-$categoryロビーにテレポートする", "&b-Teleport to $category Lobby")
                            .textBy(player)
                            .setAttribute("$category", category.name)
                            .color()
                            .toString();

                    s.icon(Material.NETHER_STAR, i -> i.displayName = displayName);


                }, 44),
                duel
        );
    }

}
