package elicode.parkour.inventory.ui.menu;

import elicode.parkour.Main;
import elicode.parkour.inventory.ui.InventoryLine;
import elicode.parkour.parkour.ParkourCategory;
import elicode.parkour.parkour.ParkourSet;
import elicode.parkour.party.Party;
import elicode.parkour.party.PartyManager;
import elicode.parkour.user.User;
import elicode.parkour.util.item.SkullCreator;
import elicode.parkour.util.text.BilingualText;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class PartyUI extends AbstractMenuUI {


    public PartyUI(User user, MenuCategory category) {
        super(
                user,
                category,
                layout -> {
                    final Party party = Main.getPlugin().getPartyManager().getParty(layout.player.getUniqueId());
                    if(party == null) {
                        layout.put(s -> {

                            s.onClick(e -> {

                                Main.getPlugin().getPartyManager().createParty(layout.player);

                                BilingualText.stream("partyを作成しました。", "a party create")
                                        .color()
                                        .setReceiver(layout.player)
                                        .sendChatMessage();

                                //表示を更新する
                                user.inventoryUserInterfaces.openMyProfileUI(MenuCategory.PARTY);
                            });

                            s.icon(Material.STAINED_GLASS_PANE, i -> i.displayName = BilingualText.stream("§aパーティーを作成", "§ainvite")
                                    .textBy(layout.player)
                                    .toString());


                        }, 31);

                    }else {
                        for (int index = 0; index < party.getPlayers().size(); index++) {
                            int finalIndex = index;
                            layout.put(s -> {

                                Player player = layout.player;



                                s.icon(SkullCreator.fromPlayerUniqueId(party.getPlayers().get(finalIndex).getUniqueId()), i -> i.displayName = party.getPlayers().get(finalIndex).getDisplayName());


                            }, index + 27);
                        }
                            layout.put(s -> {

                                s.onClick(e -> {
                                    if(!party.getOwner().equals(user.asBukkitPlayer())) return;

                                    party.disbandParty();
                                    user.inventoryUserInterfaces.openMyProfileUI(MenuCategory.PARTY);
                                });

                                s.icon(Material.TNT, i -> i.displayName = "§cDisband");


                            }, 19);

                            layout.put(s -> {

                                s.onClick(e -> {


                                    //表示を更新する
                                    user.inventoryUserInterfaces.openMyProfileUI(MenuCategory.PARTY);
                                });

                                s.icon(Material.BARRIER, i -> i.displayName = "§cKICK");


                            }, 18);

                    }
                });
    }
}
