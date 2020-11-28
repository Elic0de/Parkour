package elicode.parkour.util;

import elicode.parkour.Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static void log(String message, int severity) {
        switch (severity) {
            case 1:
                Main.getPlugin().getLogger().warning(message);
                break;
            case 2:
                Main.getPlugin().getLogger().severe("! " + message);
                break;
            case 0:
            default:
                Main.getPlugin().getLogger().info(message);
                break;
        }
    }

    public static void logToFile(String message) {


        try {
            File saveTo = new File(Main.getPlugin().getDataFolder(), "Parkour.log");
            if (!saveTo.exists()) {
                saveTo.createNewFile();
            }

            FileWriter fw = new FileWriter(saveTo, true);
            PrintWriter pw = new PrintWriter(fw);
            pw.println(getDateTime() + " " + message);
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void log(String message) {
        log(message, 0);
    }

    public static String getDateTime() {
        Format formatter = new SimpleDateFormat("[dd/MM/yyyy | HH:mm:ss]");
        return formatter.format(new Date());
    }

}
