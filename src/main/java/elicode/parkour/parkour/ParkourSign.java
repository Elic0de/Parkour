package elicode.parkour.parkour;

import elicode.parkour.user.User;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

public class ParkourSign {

    private final ParkourSet parkours = ParkourSet.getInstance();

    public void createStandardSign(SignChangeEvent sign, Player player, String title) {
        /*if (!Utils.hasSignPermission(player, sign, title)) {
            return;
        }*/

        sign.setLine(0, title);
        sign.setLine(1, "");
        sign.setLine(2, "-----");
        player.sendMessage( title + " sign created!");
    }
    public void createStandardCourseSign(SignChangeEvent sign, User user, String title) {
        createStandardCourseSign(sign, user, title, true);
    }

    public boolean createStandardCourseSign(SignChangeEvent sign, User user, String title, boolean message) {


        if (!parkours.containsParkour(ChatColor.translateAlternateColorCodes('&', sign.getLine(1)))) {
            sign.setLine(1, ChatColor.RED + "Unknown Course!");
            sign.setLine(2, "");
            sign.setLine(3, "");
            return false;
        }

        sign.setLine(0, title);

        if (message) {
            user.asBukkitPlayer().sendMessage(ChatColor.DARK_AQUA + title + ChatColor.WHITE + " sign for " + ChatColor.AQUA + sign.getLine(2) + ChatColor.WHITE + " created!");
        }
        return true;
    }
    public void createCheckpointSign(SignChangeEvent sign, User user, String checkpoint) {
        if (!createStandardCourseSign(sign, user, "[CP]", false)) {
            return;
        }
        sign.setLine(1,ChatColor.translateAlternateColorCodes('&',sign.getLine(1)));

        if (sign.getLine(2).isEmpty()) {
            sign.getBlock().breakNaturally();
            user.asBukkitPlayer().sendMessage("Please specify checkpoint on bottom line!");
            return;
        }

        user.asBukkitPlayer().sendMessage("Checkpoint sign for " + ChatColor.AQUA + sign.getLine(2) + ChatColor.WHITE + " created!");
    }

}
