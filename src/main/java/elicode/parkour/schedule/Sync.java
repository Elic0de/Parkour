package elicode.parkour.schedule;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import elicode.parkour.Main;

public interface Sync extends Runnable {

	public static Sync define(Sync sync){
		return sync;
	}

	public default void execute(){
		Bukkit.getScheduler().runTask(Main.getPlugin(), this);
	}

	public default void executeLater(long delay){
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(), this, delay);
	}

	public default BukkitTask executeTimer(long period, long delay){
		return Bukkit.getScheduler().runTaskTimer(Main.getPlugin(), this, period, delay);
	}

	public default BukkitTask executeTimer(long interval){
		return executeTimer(interval, interval);
	}

}
