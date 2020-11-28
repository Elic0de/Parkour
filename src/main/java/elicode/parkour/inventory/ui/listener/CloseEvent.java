package elicode.parkour.inventory.ui.listener;

import org.bukkit.event.inventory.InventoryCloseEvent;

import elicode.parkour.inventory.ui.dsl.component.InventoryLayout;

public class CloseEvent extends UIEvent {

	public CloseEvent(InventoryLayout layout, InventoryCloseEvent event){
		super(layout, event.getPlayer(), event);
	}

}
