package elicode.parkour.function.parkour;

import org.bukkit.inventory.ItemStack;

import elicode.parkour.user.User;

public interface FunctionalItem {

	ItemStack item = null;

	String key = null;

	void onClick(User user, ClickType click);

	ItemStack build(User user);

	boolean isSimilar(ItemStack item, User user);
}
