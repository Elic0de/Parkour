package elicode.parkour.function.parkour;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import elicode.parkour.parkour.ParkourCategory;
import elicode.parkour.util.text.BilingualText;
import elicode.parkour.user.User;

public class ParkourSelectionUIOpener implements FunctionalItem {

	ItemStack item;

	@Override
	public void onClick(User user, ClickType click) {
		//ステージのカテゴリーを取得する
		ParkourCategory category = user.isOnParkour() ? user.currentParkour.category : ParkourCategory.NORMAL;

		user.inventoryUserInterfaces.openParkourSelectionUI(ParkourCategory.NORMAL);
	}

	@Override
	public ItemStack build(User user) {
		item = new ItemStack(Material.MILK_BUCKET);
		ItemMeta meta = item.getItemMeta();

		String displayName = BilingualText.stream("&b-アスレチック一覧を開く", "&b-Open Parkour List")
				.textBy(user.asBukkitPlayer())
				.color()
				.toString();

		meta.setDisplayName(displayName);
		item.setItemMeta(meta);

		return item;
	}

	@Override
	public boolean isSimilar(ItemStack item, User user) {

		String displayName = BilingualText.stream("&b-アスレチック一覧を開く", "&b-Open Parkour List")
				.textBy(user.asBukkitPlayer())
				.color()
				.toString();

		return item != null && item.getType() == Material.MILK_BUCKET && item.getItemMeta().getDisplayName().equals(displayName);
	}

}
