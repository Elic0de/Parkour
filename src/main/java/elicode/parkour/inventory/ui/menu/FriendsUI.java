package elicode.parkour.inventory.ui.menu;

import elicode.parkour.Main;
import elicode.parkour.party.PartyManager;
import elicode.parkour.user.User;
import elicode.parkour.util.databases.FriendDatabase;
import elicode.parkour.util.item.SkullCreator;
import elicode.parkour.util.text.BilingualText;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

public class FriendsUI extends AbstractMenuUI {

    private final PartyManager partyManager = Main.getPlugin().getPartyManager();


    public FriendsUI(User user, MenuCategory category) {

        super(user, category,

                layout -> {
                    Player player = layout.player;
                    final FriendDatabase friendDatabase = Main.getPlugin().getFriendDatabaseManager();
                    AtomicInteger page = new AtomicInteger();

                        if(friendDatabase.getFriends(friendDatabase.getPlayerID(layout.player.getUniqueId())).isEmpty()) {
                            layout.put(s ->{
                                s.icon(Material.GLASS_BOTTLE, i -> i.displayName = BilingualText.stream("§cぼっちです :(", "§cBummer No friends!! :(")
                                        .textBy(player)
                                        .toString());
                            }, 31);


                        }else {
                            //layout.title = title;
                            layout.itemsPerPage = 19;
                            layout.square = 1;
                            layout.setPage(page.get());

                            for(int index = 0; index < friendDatabase.getFriends(friendDatabase.getPlayerID(layout.player.getUniqueId())).size(); index++){

                                int finalIndex = index + 1;

                                layout.page(s -> s.icon(SkullCreator.fromPlayerUniqueId(friendDatabase.getUUID(finalIndex)), i -> i.displayName = friendDatabase.getName(finalIndex)));
                            }
                            if (layout.getCurrentPage() < layout.getPageAmount()) {
                                layout.put((s) -> {

                                    s.onClick(e -> {
                                        page.getAndIncrement();
                                        //user.inventoryUserInterfaces.openParkourSelectionUI(category);
                                    });

                                    s.icon(Material.ARROW, i -> i.displayName = category.name);

                                }, 53);
                            }
                            if (layout.getCurrentPage() > 1) {
                                layout.put((s) -> {

                                    s.onClick(e -> {
                                        page.getAndDecrement();
                                        //user.inventoryUserInterfaces.openParkourSelectionUI(category);
                                    });

                                    s.icon(Material.ARROW, i -> i.displayName = category.name);

                                }, 45);
                            }
                        }

                });
    }

}