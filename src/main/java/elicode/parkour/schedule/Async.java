package elicode.parkour.schedule;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import elicode.parkour.Main;

public interface Async extends Runnable {

	public static Async define(Async async){
		return async;
	}

	public default BukkitTask execute(){
		return Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), this);
	}

	public default BukkitTask executeLater(long delay){
		return Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getPlugin(), this, delay);
	}

	public default BukkitTask executeTimer(long period, long delay){
		return Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getPlugin(), this, period, delay);
	}

	public default BukkitTask executeTimer(long interval){
		return executeTimer(interval, interval);
	}

}
