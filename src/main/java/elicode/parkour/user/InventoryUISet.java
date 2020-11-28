package elicode.parkour.user;

import java.util.HashMap;

import elicode.parkour.inventory.ui.menu.MenuCategory;
import elicode.parkour.inventory.ui.*;
import elicode.parkour.inventory.ui.menu.*;
import elicode.parkour.inventory.ui.menu.channel.ChannelUI;
import elicode.parkour.inventory.ui.menu.channel.ChannelsUI;
import elicode.parkour.inventory.ui.parkour.*;
import org.bukkit.entity.Player;

import elicode.parkour.inventory.ui.dsl.InventoryUI;
import elicode.parkour.parkour.ParkourCategory;

public class InventoryUISet {

	//改修予定

	private static final ParkourCategory[] COMMON_CATEGORIES = new ParkourCategory[]{ParkourCategory.NORMAL, ParkourCategory.SEGMENT, ParkourCategory.BIOME};
	private static final MenuCategory[] MENU_CATEGORIES = new MenuCategory[]{MenuCategory.MYPROFILE,MenuCategory.FRIENDS,MenuCategory.PARTY,MenuCategory.CHANNEL,MenuCategory.RESENT};

	private final Player player;
	private final InventoryUI lastCheckpointSelectionUI;
	private final InventoryUI latestCheckpointSelectionUI;
	private final ScoreboardDisplaySettingsUI scoreboardOptionSelectionUI;
	private final InventoryUI buyHatUI;
	private final InventoryUI wearHatUI;
	private final InventoryUI cretiveWorldUI;
	private final InventoryUI shopUI;
	private final InventoryUI settingUI;
	private final InventoryUI channelsUI;
	private final InventoryUI parkourCategoryUI;
	private final InventoryUI rankUpUI;
	private final InventoryUI duelSelectionUI;
	private final InventoryUI duelRankUpUI;
	private final InventoryUI gameSelectorUI;
	private final InventoryUI itemUI;
	private final InventoryUI languageSelector;
	private final HashMap<ParkourCategory, InventoryUI> parkourSelectionUIs = new HashMap<>(5);
	private final HashMap<ParkourCategory, InventoryUI> duelSelectionUIs = new HashMap<>(5);
	private final HashMap<MenuCategory, InventoryUI> myProfileUI = new HashMap<>(5);

	public InventoryUISet(User user){
		player = user.asBukkitPlayer();

		lastCheckpointSelectionUI = new LastCheckpointListUI(user);
		latestCheckpointSelectionUI = new LatestCheckpointListUI(user);
		rankUpUI = new RankUpUI(user, false);
		duelRankUpUI = new RankUpUI(user, true);

		scoreboardOptionSelectionUI = new ScoreboardDisplaySettingsUI(user);
		parkourCategoryUI = new ParkourCategoryUI(user);
		buyHatUI = new BuyHatUI(user);
		wearHatUI = new WearHatUI(user);
		itemUI = new ItemUI(user);
		cretiveWorldUI = new CreativeWorldUI(user);
		shopUI = new ShopUI(user);
		settingUI = new SettingUI(user);
		channelsUI = new ChannelsUI(user,MenuCategory.CHANNEL);
		gameSelectorUI = new GameSelectorUI(user);
		languageSelector = new LanguageSelectorUI(user);
		duelSelectionUI = new DuelCategoryUI(user);
		for(ParkourCategory category : COMMON_CATEGORIES) parkourSelectionUIs.put(category, new CommonParkourListUI(user, category,false));
		for(ParkourCategory category : COMMON_CATEGORIES) duelSelectionUIs.put(category, new CommonParkourListUI(user, category,true));

		myProfileUI.put(MenuCategory.MYPROFILE, new MyProfileUI(user, MenuCategory.MYPROFILE));
		myProfileUI.put(MenuCategory.FRIENDS, new FriendsUI(user, MenuCategory.FRIENDS));
		myProfileUI.put(MenuCategory.PARTY, new PartyUI(user, MenuCategory.PARTY));
		myProfileUI.put(MenuCategory.CHANNEL, new ChannelUI(user, MenuCategory.CHANNEL));
		myProfileUI.put(MenuCategory.RESENT, new ResentUI(user, MenuCategory.RESENT));

		parkourSelectionUIs.put(ParkourCategory.UPDATE, new RankUpParkourListUI(user, ParkourCategory.UPDATE, () -> user.updateRank(),false));
		parkourSelectionUIs.put(ParkourCategory.EXTEND, new RankUpParkourListUI(user, ParkourCategory.EXTEND, () -> user.extendRank(),false));

		duelSelectionUIs.put(ParkourCategory.UPDATE, new RankUpParkourListUI(user, ParkourCategory.UPDATE, () -> user.updateRank(),true));
		duelSelectionUIs.put(ParkourCategory.EXTEND, new RankUpParkourListUI(user, ParkourCategory.EXTEND, () -> user.extendRank(),true));
	}

	public void openMyProfileUI(MenuCategory category){
		open(myProfileUI.get(category));
	}

	public void openShopUI() {
		open(shopUI);
	}

	public void openGameSelectorUI() {
		open(gameSelectorUI);
	}

	public void openItemUI() {
		open(itemUI);
	};

	public void openLanguageSelectorUI() {
		open(languageSelector);
	}

	public void openRankUpUI() {
		open(rankUpUI);
	}

	public void openDuelRankUpUI() {
		open(duelRankUpUI);
	}

	public void openDuelSelectorUI() {
		open(duelSelectionUI);
	}

	public void openParkourCategoryUI() {
		open(parkourCategoryUI);
	}

	public void openChannelsUI(){
		open(channelsUI);
	}

	public void openCreativeWorldUI(){
		open(cretiveWorldUI);
	}

	public void openLastCheckpointSelectionUI(){
		open(lastCheckpointSelectionUI);
	}

	public void openLatestCheckpointSelectionUI(){
		open(latestCheckpointSelectionUI);
	}

	public void openScoreboardOptionSelectionUI(){
		open(scoreboardOptionSelectionUI);
	}

	public void openSettingOptionUI() {
		open(settingUI);
	}

	public void openBuyHatUI(){
		open(buyHatUI);
	}

	public void openWearHatUI(){
		open(wearHatUI);
	}

	public void openParkourSelectionUI(ParkourCategory category){
		open(parkourSelectionUIs.get(category));
	}

	public void openDuelSelectionUI(ParkourCategory category){
		open(duelSelectionUIs.get(category));
	}

	private void open(InventoryUI ui){
		ui.openInventory(player);
	}
}
