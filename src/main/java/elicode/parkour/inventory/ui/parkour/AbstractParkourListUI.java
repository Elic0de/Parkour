package elicode.parkour.inventory.ui.parkour;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import com.google.common.collect.ImmutableList;
import elicode.parkour.Main;
import elicode.parkour.game.GameState;
import elicode.parkour.game.games.PartyFight;
import elicode.parkour.inventory.ui.LocaleFunction;
import elicode.parkour.inventory.ui.menu.MenuCategory;
import elicode.parkour.inventory.ui.menu.MyProfileUI;
import elicode.parkour.lobby.Lobby;
import elicode.parkour.lobby.LobbySet;
import elicode.parkour.parkour.*;
import elicode.parkour.party.Party;
import elicode.parkour.schedule.Sync;
import elicode.parkour.util.format.CoinFormat;
import elicode.parkour.util.text.Text;
import elicode.parkour.util.tuplet.Quadruple;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import elicode.parkour.inventory.ui.InventoryLine;
import elicode.parkour.inventory.ui.dsl.InventoryUI;
import elicode.parkour.inventory.ui.dsl.component.InventoryLayout;
import elicode.parkour.util.text.BilingualText;
import elicode.parkour.util.text.TextStream;
import elicode.parkour.util.tuplet.Tuple;
import elicode.parkour.user.User;
import elicode.parkour.user.UserSet;
import elicode.parkour.util.format.TimeFormat;

public abstract class AbstractParkourListUI<T extends Parkour> implements InventoryUI {

	private static final ParkourCategory[] CATEGORIES = ParkourCategory.values();

	private final UserSet users = UserSet.getInstnace();

	private final User user;
	private final ParkourCategory category;
	private final Supplier<List<T>> parkours;
	private final Function<List<T>, InventoryLine> line;
	private final Consumer<InventoryLayout> raw;
	private int page = 0;
	private final boolean duel;
	//クールダウン中のユーザー
	private final HashSet<User> cooldownUsers = new HashSet<>();


	public AbstractParkourListUI(User user, ParkourCategory category, Supplier<List<T>> parkours, Function<List<T>, InventoryLine> line, Consumer<InventoryLayout> raw,boolean duel){
		this.user = user;
		this.category = category;
		this.parkours = parkours;
		this.line = line;
		this.raw = raw;
		this.duel = duel;
	}

	@Override
	public Function<Player, InventoryLayout> layout() {
		//カテゴリー名を取得する
		String categoryName = category.name;

		Parkour currentParkour = user.currentParkour;
		//UI上に表示可能なアスレリストを取得する
		List<T> parkours = this.parkours.get();

		InventoryLine line = this.line.apply(parkours);

		Player player = user.asBukkitPlayer();

		return build(line, l -> {
			String title = this.page + 1 > 1 ? Text.stream(categoryName + " (Page $page)")
					.setAttribute("$page", this.page + 1)
					.textBy(player)
					.toString() : Text.stream(categoryName)
					.textBy(player)
					.toString();

			l.title = title;
			l.itemsPerPage = 19;
			l.square = 1;
			l.setPage(page);

			IntStream.range(0, 9)
					.sorted()
					.forEach(index -> l.put(s -> s.icon(category.line, i -> {
						i.displayName = " ";

					}), index + 9));


			AtomicInteger counter = new AtomicInteger();

			IntStream.range(0, 5)
					.sorted()
					.forEach(index -> {
						//対応したカテゴリーを取得する
						ParkourCategory category = CATEGORIES[counter.getAndIncrement()];

						l.put(s -> {

							s.onClick(e -> user.inventoryUserInterfaces.openParkourSelectionUI(category));

							s.icon(category.icon, i -> {
								i.displayName = "§b" + category.name;

								//今開いているカテゴリーと同じであれば輝かせる
								if(category == this.category) ;
							});

						}, index + 2);
					});
			//parkours.sort(Comparator.comparingInt(parkours -> parkours.get));

			/*IntStream.range(0, 9)
					.sorted()
					.forEach(index -> l.put(s -> s.icon(Material.WHITE_STAINED_GLASS_PANE, i -> {
						i.displayName = " ";

					}), index));*/


			for(int index = 0; index < parkours.size(); index++){
				Parkour parkour = parkours.get(index);

				String parkourName = parkour.name;

				//改修予定 TODO
				l.page(s -> {

					s.onClick(e -> {

						Party party1 = Main.getPlugin().getPartyManager().getParty(player.getUniqueId());
						if (!(party1 == null)) {
							if (!(party1.isInParty(player))) return;
							if (party1.isOwner(player)) {
								if (party1.getMembers().size() < 1) return;
								if (user.getPlayingGame() == null) {
									PartyFight ffa = new PartyFight(party1, parkour);
								}else {
									if (user.getPlayingGame().getGameState() != GameState.COUNTDOWN) {
										System.out.println("do");
										PartyFight ffa = new PartyFight(party1, parkour);
									}
								}
							}
							return;
						}




							if (duel) {
								if (cooldownUsers.contains(user)) {
									BilingualText.stream("&c-入力が速すぎます", "&c-Input too fast")
											.color()
											.setReceiver(player)
											.sendChatMessage();
									return;
								}
								if (Main.getPlugin().getRequestManager().get(player, false) != null) {
									//send method
									//クールダウンさせる
									cooldownUsers.add(user);
									Main.getPlugin().getRequestManager().get(player, false).forEach((uuid, request) -> request.setParkour(parkour));
									Main.getPlugin().getRequestManager().send(player);

								} else {
									//クールダウンさせる
									cooldownUsers.add(user);
									Main.getPlugin().getRequestManager().get(player, false).remove(player.getUniqueId());
								}
								//10秒後にクールダウンを完了させる
								Sync.define(() -> cooldownUsers.remove(user)).executeLater(200 );
								return;
							}




						if (parkour instanceof RankUpParkour) {
							ParkourCategory category = parkour.category;

							//アスレのランクを取得する
							int rank = ((RankUpParkour) parkour).rank;

							//各タイプで分岐する
							switch(category){
								case UPDATE:
									//プレイヤーのランクの方が高ければ戻る
									if(!(rank <= user.updateRank() + 1)) return;

									parkour.teleport(user);

									//アスレに参加させる
									parkour.entry(users.getUser(player));

									BilingualText.stream("$color$parkour&r-にテレポートしました", "&r-You teleported to $color$parkour")
											.setAttribute("$parkour", parkourName)
											.setAttribute("$color", parkour.prefixColor)
											.color()
											.setReceiver(player)
											.sendActionBarMessage();
									break;
								case EXTEND:
									//プレイヤーのランクの方が高ければ戻る
									if(!(rank <= user.extendRank() + 1)) return;

									parkour.teleport(user);

									//アスレに参加させる
									parkour.entry(users.getUser(player));

									BilingualText.stream("$color$parkour&r-にテレポートしました", "&r-You teleported to $color$parkour")
											.setAttribute("$parkour", parkourName)
											.setAttribute("$color", parkour.prefixColor)
											.color()
											.setReceiver(player)
											.sendActionBarMessage();
									break;
								default:
									throw new NullPointerException("Ranked parkour type can not be null");
							}

						}else {
							parkour.teleport(user);

							//アスレに参加させる
							parkour.entry(users.getUser(player));

							BilingualText.stream("$color$parkour&r-にテレポートしました", "&r-You teleported to $color$parkour")
									.setAttribute("$parkour", parkourName)
									.setAttribute("$color", parkour.prefixColor)
									.color()
									.setReceiver(player)
									.sendActionBarMessage();
						}
					});

					//アスレのアイコンを設定する
					//クリア済みのアスレであれば変更する
					s.icon(user.clearedParkourNames.contains(parkourName) ? Material.PRISMARINE_SHARD: Material.DIAMOND, i -> {
						i.displayName = parkourName;

						List<String> lore = new ArrayList<>();

						//アスレの最大メジャーチェックエリア番号を取得する
						int maxMajorCheckAreaNumber = parkour.checkAreas.getMaxMajorCheckAreaNumber();

						/*TextStream numberOfCheckAreasForDisplay;
						if(maxMajorCheckAreaNumber >= 0) numberOfCheckAreasForDisplay = BilingualText.stream("$size-&7-箇所", "$size").setAttribute("$size", maxMajorCheckAreaNumber + 1);
						else numberOfCheckAreasForDisplay = BilingualText.stream("無し", "None");

						lore.add(
							BilingualText.stream("&7-チェックエリア @ &b-$size", "&7-Check Areas @ &b-$size")
							.textBy(player)
							.setAttribute("$size", numberOfCheckAreasForDisplay.textBy(player).toString())
							.color()
							.toString()
						);*/

						Rewards rewards = parkour.rewards;
						/*lore.add(
							BilingualText.stream("&7-初回/通常報酬 @ &b-$first-&7-/-&b-$after-&7-コイン",
									"&7-First/Normal Reward @ &b-$first-&7-/-&b-$after &7-Coins")
									.textBy(player)
									.setAttribute("$first", rewards.getReward(0))
									.setAttribute("$after", rewards.getReward(1))
									.color()
									.toString()
						);*/
						Difficulty difficulty = parkour.difficulty;
						lore.add(
								BilingualText.stream("&7-難易度 @ &b-$first-&7-/-&b-$after",
										"&7-Difficulty @ &b-$first-&7-/-&b-$after")
										.textBy(player)
										.setAttribute("$first", difficulty.getDifficulty(0))
										.setAttribute("$after", difficulty.getDifficulty(1))
										.color()
										.toString()
						);

						boolean timeAttackEnable = parkour.timeAttackEnable;

						TextStream textOfTimeAttackEnable;
						/*if(timeAttackEnable) textOfTimeAttackEnable = BilingualText.stream("&b-有効", "&b-Enable");
						else textOfTimeAttackEnable = BilingualText.stream("&7-無効", "&7-Disable");

						lore.add(
							BilingualText.stream("&7-タイムアタック @ $enable", "&7-Time Attack @ $enable")
							.textBy(player)
							.setAttribute("$enable", textOfTimeAttackEnable.textBy(player).toString())
							.color()
							.toString()
						);*/

						String description = parkour.description;

						//説明文が存在すれば行を1つ空けてから追加する
						if(description != null && !description.isEmpty()){
							lore.add("");
							lore.add(description);
						}

						if(timeAttackEnable){
							Records records = parkour.records;

							//上位の記録を取得する
							List<Tuple<UUID, String>> topTenRecords = records.topTenRecords;

							int size = topTenRecords.size();

							if(!(topTenRecords.size() <= 5)) size = 5;
							//記録が存在する場合
							if(!topTenRecords.isEmpty()){
								lore.add("");

								lore.add(
									BilingualText.stream("&7-上位-&b-$size-&7-件の記録", "&7-Top &b-$size-&7 Records")
									.textBy(player)
									.setAttribute("$size", size)
									.color()
									.toString()
								);

								AtomicInteger rank = new AtomicInteger(1);

								topTenRecords.stream()
								.limit(5)
								.map(record ->
									BilingualText.stream("&b-$rank-&7-位 &b-$name &7-@ &b-$time", "&b-$rank-&7-. &b-$name &7-@ &b-$time")
									.textBy(player)
									.setAttribute("$rank", rank.getAndIncrement())
									.setAttribute("$name", Bukkit.getOfflinePlayer(record.first).getName())/*Bukkit.getOfflinePlayer(record.first).getName()*/
											.setAttribute("$time", record.second)
									.color()
									.toString()
								)
								.forEach(lore::add);

								UUID uuid = user.uuid;

								//記録を保有しているのであれば自己最高記録を表示する
								if(records.containsRecord(uuid)){
									lore.add("");

									String time = TimeFormat.format(records.personalBest(uuid));

									lore.add(
										BilingualText.stream("&7-自己最高記録 @ &b-$time", "&7-Personal Best @ &b-$time")
										.textBy(player)
										.setAttribute("$time", time)
										.color()
										.toString()
									);
								}
							}
						}

						lore.add("");
						lore.add(
							BilingualText.stream("&e-クリックするとテレポートします！", "&e-Click to teleport!")
							.textBy(player)
							.color()
							.toString()
						);

						i.lore = lore;
						if(parkour.equals(currentParkour)) i.gleam();

					});

				});



				if (l.getCurrentPage() < l.getPageAmount()) {
					l.put((s) -> {

						s.onClick(e -> {
							this.page ++;
							user.inventoryUserInterfaces.openParkourSelectionUI(category);
						});

						s.icon(Material.ARROW, i -> i.displayName = BilingualText.stream("§a次のページ", "§aNext Page")
								.textBy(player)
								.toString());

					}, 35);
				}
				if (l.getCurrentPage() > 1) {
					l.put((s) -> {

						s.onClick(e -> {
							this.page --;
							user.inventoryUserInterfaces.openParkourSelectionUI(category);
						});

						s.icon(Material.ARROW, i -> i.displayName = BilingualText.stream("§a前のページ", "§aPrevious Page")
								.textBy(player)
								.toString());

					}, 27);
				}
				l.put((s) -> {

					s.onClick(e -> {
						if(duel) {
							user.inventoryUserInterfaces.openDuelSelectorUI();
						}else {
							player.closeInventory();
						}
					});

					s.icon(Material.BARRIER, i -> i.displayName = BilingualText.stream("§a閉じる", "§aClose")
							.textBy(player)
							.toString());
				}, 49);
			}


			raw.accept(l);
		},21,19,1);
	}

}
