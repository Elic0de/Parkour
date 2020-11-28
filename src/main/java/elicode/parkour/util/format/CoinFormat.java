package elicode.parkour.util.format;

public class CoinFormat {

    private static final String FORMAT_FOR_COINS;

    static{
        FORMAT_FOR_COINS = "%,d";
    }

    public static String format(long coins){
        return String.format(FORMAT_FOR_COINS, coins);
    }

}
