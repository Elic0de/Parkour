package elicode.parkour.inventory.ui;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import elicode.parkour.cosmetics.hat.Hat;
import elicode.parkour.cosmetics.hat.Hats;
import elicode.parkour.inventory.ui.dsl.component.InventoryLayout;
import elicode.parkour.util.item.ItemStackBuilder;
import elicode.parkour.util.sound.SoundMetadata;
import elicode.parkour.util.text.BilingualText;
import elicode.parkour.user.User;
import elicode.parkour.user.PurchasedHatCollection;

public class WearHatUI extends AbstractUI {

	private static final SoundMetadata WEAR_SE = new SoundMetadata(Sound.ITEM_ARMOR_EQUIP_CHAIN, 5f, 0.25f);
	private static final SoundMetadata PUT_ON_SE = new SoundMetadata(Sound.ENTITY_CHICKEN_EGG, 1.5f, 1f);
	private static final ItemStack AIR = new ItemStack(Material.AIR);

	private final PurchasedHatCollection purchasedHats;

	public WearHatUI(User user){
		super(user);
		purchasedHats = user.hats;
	}

	@Override
	public Function<Player, InventoryLayout> layout() {
		Player player = user.asBukkitPlayer();

		//購入済みハットのリスト
		List<Hat> hats = Hats.HATS.stream()
		.filter(purchasedHats::has)
		.collect(Collectors.toList());

		return build(hats.size() + 1, l -> {
			l.title = BilingualText.stream("帽子を被る", "Wear Hats")
					.textBy(player)
					.toString();

			l.defaultSlot(s -> s.icon(Material.STAINED_GLASS_PANE, i -> i.displayName = " "));

			int slotIndex = 0;
			int lastSlotIndex = l.option.size - 1;

			for(int index = 0; index < hats.size(); index++){
				Hat hat = hats.get(index);
				String hatName = hat.name;
				Material hatItem = hat.item;

				//同じ帽子であれば処理しない
				if(isSameHat(hatName, player.getInventory().getHelmet())) continue;

				l.put(s -> {

					s.onClick(e -> {
						PlayerInventory inventory = player.getInventory();

						//帽子を被らせる
						inventory.setHelmet(new ItemStack((hatItem)));

						//被った帽子はNMS側でclone()されているので取得した物を書き換える
						ItemStack equippedHelmet = inventory.getHelmet();

						//表示名を帽子のプレイヤー名にする
						ItemMeta meta = equippedHelmet.getItemMeta();
						meta.setDisplayName("§f" + hatName);
						equippedHelmet.setItemMeta(meta);

						String displayName = BilingualText.stream("&c-帽子を仕舞う", "&c-ResetHat")
								.textBy(player)
								.color()
								.toString();

						ItemStack button = new ItemStackBuilder(Material.CHEST)
						.setDisplayName(displayName)
						.build();

						e.clickedInventory.setItem(lastSlotIndex, button);

						WEAR_SE.play(player);

						BilingualText.stream("&c-$nameの帽子を被りました", "&c-You wore a $name hat")
						.setAttribute("$name", hatName)
						.color()
						.setReceiver(player)
						.sendActionBarMessage();

						openInventory(player);
					});

					s.icon(i -> {
						i.material = hatItem;
						i.displayName = "§b" + hatName;
						String lore = BilingualText.stream("&7-クリックすると被ります。", "&7-Click to wear")
								.textBy(player)
								.color()
								.toString();
						i.lore(lore);
					});

				}, slotIndex++);
			}

			l.put(s -> {

				s.onClick(e -> {
					PlayerInventory inventory = player.getInventory();
					ItemStack helmet = inventory.getHelmet();

					//帽子を被っていなければ戻る
					if(helmet == null) return;

					inventory.setHelmet(AIR);
					e.clickedInventory.setItem(lastSlotIndex, AIR);

					PUT_ON_SE.play(player);

					BilingualText.stream("&c-帽子を仕舞いました", "&c-Reset your hat")
					.color()
					.setReceiver(player)
					.sendActionBarMessage();

					openInventory(player);
				});

				String displayName = BilingualText.stream("&c-帽子を仕舞う", "&c-ResetHat")
						.textBy(player)
						.color()
						.toString();

				//帽子を被っていればそれを脱ぐ為のボタンをセットする
				if(player.getInventory().getHelmet() != null) s.icon(Material.CHEST, i -> i.displayName = displayName);

			}, lastSlotIndex);
		});
	}

	private boolean isSameHat(String hatName, ItemStack helmet){
		return helmet != null && helmet.getType() == Material.SKULL && helmet.hasItemMeta() && hatName.endsWith(helmet.getItemMeta().getDisplayName());
	}

}
