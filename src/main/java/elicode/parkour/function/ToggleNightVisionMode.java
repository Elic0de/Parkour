package elicode.parkour.function;

import elicode.parkour.schedule.Sync;
import elicode.parkour.user.User;
import elicode.parkour.user.UserSet;
import elicode.parkour.util.text.BilingualText;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;

public class ToggleNightVisionMode {
    private static ToggleNightVisionMode instance;

    public static ToggleNightVisionMode getInstance(){
        return instance != null ? instance : (instance = new ToggleNightVisionMode());
    }

    private final UserSet users = UserSet.getInstnace();

    //非表示モードの使用者
    private final HashSet<User> hideModeUsers = new HashSet<>();

    //クールダウン中のユーザー
    private final HashSet<User> cooldownUsers = new HashSet<>();

    private ToggleNightVisionMode(){

    }

    //プレイヤーがログインした時
    public void onPlayerJoin(Player player){
        off(player);


        //ユーザーを取得する
        User user = users.getUser(player);

        //非表示モードを使用する設定であればそれを適用する
        if(user.hideMode) applyHideMode(user);
    }

    //プレイヤーがログアウトした時
    public void onPlayerQuit(Player player){
        //ユーザーを取得する
        User user = users.getUser(player);

        //非表示モードの使用者リストから削除する
        hideModeUsers.remove(user);
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
        boolean isHideMode = user.playerSettings.nightVisionMode = !user.playerSettings.nightVisionMode;

        //非表示モードであれば全プレイヤーを非表示にする
        if(isHideMode) applyHideMode(user);

            //そうでなければ全プレイヤーを表示する
        else applyShowMode(user);

        //クールダウンさせる
        cooldownUsers.add(user);

        //0.5秒後にクールダウンを完了させる
        Sync.define(() -> cooldownUsers.remove(user)).executeLater(10);
    }

    public void update(User user) {

        boolean isHideMode = user.playerSettings.nightVisionMode;

        //非表示モードであれば全プレイヤーを非表示にする
        if(isHideMode) applyHideMode(user);

            //そうでなければ全プレイヤーを表示する
        else applyShowMode(user);

    }

    public boolean isNightVisionMode(User user){
        return hideModeUsers.contains(user);
    }

    //targetをplayerから非表示にする
    private void off(Player player){
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
    }

    //targetをplayerに表示する
    private void on(Player player){
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 23423525, -10, false, false));
    }

    private void applyShowMode(User user){
        //全プレイヤーを表示する
         on(user.asBukkitPlayer());

        hideModeUsers.remove(user);
    }

    private void applyHideMode(User user){
        //全プレイヤーを非表示にする
        off(user.asBukkitPlayer());

        hideModeUsers.add(user);
    }

}
