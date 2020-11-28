package elicode.parkour.inventory.ui.dsl.component;

import elicode.parkour.inventory.Apply;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Pagination {

    //改修予定（すべて）TODO

    //デフォルトのスロットに適用する処理
    private Apply<Slot> defaultSlot = (slot) -> {};

    private List<Slot> buttons = new ArrayList<>();
    private HashMap<Integer, Slot> slots = new HashMap<>();
    private int maxSize;
    private int setPer;
    private int square;


    public Pagination(int maxSize,int setPer,int square){
        this.maxSize = maxSize;
        this.setPer = setPer;
        this.square = 9 - (square * 2);

    }

    public boolean hasSpace() {
        return (buttons.size() < this.maxSize);

    }

    public boolean addButton(Slot button) {
        if (!hasSpace()) return false;
        this.buttons.add(button);
        int test = 0;
        int test2 = setPer;
        for(int slotIndex = 0; slotIndex < buttons.size(); slotIndex++){
            if(test >= square) {
                test2 += 9;
                test = 0;
            }
            slots.put(test2+test, buttons.get(slotIndex));
            test++;
        }

        return true;
    }

    public void render(Inventory inventory) {
        if(isEmpty()) return;
        clearAll(inventory);

            int test = 0;
            int test2 = setPer;
            for(int slotIndex = 0; slotIndex < buttons.size(); slotIndex++){
                if(test >= square) {
                    test2 += 9;
                    test = 0;
                }
                inventory.setItem(test2+test, buttons.get(slotIndex).buildIcon().toItemStack());
                test++;
            }
    }
    public void clearAll(Inventory inventory) {
        int test = 0;
        int test2 = setPer;
        for(int slotIndex = 0; slotIndex < maxSize; slotIndex++){
            if(test >= square) {
                test2 += 9;
                test = 0;
            }
            inventory.setItem(test2+test, new ItemStack(Material.AIR));
            test++;
        }
    }

    boolean isEmpty() {
        return buttons.isEmpty();
    }

    public void defaultSlot(Apply<Slot> slotApplier){
        Validate.notNull(slotApplier, "Slot applier can not be null");
        defaultSlot = slotApplier;
    }

    public List<Slot> getButtons() {
        return buttons;
    }

    public Slot getSlotAt(int slotIndex) {
        return getSlots().containsKey(slotIndex) ? getSlots().get(slotIndex) : defaultSlot.apply(new Slot());
    }

    public HashMap<Integer, Slot> getSlots() {
        return slots;
    }

}
