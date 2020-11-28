package elicode.parkour;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import elicode.parkour.command.commands.tags.TagsCommand;
import elicode.parkour.util.command.AbstractCommand;
import elicode.parkour.util.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import elicode.parkour.command.Arguments;
import elicode.parkour.command.Command;
import elicode.parkour.command.Sender;
import elicode.parkour.listener.PlayerJoinListener;
import elicode.parkour.listener.PlayerQuitListener;
import elicode.parkour.util.Reflection;

import javax.annotation.Nonnull;

public class Plugin extends JavaPlugin implements Listener {

	private final Map<String, AbstractCommand<Main>> commands2 = new HashMap<>();
	private final HashMap<String, Command> commands = new HashMap<>();
	private final ArrayList<PlayerJoinListener> joinListeners = new ArrayList<>();
	private final ArrayList<PlayerQuitListener> quitListeners = new ArrayList<>();

	@Override
	public void onEnable(){
		for(Player player : getServer().getOnlinePlayers()){
			PlayerJoinEvent event = new PlayerJoinEvent(player, "");
			for(PlayerJoinListener listener : joinListeners)
				listener.onJoin(event);
		}
	}

	@Override
	public void onDisable(){
		for(Player player : getServer().getOnlinePlayers()){
			PlayerQuitEvent event = new PlayerQuitEvent(player, "");
			for(PlayerQuitListener listener : quitListeners)
				listener.onQuit(event);
		}

		HandlerList.unregisterAll();
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args){

		commands.get(command.getName()).onCommand(new Sender(sender), new Arguments(args));
		return true;
	}


	protected void registerCommands(Command... commands){
		for(Command command : commands){
			//コマンドのクラス名を取得する
			String className = command.getClass().getSimpleName();

			//接尾辞のCommandを削除し小文字化した物をコマンド名とする
			String commandName = className.substring(0, className.length() - 7).toLowerCase();
			//コマンド名とコマンドを結び付けて登録する
			this.commands.put(commandName, command);
		}
	}

	@SafeVarargs
	protected  final void registerCommands2(final AbstractCommand<Main>... commands) {
		for (final AbstractCommand<Main> command : commands) {
			this.commands2.put(command.getName().toLowerCase(), command);
			command.register();
		}
	}

	public boolean registerSubCommand(@Nonnull final String command, @Nonnull final SubCommand subCommand) {
		Objects.requireNonNull(command, "command");
		Objects.requireNonNull(subCommand, "subCommand");

		final AbstractCommand<Main> result = (AbstractCommand<Main>) commands.get(command.toLowerCase());

		if (result == null || result.isChild(subCommand.getName().toLowerCase())) {
			return false;
		}

		result.child(new AbstractCommand<Main>(Main.getPlugin(), subCommand) {
			@Override
			protected void execute(final CommandSender sender, final String label, final String[] args) {
				subCommand.execute(sender, label, args);
			}
		});
		return true;
	}



	protected void registerListeners(Listener... listeners){
		for(Listener listener : listeners){
			getServer().getPluginManager().registerEvents(listener, this);
			if(listener instanceof PlayerJoinListener) joinListeners.add((PlayerJoinListener) listener);
			if(listener instanceof PlayerQuitListener) quitListeners.add((PlayerQuitListener) listener);
		}
	}

	protected void registerEnchantments(Enchantment... enchantments){
		Field acceptingNew = Reflection.getField(Enchantment.class, "acceptingNew");

		//状態を保存する
		final boolean accept = Reflection.getFieldValue(acceptingNew, null);

		//エンチャント登録が許可された状態にする
		Reflection.setFieldValue(acceptingNew, null, true);

		try{
			//エンチャントを登録する
			for(Enchantment enchantment : enchantments) Enchantment.registerEnchantment(enchantment);
		}catch(Exception e){
			//既に登録されていれば問題無いので無視する
		}finally{
			//元の状態に戻す
			Reflection.setFieldValue(acceptingNew, null, accept);
		}
	}

	public HashMap<String, Command> getCommands() {
		return commands;
	}
}
