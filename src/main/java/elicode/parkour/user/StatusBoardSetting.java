package elicode.parkour.user;

import org.bukkit.configuration.ConfigurationSection;

import elicode.parkour.util.yaml.Yaml;

public class StatusBoardSetting {

	public boolean displayScoreboard;
	public boolean displayTraceur;
	public boolean displayUpdateRank;
	public boolean displayExtendRank;
	public boolean displayJumps;
	public boolean displayCoins;
	public boolean displayTimePlayed;
	public boolean displayOnlinePlayers;
	public boolean displayPing;
	public boolean displayServerAddress;

	public StatusBoardSetting(Yaml yaml){
		ConfigurationSection scoreboardSection = yaml.getConfigurationSection("Values displayed on scoreboard");

		displayScoreboard = scoreboardSection.getBoolean("Scoreboard");
		displayTraceur = scoreboardSection.getBoolean("Traceur");
		displayUpdateRank = scoreboardSection.getBoolean("Update rank");
		displayExtendRank = scoreboardSection.getBoolean("Extend rank");
		displayJumps = scoreboardSection.getBoolean("Jumps");
		displayCoins = scoreboardSection.getBoolean("Coins");
		displayTimePlayed = scoreboardSection.getBoolean("Time played");
		displayOnlinePlayers = scoreboardSection.getBoolean("Online players");
		displayPing = scoreboardSection.getBoolean("Ping");
		displayServerAddress = scoreboardSection.getBoolean("Server address");
	}

	public void save(Yaml yaml){
		ConfigurationSection scoreboardSection = yaml.getConfigurationSection("Values displayed on scoreboard");

		scoreboardSection.set("Scoreboard", displayScoreboard);
		scoreboardSection.set("Traceur", displayTraceur);
		scoreboardSection.set("Update rank", displayUpdateRank);
		scoreboardSection.set("Extend rank", displayExtendRank);
		scoreboardSection.set("Jumps", displayJumps);
		scoreboardSection.set("Coins", displayCoins);
		scoreboardSection.set("Time played", displayTimePlayed);
		scoreboardSection.set("Online players", displayOnlinePlayers);
		scoreboardSection.set("Ping", displayPing);
		scoreboardSection.set("Server address", displayServerAddress);
	}

}
