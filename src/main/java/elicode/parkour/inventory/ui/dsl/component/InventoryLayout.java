package elicode.parkour.inventory.ui.dsl.component;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import elicode.parkour.inventory.Apply;
import elicode.parkour.inventory.InventoryOption;
import elicode.parkour.inventory.ui.dsl.InventoryUI;
import elicode.parkour.inventory.ui.listener.ClickEvent;
import elicode.parkour.inventory.ui.listener.CloseEvent;
import elicode.parkour.inventory.ui.listener.OpenEvent;
import elicode.parkour.schedule.Async;

public class InventoryLayout {

	//インベントリを開いているプレイヤー
	public final Player player;

	public final InventoryUI ui;
	public final InventoryOption option;

	public Inventory inventory;

	//インベントリのタイトル
	public String title;

	private int page;

	private int maxSize;

	public int itemsPerPage = 0;

	public int square = 0;
	//各スロット
	public final HashMap<Integer, Slot> slots = new HashMap<>();

	//各ページスロット
	private final HashMap<Integer, Slot> pageSlots = new HashMap<>();

	//ページ
	private final SortedMap<Integer, Pagination> pages = new TreeMap<>();

	//デフォルトのスロットに適用する処理
	private Apply<Slot> defaultSlot = (slot) -> {};

	//非同期でクリック処理を実行するかどうか
	public boolean asynchronouslyRunActionOnClick;

	//クリック処理
	private Consumer<ClickEvent> actionOnClick = (event) -> {};

	//非同期でオープン処理を実行するかどうか
	public boolean asynchronouslyRunActionOnOpen;

	//オープン処理
	private Consumer<OpenEvent> actionOnOpen = (event) -> {};

	//非同期でクローズ処理を実行するかどうか
	public boolean asynchronouslyRunActionOnClose;

	//クローズ処理
	private Consumer<CloseEvent> actionOnClose = (event) -> {};

	public InventoryLayout(Player player, InventoryUI ui, InventoryOption option){
		this.player = player;
		this.ui = ui;
		this.option = option;
		this.pages.put(0, new Pagination(this.maxSize, itemsPerPage,square));
	}
	public InventoryLayout(Player player, InventoryUI ui, InventoryOption option,int maxSize,int itemsPerPage,int square){
		this.player = player;
		this.ui = ui;
		this.option = option;
		this.maxSize = (2 * 9) + 3;
		this.pages.put(0, new Pagination(this.maxSize, itemsPerPage,square));
	}

	public Inventory buildInventory(){
		Inventory inventory = createInventory(ui, option, title);
		for(int slotIndex = 0; slotIndex < inventory.getSize(); slotIndex++)
			inventory.setItem(slotIndex, getSlotAt(slotIndex).buildIcon().toItemStack());
		if(!(pages.get(this.page) == null)) this.pages.get(this.page).render(inventory);


		return inventory;
	}
	//TODO
	public void addButton(Slot button) {

		if(pages.get(pages.lastKey().intValue()).addButton(button)) {
			return;
		}

		Pagination page = new Pagination(maxSize, itemsPerPage,square);
		page.addButton(button);
		this.pages.put(this.pages.lastKey().intValue() + 1, page);
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageAmount() {
		return this.pages.size();
	}

	public int getCurrentPage() {
		return page+1;
	}

	public Slot getSlotAt(int slotIndex){
		return slots.containsKey(slotIndex) ? slots.get(slotIndex) : defaultSlot.apply(new Slot());
	}

	public void defaultSlot(Apply<Slot> slotApplier){
		Validate.notNull(slotApplier, "Slot applier can not be null");
		defaultSlot = slotApplier;
	}

	public Slot test(int slot){
		 return	getPages().get(page).getSlotAt(slot);
	}

	public SortedMap<Integer, Pagination> getPages() {
		return pages;
	}

	public void put(Apply<Slot> slotApplier, IntStream range){
		put(slotApplier, range.toArray());
	}

	public void put(Apply<Slot> slotApplier, int... slotIndexes){
		for(int slotIndex : slotIndexes) slots.put(slotIndex, slotApplier.apply(new Slot()));
	}

	public void page(Apply<Slot> slotApplier, IntStream range){
		page(slotApplier, Arrays.stream(range.toArray()));
	}

	public void page(Apply<Slot> slotApplier){
		addButton(slotApplier.apply(new Slot()));
	}
	public void setUpPage(){
		this.pages.put(0, new Pagination(this.maxSize, itemsPerPage,square));
	}

	public void remove(IntStream range){
		remove(range.toArray());
	}

	public void remove(int... slotIndexes){
		for(int slotIndex : slotIndexes) slots.remove(slotIndex);
	}

	public void onClick(Consumer<ClickEvent> action){
		Validate.notNull(action, "Action can not be null");
		actionOnClick = action;
	}

	public void fire(ClickEvent event){
		if(asynchronouslyRunActionOnOpen) Async.define(() -> {
			actionOnClick.accept(event);
		}).execute();
		else {
			actionOnClick.accept(event);
		}
	}

	public void onOpen(Consumer<OpenEvent> action){
		Validate.notNull(action, "Action can not be null");
		actionOnOpen = action;
	}

	public void fire(OpenEvent event){
		if(asynchronouslyRunActionOnOpen) Async.define(() -> {
			actionOnOpen.accept(event);
		}).execute();
		else {
			actionOnOpen.accept(event);
		}
	}

	public void onClose(Consumer<CloseEvent> action){
		Validate.notNull(action, "Action can not be null");
		actionOnClose = action;
	}

	public void fire(CloseEvent event){
		if(asynchronouslyRunActionOnClose) Async.define(() -> actionOnClose.accept(event)).execute();
		else actionOnClose.accept(event);
	}

	private Inventory createInventory(InventoryHolder holder, InventoryOption option, String title){
		int size = option.size;
		InventoryType type = option.type;

		if(option.type == null)
			if(title != null) return Bukkit.createInventory(holder, size, title);
			else return Bukkit.createInventory(holder, size);
		else
			if(title != null) return Bukkit.createInventory(holder, type, title);
			else return Bukkit.createInventory(holder, type);
	}
}
