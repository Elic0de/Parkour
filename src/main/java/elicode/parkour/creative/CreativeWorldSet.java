package elicode.parkour.creative;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreativeWorldSet {

    public final static List<CreativeWorld> HATS = new ArrayList<>(37);

    static{
        initialize(
                "0,10000000","1,10000000"
        );
    }

    private static void initialize(String... texts){
        Arrays.stream(texts)
                .map(text -> text.split(","))
                .map(data -> new CreativeWorld(Integer.parseInt(data[0]), Integer.parseInt(data[1])))
                .forEach(HATS::add);

    }

}
