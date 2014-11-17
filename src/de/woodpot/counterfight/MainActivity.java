package de.woodpot.counterfight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity implements OnChildClickListener {
	
	/** Die MainActivity ist für den Nutzer im Prinzip nicht sichtbar. Sie entscheidet nur,
	 * wohin der Nutzer geleitet werden soll und ist Layout für den NavigationDrawer
	 */
	
	// Variablen für den NavigationDrawer
	private DrawerLayout drawer;
	private ActionBarDrawerToggle toggle;
	SimpleExpandableListAdapter expListAdapter;
	private ExpandableListView allgroupsListView;
	
	// Layout-Konstanten
	private static int LAYOUT_SECTION_TITLE = 1;
	private static int LAYOUT_ICON_TEXT = 2;
	private static int LAYOUT_TEXT_ONLY = 3;
	
	// Menüeinträge
	ArrayList<HashMap<String, Object>> groupItems = new ArrayList<HashMap<String, Object>>();	
	ArrayList<Object> childItems = new ArrayList<Object>();
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setGroupData();
		setChildGroupData();
		initializeDrawer();

		getSupportActionBar();
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	
		
	}
	
	public void initializeDrawer(){
		// Zuweisungen für den NavigationDrawer
		drawer = (DrawerLayout) findViewById(R.id.drawer_layout); // befindet sich innerhalb activity_main.xml		
		toggle = new ActionBarDrawerToggle(this, drawer, R.drawable.ic_drawer, R.string.string_navigationdrawer_open, R.string.string_navigationdrawer_close);
		drawer.setDrawerListener(toggle);
		
		// ExpandableListView finden
		allgroupsListView = (ExpandableListView) findViewById(R.id.listview_navigationdrawer_allgroups);
		
		// Adapter verbinden
		expListAdapter = new SimpleExpandableListAdapter(this, groupItems, childItems);
		allgroupsListView.setAdapter(expListAdapter);
		
		// Größe der ExpandableListView anpassen
		//allgroupsListView.setLayoutParams(new LinearLayout.LayoutParams(240, setListViewHeightBasedOnChildren(expListAdapter.getChildrenCount(0))));
	
		// OnClickListener für die NavigationDrawer-ARRAY-Items (einzelne Gruppen)
		//allgroupsListView.setOnItemClickListener(new DrawerItemClickListener());
		//allgroupsListView.setOnChildClickListener(this);	
	}
	
	public void setGroupData() {
		// Gruppenverwaltung
		HashMap<String, Object> groupSettingsData = new HashMap<String, Object>();				
		groupSettingsData.put("KEY_TITLE", getString(R.string.string_navigationdrawer_groupsettings));		// Titel
		groupSettingsData.put("KEY_LAYOUT", LAYOUT_SECTION_TITLE);											// Layout
		groupSettingsData.put("KEY_ICON", null);															// Section-Layout enthält kein Icon, daher null bei KEY_ICON
		groupItems.add(groupSettingsData);	
		
		// Gruppen aufklappen
		HashMap<String, Object> expandGroupData = new HashMap<String, Object>();	
		expandGroupData.put("KEY_TITLE", getString(R.string.string_navigationdrawer_expandgroups));			// Titel
		expandGroupData.put("KEY_LAYOUT", LAYOUT_TEXT_ONLY);												// Layout
		expandGroupData.put("KEY_ICON", null);																// LAYOUT_TEXT_ONLY enthält kein Icon, daher null bei KEY_ICON
		groupItems.add(expandGroupData);	
		
		// Accountverwaltung
		HashMap<String, Object> accountSettingsData = new HashMap<String, Object>();
		accountSettingsData.put("KEY_TITLE", getString(R.string.string_navigationdrawer_accountsettings));	// Titel
		accountSettingsData.put("KEY_LAYOUT", LAYOUT_SECTION_TITLE);											// Layout
		accountSettingsData.put("KEY_ICON", null);															// LAYOUT_TEXT_ONLY enthält kein Icon, daher null bei KEY_ICON
		groupItems.add(accountSettingsData);			

		// sonstige Einstellungen
		HashMap<String, Object> otherSettingsData = new HashMap<String, Object>();		
		otherSettingsData.put("KEY_TITLE", getString(R.string.string_navigationdrawer_othersettings));		// Titel
		otherSettingsData.put("KEY_LAYOUT", LAYOUT_SECTION_TITLE);												// Layout
		otherSettingsData.put("KEY_ICON", null);															// LAYOUT_TEXT_ONLY enthält kein Icon, daher null bei KEY_ICON
		groupItems.add(otherSettingsData);					
	}

	public void setChildGroupData() {	
		// Gruppenverwaltung
		ArrayList<String> groupSettingsChild = new ArrayList<String>();
		groupSettingsChild.add(getString(R.string.string_navigationdrawer_groupsummary));
		groupSettingsChild.add(getString(R.string.string_navigationdrawer_creategroup));
		groupSettingsChild.add(getString(R.string.string_navigationdrawer_searchgroup));
		childItems.add(groupSettingsChild);
		
		// Gruppen aufklappen   --> Wird später dynamisch aus der AllGroupsActivity aufgerufen
		ArrayList<String> expandGroupsChild = new ArrayList<String>();
		expandGroupsChild.add("Gruppe 1");
		expandGroupsChild.add("Gruppe 2");
		expandGroupsChild.add("Gruppe 3");
		expandGroupsChild.add("Gruppe 4");
		expandGroupsChild.add("Gruppe 5");
		childItems.add(expandGroupsChild); 
		
		// Accountverwaltung
		ArrayList<String> accountSettingsChild = new ArrayList<String>();
		accountSettingsChild.add(getString(R.string.string_navigationdrawer_changeaccountdata));
		accountSettingsChild.add(getString(R.string.string_navigationdrawer_logout));
		childItems.add(accountSettingsChild);
		
		// sonstige Einstellungen
		ArrayList<String> otherSettingsChild = new ArrayList<String>();
		otherSettingsChild.add(getString(R.string.string_navigationdrawer_settings));
		otherSettingsChild.add(getString(R.string.string_navigationdrawer_faq));
		otherSettingsChild.add(getString(R.string.string_navigationdrawer_developerinfo));
		childItems.add(otherSettingsChild);
		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		
		toggle.syncState();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
		toggle.onConfigurationChanged(newConfig);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		Log.d("MainActivity:" , "menüitem id: " + id + "R.id: " + R.id.action_settings);
		if (id == R.id.action_settings) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		}
		
		if (toggle.onOptionsItemSelected(item)){
			return true;
		}
	
		return super.onOptionsItemSelected(item);
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView parent, View view, int position, long id) {
	        selectItem(position);
	    }
	    
	    public void selectItem(int position){
	    	if (position == 0) {
	    		Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
	    		startActivity(intent);
	    	}
	    }
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		// TODO Auto-generated method stub
		return false;
	}
}
