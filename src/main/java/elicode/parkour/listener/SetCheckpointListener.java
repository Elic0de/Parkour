package elicode.parkour.listener;

import elicode.parkour.Main;
import elicode.parkour.parkour.ParkourSet;
import elicode.parkour.util.sound.SoundMetadata;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import elicode.beta.parkour.location.ImmutableLocation;
import elicode.parkour.parkour.Parkour;
import elicode.parkour.util.text.BilingualText;
import elicode.parkour.parkour.ParkourRegion;
import elicode.parkour.user.User;
import elicode.parkour.user.UserSet;

public class SetCheckpointListener implements Listener {

	private final UserSet users = UserSet.getInstnace();
	private final ParkourSet parkours = ParkourSet.getInstance();
	private static final SoundMetadata CP_SET_NO = new SoundMetadata(Sound.ENTITY_BLAZE_SHOOT, 0.5f, 1.2f);
	private static final SoundMetadata CP_SET = new SoundMetadata(Sound.ENTITY_BLAZE_SHOOT, 0.5f, 1.2f);

	@EventHandler
	public void onSwap(PlayerSwapHandItemsEvent event){

		//固定キャンセル
		event.setCancelled(true);

		Player player = event.getPlayer();

		//ユーザーを取得する
		User user = users.getUser(player);

		//System.out.println(user.isPlayingParkour() + ":" + user.isOnCheckArea());

		if(!user.isPlayingParkour() || !user.isOnCheckArea()) return;

		ParkourRegion checkArea = user.currentCheckArea;

		//チェックエリアがあるアスレを取得する
		Parkour parkour = checkArea.parkour;

		//プレイヤーが今いるアスレでなければ戻る
		if(!user.currentParkour.equals(parkour)) return;

		//メジャーチェックエリア番号を取得する
		int majorCheckAreaNumber = parkour.checkAreas.getMajorCheckAreaNumber(checkArea);

		//不正な番号であれば戻る
		if(majorCheckAreaNumber < 0) return;

		//地に足をついていなければ戻る
		if(!player.isOnGround()){

			CP_SET_NO.play(player);

			BilingualText.stream("&c-空中でチェックポイントを設定することはできません", "&c-You can't set Checkpoint in midair")
					.setAttribute("$color", parkour.prefixColor)
					.color()
					.setReceiver(player)
					.sendActionBarMessage();
			return;
		}

		//チェックポイントとして設定する
		user.checkpoints.setCheckpoint(parkour, majorCheckAreaNumber + 1, new ImmutableLocation(player.getLocation()));

		BilingualText.stream("$colorチェックポイント$numberを設定しました", "$colorSet checkpoint$number")
		.setAttribute("$color", parkour.prefixColor)
		.setAttribute("$number", majorCheckAreaNumber + 1)
		.color()
		.setReceiver(player)
		.sendActionBarMessage();

		CP_SET.play(player);

	}

	@EventHandler(ignoreCancelled = true)
	public void onSignInteract(PlayerInteractEvent event){



		//固定キャンセル
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}

		if (event.getClickedBlock() == null ||
				!(event.getClickedBlock().getState() instanceof Sign)) {
			return;
		}


		Sign sign = (Sign) event.getClickedBlock().getState();
		String[] lines = sign.getLines();

		if (!ChatColor.stripColor(lines[0]).contains(ChatColor.stripColor("[CP]"))) {return;}

		Player player = event.getPlayer();

		//ユーザーを取得する
		User user = users.getUser(player);

		System.out.println(user.isPlayingParkour() + ":2" + user.isOnCheckArea());

		if(!user.isPlayingParkour()) return;

		System.out.println("1");

		ParkourRegion checkArea = user.currentCheckArea;

		//チェックエリアがあるアスレを取得する
		Parkour parkour = parkours.getParkour( ChatColor.translateAlternateColorCodes('&',lines[1]));
		//プレイヤーが今いるアスレでなければ戻る
		if(!user.currentParkour.equals(parkour)) return;

		System.out.println("2");

		//メジャーチェックエリア番号を取得する
		int majorCheckAreaNumber = Integer.parseInt(ChatColor.stripColor(lines[2]));

		System.out.println("3");

		//不正な番号であれば戻る
		if(majorCheckAreaNumber < 0) return;

		System.out.println("4");

		//地に足をついていなければ戻る
		if(!player.isOnGround()){

			CP_SET_NO.play(player);

			BilingualText.stream("&c-空中でチェックポイントを設定することはできません", "&c-You can't set Checkpoint in midair")
					.setAttribute("$color", parkour.prefixColor)
					.color()
					.setReceiver(player)
					.sendActionBarMessage();
			return;
		}

		System.out.println("5");

		//チェックポイントとして設定する
		user.checkpoints.setCheckpoint(parkour, majorCheckAreaNumber, new ImmutableLocation(player.getLocation()));

		BilingualText.stream("$colorチェックポイント$numberを設定しました", "$colorSet checkpoint$number")
				.setAttribute("$color", parkour.prefixColor)
				.setAttribute("$number", majorCheckAreaNumber)
				.color()
				.setReceiver(player)
				.sendActionBarMessage();

		CP_SET.play(player);

	}

}
