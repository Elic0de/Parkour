package elicode.parkour.parkour;

import elicode.parkour.Main;
import elicode.parkour.util.item.SkullCreator;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public enum ParkourCategory {

	NORMAL(Material.SEEDS, Material.STAINED_GLASS_PANE),
	SEGMENT(Material.REDSTONE, Material.STAINED_GLASS_PANE),
	BIOME(Material.REDSTONE_TORCH_ON, Material.STAINED_GLASS_PANE),
	UPDATE(Material.DIAMOND, Material.STAINED_GLASS_PANE),
	EXTEND(Material.GOLD_INGOT, Material.STAINED_GLASS_PANE);

	public final String name;
	public final Material icon;
	public final Material line;

	private ParkourCategory(Material icon, Material line){
		this.name = toString().charAt(0) + toString().substring(1).toLowerCase();
		this.icon = icon;
		this.line = line;
	}

}
