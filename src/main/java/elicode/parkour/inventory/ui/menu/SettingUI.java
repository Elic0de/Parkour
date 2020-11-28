package elicode.parkour.inventory.ui.menu;

import com.google.common.collect.ImmutableList;
import elicode.parkour.inventory.ui.InventoryLine;
import elicode.parkour.inventory.ui.LocaleFunction;
import elicode.parkour.inventory.ui.dsl.InventoryUI;
import elicode.parkour.inventory.ui.dsl.component.Icon;
import elicode.parkour.inventory.ui.dsl.component.InventoryLayout;
import elicode.parkour.user.PlayerSettings;
import elicode.parkour.user.User;
import elicode.parkour.util.text.BilingualText;
import elicode.parkour.util.tuplet.Quadruple;
import elicode.parkour.util.tuplet.Quintuple;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class SettingUI implements InventoryUI {

    //PlayerSettingを引数に受け取って結果を生成する関数を表す
    private static interface DisplaySetting extends Function<PlayerSettings, Boolean> { };

    //ボタンの構造体を表す
    private static class Button extends Quadruple<Integer, Material, LocaleFunction, Consumer<User>> {

        public Button(Integer slotIndex, Material material, String japanise, String english, Consumer<User> processing) {
            super(slotIndex, material, new LocaleFunction(japanise, english), processing);
        }

    }

    private static class ToggleButton extends Quintuple<Integer, Material, LocaleFunction, DisplaySetting, Consumer<PlayerSettings>> {

        public ToggleButton(Integer slotIndex, Material material, String japanise, String english, DisplaySetting setting, Consumer<PlayerSettings> settingInverter) {
            super(slotIndex, material, new LocaleFunction(japanise, english), setting, settingInverter);
        }

    }


    private static final List<ToggleButton> TOGGLEBUTTONS;
    private static final List<ToggleButton> ICONS;
    static {

        ICONS = ImmutableList.of(
                new ToggleButton(19, Material.SKULL, "プレーヤーの可視性", "Player Visibility",s ->  s.hideMode, s -> s.hideMode = !s.hideMode),
                new ToggleButton(20, Material.POTION, "暗視", "Night Vision",  s -> s.nightVisionMode, s -> s.nightVisionMode = !s.nightVisionMode),
                new ToggleButton(21, Material.SKULL, "チェックエリアのパーティクル","CheckArena Particle", s -> s.particle, s -> s.particle = !s.particle),
                new ToggleButton(22, Material.MAP, "チェックエリアの通知","CheckArena Notification", s -> s.checkArenaNotification, s -> s.checkArenaNotification = !s.checkArenaNotification)
        );

        TOGGLEBUTTONS = ImmutableList.of(

                new ToggleButton(28, Material.SKULL, "プレーヤーの可視性", "Player Visibility", s -> s.hideMode, s -> s.hideMode = !s.hideMode),
                new ToggleButton(29, Material.SIGN, "暗視", "Night Vision", s -> s.nightVisionMode, s -> s.nightVisionMode = !s.nightVisionMode),
                new ToggleButton(30, Material.SKULL, "チェックエリアのパーティクル","CheckArena Particle", s -> s.particle, s -> s.particle = !s.particle),
                new ToggleButton(31, Material.MAP, "チェックエリアの通知","CheckArena Notification", s -> s.checkArenaNotification, s -> s.checkArenaNotification = !s.checkArenaNotification)
        );

    }

    private static final List<Button> BUTTONS;


    static {
        BUTTONS = ImmutableList.of(
                new Button(23, Material.SIGN, "スコアボード設定", "Scoreboard Settings", user -> user.inventoryUserInterfaces.openScoreboardOptionSelectionUI()),
                new Button(24, Material.PAPER, "チャット設定", "Chat Settings", user -> {}),
                new Button(25, Material.BARRIER, "プライバシー設定", "Privacy Setting", user -> {}),

                new Button(32, Material.SKULL, "スコアボード設定", "Scoreboard Settings", user -> user.inventoryUserInterfaces.openScoreboardOptionSelectionUI()),
                new Button(33, Material.SKULL   , "チャット設定", "Chat Settings", user -> {}),
                new Button(34, Material.SKULL, "プライバシー設定", "Privacy Setting", user -> {})

        );

    }


    private final User user;

    public SettingUI(User user){
        this.user = user;
    }

    @Override
    public Function<Player, InventoryLayout> layout() {
        Player player = user.asBukkitPlayer();
        PlayerSettings setting = user.playerSettings;

        return build(InventoryLine.x6, l -> {
            l.title = BilingualText.stream("設定", "Settings")
                    .textBy(player)
                    .toString();

            l.onClose(e -> user.playerSettings.loadScoreboard(user));

            for(Button button : BUTTONS){
                l.put(s -> {

                    s.onClick(e -> button.fourth.accept(user));

                    s.icon(button.second, i -> {
                        i.displayName = "§a" + button.third.apply(player);
                    });

                }, button.first);
            }

            for(ToggleButton button : TOGGLEBUTTONS){
                String buttonName = button.third.apply(player);
                DisplaySetting displaySetting = button.fourth;

                l.put(s -> {
                    s.icon(button.second, i -> applyDisplaySetting(i, buttonName, displaySetting.apply(setting)));

                    s.onClick(e -> {
                        //表示設定を反転させる
                        button.fifth.accept(setting);
                        user.inventoryUserInterfaces.openSettingOptionUI();

                        applyDisplaySetting(e.currentIcon, buttonName, displaySetting.apply(setting));
                    });
                }, button.first);

            }
            for(ToggleButton button : ICONS){
                String buttonName = button.third.apply(player);
                DisplaySetting displaySetting = button.fourth;

                l.put(s -> {
                    s.icon(button.second, i -> applyDisplayIcon(i, buttonName, displaySetting.apply(setting)));

                    s.onClick(e -> {
                        //表示設定を反転させる
                        button.fifth.accept(setting);
                        user.inventoryUserInterfaces.openSettingOptionUI();

                    });
                }, button.first);

            }
        });
    }
    private void applyDisplayIcon(Icon icon, String iconName, boolean display){
        if(!display){
            icon.displayName = "§a" + iconName;
        }else{
            icon.displayName = "§c" + iconName;
        }
    }

    private void applyDisplaySetting(Icon icon, String iconName, boolean display){
        if(!display){
            icon.displayName = "§a" + iconName;
            icon.material = Material.SKULL;
        }else{
            icon.displayName = "§c" + iconName;
            icon.material = Material.SKULL;
        }
    }

}