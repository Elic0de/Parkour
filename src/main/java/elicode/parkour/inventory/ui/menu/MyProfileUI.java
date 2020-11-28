package elicode.parkour.inventory.ui.menu;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import elicode.parkour.Main;
import elicode.parkour.inventory.ui.AbstractUI;
import elicode.parkour.inventory.ui.LocaleFunction;
import elicode.parkour.lobby.Lobby;
import elicode.parkour.lobby.LobbySet;
import elicode.parkour.parkour.ParkourCategory;
import elicode.parkour.util.format.CoinFormat;
import elicode.parkour.util.format.CountFormat;
import elicode.parkour.util.format.TimeFormat;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.ImmutableList;

import elicode.parkour.inventory.ui.InventoryLine;
import elicode.parkour.inventory.ui.dsl.InventoryUI;
import elicode.parkour.inventory.ui.dsl.component.InventoryLayout;
import elicode.parkour.util.item.SkullCreator;
import elicode.parkour.util.text.BilingualText;
import elicode.parkour.util.text.Text;
import elicode.parkour.util.tuplet.Quadruple;
import elicode.parkour.util.tuplet.Triple;
import elicode.parkour.user.User;

public class MyProfileUI extends AbstractMenuUI {

	private static final MenuCategory[] CATEGORIES = MenuCategory.values();

	private final MenuCategory category;

	//ボタンの構造体を表す
	private static class Button extends Quadruple<Integer, Material, LocaleFunction, Consumer<User>> {

		public Button(Integer slotIndex, Material material, String japanise, String english, Consumer<User> processing) {
			super(slotIndex, material, new LocaleFunction(japanise, english), processing);
		}

	}

	//説明文を表す
	private static class LoreBuilder extends Triple<LocaleFunction, String, Function<User, ?>> {

		public LoreBuilder(String japanise, String english, Function<User, ?> status) {
			this(japanise, english, "", status);
		}

		public LoreBuilder(String japanise, String english, String unit, Function<User, ?> status) {
			super(new LocaleFunction(japanise, english), unit, status);
		}

		public String buildBy(User user){
			//"&7-: &b-Updateランク &7-@ &b-$0
			return Text.stream("&7-$name: &e-$value$unit")
					.setAttribute("$name", first.apply(user.asBukkitPlayer()))
					.setAttribute("$value", third.apply(user))
					.setAttribute("$unit", second)
					.color()
					.toString();
		}

	}

	private static final List<Button> BUTTONS;
	private static final List<LoreBuilder> LORE_BUILDERS;

	static{
		BUTTONS = ImmutableList.of(
				//new Button(31, Material.GRASS_BLOCK, "クリエイティブワールド メニュー", "Creative World Menu", user -> user.inventoryUserInterfaces.openCreativeWorldUI()),
				new Button(22, Material.SKULL, "設定", "Settings", user -> user.inventoryUserInterfaces.openSettingOptionUI()),
				new Button(23, Material.EMERALD, "ショップ & 化粧品", "Shop & My Cosmetics", user -> user.inventoryUserInterfaces.openShopUI()),
				//new Button(21, Material.DARK_PRISMARINE_SLAB, "帽子を被る", "Wear Hats", user -> user.inventoryUserInterfaces.openWearHatUI()),
				new Button(45, Material.NETHER_STAR, "ロビーにテレポートする", "Teleport to Lobby", user -> {
				//アスレから退出させる
				user.exitCurrentParkour();


				Player player = user.asBukkitPlayer();

				//本番環境では変える
				LobbySet lobbies = LobbySet.getInstance();
				//Chatcolorは応急処置
				Lobby lobby = lobbies.getLobby(ChatColor.WHITE + "Main");
				//リンクされたロビーのスポーン地点にテレポートさせる
				lobby.teleport(player);

				BilingualText.stream("&b-ロビーにテレポートしました", "&b-You teleported to lobby")
				.color()
				.setReceiver(player)
				.sendActionBarMessage();
			})
		);

		LORE_BUILDERS = ImmutableList.of(
			new LoreBuilder("Updateランク", "Update Rank", user -> user.updateRank()),
			new LoreBuilder("Extendランク", "Extend Rank", user -> user.extendRank()),
				//new LoreBuilder("レベル", "Level", user -> Main.getPlugin().getLevel().getLevel(user.asBukkitPlayer())),
			new LoreBuilder("ジャンプ数", "Jumps", user -> CountFormat.format(user.asBukkitPlayer().getStatistic(Statistic.JUMP))),
			new LoreBuilder("所持コイン数", "Coins", user -> CoinFormat.format(user.coins())),
			new LoreBuilder("総プレイ時間", "Time Played", user -> TimeFormat.format(user.asBukkitPlayer().getStatistic(Statistic.PLAY_ONE_TICK)))
				//new LoreBuilder("カルマ", "Karma", user -> Main.getPlugin().getKarmaPlugin().getPlayerKarma(user.asBukkitPlayer()))
		);
	}

	private final User user;

	public MyProfileUI(User user , MenuCategory category){
		super(user,category,
				l -> {
					Player player = user.asBukkitPlayer();
					l.title = BilingualText.stream("プロフィール", "My Profile")
							.textBy(player)
							.toString();


					//自分のステータス表示
					l.put((s) -> {
						//プレイヤーのスカルヘッドを作成する
						ItemStack skull = SkullCreator.fromPlayerUniqueId(user.uuid);

						s.icon(skull, i -> {

							i.displayName = BilingualText.stream("§aキャラクター情報", "§aCharacter Infomation")
									.textBy(player)
									.toString();

							List<String> lore = LORE_BUILDERS.stream().map(builder -> builder.buildBy(user)).collect(Collectors.toList());
							lore.add(0, "");
							lore.add(3, "");

							i.lore = lore;
						});

					}, 13);

					for(Button button : BUTTONS){
						l.put(s -> {

							s.onClick(e -> button.fourth.accept(user));

							s.icon(button.second, i -> {
								i.displayName = "§a" + button.third.apply(player);
								i.gleam();
							});

						}, button.first);
					}

					l.put((s) -> {
						//プレイヤーのスカルヘッドを作成する
						ItemStack skull = SkullCreator.fromPlayerUniqueId(user.uuid);

						s.icon(skull, i -> {
							i.displayName = player.getDisplayName();

							List<String> lore = LORE_BUILDERS.stream().map(builder -> builder.buildBy(user)).collect(Collectors.toList());
							lore.add(0, "");
							lore.add(3, "");

							i.lore = lore;
						});

					}, 2);

					l.put((s) -> {
						s.onClick(e -> user.inventoryUserInterfaces.openLanguageSelectorUI());
						//プレイヤーのスカルヘッドを作成する
						ItemStack skull = SkullCreator.fromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOThkYWExZTNlZDk0ZmYzZTMzZTFkNGM2ZTQzZjAyNGM0N2Q3OGE1N2JhNGQzOGU3NWU3YzkyNjQxMDYifX19");

						s.icon(skull, i -> i.displayName = BilingualText.stream("§a言語", "§aLanguage")
								.textBy(player)
								.toString());

					}, 32);

				});



		this.user = user;
		this.category = category;
	}
}
