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

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;


public class GroupDetailActivity extends ListActivity {

	// JSONParser Objekt erstellen
	JSONParser jParser = new JSONParser();
	SessionManager sm;
	
	private ProgressDialog pDialog;
	
	// Server-Urls
	private static String url_get_groups = "http://counterfight.net/get_group_details.php";
	
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_COUNTER = "get_details";
	private static final String TAG_USER = "user";
	private static final String TAG_COUNTERVALUE = "counterValue";
	private static final String TAG_GROUPNAME = "groupName";
	private static final String TAG_USERNAME = "userName";
	private static final String TAG_GROUPID = "groupId";
	
	String parameter1;
	String parameter2;
	
	// JSONArray f�r Counterdaten
	JSONArray counterData = null;
	private Map<String, String> users = new HashMap<String,String>(); 
	
	ArrayList<HashMap<String, String>> contactList = new ArrayList<HashMap<String, String>>();

	// JSON parser class
	JSONParser jsonParser = new JSONParser();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_group_detail);
		 
        contactList = new ArrayList<HashMap<String, String>>();
 
        ListView lv = getListView();
		
		//Toast.makeText(this, "GroupDetailActivity", Toast.LENGTH_LONG).show();
       
		new LoadGroupUser().execute();
		
		registerForContextMenu(getListView());
	}

	
	
	
	
	
	
	class LoadGroupUser extends AsyncTask<String, String, String> {
	
		@Override
		 protected void onPreExecute() {
		 	
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if(extras != null){
			parameter1 = extras.getString("groupId");
			parameter2 = extras.getString("groupName");	
			Log.d("GroupDetailActivity", "groupId: " + parameter1);
			Log.d("GroupDetailActivity,", "groupName: " + parameter2);
		}
		else{
			Log.d("GroupDetailActivity,intent", "Intent groupId fail");	
		}
		
		 
		// pDialog.setMessage(GroupDetailActivity.this.getResources().getString(R.string.string_loginact_loading));
		// pDialog.setIndeterminate(false);
		// pDialog.setCancelable(false);
		// pDialog.show();
		 }
		
		@Override
		protected String doInBackground(String... args) {
			// Building Parameters	
			
				
				//groupId aus AllGroupsActivity �bergeben
				String username = null;
				String groupId = null;
				
				sm = new SessionManager(getApplicationContext());
				if (sm.isLoggedIn() == true) {
					username = sm.getUsername();
				}
				
				final List<NameValuePair> params = new ArrayList<NameValuePair>();	
				params.add(new BasicNameValuePair(TAG_GROUPID, parameter1));
				Log.d("GroupDetailActivity params: ", params.toString());
				JSONObject json = null;
			
				try {
					json = jParser.makeHttpRequest(url_get_groups, "POST", params);
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
					
					
					
					ListAdapter adapter = new SimpleAdapter(
							GroupDetailActivity.this, contactList,
		                    R.layout.list_item, new String[] { TAG_USERNAME, TAG_COUNTERVALUE }, 
		                    new int[] { R.id.user_row_userName, R.id.user_countervalue });
		 
		            setListAdapter(adapter);
					
				
		           // pDialog.dismiss();
			         
				}
			}); 
		}
		

		
		
			
	}	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.group_detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
