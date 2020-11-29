package elicode.parkour.function.parkour;

import elicode.parkour.parkour.CheckAreas;
import elicode.parkour.parkour.ParkourRegion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import elicode.location.ImmutableLocation;
import elicode.parkour.util.enchantment.GleamEnchantment;
import elicode.parkour.parkour.Parkour;
import elicode.parkour.util.text.BilingualText;
import elicode.parkour.util.tuplet.Tuple;
import elicode.parkour.user.CheckpointSet;
import elicode.parkour.user.User;
import elicode.parkour.util.Optional;

import java.util.List;

public class CheckpointTeleporter implements FunctionalItem {

	ItemStack item;

	@Override
	public void onClick(User user, ClickType click) {
		Player player = user.asBukkitPlayer();

		//アスレをプレイ中でなければ戻る
		if(!user.isOnParkour()){
			BilingualText.stream("&c-アスレチックのプレイ中でないためテレポート出来ません",
					"&c-You can't teleport because you aren't playing parkour now")
					.color()
					.setReceiver(player)
					.sendActionBarMessage();
			return;
		}

		//今いるアスレ
		Parkour parkour = user.currentParkour;
		CheckpointSet checkpoints = user.checkpoints;

		if(!checkpoints.hasCheckpoint(parkour)){

			user.asBukkitPlayer().teleport(parkour.spawn.asBukkit());

			/*BilingualText.stream("&c-チェックポイントが設定されていないためテレポート出来ません",
					"&c-You can't teleport because you have not set any checkpoints")
					.color()
					.setReceiver(player)
					.sendActionBarMessage();*/
			return;
		}

		if(click.equals(ClickType.LEFT)) {

			if (user.getPlayingGame() != null || parkour.timeAttackEnable == true ) {
				if(user.getTimeAttackProgress() == null) {
					BilingualText.stream("&c-実行できません",
							"&c-You can't teleport")
							.color()
							.setReceiver(user.asBukkitPlayer())
							.sendActionBarMessage();
				}
				return;
			}
		}

		//右クリックしたのであれば最終チェックポイント、左クリックしたのであれば最新チェックポイント
		Optional<Tuple<Integer, ImmutableLocation>> wrappedCheckpoint = click == ClickType.LEFT ? checkpoints.getLastCheckpoint(parkour) : checkpoints.getLatestCheckpoint(parkour);


		//チェックポイントが無ければ戻る
		if(!wrappedCheckpoint.isPresent()){

			user.asBukkitPlayer().teleport(parkour.spawn.asBukkit());

			BilingualText.stream("&c-チェックポイントが設定されていないためテレポート出来ません",
					"&c-You can't teleport because you have not set any checkpoints")
					.color()
					.setReceiver(player)
					.sendActionBarMessage();
			return;
		}

		Tuple<Integer, ImmutableLocation> checkpoint = wrappedCheckpoint.forcedUnwrapping();


		//チェックポイントにテレポートさせる
		player.teleport(checkpoint.second.asBukkit());

		BilingualText.stream("$colorチェックポイント$numberにテレポートしました", "$colorTeleported to checkpoint$number")
		.setAttribute("$color", parkour.prefixColor)
		.setAttribute("$number", checkpoint.first )
		.setReceiver(player)
		.sendActionBarMessage();

		Location location = player.getLocation();
		CheckAreas checkAreas = parkour.checkAreas;

		//どこかのチェックエリア内にいるか調べる
		label: for(List<ParkourRegion> areas : checkAreas.getCheckAreas().values()){
			for(ParkourRegion area : areas){
				if(!area.isIn(location)) continue;

				user.currentCheckArea = area;
				break label;
			}
		}
	}

	@Override
	public ItemStack build(User user) {
		item = new ItemStack(Material.valueOf(user.teleporterItem));

		ItemMeta meta = item.getItemMeta();

		//最新@左 最終@右
		String displayName = BilingualText.stream("&b-チェックポイントにテレポートする &7-(最終 @ 右 / 最新 @ 左)",
				"&b-Teleport to Checkpoint &7-(Last @ R / Latest @ L )")
				.textBy(user.asBukkitPlayer())
				.color()
				.toString();

		meta.setDisplayName(displayName);

		item.setItemMeta(meta);

		//プレイヤーがチェックエリア内にいれば輝かせる
		if(user.isOnCheckArea()) GleamEnchantment.gleam(item);

		return item;
	}

	@Override
	public boolean isSimilar(ItemStack item, User user) {

		String displayName = BilingualText.stream("&b-チェックポイントにテレポートする &7-(最終 @ 右 / 最新 @ 左)",
				"&b-Teleport to Checkpoint &7-(Last @ R / Latest @ L )")
				.textBy(user.asBukkitPlayer())
				.color()
				.toString();

		return item != null && item.getType() == this.item.getType() || item.getType() == Material.valueOf(user.teleporterItem) && item.getItemMeta().getDisplayName().equals(displayName);
	}

}
