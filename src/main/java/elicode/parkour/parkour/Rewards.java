package elicode.parkour.parkour;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Rewards {

	private final int[] coins;

	public Rewards(int[] coins){
		this.coins = coins;
	}

	public int getReward(int numberOfTimesCleared){
		return coins[Math.min(numberOfTimesCleared, coins.length - 1)];
	}

	public String serialize(){
		return String.join(",", Arrays.stream(coins).mapToObj(String::valueOf).collect(Collectors.toList()));
	}

}
