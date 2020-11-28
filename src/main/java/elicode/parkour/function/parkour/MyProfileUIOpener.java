package elicode.parkour.function.parkour;

import elicode.parkour.inventory.ui.menu.MenuCategory;
import elicode.parkour.util.sound.SoundMetadata;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import elicode.parkour.util.item.SkullCreator;
import elicode.parkour.util.text.BilingualText;
import elicode.parkour.user.User;

public class MyProfileUIOpener implements FunctionalItem {

	private static final SoundMetadata ERROR_SE = new SoundMetadata(Sound.BLOCK_ANVIL_PLACE, 1f, 1.75f);

	ItemStack item;

	@Override
	public void onClick(User user, ClickType click) {

		//ERROR_SE.play(user.asBukkitPlayer());
		user.inventoryUserInterfaces.openMyProfileUI(MenuCategory.MYPROFILE);
	}

	@Override
	public ItemStack build(User user) {
		//ユーザーのUUIDに基づきスカルヘッドを作成する
		item = new ItemStack(Material.EMERALD);

		ItemMeta meta = item.getItemMeta();

		String displayName = BilingualText.stream("&a-プロフィール &7-(Click)", "&a-My Profile &7-(Click)")
				.textBy(user.asBukkitPlayer())
				.color()
				.toString();

		meta.setDisplayName(displayName);
		item.setItemMeta(meta);

		return item;
	}

	@Override
	public boolean isSimilar(ItemStack item, User user) {

		String displayName = BilingualText.stream("&a-プロフィール &7-(Click)", "&a-My Profile &7-(Click)")
				.textBy(user.asBukkitPlayer())
				.color()
				.toString();

		return item != null && item.getType() == Material.EMERALD && item.getItemMeta().getDisplayName().equals(displayName);
	}

}
