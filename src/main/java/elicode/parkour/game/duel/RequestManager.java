package elicode.parkour.game.duel;

import elicode.parkour.Main;
import elicode.parkour.parkour.Parkour;
import elicode.parkour.util.message.ClickableMessage;
import elicode.parkour.util.message.MessageStyle;
import elicode.parkour.util.text.BilingualText;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RequestManager {

    private final Map<UUID, Map<UUID, Request>> requests = new HashMap<>();

    public Map<UUID, Request> get(final Player player, final boolean create) {
        Map<UUID, Request> cached = requests.get(player.getUniqueId());

        if (cached == null && create) {
            requests.put(player.getUniqueId(), cached = new HashMap<>());
            return cached;
        }

        return cached;
    }

    public void make(final Player sender, final Player target) {
        final Request request = new Request(sender, target);
        get(sender, true).put(target.getUniqueId(), request);
    }

    public void send(final Player sender) {


        get(sender,false).forEach((uuid, request) -> {
            Player player = Bukkit.getPlayer(request.getSender());
            Player target = Bukkit.getPlayer(request.getTarget());

           /* if (*//*Main.getPlugin().getRequestManager().has(player, target) && *//*request.getParkour() != request.getParkour()) {

                BilingualText.stream("&c-すでにリクエストを送信しています","$target &c-has already been invited to Duel! Wait for them to accept!")
                        .color()
                        .setAttribute("$target", target.getName())
                        .setReceiver(player)
                        .sendChatMessage();
                return;
            }
*/
            BilingualText.stream("$target&e-にリクエストを送信しました!","&e-You invited $target &e-Duels! They have 30 seconds to accept.")
                    .color()
                    .setAttribute("$target", target.getName())
                    .setReceiver(player)
                    .sendChatMessage();

            BilingualText.stream("&6--------------------------------------------------\n" +
                    "$playerName&b-からDuelの招待が来ました！マップは$courseName\n" +
                    "&e-ここをクリックして今すぐ開始！","$playerName &bhas invited you to $courseName\n" +
                    "&6-Click Here &e-to Duels accpet You have 30 seconds to Duels accept" +
                    "&6\n--------------------------------------------------\n")

                    .color()
                    .setAttribute("$playerName", player.getName())
                    .setAttribute("$courseName", request.getParkour().name)
                    .setReceiver(target)
                    .send(new ClickableMessage(ClickableMessage.ClickAction.RUN_COMMAND, "/duel accept " + player.getName()));


        });
    }

    public Request get(final Player sender, final Player target) {
        final Map<UUID, Request> cached = get(sender, false);

        if (cached == null) {
            return null;
        }

        final Request request = cached.get(target.getUniqueId());

        if (request == null) {
            return null;
        }
        System.out.println(TimeUnit.SECONDS.toMillis(30L));
        if (System.currentTimeMillis() - request.getCreation() >= TimeUnit.SECONDS.toMillis(30L)) {
            cached.remove(target.getUniqueId());
            return null;
        }

        return request;
    }

    public boolean has(final Player sender, final Player target) {
        return get(sender, target) != null;
    }

    public Request remove(final Player sender, final Player target) {
        final Map<UUID, Request> cached = get(sender, false);

        if (cached == null) {
            return null;
        }

        final Request request = cached.remove(target.getUniqueId());

        if (request == null) {
            return null;
        }

        if (System.currentTimeMillis() - request.getCreation() >= 30 * 1000L) {
            cached.remove(target.getUniqueId());
            return null;
        }

        return request;
    }

    /*@EventHandler
    public void on(final PlayerQuitEvent event) {
        requests.remove(event.getPlayer().getUniqueId());
    }*/
}
