package elicode.parkour.inventory.ui.dsl.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import elicode.parkour.util.enchantment.GleamEnchantment;
import elicode.parkour.inventory.Apply;

public class Icon {

	public ItemStack basedItemStack;
	public Material material = Material.AIR;
	public int amount = 1;
//	public int damage;
	public String displayName;
	public List<String> lore = new ArrayList<>();
	public Map<Enchantment, Integer> enchantments = new HashMap<>();
	public Set<ItemFlag> flags = new HashSet<>();
	public Apply<ItemStack> raw;

	public Icon(){ }

	public Icon(ItemStack basedItemStack){
		this.basedItemStack = basedItemStack;

		material = basedItemStack.getType();
		amount = basedItemStack.getAmount();

		ItemMeta meta = basedItemStack.getItemMeta();

		if(meta != null){
//			damage = meta instanceof Damageable ? ((Damageable) meta).getDamage() : 0;

			displayName = meta.getDisplayName();
			if(meta.hasLore()) lore.addAll(meta.getLore());

			enchantments.putAll(meta.getEnchants());

			flags.addAll(meta.getItemFlags());

			basedItemStack.setItemMeta(meta);
		}
	}

	public ItemStack toItemStack(){
		ItemStack item = basedItemStack != null ? basedItemStack : new ItemStack(material);
		apply(item);
		return item;
	}

	//受け取ったアイテムをアイコンの情報で上書きする
	public void apply(ItemStack item){
		item.setAmount(amount);

		ItemMeta meta = item.getItemMeta();

		if(meta != null){
//			if(meta instanceof Damageable) ((Damageable) meta).setDamage(damage);

			meta.setDisplayName(displayName);
			meta.setLore(lore);

			meta.getEnchants().keySet().forEach(meta::removeEnchant);
			for(Entry<Enchantment, Integer> entry : enchantments.entrySet()) meta.addEnchant(entry.getKey(), entry.getValue(), true);

			meta.addItemFlags(flags.toArray(new ItemFlag[flags.size()]));

			item.setItemMeta(meta);
		}

		if(raw != null)
			raw.apply(item);
	}

	public void lore(String... texts){
		lore = Arrays.asList(texts);
	}

	public void gleam(){
		enchantments.put(GleamEnchantment.GLEAM_ENCHANTMENT, 0);
	}

	public void tarnish(){
		enchantments.remove(GleamEnchantment.GLEAM_ENCHANTMENT);
	}

	public boolean isGleaming(){
		return enchantments.containsKey(GleamEnchantment.GLEAM_ENCHANTMENT);
	}

}
