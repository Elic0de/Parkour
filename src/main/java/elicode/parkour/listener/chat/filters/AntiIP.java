package elicode.parkour.listener.chat.filters;

public class AntiIP {

    public static boolean pass(String message) {
        /*FileConfiguration config = getPlugin(Antimatter.class).getConfig();
        if (!config.getBoolean("enabled.antiIp")) {
            return true;
        }*/
        boolean pass = !message.matches(".*([01]?\\d\\d?|2[0-4]\\d|25[0-5])." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5]).*");
        if (!pass) {
            /*for (String address : config.getStringList("whitelistedAddresses")) {
                if (message.contains(address)) {
                    pass = true;
                }
            }*/
        }
        return pass;
    }

}
