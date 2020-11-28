package elicode.parkour.creative;

public class CreativeWorld {

    public final int id;
    public final int value;


    public CreativeWorld(int id, int value){
        this.id = id;
        this.value = value;
    }

    @Override
    public int hashCode(){
        return id;
    }

}

