package de.woodpot.counterfight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class GroupDetailActivity extends ListActivity {

	// JSONParser Objekt erstellen
	JSONParser jParser = new JSONParser();
	SessionManager sm;
	
	ListAdapter adapter;
	
	Button increaseCounterButton;
	TextView groupName;
	
	final Context context = this;
	
	private ProgressDialog pDialog;
	
	// Server-Urls
	private static String url_get_groups = "http://counterfight.net/get_group_details.php";
	private static String url_update_counter = "http://counterfight.net/update_counter_value.php";
	
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_COUNTER = "get_details";
	private static final String TAG_USER = "user";
	private static final String TAG_COUNTERVALUE = "counterValue";
	private static final String TAG_GROUPNAME = "groupName";
	private static final String TAG_USERNAME = "userName";
	private static final String TAG_GROUPID = "groupId";

	//Strings IntentExtra
	String groupIdIntent;
	String groupNameIntent;
	
	// JSONArray für Counterdaten
	JSONArray counterData = null;
	private Map<String, String> users = new HashMap<String,String>(); 
	
	ArrayList<HashMap<String, String>> contactList = new ArrayList<HashMap<String, String>>();

	// JSON parser class
	JSONParser jsonParser = new JSONParser();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_group_detail);
		
		groupName = (TextView) findViewById(R.id.group_name);
		increaseCounterButton = (Button) findViewById(R.id.increase_button);
		
        contactList = new ArrayList<HashMap<String, String>>();
 
        ListView lv = getListView();
        
      //  ListView listView = (ListView) findViewById(R.id.list);
		//Toast.makeText(this, "GroupDetailActivity", Toast.LENGTH_LONG).show();
       
		new LoadGroupUser().execute();
		
		registerForContextMenu(getListView());
		
		increaseCounterButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				new UpdateCounterValue().execute();
				
			}	
		});
	}

	
	
	class LoadGroupUser extends AsyncTask<String, String, String> {
	
		@Override
		protected void onPreExecute() {
			pDialog = new ProgressDialog(GroupDetailActivity.this);
			pDialog.setMessage(GroupDetailActivity.this.getString(R.string.string_loginact_loading));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		 	
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if(extras != null){
			groupIdIntent = extras.getString("groupId");
			groupNameIntent = extras.getString("groupName");	
			Log.d("GroupDetailActivity", "intent groupId: " + groupIdIntent);
			Log.d("GroupDetailActivity,", "intent groupName: " + groupNameIntent);
			groupName.setText(groupNameIntent);
		}
		else{
			Log.d("GroupDetailActivity, Intent von AllGroups:", "extras: fail");	
		}

		 }
		
		@Override
		protected String doInBackground(String... args) {
			// Building Parameters	
			
				//groupId aus AllGroupsActivity übergeben
				String username = null;
				String groupId = null;
				
				sm = new SessionManager(getApplicationContext());
				if (sm.isLoggedIn() == true) {
					username = sm.getUsername();
				}
				
				final List<NameValuePair> params = new ArrayList<NameValuePair>();	
				params.add(new BasicNameValuePair(TAG_GROUPID, groupIdIntent));
				Log.d("GroupDetailActivity params: ", params.toString());
				JSONObject json = null;
			
				try {
					json = jParser.makeHttpRequest(url_get_groups, "POST", params);
					Log.d("GroupDetailActivity post anfrage: ", params.toString());
				} catch (Exception e){
					Log.e("GroupDetailActivity", "JSON (username POST): " + e.getMessage());
				}
					

				List<NameValuePair> params2 = new ArrayList<NameValuePair>();
				params2.add(new BasicNameValuePair(TAG_GROUPID, groupId));
				Log.d("GroupDetailActivity params2: ", params2.toString());
				
				JSONObject json2 = null;
				try {
					json2 = jParser.makeHttpRequest(url_get_groups, "GET", params);
				} catch (Exception e){
					Log.e("GroupDetailActivity", "JSON: " + e.getMessage());
				}
				
				// Check your log cat for JSON reponse
				Log.d("GroupDetailActivityFragment JSON: ", "JSONObject: " + json2.toString());

				// Checking for SUCCESS TAG
				
				try {
					int success = json2.getInt(TAG_SUCCESS);

				if (success == 1) {

					counterData = json2.getJSONArray(TAG_COUNTER);
					Log.d("GroupDetailActivityFragment JSON: ", "counterDataLength: " + counterData.length());

					// looping through All items
					for (int i = 0; i < counterData.length(); i++) {
						JSONObject c = counterData.getJSONObject(i);
						Log.d("GroupDetailActivityFragment JSON: ", "JSONArray: " + c.toString());
						
						// Storing each json item in variable
						users.put(c.getString(TAG_USERNAME), c.getString(TAG_COUNTERVALUE));
						Log.d("GroupDetailActivityFragment JSON: ", "COUNTER USER: " + users.toString());
						
						if (isCancelled()) break;
					}
					
					for (int i = 0; i < counterData.length(); i++) {
						JSONObject c = counterData.getJSONObject(i);
						
						String userName = c.getString(TAG_USERNAME);
						String countervalue = c.getString(TAG_COUNTERVALUE);
						
						// tmp hashmap for single contact
						HashMap<String, String> contact = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						contact.put(TAG_USERNAME, userName);
						contact.put(TAG_COUNTERVALUE, countervalue);
						//adding contact to contact list
						contactList.add(contact);
	
						//background-color hier ändern
						sm = new SessionManager(getApplicationContext());
						if (sm.isLoggedIn() == true) {
							username = sm.getUsername();
						}
					/*	für listview item highlighten
						for (int j = 0; j < contactList.size(); j++) {
						    if(contactList.get(j).equals(username)){
						       
						    }
						}
					*/	
										
						
						
					}
				
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(String file_url) {
			
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					Log.d("GroupDetailActivity JSON: ", "onPostExecute ausgeführt");
					BaseAdapter adapter = new SimpleAdapter(
							GroupDetailActivity.this, contactList,
		                    R.layout.group_detail_list_item, new String[] { TAG_USERNAME, TAG_COUNTERVALUE }, 
		                    new int[] { R.id.user_row_username, R.id.user_countervalue });
		 
		            setListAdapter(adapter);

		            pDialog.dismiss();
			         
				}
			}); 
		}
			
	}	
	
	

	
	
	class UpdateCounterValue extends AsyncTask<String, String, String> {
		
		@Override
		protected void onPreExecute() {}
			
		@Override
		protected String doInBackground(String... args) {
		
			//groupId aus AllGroupsActivity übergeben
			String username = null;
			String groupId = null;
			
			sm = new SessionManager(getApplicationContext());
			if (sm.isLoggedIn() == true) {
				username = sm.getUsername();
			}	
			
			final List<NameValuePair> increase_params = new ArrayList<NameValuePair>();	
			increase_params.add(new BasicNameValuePair(TAG_GROUPID, groupIdIntent));
			increase_params.add(new BasicNameValuePair(TAG_USERNAME, username));
			Log.d("GroupDetailActivity increase_params: ", increase_params.toString());
			JSONObject json4 = null;
		
			try {
				json4 = jParser.makeHttpRequest(url_update_counter, "POST", increase_params);
				Log.d("GroupDetailActivity POST: ", increase_params.toString());
			} catch (Exception e){
				Log.e("GroupDetailActivity", "JSON POST: " + e.getMessage());
			}
			try {
				int success = json4.getInt(TAG_SUCCESS);

				if (success == 1) {
					Log.d("GroupDetailActivityFragment JSON: ", "(get) success 1: update");		
									
				}
				else {
					Log.d("GroupDetailActivityFragment JSON: ", "(get) success 0: kein update");		
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		
		protected void onPostExecute(String file_url) {
			
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */			
					showAlert();
				}
			}); 
		}
		
	}
		
	public void showAlert(){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle(R.string.string_groupdetailact_alerttitle);
		alertDialogBuilder.setMessage(R.string.string_groupdetailact_alerttext);
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setPositiveButton(R.string.string_groupdetailact_alertokay, null); 
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.reload_groups, menu);
	    return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		
	switch (item.getItemId()) {
		case R.id.action_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
        
		case R.id.action_reload:
			new LoadGroupUser().execute();
			BaseAdapter adapter = new SimpleAdapter(
				GroupDetailActivity.this, contactList,
                R.layout.group_detail_list_item, new String[] { TAG_USERNAME, TAG_COUNTERVALUE }, 
                new int[] { R.id.user_row_username, R.id.user_countervalue });

		contactList.clear();
		Log.d("GroupDetailActivity: ", "alte ListView gecleart");	
		adapter.notifyDataSetChanged();
		Log.d("GroupDetailActivity: ", "neue ListView erstellt");	
        return true;
            
		default:
        return super.onOptionsItemSelected(item);
    }			
	}
}
