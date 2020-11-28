package elicode.parkour.lobby;

import elicode.parkour.Main;
import elicode.parkour.util.yaml.Yaml;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LobbySet {

	private static LobbySet instance;

	public static void load(){
		instance = new LobbySet();
	}

	public static LobbySet getInstance(){
		return instance;
	}

	private final Main plugin = Main.getPlugin();

	//ロビーを保存するフォルダー
	public final File folder = new File(plugin.getDataFolder() + File.separator + "Lobby");

	//アスレのマップ
	private final Map<String, Lobby> lobbies = new HashMap<>();

	private LobbySet(){
		//フォルダーが存在しなければ作成する
		if(!folder.exists()) folder.mkdirs();

		//各アスレコンフィグ毎に処理をする
		for(File file : Optional.ofNullable(folder.listFiles()).orElse(new File[0])){
			String fileName = file.getName();

			//拡張子を削除してアスレ名を取得する
			String lobbyName = fileName.substring(0, fileName.length() - 4);

			//ロビーを登録する
			registerLobby(lobbyName);
		}
	}


	public boolean existsFil(String parkourName){
		return new File(folder, parkourName + ".yml").exists();
	}

	public void registerLobby(Lobby lobby){
		lobbies.put(lobby.name, lobby);
	}

	public void registerLobby(String lobbyName){
		File file = new File(folder, lobbyName + ".yml");

		//コンフィグが存在しなければ戻る
		if(!file.exists()) return;

		//コンフィグを取得する
		Yaml yaml = makeYaml(lobbyName);

		//コンフィグに基づきアスレを生成する
		Lobby lobby =  new Lobby(this, yaml);

		registerLobby(lobby);

		//登録する
	}

	public void unregisterLobby(Lobby lobby){
		lobbies.remove(lobby.name);
	}

	public void unregisterLobby(String lobbyName){
		if(containsLobby(lobbyName)) unregisterLobby(getLobby(lobbyName));
	}


	public Collection<Lobby> getLobbies(){
		return lobbies.values();
	}

	public Lobby getLobby(String parkourName){
		return lobbies.get(parkourName);
	}

	public boolean containsLobby(Lobby lobby){
		return containsLobby(lobby.name);
	}

	public boolean containsLobby(String lobbyName){
		return lobbies.containsKey(lobbyName);
	}

	public Yaml makeYaml(String lobbyName){
		return new Yaml(plugin, new File(folder, lobbyName + ".yml"), "lobbies.yml");
	}
	public void saveAll(){
		lobbies.values().forEach(Lobby::save);
	}


}
