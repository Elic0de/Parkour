package elicode.parkour.function.parkour;

import elicode.parkour.inventory.ui.ParkourCheckpointListUI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import elicode.parkour.util.text.BilingualText;
import elicode.parkour.user.InventoryUISet;
import elicode.parkour.user.User;

public class CheckpointSelectionUIOpener implements FunctionalItem {

	ItemStack item;

	@Override
	public void onClick(User user, ClickType click) {
		//どこのアスレにもいなければ戻る
		if(!user.isOnParkour()){
			BilingualText.stream("&c-パルクール中でないためチェックポイントの選択画面を開けません",
					"&c-You can't open checkpoint selection UI because you aren't playing parkour now")
					.color()
					.setReceiver(user.asBukkitPlayer())
					.sendActionBarMessage();
			return;
		}

		if (user.getPlayingGame() != null || user.currentParkour.timeAttackEnable == true) {
			if(user.getTimeAttackProgress() == null) {
				BilingualText.stream("&c-実行できません",
						"&c-You can't teleport")
						.color()
						.setReceiver(user.asBukkitPlayer())
						.sendActionBarMessage();
			}
			return;
		}

		InventoryUISet inventoryUserInterfaces = user.inventoryUserInterfaces;

		if(user.parkourPlayingNow != null) {

			new ParkourCheckpointListUI(user, user.parkourPlayingNow).openInventory(user.asBukkitPlayer());

			return;
		}

		//右クリックしたのであれば最終、左クリックしたのであれば最新のチェックポイントリストを表示する
		if(click == ClickType.RIGHT) inventoryUserInterfaces.openLastCheckpointSelectionUI();
		else if(click == ClickType.LEFT) inventoryUserInterfaces.openLatestCheckpointSelectionUI();
	}

	@Override
	public ItemStack build(User user) {
		item = new ItemStack(Material.CARROT_STICK);

		ItemMeta meta = item.getItemMeta();

		String displayName = BilingualText.stream("&b-チェックポイント一覧を開く &7-(最新 @ 左 / 最終 @ 右)",
				"&b-Open Checkpoint List &7-(Latest @ L / Last @ R)")
				.textBy(user.asBukkitPlayer())
				.color()
				.toString();


		meta.setDisplayName(displayName);

		item.setItemMeta(meta);
		return item;
	}

	@Override
	public boolean isSimilar(ItemStack item, User user) {

		String displayName = BilingualText.stream("&b-チェックポイント一覧を開く &7-(最新 @ 左 / 最終 @ 右)",
				"&b-Open Checkpoint List &7-(Latest @ L / Last @ R)")
				.textBy(user.asBukkitPlayer())
				.color()
				.toString();

		return item != null && item.getType() == Material.CARROT_STICK && item.getItemMeta().getDisplayName().equals(displayName);

	}
}
