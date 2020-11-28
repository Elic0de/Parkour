package elicode.parkour.parkour;

import org.bukkit.ChatColor;

public enum RankColor {

	Default(ChatColor.GRAY),
	Update1(ChatColor.GREEN),
	Update2(ChatColor.YELLOW),
	Update3(ChatColor.AQUA),
	Update4(ChatColor.DARK_GREEN),
	Update5(ChatColor.RED),
	Update6(ChatColor.WHITE),
	Update7(ChatColor.DARK_GRAY),
	Update8(ChatColor.DARK_AQUA),
	Update9(ChatColor.DARK_RED),
	Update10(ChatColor.BLUE),
	Update11(ChatColor.LIGHT_PURPLE),
	Update12(ChatColor.DARK_PURPLE),
	Update13(ChatColor.DARK_BLUE);

	public final ChatColor color;

	private RankColor(ChatColor color){
		this.color = color;
	}

}
