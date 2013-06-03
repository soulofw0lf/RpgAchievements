package com.vartala.soulofw0lf.achievements;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {
	RPGAchievements Ach;

	public CommandHandler(RPGAchievements ach) {
		this.Ach = ach;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {

		if (cmd.getName().equalsIgnoreCase("titles")) {
			if (args.length > 0) {
				if ((!(sender instanceof Player) || sender.isOp())
						&& args[0].equalsIgnoreCase("add")) {
					Player p = Bukkit.getPlayer(args[2]);
					if (p == null) {
						sender.sendMessage("Could Not Find Player!!");
						return true;
					}
					String name = p.getName();
					// If Player Is Not In Map Add Him To Map
					if (!this.Ach.playersTitles.containsKey(name)) {
						this.Ach.playersTitles.put(name,
								new ArrayList<String>());
					}
					Achieve achieve = this.Ach.getAchieveByTitle(args[1]
							.replaceAll("_", " "));
					if (achieve == null){
						sender.sendMessage("This achievement could not be found!");
						return true;
					}
					if (this.Ach.playersTitles.get(name).contains(args[1])) {
						return true;
					} else {
						// add a new achievement to a players list of earned
						// achievements
						this.Ach.playersTitles.get(name)
						.add(achieve.getTitle());
						sender.sendMessage("Title " + achieve.getTitle()
								+ " Has Been Added To Player " + name);
						Bukkit.broadcastMessage(name
								+ " Has Earned the Achievement "
								+ achieve.getAchievement());
						p.sendMessage("Congratulations " + name
								+ " You have unlocked the title "
								+ achieve.getTitle());
						this.Ach.SaveConfigs();
						return true;
					}
				}
				if (args[0].equalsIgnoreCase("reload")) {
					this.Ach.playersTitles.clear();
					this.Ach.playerPrefixes.clear();
					this.Ach.playerSuffixes.clear();
					this.Ach.allAchievements.clear();
					if (Ach.getConfig().contains("titles")) {
						for (String name : Ach.getConfig()
								.getConfigurationSection("titles")
								.getKeys(false)) {
							String title = name;
							String achievement = new String();
							String description = new String();
							boolean prefix = true;
							if (Ach.getConfig().contains(
									"titles." + name + ".achievement")) {
								achievement = Ach.getConfig().getString(
										"titles." + name + ".achievement");
							}
							if (Ach.getConfig().contains(
									"titles." + name + ".description")) {
								description = Ach.getConfig().getString(
										"titles." + name + ".description");
							}
							if (Ach.getConfig().contains(
									"titles." + name + ".prefix")) {
								prefix = Ach.getConfig().getBoolean(
										"titles." + name + ".prefix");
							}
							this.Ach.allAchievements.add(new Achieve(title,
									achievement, description, prefix));
						}
					}
					if (Ach.getConfig().contains("players")) {
						for (String name : Ach.getConfig()
								.getConfigurationSection("players")
								.getKeys(false)) {
							if (Ach.getConfig().contains(
									"players." + name + ".titles")) {
								this.Ach.playersTitles.put(
										name,
										Ach.getConfig().getStringList(
												"players." + name + ".titles"));
							}
							if (Ach.getConfig().contains(
									"players." + name + ".active prefix")) {
								this.Ach.playerPrefixes.put(
										name,
										Ach.getConfig().getString(
												"players." + name
												+ ".active prefix"));
							}
							if (Ach.getConfig().contains(
									"players." + name + ".active suffix")) {
								this.Ach.playerSuffixes.put(
										name,
										Ach.getConfig().getString(
												"players." + name
												+ ".active suffix"));
							}
						}
					}
					sender.sendMessage("Achievements Config Reloaded");
					return true;
				}
				if (args[0].equalsIgnoreCase("list")) {
					if (args.length > 2) {
						sender.sendMessage("you have included too many arguments!");
						sender.sendMessage("Proper usage: {/titles list} for all achievement titles or {/title list playername} for titles a player has earned");
						return true;
					} else {
						if (args.length == 2) {
							try {

								Player p = Bukkit.getPlayer(args[1]);
								if (p == null) {
									sender.sendMessage("Could Not Find Player!!");
									return true;
								}

								List<String> s = this.Ach.getConfig()
										.getStringList(
												"players." + p.getName()
												+ ".titles");

								for (String players : s) {



									sender.sendMessage(players + "\n");
								}
								sender.sendMessage("none §4this clears your active titles!");

								return true;
							} catch (Exception e) {
								e.printStackTrace();
							}

						} else {
							String tmp = new String();
							for (Achieve achieve : this.Ach.allAchievements) {
								tmp += achieve.getTitle() + "\n";
							}

							sender.sendMessage(tmp);
							return true;
						}
					}
				}

				if (args[0].equalsIgnoreCase("set")) {
					Player player = (Player) sender;
					if (args.length != 2) {
						sender.sendMessage("Please use </titles set {title_name}> to set your current titles");
						return true;
					} else {
						if (player.hasPermission("titles.set")) {

							// insert code here to set players active prefix -
							// suffix this should replace old prefix / suffix
							// respectively
							if (!(args[1].equalsIgnoreCase("none"))){
								List<String> j = this.Ach.playersTitles.get(player.getName());
								if (!(j.contains(args[1].replaceAll("_", " ")))){
									sender.sendMessage("you haven't earned that achievement yet!");
									return true;
								}
								Achieve achieve = this.Ach
										.getAchieveByTitle(args[1].replaceAll("_",
												" "));
								if (achieve == null) {
									sender.sendMessage("Could Not Find Achievement.");
									return true;
								}
								if (achieve.IsPrefix) {
									String current = this.Ach.getConfig().getString("players." + player.getName() + ".active prefix");
									if (this.Ach.getConfig().getString("titles." + current + ".unequip command") != null){
										Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),(this.Ach.getConfig().getString("titles." + current + ".unequip command").replaceAll("@p", player.getName())));
									}
									this.Ach.playerPrefixes
									.remove(player.getName());
									this.Ach.playerPrefixes.put(player.getName(),
											achieve.getTitle());
									sender.sendMessage("You have added "
											+ achieve.getTitle()
											+ " as your title prefix!");
									this.Ach.setPlayerName(player);
									this.Ach.SaveConfigs();
									String newtitle = this.Ach.getConfig().getString("players." + player.getName() + ".active prefix");
									if (this.Ach.getConfig().getString("titles." + newtitle + ".equip command") != null){
										Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),(this.Ach.getConfig().getString("titles." + newtitle + ".equip command").replaceAll("@p", player.getName())));
									}
									return true;
								} else {
									String current = this.Ach.getConfig().getString("players." + player.getName() + ".active suffix");
									if (this.Ach.getConfig().getString("titles." + current + ".unequip command") != null){
										Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),(this.Ach.getConfig().getString("titles." + current + ".unequip command").replaceAll("@p", player.getName())));
									}
									this.Ach.playerSuffixes
									.remove(player.getName());
									this.Ach.SaveConfigs();
									this.Ach.playerSuffixes.put(player.getName(),
											achieve.getTitle());
									sender.sendMessage("You have added "
											+ achieve.getTitle()
											+ " as your title suffix!");
									this.Ach.setPlayerName(player);
									this.Ach.SaveConfigs();
									String newtitle = this.Ach.getConfig().getString("players." + player.getName() + ".active suffix");
									if (this.Ach.getConfig().getString("titles." + newtitle + ".equip command") != null){
										Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),(this.Ach.getConfig().getString("titles." + newtitle + ".equip command").replaceAll("@p", player.getName())));
									}
									return true;
								} 
							}else {
								if (this.Ach.getConfig().getString("players." + player.getName() + ".active prefix") != null){
									String current = this.Ach.getConfig().getString("players." + player.getName() + ".active prefix");
									if (this.Ach.getConfig().getString("titles." + current + ".unequip command") != null){
										Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),(this.Ach.getConfig().getString("titles." + current + ".unequip command").replaceAll("@p", player.getName())));
									}
								}
								if (this.Ach.getConfig().getString("players." + player.getName() + ".active suffix") != null){
									String currents = this.Ach.getConfig().getString("players." + player.getName() + ".active suffix");
									if (this.Ach.getConfig().getString("titles." + currents + ".unequip command") != null){
										Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),(this.Ach.getConfig().getString("titles." + currents + ".unequip command").replaceAll("@p", player.getName())));
									}
								}
								this.Ach.playerPrefixes.remove(player.getName());
								this.Ach.playerSuffixes.remove(player.getName());
								this.Ach.setPlayerName(player);
								this.Ach.SaveConfigs();
								sender.sendMessage("You have successfully removed your titles");
								return true;
							} 

						} else {
							sender.sendMessage("You do not have permission to use this command!");
							return true;
						}
					}
				}

				if (args[0].equalsIgnoreCase("info")) {
					Player player = (Player) sender;
					if (args.length != 2) {
						sender.sendMessage("Please use </titles info {title_name} to see a titles objectives");
						return true;
					} else {
						if (player.hasPermission("titles.info")) {
							args[1] = args[1].replaceAll("_", " ");
							// this code displays a specific achievement to the
							// player
							Achieve achieve = this.Ach
									.getAchieveByTitle(args[1]);
							if (achieve == null) {
								sender.sendMessage("Could Not Find Achievement.");
								return true;
							}
							sender.sendMessage("§6Achievement: §f"
									+ achieve.getAchievement());
							sender.sendMessage("§6Associated Title: §f"
									+ achieve.getTitle());
							sender.sendMessage("§6Description: §f"
									+ achieve.getDescription());
							if (achieve.IsPrefix) {
								sender.sendMessage("§6Type: §fPrefix");
								return true;
							} else {
								sender.sendMessage("§6Type: §fSuffix");
								return true;
							}
						} else {
							sender.sendMessage("You do not have permission to use this command!");
							return true;
						}
					}
				}

				if (args[0].equalsIgnoreCase("new")) {
					if (args.length != 5) {
						sender.sendMessage("proper usage: /titles new title_name achievement_name description_text true/false");
					} else {
						Player player = (Player) sender;
						if (player.hasPermission("titles.new")) {
							// replace all underscores with spaces
							args[1] = args[1].replaceAll("_", " ");
							args[1] = args[1].replaceAll("&", "§");
							args[2] = args[2].replaceAll("_", " ");
							args[2] = args[2].replaceAll("&", "§");
							args[3] = args[3].replaceAll("_", " ");
							args[3] = args[3].replaceAll("&", "§");
							// add a new achievement with parameters Title /
							// Achievement
							// name / Description / Boolean prefix true or false
							this.Ach.allAchievements.add(new Achieve(args[1],
									args[2], args[3], args[4]
											.equalsIgnoreCase("true")));
							this.Ach.SaveConfigs();
							return true;
						} else {
							sender.sendMessage("You do not have permission to use this command!");
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
