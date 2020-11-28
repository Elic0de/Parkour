package elicode.parkour.inventory;

import elicode.parkour.inventory.ui.InventoryLine;
import org.bukkit.event.inventory.InventoryType;

public class InventoryOption {

	public final int size;
	public final InventoryType type;

	public InventoryOption(InventoryLine line, InventoryType type){
		this.size = line != null ? line.inventorySize() : 0;
		this.type = type;
	}

}
