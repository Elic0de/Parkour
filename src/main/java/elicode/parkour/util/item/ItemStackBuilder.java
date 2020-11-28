package elicode.parkour.util.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import elicode.parkour.util.enchantment.GleamEnchantment;

public class ItemStackBuilder {

	private final ItemStack basedItemStack;
	private final Material material;
	private int amount = 1;
	private int damage;
	private String displayName;
	private ArrayList<String> lore = new ArrayList<>();
	private Map<Enchantment, Integer> enchantments = new HashMap<>();
	private Set<ItemFlag> flags = new HashSet<>();
	private Consumer<ItemStack> raw;

	public ItemStackBuilder(ItemStack basedItemStack){
		this.basedItemStack = basedItemStack;
		this.material = basedItemStack.getType();
	}

	public ItemStackBuilder(Material material){
		this.basedItemStack = null;
		this.material = material;
	}

	public ItemStackBuilder setAmount(int amount){
		this.amount = amount;
		return this;
	}

	public ItemStackBuilder setDamage(int damage){
		this.damage = damage;
		return this;
	}

	public ItemStackBuilder setDisplayName(String displayName){
		this.displayName = displayName;
		return this;
	}

	public ItemStackBuilder addLore(String text){
		lore.add(text);
		return this;
	}

	public ItemStackBuilder setLore(Collection<String> lore){
		this.lore.clear();
		this.lore.addAll(lore);
		return this;
	}

	public ItemStackBuilder addEnchantment(Enchantment enchantment){
		return addEnchantment(enchantment, 1);
	}

	public ItemStackBuilder addEnchantment(Enchantment enchantment, int level){
		enchantments.put(enchantment, level);
		return this;
	}

	public ItemStackBuilder addFlag(ItemFlag flag){
		flags.add(flag);
		return this;
	}

	public ItemStackBuilder gleam(){
		return addEnchantment(GleamEnchantment.GLEAM_ENCHANTMENT);
	}

	public ItemStackBuilder raw(Consumer<ItemStack> raw){
		this.raw = raw;
		return this;
	}

	public ItemStack build(){
		ItemStack item = basedItemStack != null ? basedItemStack : new ItemStack(material);

		item.setAmount(amount);

		ItemMeta meta = item.getItemMeta();

		if(meta != null){
			//if(meta instanceof Damageable) ((Damageable) meta).setDamage(damage);

			meta.setDisplayName(displayName);
			meta.setLore(lore);

			enchantments.entrySet().forEach(entry -> meta.addEnchant(entry.getKey(), entry.getValue(), true));

			meta.addItemFlags(flags.toArray(new ItemFlag[flags.size()]));

			item.setItemMeta(meta);
		}

		if(raw != null) raw.accept(item);

		return item;
	}

}
