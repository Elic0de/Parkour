package elicode.inventory.ui;

import java.util.function.Function;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public interface InventoryUI extends InventoryHolder {

	Function<Player, InventoryLayout> layout();

	default void open(Player player){
		layout().apply(player);
	}

	@Override
	default Inventory getInventory(){
		throw new UnsupportedOperationException();
	}

}
