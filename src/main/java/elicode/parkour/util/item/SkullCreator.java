package elicode.parkour.util.item;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import elicode.parkour.util.text.Text;

public class SkullCreator {

	private static final ItemStack BASE_SKULL = new ItemStack(Material.SKULL_ITEM);

	public static ItemStack fromPlayerUniqueId(UUID uuid){
		return fromOfflinePlayer(Bukkit.getOfflinePlayer(uuid));
	}

	public static ItemStack fromOfflinePlayer(OfflinePlayer player){
		ItemStack skull = BASE_SKULL.clone();

		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		meta.setOwningPlayer(player);

		skull.setItemMeta(meta);

		return skull;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack fromBase64(String base64){
		int hash = base64.hashCode();

		String data = Text.stream("{SkullOwner:{Id:\"$uuid\",Properties:{textures:[{Value:\"$base64\"}]}}}")
				.setAttribute("$uuid", new UUID(hash, hash))
				.setAttribute("$base64", base64)
				.color()
				.toString();

		return Bukkit.getUnsafe().modifyItemStack(BASE_SKULL.clone(), data);
	}

}
