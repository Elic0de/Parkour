package elicode.parkour.function;

import java.util.HashSet;
import java.util.function.Consumer;

import elicode.parkour.function.parkour.ControlFunctionalItem;
import elicode.parkour.function.parkour.ItemType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import elicode.parkour.Main;
import elicode.parkour.schedule.Sync;
import elicode.parkour.util.text.BilingualText;
import elicode.parkour.user.User;
import elicode.parkour.user.UserSet;

public class ToggleHideMode {

	private static ToggleHideMode instance;

	public static ToggleHideMode getInstance(){
		return instance != null ? instance : (instance = new ToggleHideMode());
	}

	private final UserSet users = UserSet.getInstnace();

	//非表示モードの使用者
	private final HashSet<User> hideModeUsers = new HashSet<>();

	//クールダウン中のユーザー
	private final HashSet<User> cooldownUsers = new HashSet<>();

	private ToggleHideMode(){

	}

	//プレイヤーがログインした時
	public void onPlayerJoin(Player player){
		//ログインしたプレイヤーを全非表示モードの使用者から非表示にする
		forEachHideModeUser(user -> hide(user, player));

		//ユーザーを取得する
		User user = users.getUser(player);
		//非表示モードを使用する設定であればそれを適用する
		if(user.playerSettings.hideMode) applyHideMode(user);
	}

	//プレイヤーがログアウトした時
	public void onPlayerQuit(Player player){
		//ユーザーを取得する
		User user = users.getUser(player);

		//非表示モードの使用者リストから削除する
		hideModeUsers.remove(user);
	}

	public void update(User user) {

		boolean isHideMode = (user.playerSettings.hideMode);

		//非表示モードであれば全プレイヤーを非表示にする
		if(isHideMode) applyHideMode(user);

			//そうでなければ全プレイヤーを表示する
		else applyShowMode(user);

		ControlFunctionalItem.updateSlot(user.asBukkitPlayer(), ItemType.HIDE_MODE_TOGGLER);

	}

	public void change(User user){
		//プレイヤーを取得する
		Player player = user.asBukkitPlayer();

		//クールダウン中なら戻る
		if(cooldownUsers.contains(user)){
			BilingualText.stream("&c-入力が速すぎます", "&c-Input too fast")
			.color()
			.setReceiver(player)
			.sendActionBarMessage();
			return;
		}

		//設定を切り替える
		boolean isHideMode = user.playerSettings.hideMode = !user.playerSettings.hideMode;

		//非表示モードであれば全プレイヤーを非表示にする
		if(isHideMode) applyHideMode(user);

		//そうでなければ全プレイヤーを表示する
		else applyShowMode(user);

		//クールダウンさせる
		cooldownUsers.add(user);

		update(user);

		//0.5秒後にクールダウンを完了させる
		Sync.define(() -> cooldownUsers.remove(user)).executeLater(10);
	}

	public boolean isHideMode(User user){
		return hideModeUsers.contains(user);
	}

	//targetをplayerから非表示にする
	private void show(Player player, Player target){
		player.showPlayer(Main.getPlugin(), target);
	}

	//targetをplayerに表示する
	private void hide(Player player, Player target){
		player.hidePlayer(Main.getPlugin(), target);
	}

	private void applyShowMode(User user){
		//全プレイヤーを表示する
		forEachOnlinePlayer((target) -> show(user.asBukkitPlayer(), target));

		hideModeUsers.remove(user);
	}

	private void applyHideMode(User user){
		//全プレイヤーを非表示にする
		forEachOnlinePlayer((target) -> hide(user.asBukkitPlayer(), target));
		hideModeUsers.add(user);
	}

	//全プレイヤーに対して処理をする
	private void forEachOnlinePlayer(Consumer<Player> processing){
		Bukkit.getOnlinePlayers().forEach(processing::accept);
	}

	//全非表示モード使用者に対して処理をする
	private void forEachHideModeUser(Consumer<Player> processing){
		hideModeUsers.stream().map(User::asBukkitPlayer).forEach(processing::accept);
	}

}
