package elicode.parkour.inventory.ui.menu;

import org.bukkit.Material;

public enum MenuCategory {

    MYPROFILE(Material.SKULL, Material.SIGN),
    FRIENDS(Material.SKULL,Material.SIGN),
    PARTY(Material.SKULL, Material.SIGN),
    CHANNEL(Material.SKULL, Material.SIGN  ),
    RESENT(Material.SKULL, Material.SIGN);

    public final String name;
    public final Material icon;
    public final Material line;

    private MenuCategory(Material icon, Material line){
        this.name = toString().charAt(0) + toString().substring(1).toLowerCase();
        this.icon = icon;
        this.line = line;
    }

}
