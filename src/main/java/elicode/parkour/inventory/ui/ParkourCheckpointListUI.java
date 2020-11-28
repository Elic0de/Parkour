package elicode.parkour.inventory.ui;

import java.util.List;
import java.util.function.Function;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import elicode.beta.parkour.location.ImmutableLocation;
import elicode.parkour.inventory.ui.dsl.component.InventoryLayout;
import elicode.parkour.parkour.Parkour;
import elicode.parkour.util.text.BilingualText;
import elicode.parkour.util.text.Text;
import elicode.parkour.util.tuplet.Tuple;
import elicode.parkour.user.CheckpointSet;
import elicode.parkour.user.User;

public class ParkourCheckpointListUI extends AbstractUI {

	private final Parkour parkour;

	public ParkourCheckpointListUI(User user, Parkour parkour){
		super(user);
		this.parkour = parkour;
	}

	@Override
	public Function<Player, InventoryLayout> layout() {
		Player player = user.asBukkitPlayer();
		user.progress = null;

		CheckpointSet checkpoints = user.checkpoints;

		List<Tuple<Integer, ImmutableLocation>> points = user.parkourChallengeProgress()
				.setPresentFunction(it -> checkpoints.getCheckpoints(parkour, it.currentCheckAreaNumber()))
				.setEmptyFunction(() -> checkpoints.getCheckpoints(parkour))
				.apply();

		//アスレ名を取得する
		String parkourName = parkour.name;
		String prefixColor = parkour.prefixColor;

		return build(points.size(), l -> {
			l.title = parkour.colorlessName();

			l.defaultSlot(s -> s.icon(Material.STAINED_GLASS_PANE, i -> i.displayName = " "));

			//各座標毎に処理をする
			for(int slotIndex = 0; slotIndex < points.size(); slotIndex++){
				Tuple<Integer, ImmutableLocation> checkpoint = points.get(slotIndex);
				ImmutableLocation point = checkpoint.second;
				int majorCheckAreaNumberForDisplay = checkpoint.first + 1;
				l.put(s -> {

					s.onClick(e -> {
						//今いるアスレを取得する
						Parkour current = user.currentParkour;

						//別のアスレに移動するのであれば参加処理をする
						if(!parkour.equals(current)) parkour.entry(user);

						//プレイヤーを最終チェックポイントにテレポートさせる
						player.teleport(point.asBukkit());

						BilingualText.stream("$parkour-&r-$colorのチェックポイント$numberにテレポートしました",
								"You teleported to $parkour checkpoint$number")
								.setAttribute("$parkour", parkourName)
								.setAttribute("$color", prefixColor)
								.setAttribute("$number", majorCheckAreaNumberForDisplay)
								.color()
								.setReceiver(player)
								.sendActionBarMessage();
					});

					s.icon(Material.PRISMARINE_CRYSTALS, i -> {
						i.displayName = Text.stream("$color$number &7-@ $parkour")
								.setAttribute("$color", prefixColor)
								.setAttribute("$number", majorCheckAreaNumberForDisplay)
								.setAttribute("$parkour", parkourName)
								.color()
								.toString();

						String lore = BilingualText.stream("&7-: &b-クリック &7-@ このチェックポイントにテレポートします。",
								"&7-: &b-Click &7-@ Teleport to this checkpoint.")
								.textBy(player)
								.color()
								.toString();

						i.lore(lore);

						i.amount = majorCheckAreaNumberForDisplay;
					});

				}, slotIndex);
			}
		});
	}

}
