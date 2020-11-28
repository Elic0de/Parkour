package elicode.parkour.user;

import elicode.parkour.Main;
import elicode.parkour.util.text.Text;
import elicode.parkour.util.text.TextStream;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import elicode.parkour.parkour.Parkour;
import elicode.parkour.schedule.Async;
import elicode.parkour.util.format.TimeFormat;
import elicode.parkour.util.yaml.Yaml;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class TimeAttackProgress implements Runnable{

	//タイムアタック中のユーザー
	public final User user;

	//タイムアタックが行われているパルクール
	public final Parkour parkour;

	//タイムアタックの開始時間
	private long startTime;

	//どのチェックエリアまで進んだか
	private int numberOfLastCheckAreaPassed;

	//経過時間を表示する実行中のタスク
	private BukkitTask taskThatDisplaysElapsedTime;

	private int id;

	public TimeAttackProgress(User user, Parkour parkour){
		this.user = user;
		this.parkour = parkour;
	}

	public void startMeasuringTime(){
		startTime = System.currentTimeMillis();
	}

	public long getStartTime(){
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getElapsedTime(){
		return System.currentTimeMillis() - startTime;
	}

	public int getNumberOfLastCheckAreaPassed(){
		return numberOfLastCheckAreaPassed;
	}

	public void incrementNumberOfLastCheckAreaPassed(){
		numberOfLastCheckAreaPassed++;
	}

	public void runTaskThatDisplaysElapsedTime(){
		//非同期で実行する
		this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), this,0,0);
		run();
		/*taskThatDisplaysElapsedTime = Async.define(() -> {
			*//*TextComponent component = new TextComponent(TimeFormat.format(getElapsedTime()));
			user.asBukkitPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, component);*//*
			Text.stream("$time")
					.setAttribute("$time",TimeFormat.format(getElapsedTime()))
					.setReceiver(user.asBukkitPlayer())
					.sendActionBarMessage();
		}).executeTimer(0);*/
	}

	public long cancelTaskThatDisplaysElapsedTime(){
		stop();
		if(id != -1) {

			stop();
			return System.currentTimeMillis() - startTime;
		}
		/*if(taskThatDisplaysElapsedTime != null) {

			taskThatDisplaysElapsedTime.cancel();
			return System.currentTimeMillis() - startTime;
		}*/
		return System.currentTimeMillis() - startTime;
	}

	public void save(Yaml yaml){
		yaml.set("Time attack progress.Elapsed time", getElapsedTime());
		yaml.set("Time attack progress.Number of last check area passed", numberOfLastCheckAreaPassed);
	}

	@Override
	public void run() {
		Text.stream("$time")
				.setAttribute("$time",TimeFormat.format(getElapsedTime()))
				.setReceiver(user.asBukkitPlayer())
				.sendActionBarMessage();
	}

	public void stop() {
		Bukkit.getScheduler().cancelTask(id);
	}
}
