package elicode.parkour.parkour;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Difficulty {

    private final int[] difficulty;

    public Difficulty(int[] difficulty){
        this.difficulty = difficulty;
    }

    public int getDifficulty(int numberOfTimesCleared){
        return difficulty[Math.min(numberOfTimesCleared, difficulty.length - 1)];
    }

    public String serialize(){
        return String.join(",", Arrays.stream(difficulty).mapToObj(String::valueOf).collect(Collectors.toList()));
    }

}
