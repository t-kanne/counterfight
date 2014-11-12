package de.woodpot.counterfight;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

	TextView counterDesc;
	TextView counter;
	Button refreshCounterButton;
	Button increaseCounterButton;
	Button showAllUsersOfGroupButton;
	Button openNoGroupActivity;
	Button loginActivity;
	Button openAllGroupsActivity;
	Button logoutButton;
	Button openGroupDetailActivity;
	
	// Variablen für den NavigationDrawer
	private DrawerLayout drawer;
	private ActionBarDrawerToggle toggle;
	private String[] navDrawArray;
	private ListView navDrawListView;
	
	// JSONParser Objekt erstellen
	JSONParser jParser = new JSONParser();
	
	// Server-Urls
	private static String url_read_counter = "http://www.counterfight.net/get_counter.php";
	private static String url_update_counter = "http://www.counterfight.net/update_counter.php";
	
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_COUNTER = "counter";
	private static final String TAG_USER = "user";
	private static final String TAG_COUNTERVALUE = "counterValue";
	
	// JSONArray für Counterdaten
	JSONArray counterData = null;
	String user;
	String counterValue = null;
	
	// JSON parser class
	JSONParser jsonParser = new JSONParser();
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		showAllUsersOfGroupButton = (Button) findViewById(R.id.btnShowAllCountersOfGroup);
		openNoGroupActivity = (Button) findViewById(R.id.btnOpenNoGroupActivity);
		loginActivity = (Button) findViewById(R.id.btnLoginActivity);
		openAllGroupsActivity = (Button) findViewById(R.id.btnOpenAllGroupsActivity);
		openGroupDetailActivity = (Button) findViewById(R.id.btnOpenGroupDetailActivity);
		logoutButton = (Button) findViewById(R.id.btnLogout);
		
		// Zuweisungen für den NavigationDrawer
		navDrawArray = getResources().getStringArray(R.array.navigation_drawer_string_array);
		drawer = (DrawerLayout) findViewById(R.id.drawer_layout); // befindet sich innerhalb activity_main.xml
		navDrawListView = (ListView) findViewById(R.id.navigation_drawer_listview);
		toggle = new ActionBarDrawerToggle(this, drawer, R.drawable.ic_drawer, R.string.string_navigationdrawer_open, R.string.string_navigationdrawer_close);
		drawer.setDrawerListener(toggle);
		
		// ListView Adapter für den NavigationDrawer
		navDrawListView.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, navDrawArray));
		
		// OnClickListener für die NavigationDrawer-Items
		navDrawListView.setOnItemClickListener(new DrawerItemClickListener());
		
		
		getSupportActionBar();
		
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		
		
		// Button Listener		
		showAllUsersOfGroupButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				openAllUsersOfGroup();
				finish();
			}
			
		});
		
		openNoGroupActivity.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), NoGroupActivity.class);
				startActivity(intent);				
			}
		});
		
		loginActivity.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
				startActivity(intent);		
			}
		});	
		
		openAllGroupsActivity.setOnClickListener(new OnClickListener() {

			 @Override
			 public void onClick(View v) {
			 
			 Intent intent = new Intent(getApplicationContext(), AllGroupsActivity.class);
			 startActivity(intent); 
			 }
			 
		});
		
		openGroupDetailActivity.setOnClickListener(new OnClickListener() {

			 @Override
			 public void onClick(View v) {
				 Intent intent = new Intent(getApplicationContext(), GroupDetailActivity.class);
				 startActivity(intent); 
			 }
			 
		});
		
		logoutButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SessionManager sm = new SessionManager(getApplicationContext());
				sm.clearSession();
			}
			
		});
		
	}
	
	public void openAllUsersOfGroup(){
		Intent intent = new Intent(this.getApplicationContext(), ShowAllUsersOfGroupActivity.class);
		Log.d("MainActivity: ", "Intent: " + intent.toString());
		startActivity(intent);
	}
	
	
	
	
	
	class UpdateCounter extends AsyncTask<String, String, String> {


		protected String doInBackground(String... args) {

			// getting updated data from EditTexts
			String newCounter = counter.getText().toString();

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(TAG_USER, "thom-ass"));
			params.add(new BasicNameValuePair(TAG_COUNTERVALUE, newCounter));
			Log.d("MainActivity JSON: ", "New Counter value: " + newCounter);

			// sending modified data through http request
			// Notice that update product url accepts POST method
			JSONObject json = null;

			try {
				json = jsonParser.makeHttpRequest(url_update_counter, "POST", params);
			} catch (Exception e){
				e.printStackTrace();
			}


			// check json success tag
			try {
				int success = json.getInt(TAG_SUCCESS);
				
				if (success == 1) {
					// successfully updated
					Log.d("MainActivity JSON: ", json.toString());
					
				} else {
					// failed to update product
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

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


}
