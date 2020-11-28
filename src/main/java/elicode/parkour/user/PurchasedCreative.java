package elicode.parkour.user;

import elicode.parkour.creative.CreativeWorld;
import elicode.parkour.util.yaml.Yaml;

import java.util.HashSet;
import java.util.Set;

public class PurchasedCreative {

    private final User user;
    private final Set<Integer> hatIds;

    public PurchasedCreative(User user, Yaml yaml){
        this.user = user;
        this.hatIds = new HashSet<>(yaml.getIntegerList("Purchased"));
    }

    public boolean canBuy(CreativeWorld creativeWorld){
        return creativeWorld.value <= user.coins();
    }

    public void buy(CreativeWorld creativeWorld){
        user.withdrawCoins(creativeWorld.value);
        hatIds.add(creativeWorld.id);
    }

    public boolean has(CreativeWorld creativeWorld){
        return hatIds.contains(creativeWorld.id);
    }

    public void save(Yaml yaml){
        yaml.set("Purchased",0);
    }


}
