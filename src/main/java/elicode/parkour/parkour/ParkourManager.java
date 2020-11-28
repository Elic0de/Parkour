package elicode.parkour.parkour;

public class ParkourManager {

    private static ParkourManager instance = null;
    /*private final Universal universal = Universal.get();
    private final Set<Punishment> punishments = Collections.synchronizedSet(new HashSet<>());
    private final Set<Punishment> history = Collections.synchronizedSet(new HashSet<>());
    private final Set<String> cached = Collections.synchronizedSet(new HashSet<>());*/

    public static ParkourManager get() {
        return instance == null ? instance = new ParkourManager() : instance;
    }

}
