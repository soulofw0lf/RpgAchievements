package com.vartala.soulofw0lf.achievements;

public class Achieve {
	
	String Title;
	String Achievement;
	String Description;
	boolean IsPrefix;
	
	public Achieve(String title, String achieve, String description, boolean prefix)
	{
		this.Title = title;
		this.Achievement = achieve;
		this.Description = description;
		this.IsPrefix = prefix;
	}
	
	public String getTitle(){ return this.Title; }
	
	public String getAchievement(){ return this.Achievement; }
	
	public String getDescription(){ return this.Description; }
	
	public boolean getIsPrefix(){ return this.IsPrefix; }
	
}
