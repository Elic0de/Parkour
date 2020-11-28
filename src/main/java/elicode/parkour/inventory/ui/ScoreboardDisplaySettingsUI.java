package elicode.parkour.inventory.ui;

import com.google.common.collect.ImmutableList;
import elicode.parkour.inventory.ui.dsl.InventoryUI;
import elicode.parkour.inventory.ui.dsl.component.Icon;
import elicode.parkour.inventory.ui.dsl.component.InventoryLayout;
import elicode.parkour.user.StatusBoardSetting;
import elicode.parkour.user.User;
import elicode.parkour.util.text.BilingualText;
import elicode.parkour.util.tuplet.Quintuple;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ScoreboardDisplaySettingsUI implements InventoryUI {

	//トグルボタンの構造体を表す
	private static class ToggleButton extends Quintuple<Integer, Material, LocaleFunction, DisplaySetting, Consumer<StatusBoardSetting>> {

		public ToggleButton(Integer slotIndex, Material material, String japanise, String english, DisplaySetting setting, Consumer<StatusBoardSetting> settingInverter) {
			super(slotIndex, material, new LocaleFunction(japanise, english), setting, settingInverter);
		}

	}

	//StatusBoardSettingを引数に受け取って結果を生成する関数を表す
	private static interface DisplaySetting extends Function<StatusBoardSetting, Boolean> { };

	private static final List<ToggleButton> BUTTONS;
	private static final List<ToggleButton> ICONS;
	static {
		ICONS = ImmutableList.of(
				new ToggleButton(1, Material.SIGN, "スコアボード", "Scoreboard", s -> s.displayScoreboard, s -> s.displayScoreboard = !s.displayScoreboard),
				new ToggleButton(2, Material.BLACK_GLAZED_TERRACOTTA, "Updateランク", "Update Rank", s -> s.displayUpdateRank, s -> s.displayUpdateRank = !s.displayUpdateRank),
				new ToggleButton(3, Material.BLUE_GLAZED_TERRACOTTA, "Extendランク", "Extend Rank", s -> s.displayExtendRank, s -> s.displayExtendRank = !s.displayExtendRank),
				new ToggleButton(4, Material.GOLD_BOOTS, "ジャンプ数", "Jumps", s -> s.displayJumps, s -> s.displayJumps = !s.displayJumps),
				new ToggleButton(5, Material.GOLD_INGOT, "所持コイン数", "Coins", s -> s.displayCoins, s -> s.displayCoins = !s.displayCoins),
				new ToggleButton(6, Material.SIGN, "トレイサー", "Traceur", s -> s.displayTraceur, s -> s.displayTraceur = !s.displayTraceur),
				new ToggleButton(7, Material.ARROW, "総プレイ時間", "Time Played", s -> s.displayTimePlayed, s -> s.displayTimePlayed = !s.displayTimePlayed),
				new ToggleButton(28, Material.SIGN, "接続プレイヤー数", "Online Players", s -> s.displayOnlinePlayers, s -> s.displayOnlinePlayers = !s.displayOnlinePlayers),
				new ToggleButton(29, Material.SIGN, "遅延", "Ping", s -> s.displayPing, s -> s.displayPing = !s.displayPing),
				new ToggleButton(30, Material.SIGN, "サーバーアドレス", "Server Address", s -> s.displayServerAddress, s -> s.displayServerAddress = !s.displayServerAddress)
		);

		BUTTONS = ImmutableList.of(
				new ToggleButton(10, Material.SIGN, "スコアボード", "Scoreboard", s -> s.displayScoreboard, s -> s.displayScoreboard = !s.displayScoreboard),
				new ToggleButton(11, Material.BLACK_GLAZED_TERRACOTTA, "Updateランク", "Update Rank", s -> s.displayUpdateRank, s -> s.displayUpdateRank = !s.displayUpdateRank),
				new ToggleButton(12, Material.BLUE_GLAZED_TERRACOTTA, "Extendランク", "Extend Rank", s -> s.displayExtendRank, s -> s.displayExtendRank = !s.displayExtendRank),
				new ToggleButton(13, Material.TRAPPED_CHEST, "ジャンプ数", "Jumps", s -> s.displayJumps, s -> s.displayJumps = !s.displayJumps),
				new ToggleButton(14, Material.GOLD_INGOT, "所持コイン数", "Coins", s -> s.displayCoins, s -> s.displayCoins = !s.displayCoins),
				new ToggleButton(15, Material.SIGN, "トレイサー", "Traceur", s -> s.displayTraceur, s -> s.displayTraceur = !s.displayTraceur),
				new ToggleButton(16, Material.TRAPPED_CHEST, "総プレイ時間", "Time Played", s -> s.displayTimePlayed, s -> s.displayTimePlayed = !s.displayTimePlayed),
				new ToggleButton(37, Material.SIGN, "接続プレイヤー数", "Online Players", s -> s.displayOnlinePlayers, s -> s.displayOnlinePlayers = !s.displayOnlinePlayers),
				new ToggleButton(38, Material.SIGN, "遅延", "Ping", s -> s.displayPing, s -> s.displayPing = !s.displayPing),
				new ToggleButton(39, Material.SIGN, "サーバーアドレス", "Server Address", s -> s.displayServerAddress, s -> s.displayServerAddress = !s.displayServerAddress)
		);

	}

	private final User user;

	public ScoreboardDisplaySettingsUI(User user){
		this.user = user;
	}

	@Override
	public Function<Player, InventoryLayout> layout() {
		Player player = user.asBukkitPlayer();
		StatusBoardSetting setting = user.setting;

		return build(elicode.parkour.inventory.ui.InventoryLine.x6, l -> {
			l.title = BilingualText.stream("ステータスボードの表示設定", "Status Board Display Settings")
					.textBy(player)
					.toString();

			l.onClose(e -> user.statusBoard.loadScoreboard());

			for(ToggleButton button : BUTTONS){
				String buttonName = button.third.apply(player);
				DisplaySetting displaySetting = button.fourth;

				l.put(s -> {
					s.icon(button.second, i -> applyDisplaySetting(i, buttonName, displaySetting.apply(setting)));

					s.onClick(e -> {
						//表示設定を反転させる
						button.fifth.accept(setting);
						user.inventoryUserInterfaces.openScoreboardOptionSelectionUI();

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
						user.inventoryUserInterfaces.openScoreboardOptionSelectionUI();

					});
				}, button.first);

			}
		});
	}
	private void applyDisplayIcon(Icon icon, String iconName, boolean display){
		if(display){
			icon.displayName = "§a" + iconName;
		}else{
			icon.displayName = "§c" + iconName;
		}
	}

	private void applyDisplaySetting(Icon icon, String iconName, boolean display){
		if(display){
			icon.displayName = "§a" + iconName;
			icon.material = Material.ARROW;
		}else{
			icon.displayName = "§c" + iconName;
			icon.material = Material.DIAMOND;
		}
	}

}
