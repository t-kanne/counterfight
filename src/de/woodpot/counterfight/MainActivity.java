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
	ArrayList<DrawerItem> groupItems = new ArrayList<DrawerItem>();	// Array-List für alle Gruppen
	ArrayList<DrawerItem[]> childItems = new ArrayList<DrawerItem[]>();	// Array-List für alle Gruppenelemente
	ArrayList<String> expandGroupsChild;	// Array-List für DYNNAMISCHE GRUPPEN
	
	
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
		
	
		// OnClickListener für die NavigationDrawer-ARRAY-Items (einzelne Gruppen)
		//allgroupsListView.setOnItemClickListener(new DrawerItemClickListener());
		//allgroupsListView.setOnChildClickListener(this);	
	}
	
	// ######### WICHTIG #############
	// Die Reihenfolge der folgende Methoden-Abschnitte muss zwingend so eingehalten werden. Wenn die Menüpunkte verschoben werden sollen,
	// muss das in der setGroupData und der setChildData Methode jeweils synchron passieren. Ansonsten werden die Zuordnungen vertauscht!
	
	public void setGroupData() {
		// Gruppenverwaltung (0)
		DrawerItem groupSettingsChild = new DrawerItem();													// DrawerItem-Object für Gruppenverwaltung
		groupSettingsChild.setTitle(getString(R.string.string_navigationdrawer_groupsettings));				// Titel
		groupSettingsChild.setLayoutType(LAYOUT_SECTION_TITLE);												// Section-Layout enthält kein Icon
		groupItems.add(groupSettingsChild);																	// ab in die Group-ArrayList damit
		
		// Gruppen aufklappen (1)
		DrawerItem expandGroupData = new DrawerItem();	
		expandGroupData.setTitle(getString(R.string.string_navigationdrawer_expandgroups));			// Titel
		expandGroupData.setLayoutType(LAYOUT_TEXT_ONLY);												// Layout															// LAYOUT_TEXT_ONLY enthält kein Icon, daher null bei KEY_ICON
		groupItems.add(expandGroupData);	
		
		// Accountverwaltung (2)
		DrawerItem accountSettingsChild = new DrawerItem();
		accountSettingsChild.setTitle(getString(R.string.string_navigationdrawer_accountsettings));			// Titel
		accountSettingsChild.setLayoutType(LAYOUT_SECTION_TITLE);											
		groupItems.add(accountSettingsChild);															
		
		// sonstige Einstellungen (3)
		DrawerItem otherSettingsChild = new DrawerItem();		
		otherSettingsChild.setTitle(getString(R.string.string_navigationdrawer_othersettings));				// Titel
		otherSettingsChild.setLayoutType(LAYOUT_SECTION_TITLE);												// Layout
		groupItems.add(otherSettingsChild);				
	}

	public void setChildGroupData() {
		// Gruppenverwaltung
		DrawerItem[] groupSettingsChildren = new DrawerItem[3]; 													// Array für alle Elemente der Gruppenverwaltung		
		DrawerItem groupSummaryChild = new DrawerItem();										// DrawerItem für Gruppenübersicht-Element		
		groupSummaryChild.setTitle(getString(R.string.string_navigationdrawer_groupsummary));	// Titel
		groupSummaryChild.setIcon(R.drawable.ic_navdraw_allgroups);								// Icon
		groupSettingsChildren[0] = groupSummaryChild;											// Objekt in Array speichern
		
		DrawerItem createGroupChild = new DrawerItem();				
		createGroupChild.setTitle(getString(R.string.string_navigationdrawer_creategroup));
		createGroupChild.setIcon(R.drawable.ic_navdraw_creategroup);
		groupSettingsChildren[1] = createGroupChild;
		
		DrawerItem searchGroupChild = new DrawerItem();					
		searchGroupChild.setTitle(getString(R.string.string_navigationdrawer_searchgroup));
		searchGroupChild.setIcon(R.drawable.ic_navdraw_searchgroup);	
		groupSettingsChildren[2] = searchGroupChild;
		
		childItems.add(groupSettingsChildren);	
		// -------------------------------------------------------------------------------------------------------------------------------------------
		// Gruppen ausklappen	- SONDERFALL: keine Icons notwendig, weil es immer dasselbe ist. Wird im Adapter festgelegt.
		
		DrawerItem[] individualUserGroupChildren = new DrawerItem[getIndividualGroupsOfUser().size()]; // Array-List für alle Usergruppen
		Log.d("MainActivity: ", "getIndividualGroupsOfUser().size() = " + getIndividualGroupsOfUser().size());
		DrawerItem individualUserGroup;
		for (int i = 0; this.getIndividualGroupsOfUser().size() > i; i++) {
			individualUserGroup = new DrawerItem();													// DrawerItem-Object für persönliche Gruppe
			individualUserGroup.setTitle(this.getIndividualGroupsOfUser().get(i));					// Namen aus Array-Liste lesen
			individualUserGroupChildren[i] = individualUserGroup;
		}
		childItems.add(individualUserGroupChildren); 
		// -------------------------------------------------------------------------------------------------------------------------------------------
		// Accountverwaltung
		DrawerItem[] accountSettingsChildren = new DrawerItem[2];
		DrawerItem changeAccountDataChild = new DrawerItem();						
		changeAccountDataChild.setTitle(getString(R.string.string_navigationdrawer_changeaccountdata));
		changeAccountDataChild.setIcon(R.drawable.ic_navdraw_changepassword);
		accountSettingsChildren[0] = changeAccountDataChild;
			
		DrawerItem logoutChild = new DrawerItem();									
		logoutChild.setTitle(getString(R.string.string_navigationdrawer_logout));
		logoutChild.setIcon(R.drawable.ic_navdraw_logout);
		accountSettingsChildren[1] = logoutChild;
		
		childItems.add(accountSettingsChildren);				
		// -------------------------------------------------------------------------------------------------------------------------------------------
		// sonstige Einstellungen
		DrawerItem[] otherSettingsChildren = new DrawerItem[3];	
		DrawerItem settingsChild = new DrawerItem();									
		settingsChild.setTitle(getString(R.string.string_navigationdrawer_settings));
		settingsChild.setIcon(R.drawable.ic_navdraw_settings);
		otherSettingsChildren[0] = settingsChild;														// Einstellungen => sonstige Einstellungen	
		
		DrawerItem faqChild = new DrawerItem();									
		faqChild.setTitle(getString(R.string.string_navigationdrawer_faq));
		faqChild.setIcon(R.drawable.ic_navdraw_faq);
		otherSettingsChildren[1] = faqChild;																// FAQ => sonstige Einstellungen	
		
		DrawerItem developerInfoChild = new DrawerItem();							
		developerInfoChild.setTitle(getString(R.string.string_navigationdrawer_developerinfo));
		developerInfoChild.setIcon(R.drawable.ic_navdraw_developerinfo);
		otherSettingsChildren[2] = developerInfoChild;														// Info => sonstige Einstellungen		
		
		childItems.add(otherSettingsChildren);
	}
	
	public ArrayList<String> getIndividualGroupsOfUser(){
		expandGroupsChild = new ArrayList<String>();
		expandGroupsChild.add("Gruppe 1");
		expandGroupsChild.add("Gruppe 2");
		expandGroupsChild.add("Gruppe 3");
		expandGroupsChild.add("Gruppe 4");
		expandGroupsChild.add("Gruppe 5");
		return expandGroupsChild;
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
