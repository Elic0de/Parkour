package elicode.parkour;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.github.klyser8.karma.KarmaPlugin;
import com.github.ucchyocean.lc.LunaChat;
import com.github.ucchyocean.lc.LunaChatAPI;
import elicode.parkour.command.commands.parkours.ParkoursCommand;
import elicode.parkour.command.commands.tags.TagsCommand;
import elicode.parkour.command.parkour.*;
import elicode.parkour.command.party.PartyCommand;
import elicode.parkour.config.Config;
import elicode.parkour.config.Lang;
import elicode.parkour.game.GameManager;
import elicode.parkour.game.duel.RequestManager;
import elicode.parkour.game.queue.QueueManager;
import elicode.parkour.listener.*;
import elicode.parkour.listener.chat.PlayerChatListener;
import elicode.parkour.listener.parkour.*;
import elicode.parkour.lobby.LobbySet;
import elicode.parkour.maneger.LogManager;
import elicode.parkour.mysql.Database;
import elicode.parkour.other.StartPlugin;
import elicode.parkour.party.PartyManager;
import elicode.parkour.util.Loadable;
import elicode.parkour.util.Utils;
import elicode.parkour.util.databases.DatabaseManager;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import elicode.parkour.util.enchantment.GleamEnchantment;
import elicode.parkour.events.PlayerJumpEvent.PlayerJumpListener;
import elicode.parkour.function.PlayerLocaleChange;
import elicode.parkour.function.parkour.ControlFunctionalItem;
import elicode.parkour.inventory.ui.dsl.InventoryUI;
import elicode.parkour.inventory.ui.listener.UIListener;
import elicode.parkour.parkour.ParkourSet;
import elicode.parkour.region.selection.RegionSelectionSet;
import elicode.parkour.schedule.task.AsyncTask;
import elicode.parkour.schedule.task.SaveParkourDataTask;
import elicode.parkour.schedule.task.SaveUserDataTask;
import elicode.parkour.schedule.task.UpdatePingTask;
import elicode.parkour.schedule.task.UpdateTimePlayedTask;
import elicode.parkour.user.UserSet;

public class Main extends Plugin {

	private static Main plugin;
	private QueueManager queueManager;
	private PartyManager partyManager;
	private Database database;
	private RequestManager requestManager;
	private LunaChatAPI lunachatapi;
	private KarmaPlugin karmaPlugin;
	private me.joseph.levels.Main level;
	private LogManager logManager;
	private Config configuration;
	private Lang lang;
	private final List<Loadable> loadables = new ArrayList<>();
	private final ArrayList<AsyncTask> activeTasks = new ArrayList<>(4);

	@Override
	public void onEnable() {
		//改修予定

		plugin = this;

		StartPlugin.run();
		//setup();

		this.queueManager = new QueueManager();
		this.partyManager = new PartyManager();
		this.requestManager = new RequestManager();
		this.logManager = new LogManager();

		saveDefaultConfig();

		LobbySet.load();
		ParkourSet.load();
		UserSet.load();
		GameManager.load();
		RegionSelectionSet.load();

		registerCommands(
				new ParkourCommand(),
				new ParkourRegionCommand(),
				new ParkourSettingCommand(),
				new ParkourEditCommand(),
				new CheckAreaCommand(),
				new CoinCommand(),
				new ItemCommand(),
				new DirectionCommand(),
				new TweetCommand(),
				new ParkourLinkCommand(),
				new LobbyCommand(),
				new TestCommand(),
				new PartyCommand(),
				new StatsCommand(),
				new DuelCommand()
		);

		registerCommands2(
				new ParkoursCommand(this),
				new TagsCommand(this)
				//new ParkourCommand(this),
				//new DuelCommand(this),
				//new PartyCommand(this)

		);

		registerListeners(
				new PlayerMoveListener(),
				new UIListener(),
				new PlayerJumpListener(),
				UserSet.getInstnace(),
				RegionSelectionSet.getInstance(),
				new PlayerChatListener(),
				new UserJoinListener(),
				new ControlFunctionalItem(),
				new VoteListener(),
				new SignListener(),
				new PassFinishLineListener(),
				new PassStartLineListener(),
				new PassArenaListener(),
				new PassPortalListener(),
				new PassCheckAreaListener(),
				new SetCheckpointListener(),
				new PlayerDamageListener(),
				new FoodLevelChangeListener(),
				new JumpListener(),
				new PlayerLocaleChange(),
				new UserQuitListener(),
				new PlayerBreakListener()
		);

		registerEnchantments(
				GleamEnchantment.GLEAM_ENCHANTMENT
		);

		startTasks(
				new SaveParkourDataTask(),
				new SaveUserDataTask(),
				new UpdateTimePlayedTask(),
				new UpdatePingTask()
		);

		loadables.add(lang = new Lang(this));

		for (final Loadable loadable : loadables) {
			final String name = loadable.getClass().getSimpleName();

			try {
				final long now = System.currentTimeMillis();
				debug("Starting load of " + name + " at " + now);
				loadable.handleLoad();
				debug(name + " has been loaded. (took " + (System.currentTimeMillis() - now) + "ms)");
				//lastLoad = loadables.indexOf(loadable);
			} catch (Exception ex) {
				// Handles the case of exceptions from LogManager not being logged in file

				debug(ex);
				return ;
			}
		}

		super.onEnable();

		if ( getServer().getPluginManager().isPluginEnabled("LunaChat") ) {
			LunaChat lunachat = (LunaChat)getServer().getPluginManager().getPlugin("LunaChat");
			lunachatapi = lunachat.getLunaChatAPI();
		}
		if ( getServer().getPluginManager().isPluginEnabled("Karma") ) {
			KarmaPlugin karmaPlugin = (KarmaPlugin) getServer().getPluginManager().getPlugin("Karma");
			this.karmaPlugin = karmaPlugin;
		}
		if ( getServer().getPluginManager().isPluginEnabled("NetworkLevels") ) {
			me.joseph.levels.Main networkLevels = (me.joseph.levels.Main) getServer().getPluginManager().getPlugin("NetworkLevels");
			this.level = networkLevels;
		}



	}

	@Override
	public void onDisable(){
		super.onDisable();

		forceCloseAllPlayerInventoryUIs();

		saveConfig();
		cancelTasks();
		database.closeConnection();
		//friendDatabase.closeConnection();
		DatabaseManager.get().shutdown();

		queueManager.onDisable();
		LobbySet.getInstance().saveAll();
		UserSet.getInstnace().saveAll();
		ParkourSet.getInstance().saveAll();
	}

	public void setup(){
		try {
			DatabaseManager.get().setup(true);
		} catch (Exception ex) {
			Utils.log("Failed enabling database-manager...");
			debugException(ex);
		}
	}

	public static Main getPlugin(){
		return plugin;
	}

	public RequestManager getRequestManager() {
		return requestManager;
	}

	public LunaChatAPI getLunachatapi() {
		return lunachatapi;
	}

	public KarmaPlugin getKarmaPlugin() {
		return karmaPlugin;
	}

	public me.joseph.levels.Main getLevel() {
		return level;
	}

	public Database getDatabase() {
		return database;
	}

	public Config getConfiguration() {
		return configuration;
	}

	public Lang getLang() {
		return lang;
	}

	public static FileConfiguration getParkourConfig() {
		return getPlugin().getConfig();
	}

	public void setDatabase(Database database) {
		this.database = database;
	}

	public PartyManager getPartyManager() {
		return partyManager;
	}

	public QueueManager getQueueManager() {
		return this.queueManager;
	}

	public static World getCreativeWorld(){
		return Bukkit.getWorld("Creative");
	}

	private void startTasks(AsyncTask... tasks){
		for(AsyncTask task : tasks){
			activeTasks.add(task);

			task.start();
		}
	}

	private void cancelTasks(){
		for(AsyncTask task : activeTasks) task.cancel();

		activeTasks.clear();
	}

	private void forceCloseAllPlayerInventoryUIs(){
		for(Player player : Bukkit.getOnlinePlayers()){
			InventoryView opened = player.getOpenInventory();
			if(opened == null)
				continue;

			closeInventoryUI(player, opened.getTopInventory());
			closeInventoryUI(player, opened.getBottomInventory());
		}
	}

	private void closeInventoryUI(Player player, Inventory inventory){
		if(inventory != null && inventory.getHolder() instanceof InventoryUI) player.closeInventory();
	}

	public void debug(Object msg) {
		if (true) {
			Utils.log("§8[§cParkour§8] §cDebug: §7" + msg.toString());
		}
		debugToFile(msg);
	}

	public void debugException(Exception exc) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exc.printStackTrace(pw);

		debug(sw.toString());
	}

	/**
	 * Debug.
	 *
	 * @param ex the ex
	 */
	public void debugSqlException(SQLException ex) {
		if (true) {
			debug("§7An error has occurred with the database, the error code is: '" + ex.getErrorCode() + "'");
			debug("§7The state of the sql is: " + ex.getSQLState());
			debug("§7Error message: " + ex.getMessage());
		}
		debugException(ex);
	}

	private void debugToFile(Object msg) {
		File debugFile = new File(plugin.getDataFolder(), "logs/latest.log");
		if (!debugFile.exists()) {
			System.out.print("Seems that a problem has occurred while creating the latest.log file in the startup.");
			try {
				debugFile.createNewFile();
			} catch (IOException ex) {
				System.out.print("An error has occurred creating the 'latest.log' file again, check your server.");
				System.out.print("Error message" + ex.getMessage());
			}
		} else {
			logManager.checkLastLog(false);
		}
		try {
			FileUtils.writeStringToFile(debugFile, "[" + new SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis()) + "] " + ChatColor.stripColor(msg.toString()) + "\n", Charsets.UTF_8, true);
		} catch (IOException ex) {
			System.out.print("An error has occurred writing to 'latest.log' file.");
			System.out.print(ex.getMessage());
		}
	}

}
