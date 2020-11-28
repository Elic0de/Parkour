package elicode.parkour.cosmetics.item;

import org.bukkit.Material;

//TODO 親クラスを作る

public class Item {

    public final int id;
    public final int value;
    public final String name;
    public final Material item;

    public Item(int id, int value, String name, Material item){
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
