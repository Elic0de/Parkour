package elicode.parkour.inventory.ui.listener;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;

import elicode.parkour.inventory.ui.dsl.InventoryUI;
import elicode.parkour.inventory.ui.dsl.component.Icon;
import elicode.parkour.inventory.ui.dsl.component.InventoryLayout;
import elicode.parkour.inventory.ui.dsl.component.Slot;

public class UIListener implements Listener {

	public UIListener(){

	}

	@EventHandler
	public void onClick(InventoryClickEvent event){
		Inventory displayed = event.getInventory();
		Inventory clicked = event.getClickedInventory();

		InventoryLayout layout = getLayout(displayed != null && clicked == null ? displayed.getHolder() : clicked.getHolder(), event.getWhoClicked());

		if(layout == null)
			return;

		ClickEvent clickEvent = new ClickEvent(layout, event);

		Slot slot1 = layout.test(event.getSlot());
		Slot slot = layout.getSlotAt(event.getSlot());
		event.setCancelled(!slot1.editable);
		event.setCancelled(!slot.editable);


		slot.fire(clickEvent);
		slot1.fire(clickEvent);
		layout.fire(clickEvent);

		Icon currentIcon = clickEvent.currentIcon;
		if(currentIcon != null) currentIcon.apply(event.getCurrentItem());

		Icon cursorIcon = clickEvent.cursorIcon;
		if(cursorIcon != null) cursorIcon.apply(event.getCursor());
	}

	@EventHandler
	public void onOpen(InventoryOpenEvent event){
		InventoryLayout layout = getLayout(event.getInventory().getHolder(), event.getPlayer());

		if(layout != null)
			layout.fire(new OpenEvent(layout, event));
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event){
		InventoryLayout layout = getLayout(event.getInventory().getHolder(), event.getPlayer());

		if(layout != null)
			layout.fire(new CloseEvent(layout, event));
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event){
		Player player = event.getPlayer();
		InventoryView opened = player.getOpenInventory();
		if(opened == null)
			return;

		InventoryLayout layout;
		Inventory top = opened.getTopInventory();
		if(top != null){
			layout = getLayout(top.getHolder(), player);
			if(layout != null){
				player.closeInventory();
				return;
			}
		}

		Inventory bottom = opened.getBottomInventory();
		if(bottom != null){
			layout = getLayout(bottom.getHolder(), player);
			if(layout != null)
				player.closeInventory();
		}
	}

	public static InventoryLayout getLayout(InventoryHolder holder, HumanEntity human){
		return holder instanceof InventoryUI && human instanceof Player ? ((InventoryUI) holder).layout().apply((Player) human) : null;
	}
}
