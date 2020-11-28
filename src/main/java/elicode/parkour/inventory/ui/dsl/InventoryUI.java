package elicode.parkour.inventory.ui.dsl;

import java.util.function.Function;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import elicode.parkour.inventory.Apply;
import elicode.parkour.inventory.ui.InventoryLine;
import elicode.parkour.inventory.InventoryOption;
import elicode.parkour.inventory.ui.dsl.component.InventoryLayout;

public interface InventoryUI extends InventoryHolder {

	Function<Player, InventoryLayout> layout();

	default void openInventory(Player player){
		player.openInventory(layout().apply(player).buildInventory());
	}

	@Override
	default Inventory getInventory(){
		throw new UnsupportedOperationException("Use InventoryUI#openInvnetory(Player) instead.");
	}

	default Function<Player, InventoryLayout> build(InventoryOption option, Apply<InventoryLayout> applier){
		return LayoutBuilder.build(this, option, applier);
	}

	default Function<Player, InventoryLayout> build(InventoryType type, Apply<InventoryLayout> applier){
		return build(new InventoryOption(null, type), applier);
	}

	default Function<Player, InventoryLayout> build(InventoryLine line, Apply<InventoryLayout> applier){
		return build(new InventoryOption(line, null), applier);
	}

	default Function<Player, InventoryLayout> build(int size, Apply<InventoryLayout> applier){
		return build(InventoryLine.necessaryInventoryLine(size), applier);
	}
	//TODO
	default Function<Player, InventoryLayout> build(InventoryOption option, Apply<InventoryLayout> applier,int maxSize, int itemPer,int square){
		return LayoutBuilder.build(this, option, applier,maxSize,itemPer,square);
	}

	default Function<Player, InventoryLayout> build(InventoryType type, Apply<InventoryLayout> applier ,int maxSize, int itemPer,int square){
		return build(new InventoryOption(null, type), applier,maxSize,itemPer,square);
	}

	default Function<Player, InventoryLayout> build(InventoryLine line, Apply<InventoryLayout> applier, int maxSize, int itemPer,int square){
		return build(new InventoryOption(line, null), applier,maxSize,itemPer,square);
	}

	default Function<Player, InventoryLayout> build(int size, Apply<InventoryLayout> applier, int maxSize, int itemPer,int square){
		return build(InventoryLine.necessaryInventoryLine(size), applier, maxSize,itemPer,square);
	}
}
