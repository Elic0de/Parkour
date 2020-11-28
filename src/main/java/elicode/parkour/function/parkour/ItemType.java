package elicode.parkour.function.parkour;

import org.bukkit.Material;

public enum ItemType {

	CHERCKPOINT_TELEPORTER(0,Material.CARROT_STICK,null),
	CHECKPOINT_ITEM(0,Material.SADDLE,null),
	CHECKPOINT_SELECTION_UI_OPENER(2,Material.CARROT_STICK,null),
	PARKOUR_SELECTION_UI_OPENER(4,Material.MILK_BUCKET,null),
	HIDE_MODE_TOGGLER(6,Material.SIGN,Material.STONE_SLAB2),
	MY_PROFILE_UI_OPENER(8,Material.EMERALD,null);

	public final int slotIndex;
	public final Material mate;
	public final Material mate2;

	private ItemType(int slotIndex,Material mate, Material mate2){
		this.slotIndex = slotIndex;
		this.mate = mate;
		this.mate2 = mate2;
	}

}
