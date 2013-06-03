package com.vartala.soulofw0lf.achievements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class RPGAchievements extends JavaPlugin implements Listener {

	public static String playerLabel = null;
	// Map<PlayerName, <Titles>>
	Map<String, List<String>> playersTitles = new HashMap<>();
	Map<String, String> playerSuffixes = new HashMap<>();
	Map<String, String> playerPrefixes = new HashMap<>();
	List<Kills> playerKills = new ArrayList<>();
	// Map<<Title>, <achievemnt:title:desc:true/false>>
	List<Achieve> allAchievements = new ArrayList<>();

	


	@Override
	public void onEnable() {
		getCommand("titles").setExecutor(new CommandHandler(this));
		getCommand("kills").setExecutor(new KillCommandHandler(this));
		getLogger().info("onEnable has been invoked!");
		saveDefaultConfig();
		Bukkit.getPluginManager().registerEvents(this, this);
		this.LoadConfigs();
		if (this.getAchieveByTitle("The Logger") == null) {
			this.allAchievements.add(new Achieve("The Logger",
					"Joining The Server",
					"This achievement is earned by joining the server", false));
		}
		this.SaveConfigs();
	
	}
	








	@Override
	public void onDisable() {
		getLogger().info("onDisable has been invoked!");
	}

	public void SaveConfigs() {
		try {
			if (!(getConfig().contains("titles"))){
			getConfig().createSection("titles");
			}
			for (Achieve achieve : this.allAchievements) {
				getConfig().set(
						"titles." + achieve.getTitle() + ".achievement",
						achieve.getAchievement());
				getConfig().set(
						"titles." + achieve.getTitle() + ".description",
						achieve.getDescription());
				getConfig().set("titles." + achieve.getTitle() + ".prefix",
						achieve.getIsPrefix());
			}
			if (!(getConfig().contains("kills"))){
			getConfig().createSection("kills");
			}
			for (Kills kills : this.playerKills) {
				for (Entry<String, Integer> entry : kills.getBossDeaths()
						.entrySet()) {
					getConfig().set(
							"kills.player." + kills.getPlayer() + "."
									+ entry.getKey(), entry.getValue());
				}
			}
			if (!(getConfig().contains("players"))){
			getConfig().createSection("players");
			}
			for (Entry<String, List<String>> entry : this.playersTitles
					.entrySet()) {
				getConfig().set("players." + entry.getKey() + ".titles",
						entry.getValue());
				getConfig().set("players." + entry.getKey() + ".active prefix",
						this.playerPrefixes.get(entry.getKey()));
				getConfig().set("players." + entry.getKey() + ".active suffix",
						this.playerSuffixes.get(entry.getKey()));
			}
			saveConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void LoadConfigs() {
		this.playersTitles.clear();
		this.playerPrefixes.clear();
		this.playerSuffixes.clear();
		this.playerKills.clear();
		if (getConfig().contains("kills.player")) {
			for (String name : getConfig().getConfigurationSection(
					"kills.player").getKeys(false)) {
				Map<String, Integer> TempMap = new HashMap<String, Integer>();
				for (String boss : getConfig().getConfigurationSection(
						"kills.player." + name).getKeys(false)) {
					int TempBob = getConfig().getInt(
							"kills.player." + name + "." + boss);
					TempMap.put(boss, TempBob);
				}
				this.playerKills.add(new Kills(name, TempMap));
			}
		}
		if (getConfig().contains("titles")) {
			for (String name : getConfig().getConfigurationSection("titles")
					.getKeys(false)) {
				String title = name;
				String achievement = new String();
				String description = new String();
				boolean prefix = true;
				if (getConfig().contains("titles." + name + ".achievement")) {
					achievement = getConfig().getString(
							"titles." + name + ".achievement");
				}
				if (getConfig().contains("titles." + name + ".description")) {
					description = getConfig().getString(
							"titles." + name + ".description");
				}
				if (getConfig().contains("titles." + name + ".prefix")) {
					prefix = getConfig().getBoolean(
							"titles." + name + ".prefix");
				}

				this.allAchievements.add(new Achieve(title, achievement,
						description, prefix));
			}
		}

		if (getConfig().contains("players")) {
			for (String name : getConfig().getConfigurationSection("players")
					.getKeys(false)) {
				if (getConfig().contains("players." + name + ".titles")) {
					this.playersTitles.put(
							name,
							getConfig().getStringList(
									"players." + name + ".titles"));
				}
				if (getConfig().contains("players." + name + ".active prefix")) {
					this.playerPrefixes.put(
							name,
							getConfig().getString(
									"players." + name + ".active prefix"));
				}
				if (getConfig().contains("players." + name + ".active suffix")) {
					this.playerSuffixes.put(
							name,
							getConfig().getString(
									"players." + name + ".active suffix"));
				}
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		 
	
		if (!this.playersTitles.containsKey(player.getName())) {
			this.playersTitles.put(player.getName(), new ArrayList<String>());
			Achieve achieve = this.getAchieveByTitle("The Logger");
			playersTitles.get(player.getName()).add(achieve.getTitle());
			Bukkit.broadcastMessage(player.getName()
					+ " Has Earned the Achievement " + achieve.getAchievement());
			player.sendMessage("Congratulations "
					+ player.getName()
					+ " You have unlocked the title "
					+ achieve.getTitle()
					+ " type </titles set The_Logger> to set this as your active title!");
			this.SaveConfigs();
		}

	

		// Add Player To List
		if (this.getKillsByPlayer(event.getPlayer().getName()) == null) {
			this.playerKills.add(new Kills(event.getPlayer().getName(),
					new HashMap<String, Integer>()));
		}
	}

	// Utility Functions That Help Us Do Stuff

	// Achievements
	public Achieve getAchieveByTitle(String title) {
		for (Achieve achieve : this.allAchievements) {
			if (achieve.getTitle().equals(title)) {
				return achieve;
			}
		}
		return null;
	}

	public boolean containsAchieveByTitle(String title) {
		for (Achieve achieve : this.allAchievements) {
			if (achieve.getTitle().equals(title)) {
				return true;
			}
		}
		return false;
	}

	// Kills
	public Kills getKillsByPlayer(String name) {
		for (Kills kill : this.playerKills) {
			if (kill.getPlayer().equals(name)) {
				return kill;
			}
		}
		return null;
	}

	public String[] splitStringToMineCraftArray(String str) {
		String[] words = str.split(" ");
		String[] newArray = {};

		int arrayIndex = 0;
		String tmp = new String();
		for (int x = 0; x < words.length; x++) {
			if (tmp.length() + words[x].length() + 1 >= 100) {
				newArray[arrayIndex++] = tmp;
				tmp = new String();
				continue;
			}
			tmp += words[x] + " ";
		}
		return newArray;
	}
	public void onChat(AsyncPlayerChatEvent event){
		Player player = event.getPlayer();
		String pname = player.getDisplayName();
		if ((this.playerPrefixes.containsKey(player.getName()))
				&& (this.playerSuffixes.containsKey(player.getName()))) {
			player.setDisplayName(this.playerPrefixes.get(player.getName())
					+ " " + pname + " "
					+ this.playerSuffixes.get(player.getName()));

		} else {
			if (this.playerPrefixes.containsKey(player.getName())) {
				player.setDisplayName(this.playerPrefixes.get(player.getName())
						+ " " + pname);
			} else {
				if (this.playerSuffixes.containsKey(player.getName())) {
					player.setDisplayName(pname + " "
							+ this.playerSuffixes.get(player.getName()));
				}
			}
		}
	}
	public void setPlayerName(Player player) {
		
		String pname = player.getDisplayName();
		if (!(this.playerPrefixes.containsKey(player.getName())) && !(this.playerSuffixes.containsKey(player.getName()))){
			player.setDisplayName(pname);
		}
		if ((this.playerPrefixes.containsKey(player.getName()))
				&& (this.playerSuffixes.containsKey(player.getName()))) {
			player.setDisplayName(this.playerPrefixes.get(player.getName())
					+ " " + pname + " "
					+ this.playerSuffixes.get(player.getName()));

		} else {
			if (this.playerPrefixes.containsKey(player.getName())) {
				player.setDisplayName(this.playerPrefixes.get(player.getName())
						+ " " + pname);
			} else {
				if (this.playerSuffixes.containsKey(player.getName())) {
					player.setDisplayName(pname + " "
							+ this.playerSuffixes.get(player.getName()));
				}
			}
		}
	}
}
