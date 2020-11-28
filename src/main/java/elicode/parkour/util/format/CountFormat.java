package elicode.parkour.util.format;

public class CountFormat {

    private static final String FORMAT_FOR_COUNT;

    static{
        FORMAT_FOR_COUNT = "%,d";
    }

    public static String format(long count){
        return String.format(FORMAT_FOR_COUNT, count);
    }

}
