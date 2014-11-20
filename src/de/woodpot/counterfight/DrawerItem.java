package de.woodpot.counterfight;

import java.util.ArrayList;

public class DrawerItem {
	private String title;
	private int icon;
	private int layoutType;
	
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
	
}
