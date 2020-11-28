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
import elicode.parkour.util.text.BilingualText;
import elicode.parkour.util.text.Text;
import elicode.parkour.util.tuplet.Triple;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChannelsUI extends AbstractMenuUI {
    //説明文を表す
    private static class LoreBuilder extends Triple<LocaleFunction, String, Function<Channel, ?>> {

        public LoreBuilder(String japanise, String english, Function<Channel, ?> status) {
            this(japanise, english, "", status);
        }

        public LoreBuilder(String japanise, String english, String unit, Function<Channel, ?> status) {
            super(new LocaleFunction(japanise, english), unit, status);
        }

        public String buildBy(Channel channel,User user){
            //"&7-: &b-Updateランク &7-@ &b-$0
            return Text.stream("&a-$name: &e-$value$unit")
                    .setAttribute("$name", first.apply(user.asBukkitPlayer()))
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

    public ChannelsUI(User user, MenuCategory category) {



        super(user, category,
                layout -> {
                    final LunaChatAPI lunaChatAPI = Main.getPlugin().getLunachatapi();
                    Player player = layout.player;
                    if (!lunaChatAPI.getChannels().contains(lunaChatAPI.getChannel(player.getName()))) {
                        layout.put(s -> {

                            s.icon(Material.GLASS_BOTTLE, i -> i.displayName = "§cBummer! No channels:(");


                        }, 31);


                    }else {
                        int index = 0;
                        for (Channel channel : lunaChatAPI.getChannels()) {
                            index++;
                            layout.put(s -> {

                                s.onClick(e -> {


                                });
                                s.icon(Material.PAPER, i -> {

                                    i.displayName = BilingualText.stream("§6" + channel.getName(), "§6" + channel.getName())
                                            .textBy(player)
                                            .toString();

                                    List<String> lore = LORE_BUILDERS.stream().map(builder -> builder.buildBy(channel ,user)).collect(Collectors.toList());
                                    lore.add(0, "");
                                    lore.add(3, "");

                                    i.lore = lore;
                                });

                            }, 26 + index);
                        }
                    }
                });

    }
}
