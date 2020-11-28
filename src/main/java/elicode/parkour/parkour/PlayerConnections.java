package elicode.parkour.parkour;

import java.util.*;

import elicode.parkour.util.joor.Reflect;
import org.bukkit.entity.Player;


public class PlayerConnections {

	private final Map<UUID, Reflect> connections = new HashMap<>();

	public void add(Player player){
		UUID uuid = player.getUniqueId();
		Reflect connection = Reflect.on(player).call("getHandle").field("playerConnection");
		connections.put(uuid, connection);
	}

	public void remove(Player player){
		connections.remove(player.getUniqueId());
	}

	public Collection<Reflect> getConnections(){
		return connections.values();
	}

	public boolean isEmpty(){
		return connections.isEmpty();
	}

}
