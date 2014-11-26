package de.woodpot.counterfight;

import java.util.ArrayList;

public class DrawerItem {
	private String title;
	private int icon;
	private int layoutType;
	private Object extras;
	
	public DrawerItem() {
		
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void setIcon(int icon) {
		this.icon = icon;
	}
	
	public int getIcon() {
		return this.icon;
	}
	
	public void setLayoutType(int layoutType) {
		this.layoutType = layoutType;
	}
	
	public int getLayoutType() {
		return this.layoutType;
	}
	
	// Extras können verschiedene Dinge sein: z.B. zusätzliche Icons/Strings für bestimmte Fallunterscheidungen
	public void setExtras(int extras) {
		this.extras = extras;
	}
	
	public Object getExtras() {
		return this.extras;
	}
	
}
