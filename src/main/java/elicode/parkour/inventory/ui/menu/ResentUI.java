package elicode.parkour.inventory.ui.menu;

import elicode.parkour.user.User;
import org.bukkit.Material;

public class ResentUI extends AbstractMenuUI {
    public ResentUI(User user, MenuCategory category) {
        super(user, category,
                layout -> layout.put(s -> s.icon(Material.GLASS_BOTTLE, i -> i.displayName = "§cNo Recent Players Found"), 31));
    }
}
