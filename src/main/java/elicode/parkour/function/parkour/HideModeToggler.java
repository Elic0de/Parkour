package elicode.parkour.function.parkour;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import elicode.parkour.function.ToggleHideMode;
import elicode.parkour.util.text.BilingualText;
import elicode.parkour.util.text.TextStream;
import elicode.parkour.user.User;

public class HideModeToggler implements FunctionalItem {

	ItemStack item;

	@Override
	public void onClick(User user, ClickType click) {
		ToggleHideMode.getInstance().change(user);
		ControlFunctionalItem.updateSlot(user.asBukkitPlayer(), ItemType.HIDE_MODE_TOGGLER);
	}

	@Override
	public ItemStack build(User user) {
		boolean hideMode = user.playerSettings.hideMode;
		item = new ItemStack(hideMode ? Material.SKULL : Material.SKULL);
		ItemMeta meta = item.getItemMeta();

		TextStream stream;
		if(!hideMode) stream = BilingualText.stream("&f-プレイヤー:&a-表示 &7-(右クリック)", "&f-Players: &a-Visible &7-(Right Click)");
		else stream = BilingualText.stream("&f-プレイヤー:&c-非表示 &7-(右クリック)", "&f-Players: &c-Hidden &7-(Right Click)");

		meta.setDisplayName(stream.textBy(user.asBukkitPlayer()).color().toString());
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public boolean isSimilar(ItemStack item, User user) {
		if(item == null) return false;

		Material type = item.getType();

		boolean hideMode = user.playerSettings.hideMode;
		TextStream stream;
		if(!hideMode) stream = BilingualText.stream("&f-プレイヤー:&a-表示 &7-(右クリック)", "&f-Players: &a-Visible &7-(Right Click)");
		else stream = BilingualText.stream("&f-プレイヤー:&c-非表示 &7-(右クリック)", "&f-Players: &c-Hidden &7-(Right Click)");

		String displayName =
				stream
				.textBy(user.asBukkitPlayer())
				.color()
				.toString();

		return type == Material.SKULL || type == Material.SKULL /* && item.getItemMeta().getDisplayName().equals(displayName)*/;
	}

}
