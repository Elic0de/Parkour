package elicode.parkour.game;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum  RankedColor  {

    Default(ChatColor.GRAY,100),
    Copper(ChatColor.RED,130),
    Bronze(ChatColor.GOLD,160),
    Silver(ChatColor.WHITE,210),
    Gold(ChatColor.YELLOW,260),
    Platinum(ChatColor.AQUA,320),
    Diamond(ChatColor.LIGHT_PURPLE,440),
    Champion(ChatColor.DARK_RED,500);

    public final String name;
    public final int elo;
    public final ChatColor color;

    RankedColor(ChatColor color, int elo){
        this.name = toString().charAt(0) + toString().substring(1).toLowerCase();
        this.color = color;
        this.elo = elo;

    }
}
