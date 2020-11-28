package elicode.parkour.function;

import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLocaleChangeEvent;

import elicode.parkour.function.parkour.ControlFunctionalItem;
import elicode.parkour.schedule.Sync;
import elicode.parkour.user.User;
import elicode.parkour.user.UserSet;

public class PlayerLocaleChange implements Listener {

	public static <T> void applyIfLocaleChanged(T target, long delay, Consumer<T> apply){
		//プレイヤーを取得する
		Player player = target instanceof Player ? (Player) target : (target instanceof User) ? ((User) target).asBukkitPlayer() : null;

		//TがPlayerでもUserでも無ければエラー
		if(player == null) throw new IllegalArgumentException("Target must be Player or User");

		String locale = player.getLocale();

		//5秒後に実行する
		Sync.define(() -> {

			//プレイヤーがオンラインかつ使用言語に変更があれば適用する
			if(player.isOnline() && !player.getLocale().equals(locale)) apply.accept(target);

		}).executeLater(delay);
	}

	@EventHandler
	public void onLocaleChange(PlayerLocaleChangeEvent event){
		//言語が変更された事が分かった上で実行している為これは確実に適用される
		applyIfLocaleChanged(event.getPlayer(), 5, player -> {
			//ホットバーに存在するアイテムの言語表記を更新する
			ControlFunctionalItem.updateAllSlots(player);

			//スコアボードの言語表記を更新する
			UserSet.getInstnace().getUser(player).statusBoard.updateAll();
		});
	}

}
