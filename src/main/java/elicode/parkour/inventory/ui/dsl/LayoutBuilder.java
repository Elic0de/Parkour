package elicode.parkour.inventory.ui.dsl;

import java.util.function.Function;

import org.bukkit.entity.Player;

import elicode.parkour.inventory.Apply;
import elicode.parkour.inventory.InventoryOption;
import elicode.parkour.inventory.ui.dsl.component.InventoryLayout;

public class LayoutBuilder {

	public static Function<Player, InventoryLayout> build(InventoryUI ui, InventoryOption option, Apply<InventoryLayout> applier){
		return (player) -> applier.apply(new InventoryLayout(player, ui, option));
	}
	public static Function<Player, InventoryLayout> build(InventoryUI ui, InventoryOption option, Apply<InventoryLayout> applier,int maxSize,int itemPer,int square){
		return (player) -> applier.apply(new InventoryLayout(player, ui, option,maxSize,itemPer,square));
	}

}
