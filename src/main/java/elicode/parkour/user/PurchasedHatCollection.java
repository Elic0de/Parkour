package elicode.parkour.user;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import elicode.parkour.cosmetics.hat.Hat;
import elicode.parkour.util.yaml.Yaml;

public class PurchasedHatCollection {

	private final User user;
	private final Set<Integer> hatIds;

	public PurchasedHatCollection(User user, Yaml yaml){
		this.user = user;
		this.hatIds = new HashSet<>(yaml.getIntegerList("Purchased hat ids"));
	}

	public boolean canBuy(Hat hat){
		return hat.value <= user.coins();
	}

	public void buy(Hat hat){
		user.withdrawCoins(hat.value);
		hatIds.add(hat.id);
	}

	public boolean has(Hat hat){
		return hatIds.contains(hat.id);
	}

	public void save(Yaml yaml){
		yaml.set("Purchased hat ids", new ArrayList<>(hatIds));
	}

}
