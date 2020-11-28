package elicode.parkour.function.parkour;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import elicode.parkour.cosmetics.item.Items;
import elicode.parkour.function.creative.CheckPointItem;
import elicode.parkour.cosmetics.hat.Hats;
import elicode.parkour.util.tuplet.Triple;
import elicode.parkour.util.tuplet.Tuple;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import elicode.parkour.function.PlayerLocaleChange;
import elicode.parkour.listener.PlayerJoinListener;
import elicode.parkour.listener.PlayerQuitListener;
import elicode.parkour.user.User;
import elicode.parkour.user.UserSet;

import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.google.common.collect.ImmutableSet;


public class ControlFunctionalItem implements PlayerJoinListener, PlayerQuitListener {

	private static final Map<Material, Tuple<Integer,FunctionalItem>> ITEMS = new HashMap<>(5);
	private static final Map<Material, Tuple<Integer,FunctionalItem>> CREATIVE_ITEMS = new HashMap<>(5);
	private static final Set<Material> CLICKABLE_MATERIALS;
	private static final ItemStack AIR = new ItemStack(Material.AIR);

	//スロットの構造体を表す
	private static class initializeCre extends Triple<Integer, Material, FunctionalItem> {

		public initializeCre(Integer slotIndex, Material material, FunctionalItem functionalItem) {
			super(slotIndex, material,functionalItem);
		}

	}
	private static final List<initializeCre> CREATIVEITEM;
	private static final List<initializeCre> ITEM;
	static {

		CREATIVEITEM = ImmutableList.of(
				new initializeCre(0, Material.PRISMARINE_SHARD, new CheckPointItem())
		);
		ITEM = ImmutableList.of(
				new initializeCre(0, Material.SADDLE, new CheckpointTeleporter()),
				new initializeCre(2, Material.CARROT_STICK, new CheckpointSelectionUIOpener()),
				new initializeCre(4, Material.MILK_BUCKET, new ParkourSelectionUIOpener()),

				new initializeCre(6, Material.EMERALD, new HideModeToggler()),
				new initializeCre(8, Material.EMERALD, new MyProfileUIOpener())

		);
	}

	static{
		initialize();

		CLICKABLE_MATERIALS = ImmutableSet.copyOf(Arrays.asList(
			Material.NOTE_BLOCK,
			Material.LEVER,
			Material.CHEST,
			Material.TRAPPED_CHEST,
			Material.ENDER_CHEST,
			Material.BLACK_SHULKER_BOX,
			Material.BLUE_SHULKER_BOX,
			Material.BROWN_SHULKER_BOX,
			Material.CYAN_SHULKER_BOX,
			Material.GRAY_SHULKER_BOX,
			Material.GREEN_SHULKER_BOX,
			Material.LIGHT_BLUE_SHULKER_BOX,
			Material.LIME_SHULKER_BOX,
			Material.MAGENTA_SHULKER_BOX,
			Material.ORANGE_SHULKER_BOX,
			Material.PINK_SHULKER_BOX,
			Material.PURPLE_SHULKER_BOX,
			Material.RED_SHULKER_BOX,
			Material.WHITE_SHULKER_BOX,
			Material.YELLOW_SHULKER_BOX,
			Material.STONE_BUTTON,
			Material.ACACIA_FENCE_GATE,
			Material.BIRCH_FENCE_GATE,
			Material.DARK_OAK_FENCE_GATE,
			Material.JUNGLE_FENCE_GATE,
			Material.SPRUCE_FENCE_GATE,
			Material.IRON_TRAPDOOR,
			Material.ACACIA_DOOR,
			Material.BIRCH_DOOR,
			Material.DARK_OAK_DOOR,
			Material.IRON_DOOR,
			Material.JUNGLE_DOOR,
			Material.SPRUCE_DOOR,
			Material.DAYLIGHT_DETECTOR
		));
	}

	private static void initialize(FunctionalItem... items){
		//統一したほうがよさそう
		List<Integer> slotsItemIndex1 = CREATIVEITEM.stream().map(builder -> builder.first).collect(Collectors.toList());
		List<FunctionalItem> functionalItemItems1 = CREATIVEITEM.stream().map(builder -> builder.third).collect(Collectors.toList());
		List<Material> functionalItemMate1 = CREATIVEITEM.stream().map(builder -> builder.second).collect(Collectors.toList());
		for (int slotIndex = 0; slotIndex < CREATIVEITEM.size(); slotIndex++) CREATIVE_ITEMS.put(functionalItemMate1.get(slotIndex), new Tuple<>(slotsItemIndex1.get(slotIndex), functionalItemItems1.get(slotIndex)));

		List<Integer> slotsItemIndex = ITEM.stream().map(builder -> builder.first).collect(Collectors.toList());
		List<FunctionalItem> functionalItemItems = ITEM.stream().map(builder -> builder.third).collect(Collectors.toList());
		List<Material> functionalItemMate = ITEM.stream().map(builder -> builder.second).collect(Collectors.toList());

		for (int slotIndex = 0; slotIndex < ITEM.size(); slotIndex++) ITEMS.put(functionalItemMate.get(slotIndex), new Tuple<>(slotsItemIndex.get(slotIndex), functionalItemItems.get(slotIndex)));


	}


	@EventHandler
	public void onJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();

		initializeSlots(player);

		PlayerLocaleChange.applyIfLocaleChanged(player, 100, p -> ControlFunctionalItem.updateAllSlots(p));
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event){
		initializeSlots(event.getPlayer());
	}

	@EventHandler
	public void clickSlot(PlayerInteractEvent event){
		Action action = event.getAction();

		//アイテムを持った状態でのメインハンドによるクリックでなければ戻る
		if(action == Action.PHYSICAL || event.getHand() != EquipmentSlot.HAND || !event.hasItem()) return;

		//クリックしたプレイヤーを取得する
		Player player = event.getPlayer();

		if(!player.isSneaking() && event.hasBlock()){
			Material material = event.getClickedBlock().getType();

			//クリック可能なアイテムであれば戻る
			if(CLICKABLE_MATERIALS.contains(material)) return;
		}

		//ユーザーを取得する
		User user = toUser(player);

		//クリックされたスロットの番号を取得する
		//ItemStack clickedSlotIndex = player.getInventory().getItemInMainHand();

		Material material = event.getMaterial();
		//対応したアイテムが存在しなければ戻る
		// if(!(ITEMS.containsKey(clickedSlotIndex) || ITEMS.containsKey(clickedSlotIndex))) return;

		ClickType click = action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK ? ClickType.RIGHT : ClickType.LEFT;

		if(material == Material.SKULL){
			//無理矢理実行させているので何らかの形で修正を行いたい
			FunctionalItem item = ITEMS.get(Material.TORCH).second;

			//対応したアイテムでなければ戻る
			if(!item.isSimilar(event.getItem(),user)) return;

			//処理をする
			item.onClick(user, click);

			event.setCancelled(true);
			return;
		}
		Items.ITEMS.forEach(hat -> {
			if(hat.item.equals(material)){
				//無理矢理実行させているので何らかの形で修正を行いたい
				FunctionalItem item = ITEMS.get(Material.PRISMARINE_SHARD).second;

				//対応したアイテムでなければ戻る
				if(!item.isSimilar(event.getItem(),user)) return;

				//処理をする
				item.onClick(user, click);

				event.setCancelled(true);
				return;
			}
		});
		if(ITEMS.containsKey(material)){
			FunctionalItem item = ITEMS.get(material).second;

			//対応したアイテムでなければ戻る
			if(!item.isSimilar(event.getItem(),user)) return;

			//処理をする
			item.onClick(user, click);
		}else if(CREATIVE_ITEMS.containsKey(material)){
			FunctionalItem creativeItem = CREATIVE_ITEMS.get(material).second;
			//対応したアイテムでなければ戻る
			if(!creativeItem.isSimilar(event.getItem(),user)) return;

			//処理をする
			creativeItem.onClick(user,click);
		}else {
			return;
		}

		event.setCancelled(true);
	}

	@EventHandler
	public void controlSlot(InventoryClickEvent event){
		HumanEntity human = event.getWhoClicked();

		//クリックしたのがプレイヤーでなければ戻る
		if(!(human instanceof Player)) return;

		Player player = (Player) human;

		Inventory inventory = event.getClickedInventory();

		//クリックされたのがプレイヤーのインベントリでなければ戻る
		if(!(inventory instanceof PlayerInventory)) return;

		//クリエイティブモードでなければ全ての操作をキャンセルする
		if(player.getGameMode() != GameMode.CREATIVE) event.setCancelled(true);
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event){
		if(event.getPlayer().getGameMode() != GameMode.CREATIVE) event.setCancelled(true);
	}

	@EventHandler
	public void onPickUp(EntityPickupItemEvent event){
		Entity entity = event.getEntity();

		if(entity instanceof Player)
			if(((Player) entity).getGameMode() != GameMode.CREATIVE)
				event.setCancelled(true);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event){
		clearSlots(event.getPlayer());
	}

	public static void initializeSlots(Player player){
		//スロットにアイテムを配置する
		//クリエモードか？
		ITEMS.forEach((material, integerFunctionalItemTuple) -> player.getInventory().setItem(integerFunctionalItemTuple.first, integerFunctionalItemTuple.second.build(toUser(player))));
		CREATIVE_ITEMS.forEach((material, integerFunctionalItemTuple) -> integerFunctionalItemTuple.second.build(toUser(player)));
	}

	public static void updateAllSlots(Player player){
		applyToAllSlots(slotIndex -> updateSlot(player, slotIndex));
	}

	public static void updateSlot(Player player, ItemType type){
		int slotIndex = type.slotIndex;
		Material mate = type.mate;

		//対応したアイテムが存在すればそれを再配置する
		if(ITEMS.containsKey(mate)) player.getInventory().setItem(slotIndex, ITEMS.get(mate).second.build(toUser(player)));
	}

	public static void clearSlots(Player player){
		applyToAllSlots(type -> player.getInventory().setItem(type.slotIndex, AIR));
	}

	private static void applyToAllSlots(Consumer<ItemType> apply){
		for(ItemType type : ItemType.values()) apply.accept(type);
	}

	private static User toUser(Player player){
		return UserSet.getInstnace().getUser(player);
	}

}
