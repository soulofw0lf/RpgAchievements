package com.vartala.soulofw0lf.achievements;

import java.util.Map;

public class Kills {
	
	String Player;
	Map<String, Integer> BossDeaths;
	
	public Kills(String player, Map<String, Integer> bossdeaths)
	{
		this.Player = player;
		this.BossDeaths = bossdeaths;
	}
	
	public String getPlayer(){ return this.Player; }
	
	public Map<String, Integer> getBossDeaths(){ return this.BossDeaths; }
}