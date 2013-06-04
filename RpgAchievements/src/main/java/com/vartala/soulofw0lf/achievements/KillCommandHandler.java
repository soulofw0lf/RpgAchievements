package com.vartala.soulofw0lf.achievements;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KillCommandHandler implements CommandExecutor {

	RPGAchievements Ach;
	
	public KillCommandHandler(RPGAchievements ach)
	{
		this.Ach = ach;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (cmd.getName().equalsIgnoreCase("kills")){
			if (args.length > 0) {
				if (!(sender instanceof Player) && (args.length == 3)) {
					Player p = Bukkit.getPlayer(args[1]);
					if (p == null) {
						sender.sendMessage("Could Not Find Player!!");
						return true;
					}
					if (args[0].equalsIgnoreCase("new")) {
						if (args.length != 3) {
							sender.sendMessage("proper usage: /kills new {playername} {bossname}");
						} else {
						//replace all underscores with spaces
							if(this.Ach.getKillsByPlayer(args[1]) == null)
							{
								this.Ach.playerKills.add(new Kills(args[1], new HashMap<String, Integer>()));
							}
							args[2] = args[2].replaceAll("_", " ");
							int x = 0;
							if(this.Ach.getKillsByPlayer(args[1]).getBossDeaths().containsKey(args[2]))
							{
								x = Ach.getKillsByPlayer(args[1]).getBossDeaths().get(args[2]);//killer.getDeaths();
							}
								// add a new kills record with parameters kills /
							// player name / boss name / death +1
							// name / Description / Boolean prefix true or false
							this.Ach.getKillsByPlayer(args[1]).getBossDeaths().put(args[2], ++x);
							this.Ach.SaveConfigs();
							BossKillAchiev.CheckBossKills(this.Ach, Ach.getKillsByPlayer(args[1]), args[2]);
							return true;
						} 
					}
				} else { sender.sendMessage("Wrong number of arguments for /kills command.");}
			}
		}
		return false;
	}
	
}
