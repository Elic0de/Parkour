package elicode.parkour.inventory.ui.listener;

import org.bukkit.event.inventory.InventoryOpenEvent;

import elicode.parkour.inventory.ui.dsl.component.InventoryLayout;

public class OpenEvent extends UIEvent {

	public OpenEvent(InventoryLayout layout, InventoryOpenEvent event){
		super(layout, event.getPlayer(), event);
	}

}
