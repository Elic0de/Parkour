package elicode.inventory.ui;

import org.bukkit.event.inventory.InventoryType;

public class InventoryOption {

	public final InventoryLine line;
	public final InventoryType type;

	public InventoryOption(InventoryLine line){
		this.line = line;
		this.type = null;
	}

	public InventoryOption(InventoryType type){
		this.line = null;
		this.type = type;
	}

	public enum InventoryLine {

		x1,
		x2,
		x3,
		x4,
		x5,
		x6;

		public int size(){
			return (ordinal() + 1) * 9;
		}

		public static InventoryLine necessaryInventoryLine(int size){
			return values()[Math.min((Math.max(size - 1, 0)) / 9, 5)];
		}

	}

}
