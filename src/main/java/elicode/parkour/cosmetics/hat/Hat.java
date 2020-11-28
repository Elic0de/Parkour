package elicode.parkour.cosmetics.hat;

import org.bukkit.Material;

public class Hat {

	public final int id;
	public final int value;
	public final String name;
	public final Material item;

	public Hat(int id, int value, String name, Material item){
		this.id = id;
		this.value = value;
		this.name = name;
		this.item = item;
	}

	@Override
	public int hashCode(){
		return id;
	}

}
