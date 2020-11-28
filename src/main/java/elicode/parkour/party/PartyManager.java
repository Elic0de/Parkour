package elicode.parkour.party;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PartyManager {

    private static HashMap<UUID, Party> parties = new HashMap<>();

    public Party getParty(UUID pUUID) {
        return parties.get(pUUID);
    }

    public Party createParty(Player player) {
        Party localPlayerParty = new Party(player);
        parties.put(player.getUniqueId(), localPlayerParty);
        return localPlayerParty;
    }

    public void deleteAllParties() {
        parties = new HashMap<>();
    }

    public void deleteParty(Party party) {
        if (party != null) {
            for (int i = 0; i < party.getPlayers().size(); i++) {
                if (party.getPlayers().get(i) != null)
                    parties.remove((party.getPlayers().get(i)).getUniqueId());
            }
            if (party.getOwner() != null)
                parties.remove(party.getOwner().getUniqueId());
        }
    }

    public void addPlayerToParty(Player player, Party party) {
        parties.put(player.getUniqueId(), party);
    }

    public void removePlayerFromParty(Player player) {
        parties.remove(player.getUniqueId());
    }

}
