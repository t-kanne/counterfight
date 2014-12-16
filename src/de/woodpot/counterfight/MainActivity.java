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
import android.media.audiofx.BassBoost.Settings;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
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


public class MainActivity extends FragmentActivity implements FragmentSwitcher {
	
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
	private GroupDetailFragment groupDetailFragment;
	private InfoFragment infoFragment;
	private FAQFragment faqFragment;
	private CreateGroupDialog createGroupDialog;
	private SearchGroupDialog searchGroupDialog;
	private NoGroupFragment noGroupFragment;
	
	FragmentTransaction fragmentTransaction;
	
	// Instanziieren des FragmentSwitcher Interfaces
	FragmentSwitcher fragmentSwitcher;
	
	// Internetverbindung ständig überprüfen können
	CheckInternetConnection checkInternetConnection;
	
	// Variablen für den NavigationDrawer
	private DrawerLayout drawer;
	private ActionBarDrawerToggle toggle;
	SimpleExpandableListAdapter expListAdapter;
	private ExpandableListView expListView;
	
	// Gruppenposition-Konstanten
	private static int GROUP_POS_GROUPMGMT = 0;
	private static int GROUP_POS_ALLGROUPS = 1;
	private static int GROUP_POS_ACCOUNT = 2;
	private static int GROUP_POS_OTHER = 3;
	
	// Child-Position-Konstanten
	private static final int CHILD_POS_GROUPSUMMARY = 0;
	private static final int CHILD_POS_CREATE_GROUP = 1;
	private static final int CHILD_POS_SEARCH_GROUP = 2;
	private static final int CHILD_POS_CHANGE_ACCOUNT_DATA = 0;
	private static final int CHILD_POS_LOGOUT = 1;
	private static final int CHILD_POS_SETTINGS = 0;
	private static final int CHILD_POS_FAQ = 1;
	private static final int CHILD_POS_DEVELOPERINFO = 2;
	
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
		fragmentSwitcher = (FragmentSwitcher) this;
		
		// Fragmente instanziieren
		registerFragment = (RegisterFragment) Fragment.instantiate(this, RegisterFragment.class.getName(), null);
		allGroupsFragment = (AllGroupsFragment) Fragment.instantiate(this, AllGroupsFragment.class.getName(), null);
		groupDetailFragment = (GroupDetailFragment) Fragment.instantiate(this, GroupDetailFragment.class.getName(), null);
		infoFragment = (InfoFragment) Fragment.instantiate(this, InfoFragment.class.getName(), null);
		faqFragment = (FAQFragment) Fragment.instantiate(this, FAQFragment.class.getName(), null);
		createGroupDialog = (CreateGroupDialog) Fragment.instantiate(this, CreateGroupDialog.class.getName(), null);
		searchGroupDialog = (SearchGroupDialog) Fragment.instantiate(this, SearchGroupDialog.class.getName(), null);
		noGroupFragment = (NoGroupFragment) Fragment.instantiate(this, NoGroupFragment.class.getName(), null);

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
		
		// Der OnGroupClickListener sorgt nur dafür, dass die Gruppen nicht einklappbar sind. Sie führen zu keinem Fragment
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
		
		// Die Child-Elemente sind die Fragmente und Activities, auf die der Nutzer zugreifen kann
		expListView.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				
					fragmentTransaction = getSupportFragmentManager().beginTransaction();
					
					if (groupPosition == GROUP_POS_GROUPMGMT) {
						switch(childPosition) {
							case CHILD_POS_GROUPSUMMARY:
								fragmentTransaction.replace(R.id.main_activity_content, allGroupsFragment);
								fragmentTransaction.commit();
								drawer.closeDrawers();
								break;
								
							case CHILD_POS_CREATE_GROUP:
								createGroupDialog.show(fragmentTransaction, "Dialog Fragment");
								drawer.closeDrawers();
								break;
							
							case CHILD_POS_SEARCH_GROUP:
								searchGroupDialog.show(fragmentTransaction, "Dialog Fragment");
								drawer.closeDrawers();
								break;
						}
					}
					
					if (groupPosition == GROUP_POS_ALLGROUPS) {
							Toast.makeText(MainActivity.this, "Funktion noch nicht verfügbar", Toast.LENGTH_SHORT).show();
					}
					
					if (groupPosition == GROUP_POS_ACCOUNT) {
						switch(childPosition) {
							case CHILD_POS_CHANGE_ACCOUNT_DATA:
								Toast.makeText(MainActivity.this, "Funktion noch nicht verfügbar", Toast.LENGTH_SHORT).show();
								break;
								
							case CHILD_POS_LOGOUT:							
								if (sessionManager.isLoggedIn() == true) {
									sessionManager.clearSession();
								}
								Intent intent = new Intent(MainActivity.this, LoginActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
								startActivity(intent);
						}
					}
					
					if (groupPosition == GROUP_POS_OTHER){					
						switch(childPosition) {
							case CHILD_POS_SETTINGS:
								Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
								startActivity(intent);
								break;
								
							case CHILD_POS_FAQ:
								fragmentTransaction.replace(R.id.main_activity_content, faqFragment);
								fragmentTransaction.commit();
								drawer.closeDrawers();
								break;
								
							case CHILD_POS_DEVELOPERINFO:
								fragmentTransaction.replace(R.id.main_activity_content, infoFragment);
								fragmentTransaction.commit();
								drawer.closeDrawers();
								break;
						}				
					}
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
	public void onBackPressed() {
		int fragCount = getSupportFragmentManager().getBackStackEntryCount();
		if (fragCount == 0) {
			super.onBackPressed();
		} else {
			getSupportFragmentManager().popBackStack();
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
				if (success == 2) {
					groupIdIntent = json.getString("groupId");
					groupNameIntent = json.getString("groupName");
					noOfGroups = json.getString("noOfGroups");
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
				fragmentSwitcher.startGroupDependingActivity(noOfGroups);
				
			}
		}		
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		Log.d("MainActivity", "onResume() ausgeführt");
	}
	
	
	// Überschriebene Methode vom Interface "FragmentSwitcher"
	// Wird von CreateGroupDialog() und SearchGroupDialog() aufgerufen, um Weiterleitung auf das GroupDetailFragment zu ermöglichen
	@Override
	public void replaceFragment(Bundle fragmentData, Fragment fragment) {
			
		Bundle newFragmentData = new Bundle();
		newFragmentData.putString("groupId", fragmentData.getString("groupId"));
		newFragmentData.putString("groupName", fragmentData.getString("groupName") + " (Id: " + fragmentData.getString("groupId") + ")");
		Log.d("MainActivity respond: ", "FragmentName: " + fragmentData.getString("fragmentName"));
		fragment.setArguments(newFragmentData);
		
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fm.beginTransaction();
		
		fragmentTransaction.replace(R.id.main_activity_content, fragment, fragment.toString());
		fragmentTransaction.commit(); 
		Log.d("LoginActivity", "RESPOND groupId: " + groupIdIntent +groupNameIntent);
		
	}

	@Override
	public void startGroupDependingActivity(String noOfGroups) {
		int noOfGroupsInt;
		
		try {
			noOfGroupsInt = Integer.valueOf(noOfGroups);
			Log.d("LoginActivity: ", "Anzahl Gruppen: " + noOfGroupsInt);
			FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
			
			if (noOfGroupsInt == 0) {
				fragmentTransaction.replace(R.id.main_activity_content, noGroupFragment);
				fragmentTransaction.commit();
			}
			
			if (noOfGroupsInt == 1) {
				Bundle fragmentData = new Bundle();
				fragmentData.putString("groupId", groupIdIntent);
				fragmentData.putString("groupName", groupNameIntent + " (Id: " + groupIdIntent + ")");
				groupDetailFragment.setArguments(fragmentData);
				fragmentTransaction.replace(R.id.main_activity_content, groupDetailFragment);
				fragmentTransaction.commit(); 
				Log.d("LoginActivity", "groupId: " + groupIdIntent +groupNameIntent);
				
			}
			if (noOfGroupsInt > 1) {
				fragmentTransaction.replace(R.id.main_activity_content,	allGroupsFragment);
				fragmentTransaction.commit();
				drawer.closeDrawers();
			}
		} catch (NumberFormatException e) {
			//Intent intent = new Intent(this, NoGroupActivity.class);
			//startActivity(intent);
			finish();
		}
		
	}
	
}
