package elicode.parkour.user;

import elicode.parkour.cosmetics.hat.Hat;
import elicode.parkour.cosmetics.item.Item;
import elicode.parkour.util.yaml.Yaml;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class PurchasedTeleportItem {

    private final User user;
    private final Set<Integer> hatIds;

    public PurchasedTeleportItem(User user, Yaml yaml){
        this.user = user;
        this.hatIds = new HashSet<>(yaml.getIntegerList("Purchased item ids"));
    }

    public boolean canBuy(Item item){
        return item.value <= user.coins();
    }

    public void buy(Item item){
        user.withdrawCoins(item.value);
        hatIds.add(item.id);
    }

    public boolean has(Item item){
        return hatIds.contains(item.id);
    }

    public void save(Yaml yaml){
        yaml.set("Purchased item ids", new ArrayList<>(hatIds));
    }

}

