package elicode.parkour.listener.chat;

import com.github.ucchyocean.lc.LunaChatAPI;
import com.github.ucchyocean.lc.channel.Channel;
import com.github.ucchyocean.lc.channel.ChannelPlayer;
import com.github.ucchyocean.lc.event.LunaChatChannelMemberChangedEvent;
import elicode.parkour.Main;
import elicode.parkour.listener.chat.filters.AntiIP;
import elicode.parkour.listener.chat.filters.AntiSameChars;
import elicode.parkour.listener.chat.filters.AntiSpam;
import elicode.parkour.listener.chat.filters.AntiUrl;
import elicode.parkour.util.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayerChatListener implements Listener {

    private final LunaChatAPI lunaChatAPI = Main.getPlugin().getLunachatapi();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player p = event.getPlayer();
        if (p.hasPermission("antimatter.bypass")) {
            return;
        }
        //String blocked = plugin.getConfig().getString("prefix")+" "+plugin.getConfig().getString("messageIfBlocked")+" ";
        boolean bypassIP = p.hasPermission("antimatter.bypass.ip");
        boolean bypassUrl = p.hasPermission("antimatter.bypass.url");
        boolean bypassSpam = p.hasPermission("antimatter.bypass.spam");
        String msg = event.getMessage();
        //boolean sendMsg = getPlugin(Antimatter.class).getConfig().getBoolean("sendMsgSenderMsgIfBlocked");
        if (!bypassIP) {
            if (!AntiIP.pass(msg)) {
                event.setCancelled(true);
                Text.stream("test1").color()
                        .setReceiver(p)
                        .sendChatMessage();
                return;
            }
        }
        if (!bypassUrl) {
            if (!AntiUrl.pass(msg)) {
                event.setCancelled(true);
                Text.stream("test12").color()
                        .setReceiver(p)
                        .sendChatMessage();
                /*if (sendMsg) {
                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', blocked + "(Url)"));
                }*/
                return;
            }
        }
        /*if (!bypassSpam) {
            if (!AntiSpam.pass(event)) {
                event.setCancelled(true);
                Text.stream("test3").color()
                        .setReceiver(p)
                        .sendChatMessage();
                *//*if (sendMsg) {
                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', blocked + "(Spam)"));
                }*//*
            }
        }*/
        boolean bypassFilter = p.hasPermission("antimatter.bypass.customfilter");
        boolean bypassCaps = p.hasPermission("antimatter.bypass.caps");
        boolean bypassChars = p.hasPermission("antimatter.bypass.char");
        /*String newMsg = msg;
        if (!bypassFilter) {
            newMsg = plugin.getReplacer().parseMessage(msg);
        }
        if (!bypassCaps) {
            if (!AntiCaps.pass(newMsg) && !bypassCaps) {
                newMsg = AntiCaps.parseNewMsg(newMsg);
            }
        }
        List<String> names = getPlayerNames();
        for (String name : names) {
            if (newMsg.contains(name.toLowerCase())) {
                newMsg = newMsg.replaceAll(name.toLowerCase(), name);
            }
        }
        if (!bypassChars) {
            if (!AntiSameChars.pass(newMsg) && !bypassChars) {
                newMsg = AntiSameChars.parseNew(newMsg);
            }
        }
        plugin.getLastMessages().put(event.getPlayer().getUniqueId(), msg.toLowerCase());*/
        event.setMessage(msg);
    }

    private List<String> getPlayerNames() {
        List<String> names = new ArrayList<>();
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            names.add(p.getName());
        }
        return names;
    }


}
