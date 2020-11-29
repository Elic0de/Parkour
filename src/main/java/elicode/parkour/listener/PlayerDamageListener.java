package elicode.parkour.listener;

import elicode.location.ImmutableLocation;
import elicode.parkour.parkour.Parkour;
import elicode.parkour.user.CheckpointSet;
import elicode.parkour.user.User;
import elicode.parkour.user.UserSet;
import elicode.parkour.util.Optional;
import elicode.parkour.util.text.BilingualText;
import elicode.parkour.util.tuplet.Tuple;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamageListener implements Listener {

	private final UserSet users = UserSet.getInstnace();


	@EventHandler
	public void onDamage(EntityDamageEvent event){
		Entity entity = event.getEntity();

		if(!(entity instanceof Player)) return;

		User user = users.getUser((Player) entity);

		switch(event.getCause()){

			case VOID:
				event.setCancelled(true);
				CheckpointSet checkpoints = user.checkpoints;
				Parkour parkour = user.currentParkour;

				if(!user.isOnParkour()) return;

				Optional<Tuple<Integer, ImmutableLocation>> wrappedCheckpoint = checkpoints.getLatestCheckpoint(parkour);

				//チェックポイントが無ければ戻る
				if(!wrappedCheckpoint.isPresent()){

					user.asBukkitPlayer().teleport(parkour.spawn.asBukkit());

					return;
				}
				Tuple<Integer, ImmutableLocation> checkpoint = wrappedCheckpoint.forcedUnwrapping();

				user.asBukkitPlayer().teleport(checkpoint.second.asBukkit());

				return;
		//ダメージをゼロにする
		case CONTACT:
		case HOT_FLOOR:
			event.setDamage(0);
			return;

		//燃焼エフェクトを削除しキャンセルする
		case FIRE:
		case FIRE_TICK:
		case LAVA:
			entity.setFireTicks(0);
			event.setCancelled(true);
			return;

		//キャンセルする
			case SUFFOCATION:
			case DROWNING:
			case ENTITY_ATTACK:
			case FALL:
			case STARVATION:
				event.setCancelled(true);
				return;

		default:
			return;
		}
	}

}
