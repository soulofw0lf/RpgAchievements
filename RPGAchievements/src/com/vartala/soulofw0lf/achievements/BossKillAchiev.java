package com.vartala.soulofw0lf.achievements;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BossKillAchiev {
          
	public static void CheckBossKills(RPGAchievements Ach, Kills playerKills, String boss)
	{  
	Player player = Bukkit.getPlayer(playerKills.getPlayer());
		int deathCount = playerKills.getBossDeaths().get(boss);
		if (deathCount == 100)
		{
			if (Ach.getAchieveByTitle("The " + boss + " Killer") == null) {
				Ach.allAchievements.add(new Achieve("The " + boss + " Killer",
						"Multitudes of " + boss + "'s", "This Achievement is earned by killing 100 " + boss + "'s", false));
				Ach.SaveConfigs();
			}
				
				Ach.playersTitles.put(playerKills.getPlayer(), new ArrayList<String>());
				Achieve achieve = Ach.getAchieveByTitle("The " + boss + " Killer");
				Ach.playersTitles.get(playerKills.getPlayer()).add(achieve.getTitle());
				Bukkit.broadcastMessage(playerKills.getPlayer()
						+ " Has Earned the Achievement " + achieve.getAchievement());
				player.sendMessage("Congratulations "
						+ playerKills.getPlayer()
						+ " You have unlocked the title "
						+ achieve.getTitle()
						+ " type </titles set The_" + boss.replaceAll(" ", "_") + "_Killer> to set this as your active title!");
				Ach.SaveConfigs();
		} else {
			
		}
		
	}
	
}
