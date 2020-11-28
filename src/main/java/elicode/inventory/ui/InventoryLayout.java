package elicode.inventory.ui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class InventoryLayout implements InventoryHolder {

	/*
	 * open -> add
	 * close -> remove
	 * stop -> clear
	 *
	 * 飽く迄これは表示用であってアイテムの入出力機能を伴う物ではない
	 * クリック時にアイテムに変更を加える場合はアイテム自体を再生成する
	 * →アイテムはイミュータブルである
	 */

	public final InventoryOption option;
	public final Player viewer;

	public String title;

	public InventoryLayout(InventoryOption option, Player viewer){
		this.option = option;
		this.viewer = viewer;
	}

	@Override
	public Inventory getInventory() {
		return null;
	}

}
