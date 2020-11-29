package elicode.parkour.inventory.ui.menu;

import elicode.parkour.Main;
import elicode.parkour.party.PartyManager;
import elicode.parkour.user.User;

public class FriendsUI extends AbstractMenuUI {

    private final PartyManager partyManager = Main.getPlugin().getPartyManager();


    public FriendsUI(User user, MenuCategory category) {

        super(user, category,

                layout -> {


                });
    }

}