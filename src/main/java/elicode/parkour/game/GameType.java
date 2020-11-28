package elicode.parkour.game;

public enum GameType {
    RACE(1, "Race"),
    SOLO(2, "Solo"),
    DOUBLES(3, "Doubles"),
    RANKED(4, "Ranked"),
    DUEL(5, "Duel");

    private int id;

    private String name;

    GameType(int paramInt1, String paramString1) {
        this.id = paramInt1;
        this.name = paramString1;
    }

    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public static GameType getEnum(String paramString) {
        byte b;
        int i;
        GameType[] arrayOfMiniGameType;
        for (i = (arrayOfMiniGameType = values()).length, b = 0; b < i; ) {
            GameType miniGameType = arrayOfMiniGameType[b];
            if (miniGameType.name().equalsIgnoreCase(paramString))
                return miniGameType;
            b++;
        }
        return null;
    }
}