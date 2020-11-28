package elicode.parkour.inventory.ui;

import org.bukkit.Material;

import elicode.parkour.inventory.Apply;
import elicode.parkour.inventory.ui.dsl.InventoryUI;
import elicode.parkour.inventory.ui.dsl.component.Slot;
import elicode.parkour.user.User;

public abstract class AbstractUI implements InventoryUI {

	public static final Apply<Slot> DEFAULT_SLOT = s -> s.icon(Material.STAINED_GLASS_PANE, i -> i.displayName = " ");

	public static final Apply<Slot> PROFILE_SLOT = s -> s.icon(Material.STAINED_GLASS_PANE, i -> i.displayName = " ");

	protected final User user;

	public AbstractUI(User user){
		this.user = user;
	}

}
