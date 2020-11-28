package elicode.parkour.inventory.ui.menu.channel;

import com.github.ucchyocean.lc.LunaChatAPI;
import com.github.ucchyocean.lc.channel.Channel;
import com.github.ucchyocean.lc.channel.ChannelPlayer;
import com.google.common.collect.ImmutableList;
import elicode.parkour.Main;
import elicode.parkour.inventory.ui.LocaleFunction;
import elicode.parkour.inventory.ui.menu.AbstractMenuUI;
import elicode.parkour.inventory.ui.menu.MenuCategory;
import elicode.parkour.user.User;
import elicode.parkour.util.format.CoinFormat;
import elicode.parkour.util.format.CountFormat;
import elicode.parkour.util.format.TimeFormat;
import elicode.parkour.util.item.SkullCreator;
import elicode.parkour.util.text.BilingualText;
import elicode.parkour.util.text.Text;
import elicode.parkour.util.tuplet.Triple;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChannelUI extends AbstractMenuUI {

    //説明文を表す
    private static class LoreBuilder extends Triple<LocaleFunction, String, Function<Channel, ?>> {

        public LoreBuilder(String japanise, String english, Function<Channel, ?> status) {
            this(japanise, english, "", status);
        }

        public LoreBuilder(String japanise, String english, String unit, Function<Channel, ?> status) {
            super(new LocaleFunction(japanise, english), unit, status);
        }

        public String buildBy(Channel channel){
            //"&7-: &b-Updateランク &7-@ &b-$0
            return Text.stream("&a-$name: &e-$value$unit")
                    .setAttribute("$value", third.apply(channel))
                    .setAttribute("$unit", second)
                    .color()
                    .toString();
        }

    }
    private static final List<LoreBuilder> LORE_BUILDERS;

    static {
        LORE_BUILDERS = ImmutableList.of(
                new LoreBuilder("Channel Master", "Channel Master", channel -> channel.getName()),
                new LoreBuilder("Members", "Members", channel -> channel.getMembers().size())
        );
}

    public ChannelUI(User user, MenuCategory category) {



        super(user, category,
                layout -> {
                    final LunaChatAPI lunaChatAPI = Main.getPlugin().getLunachatapi();
                    Player player = layout.player;
                    if (!lunaChatAPI.getChannels().contains(lunaChatAPI.getChannel(getChannelPlayer(player)))) {
                        layout.put(s -> {

                            s.onClick(e -> {

                                String channelName = player.getName();

                                Channel channel = lunaChatAPI.createChannel(channelName, player);
                                ChannelPlayer channelPlayer = ChannelPlayer.getChannelPlayer(player);

                                BilingualText.stream("[$name]という名でチャンネルを作成しました。", "Create a Channel")
                                        .color()
                                        .setAttribute("$name", channelName)
                                        .setReceiver(player)
                                        .sendChatMessage();

                                channel.addMember(channelPlayer);
                                lunaChatAPI.setDefaultChannel(player.getName(), channelName);
                                //channel.addModerator(channelPlayer);
                                user.inventoryUserInterfaces.openMyProfileUI(MenuCategory.CHANNEL);
                            });

                            s.icon(Material.SIGN, i -> i.displayName = BilingualText.stream("§aチャンネルを作成", "§aCreate a Channel")
                                    .textBy(layout.player)
                                    .toString());

                        }, 30);

                        layout.put(s -> {

                            s.onClick(e -> {

                                user.inventoryUserInterfaces.openChannelsUI();

                            });

                            s.icon(Material.PAPER, i -> i.displayName = BilingualText.stream("§aチャンネルを検索", "§aChannel Finder")
                                    .textBy(player)
                                    .toString());

                        }, 32);


                    }else {
                        layout.put(s -> {

                            s.onClick(e -> {
                                user.inventoryUserInterfaces.openChannelsUI();
                            });

                            s.icon(Material.PAPER, i -> {

                                i.displayName = BilingualText.stream("§aChannel Finder", "§aChannel Finder")
                                        .textBy(player)
                                        .toString();
                            });

                        }, 26);
                        int index = 0;
                        for (ChannelPlayer channelPlayer : lunaChatAPI.getChannel(getChannelPlayer(player)).getMembers()) {
                            index++;
                            layout.put(s -> {

                                s.onClick(e -> {


                                });
                                ItemStack skull = SkullCreator.fromPlayerUniqueId(channelPlayer.getPlayer().getUniqueId());
                                s.icon(skull, i -> {

                                    i.displayName = BilingualText.stream("§6" + channelPlayer.getName(), "§6" + channelPlayer.getName())
                                            .textBy(player)
                                            .toString();
                                });

                            }, 26 + index);
                        }
                    }
                });

    }
    public static String getChannelPlayer(Player player){
        final LunaChatAPI lunaChatAPI = Main.getPlugin().getLunachatapi();
        ChannelPlayer cp = ChannelPlayer.getChannelPlayer(player);

        Channel dc = lunaChatAPI.getDefaultChannel(cp.getName());
        String dchannel = "";
        if ( dc != null ) {
            dchannel = dc.getName().toLowerCase();
        }
        String channelName= null ;

        for ( Channel channel : lunaChatAPI.getChannels() ) {

            // BANされているチャンネルは表示しない
            if (channel.getBanned().contains(cp)) {
                continue;
            }

            // 個人チャットはリストに表示しない
            if (channel.isPersonalChat()) {
                continue;
            }

            // 参加していないチャンネルは、グローバルチャンネルを除き表示しない
            if (!channel.getMembers().contains(cp) && !channel.isGlobalChannel()) {
                continue;
            }
            String disp = channel.getName();
            if ( channel.getName().equals(dchannel) ) {
                disp = channel.getName();
            }

            channelName = disp;
        }
        return channelName;

    }
}
