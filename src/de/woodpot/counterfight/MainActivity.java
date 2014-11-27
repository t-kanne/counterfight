package de.woodpot.counterfight;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends FragmentActivity {
	
	/** Die MainActivity ist für den Nutzer im Prinzip nicht sichtbar. Sie entscheidet nur,
	 * wohin der Nutzer geleitet werden soll und ist Layout für den NavigationDrawer
	 */
	
	private ProgressDialog pDialog;
	private String usernameString;
	private String passwordString;
	private String groupIdIntent;
	private String groupNameIntent;
	
	// Objekt vom SessionManager erstellen
	SessionManager sessionManager;
	
	// JSONParser Objekt erstellen
	JSONParser jParser = new JSONParser();
	
	// Server-Urls
	private static String url_check_user = "http://www.counterfight.net/login_user.php";
	private static String url_count_user_groups = "http://www.counterfight.net/count_user_groups.php";
	
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_USER = "user";
	private static final String TAG_USERNAME = "username";
	private static final String TAG_PASSWORD = "password";
	private static final String TAG_GROUPID = "groupId";
	private static final String TAG_GROUPNAME = "groupName";

	
	// JSON Arrays
	JSONArray userData = null;
	String noOfGroups = null;
	Boolean correctUserdata = false;
	
	// JSON parser class
	JSONParser jsonParser = new JSONParser();
	
	Context context;
	
	// Fragmente
	private RegisterFragment registerFragment;
	private AllGroupsFragment allGroupsFragment;
	
	// Variablen für den NavigationDrawer
	private DrawerLayout drawer;
	private ActionBarDrawerToggle toggle;
	SimpleExpandableListAdapter expListAdapter;
	private ExpandableListView expListView;
	
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
		
		// SessionManager-Object benötigt, um Login-Status abzurufen
		sessionManager = new SessionManager(getApplicationContext());
		
		// Fragmente instanziieren
		registerFragment = (RegisterFragment) Fragment.instantiate(this, RegisterFragment.class.getName(), null);
		allGroupsFragment = (AllGroupsFragment) Fragment.instantiate(this, AllGroupsFragment.class.getName(), null);
		
		//FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		//fragmentTransaction.add(R.id.main_activity_content, allGroupsFragment);
		//fragmentTransaction.commit();
		
		getActionBar();
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		setGroupData();
		setChildGroupData();
		initializeDrawer();
		
		
		if (sessionManager.isLoggedIn() == false) {					// Login-Status des Nutzers überprüfen.
			Intent intent = new Intent(this, LoginActivity.class); 
			startActivityForResult(intent, 0);
			
		} else {
			new CountUserGroups().execute();						// Gruppen des Users zählen, um ihn zur entsprechenden Activity weiterzuleiten
		}
				
		
		// ***************************************************************************************
		// AB HIER: ClickListener für den NavigationDrawer
		// ***************************************************************************************
		
		expListView.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) { 
				if (getIndividualGroupsOfUser().size() > 0 && groupPosition == 1) {		// Nur Gruppe 1 (Einzelgruppen) kann eingeklappt werden
					return false;	
				} else {
					return true; // Gruppen können nicht eingeklappt werden
				}	
			}
		});	
		
		expListView.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
					return false;
			}
		});
			
	}
	
	public void initializeDrawer(){
		// Zuweisungen für den NavigationDrawer
		drawer = (DrawerLayout) findViewById(R.id.drawer_layout); // befindet sich innerhalb activity_main.xml		
		toggle = new ActionBarDrawerToggle(this, drawer, R.drawable.ic_drawer, R.string.string_navigationdrawer_open, R.string.string_navigationdrawer_close);
		drawer.setDrawerListener(toggle);
		
		// ExpandableListView finden
		expListView = (ExpandableListView) findViewById(R.id.listview_navigationdrawer_mainmenu);
		
		// Adapter verbinden
		expListAdapter = new SimpleExpandableListAdapter(this, groupItems, childItems);
		expListView.setAdapter(expListAdapter);
		
		// Gruppen standardmäßig ausklappen
		expListView.expandGroup(0);
		if (getIndividualGroupsOfUser().size() < 1) {
			expListView.expandGroup(1);	
		}
		expListView.expandGroup(2);																	
		expListView.expandGroup(3);	
		expListView.setGroupIndicator(null);	
	}
	
	// ######### WICHTIG #############
	// Die Reihenfolge der folgende Methoden-Abschnitte muss zwingend so eingehalten werden. Wenn die Menüpunkte verschoben werden sollen,
	// muss das in der setGroupData und der setChildData Methode jeweils synchron passieren. Ansonsten werden die Zuordnungen vertauscht!
	
	public void setGroupData() {
		// Gruppenverwaltung (0)
		DrawerItem groupSettingsChild = new DrawerItem();													// DrawerItem-Object für Gruppenverwaltung
		groupSettingsChild.setTitle(getString(R.string.string_navigationdrawer_groupsettings));				// Titel
		groupSettingsChild.setLayoutType(LAYOUT_SECTION_TITLE);												// Section-Layout enthält kein Icon
		groupSettingsChild.setIcon(R.drawable.ic_navdraw_onegroup);
		groupItems.add(groupSettingsChild);																	// ab in die Group-ArrayList damit
		
		if (getIndividualGroupsOfUser().size() > 0) {														// "Gruppen aufklappen" nur zeigen, wenn vorhanden
			// Gruppen aufklappen (1)
			DrawerItem expandGroupData = new DrawerItem();	
			expandGroupData.setTitle(getString(R.string.string_navigationdrawer_expandgroups));					// Titel
			expandGroupData.setLayoutType(LAYOUT_TEXT_ONLY);													// Layout // LAYOUT_TEXT_ONLY enthält kein Icon, daher null bei KEY_ICON
			expandGroupData.setExtras(R.string.string_navigationdrawer_collapsegroups);							// Extra: zusätzlicher Titel bei ausgeklapptem Menü
			groupItems.add(expandGroupData);
		}
			
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
		// Gruppen ausklappen	- SONDERFALL: für jedes Element wird hier dasselbe Icon verwendet
		if (getIndividualGroupsOfUser().size() > 0) {
			DrawerItem[] individualUserGroupChildren = new DrawerItem[getIndividualGroupsOfUser().size()]; // Array-List für alle Usergruppen
			Log.d("MainActivity: ", "getIndividualGroupsOfUser().size() = " + getIndividualGroupsOfUser().size());
			DrawerItem individualUserGroup;
			for (int i = 0; this.getIndividualGroupsOfUser().size() > i; i++) {
				individualUserGroup = new DrawerItem();													// DrawerItem-Object für persönliche Gruppe
				individualUserGroup.setTitle(this.getIndividualGroupsOfUser().get(i));					// Namen aus Array-Liste lesen
				individualUserGroup.setIcon(R.drawable.ic_navdraw_onegroup);
				individualUserGroupChildren[i] = individualUserGroup;
			}
			childItems.add(individualUserGroupChildren); 
		}
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
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		Log.d("MainActivity", "onActivityResult ausgeführt. arg0: " + arg0);
		
		if (sessionManager.isLoggedIn() == false) {					// Login-Status des Nutzers überprüfen.
			Intent intent = new Intent(this, LoginActivity.class); 
			startActivityForResult(intent, 0);
			
		} else {
			new CountUserGroups().execute();						// Gruppen des Users zählen, um ihn zur entsprechenden Activity weiterzuleiten
		}
	}

	
	class CountUserGroups extends AsyncTask <String, String, String> {
		
		protected void onPreExecute() {
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage(MainActivity.this.getString(R.string.string_allact_loading));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		
		protected String doInBackground(String... args) {
			Log.d("LoginAcitivty: ", "CountUserGroups");
			final List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(TAG_USERNAME, sessionManager.getUsername()));
			JSONObject json = null;
			
			try {
				json = jParser.makeHttpRequest(url_count_user_groups, "POST", params);
				Log.d("LoginActivity", "JSON countusergroups post ");
			} catch (Exception e){
				Log.e("LoginActivity", "JSON (UserGroups): " + e.getMessage());
			}
			
			try {
				int success = json.getInt(TAG_SUCCESS);
				
				if (success == 1) {
					
					noOfGroups = json.getString("noOfGroups");
					Log.d("LoginActivity: ", "noOfGroups: " + noOfGroups + " for user " + sessionManager.getUsername());
				
				}
				else {
					MainActivity.this.runOnUiThread(new Runnable() {
						  public void run() {
						    Toast.makeText(MainActivity.this, "Anzahl der Gruppen nicht ermittelt", Toast.LENGTH_LONG).show();
						  }
					});
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String result){
			if (sessionManager.isLoggedIn() == true){
				pDialog.dismiss();
				startGroupDependingActivity(noOfGroups);
			}
		}		
	}
	
	public void startGroupDependingActivity(String noOfGroups) {
		int noOfGroupsInt;
		
		try {
			noOfGroupsInt = Integer.valueOf(noOfGroups);
			Log.d("LoginActivity: ", "Anzahl Gruppen: " + noOfGroupsInt);
			
			if (noOfGroupsInt == 0) {
				Intent intent = new Intent(this, NoGroupActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);
				finish();
			}
			
			if (noOfGroupsInt == 1) {
				Intent intent = new Intent(this, GroupDetailActivity.class);
				intent.putExtra("groupId", groupIdIntent);
				intent.putExtra("groupName", groupNameIntent);
				Log.d("LoginActivity", "groupId: " + groupIdIntent +groupNameIntent);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);
				finish();
			}
			if (noOfGroupsInt > 1) {
				Intent intent = new Intent(this, AllGroupsActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);
				finish();
			}
		} catch (NumberFormatException e) {
			Intent intent = new Intent(this, NoGroupActivity.class);
			startActivity(intent);
			finish();
		}
				
	}
}
