package elicode.parkour.command.parkour;

import elicode.beta.parkour.location.ImmutableLocation;
import elicode.parkour.command.Arguments;
import elicode.parkour.command.Command;
import elicode.parkour.command.Sender;
import elicode.parkour.lobby.Lobby;
import elicode.parkour.lobby.LobbySet;
import elicode.parkour.parkour.Parkour;

import elicode.parkour.parkour.ParkourSet;
import elicode.parkour.region.selection.RegionSelectionSet;
import elicode.parkour.util.text.Text;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LobbyCommand implements Command {

    private final LobbySet lobbies = LobbySet.getInstance();
    private final ParkourSet parkours = ParkourSet.getInstance();
    private final RegionSelectionSet selections = RegionSelectionSet.getInstance();

    @Override
    public void onCommand(Sender sender, Arguments args) {
        //送信者がプレイヤーでなければ戻る
        if (blockNonPlayer(sender)) return;

        if(hasPermission(sender,this.getClass().getSimpleName())) return;

        //第1引数が無ければ戻る
        if (!args.hasNext()) {
            displayCommandUsage(sender);
            return;
        }

        //送信者のUUIDを取得する
        Player player = sender.asPlayerCommandSender();
        UUID uuid = player.getUniqueId();

        //第1引数をアスレ名として取得する
        String lobbyName = ChatColor.translateAlternateColorCodes('&', args.next());


        //第1引数がlistであればアスレ名を全て表示する
        if (lobbyName.equals("list")) {
            for (Lobby lobby : lobbies.getLobbies()) {
                Text.stream("&7-: &r-$lobby-&r &7-@ $spawn")
                        .setAttribute("$lobby", lobby.name)
                        .setAttribute("$spawn", lobby.spawn.serialize())
                        .color()
                        .setReceiver(player)
                        .sendChatMessage();
            }
            return;
        }


        //第2引数で分岐する
        switch (args.next()) {
            case "create": {
                //対応したファイルが存在していれば戻る
                if (lobbies.containsLobby(lobbyName)) {
                    Text.stream("$lobby-&r-は既に存在しています。")
                            .setAttribute("$lobby", lobbyName)
                            .color()
                            .setReceiver(player)
                            .sendChatMessage();
                    return;
                }

                //アスレ名の先頭に装飾コードが存在しない場合
                if (!Lobby.PREFIX_PATTERN.matcher(lobbyName).find()) {
                    sender.warn("アスレ名の先頭には必ず装飾コードを置いて下さい。");
                    return;
                }

                //ファイルを作成する
                lobbies.makeYaml(lobbyName);

                //無効化された状態で登録する
                lobbies.registerLobby(lobbyName);

                Lobby lobby = lobbies.getLobby(lobbyName);
                //プレイヤーの座標を取得する
                Location location = sender.asPlayerCommandSender().getLocation();

                //イミュータブルな座標にしブロックの中央に調整した上でセットする
                lobby.spawn = new ImmutableLocation(location);

                Text.stream("$lobby-&r-のデータを新規作成しました。")
                        .setAttribute("$lobby", lobbyName)
                        .color()
                        .setReceiver(player)
                        .sendChatMessage();
                break;
            }
            case "delete": {
                //指定されたロビーが存在しなければ戻る
                if (blockNotExistLobby(player, lobbyName)) return;

                //ロビーが登録されていれば登録を解除する
                lobbies.unregisterLobby(lobbyName);

                //ファイルを削除する
                lobbies.makeYaml(lobbyName).file.delete();

                Text.stream("$lobby-&r-を削除しました。")
                        .setAttribute("$lobby", lobbyName)
                        .color()
                        .setReceiver(player)
                        .sendChatMessage();
                break;
            }case "spawn": {

                //指定されたロビーが存在しなければ戻る
                if (blockNotExistLobby(player, lobbyName)) return;

                Lobby lobby = lobbies.getLobby(lobbyName);
                //プレイヤーの座標を取得する
                Location location = sender.asPlayerCommandSender().getLocation();

                //イミュータブルな座標にしブロックの中央に調整した上でセットする
                lobby.spawn = new ImmutableLocation(location);
                lobby.origin = new ImmutableLocation(location);

                Text.stream("$lobby-&r-のスポーン地点を現在地点に書き換えました。")
                        .setAttribute("$lobby", lobby.name)
                        .color()
                        .setReceiver(player)
                        .sendChatMessage();
                break;
            }
            case "link": {
                //指定されたロビーが存在しなければ戻る
                if (blockNotExistLobby(player, lobbyName)) return;

                if(!args.hasNext()){
                    sender.warn("リンクするコースを指定してください");
                    return;
                }

                String course = org.bukkit.ChatColor.translateAlternateColorCodes('&', args.next());
                Parkour parkour = parkours.getParkour(course);

                //アスレが存在しなければ戻る
                if (!parkours.containsParkour(course)) {
                    sender.warn("指定されたリンクするコースは存在しません。");
                    return;
                }

                Text.stream("$parkour-&r-とリンクしました。")
                        .setAttribute("$parkour", course)
                        .color()
                        .setReceiver(player)
                        .sendChatMessage();

                parkour.linkedLobby = lobbyName;

                break;
            }
            default:
                displayCommandUsage(sender);
                return;

        }
    }

    private void displayCommandUsage(Sender sender){
        sender.warn("/lobby [lobby] create @ 指定された名前でロビーを作成します。ロビー名の先頭には必ず装飾コードを置いて下さい。");
        sender.warn("/lobby [lobby] delete @ ロビーを削除します。");
        sender.warn("/lobby [lobby] spawn @ 現在地点をスポーン地点に設定する");
        sender.warn("/lobby [lobby] link [course] @　指定されたコースとリンクします");
        sender.warn("アスレ名の装飾コードはアンパサンドを使用して下さい。");
    }

    private boolean blockNotExistLobby(Player player, String lobbyName){
        if(lobbies.containsLobby(lobbyName)) return false;

        Text.stream("$lobby-&r-は存在しません。")
                .setAttribute("$lobby", lobbyName)
                .color()
                .setReceiver(player)
                .sendChatMessage();
        return true;
    }
}