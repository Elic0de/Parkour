package elicode.parkour.lobby;

import elicode.beta.parkour.location.ImmutableLocation;
import elicode.parkour.util.yaml.Yaml;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lobby {

	public static final Pattern PREFIX_PATTERN = Pattern.compile("(?i)§[0-9a-fA-F]");

	private final LobbySet lobbies;

	public final String name;
	public final String prefixColor;
	public ImmutableLocation spawn;
	public ImmutableLocation origin;


	public Lobby(LobbySet lobbies, Yaml yaml){
		this.lobbies = lobbies;

		//yaml.nameは拡張子を取り除いたファイル名を返すのでアスレ名としてそのまま設定する
		name = yaml.name;

		Matcher prefixColorMatcher = PREFIX_PATTERN.matcher(name);
		prefixColor = prefixColorMatcher.find() ? prefixColorMatcher.group() : "§f";

		origin = ImmutableLocation.deserialize(yaml.getString("Origin"));
		//スポーン地点を設定する
		ImmutableLocation relativeSpawn = ImmutableLocation.deserialize(yaml.getString("Spawn"));
		spawn = origin.add(relativeSpawn);

	}

	public String colorlessName(){
		return ChatColor.stripColor(name);
	}


/*
	public World world(){
		return region.world;
	}

	public ImmutableLocation originLocation(){
		return region.lesserBoundaryCorner;
	}
*/
	public void teleport(Player player){
		player.teleport(spawn.asBukkit());
	}

	public void save(){
		Yaml yaml = LobbySet.getInstance().makeYaml(name);

		yaml.set("Spawn", spawn.serialize());


		yaml.save();
	}

}
