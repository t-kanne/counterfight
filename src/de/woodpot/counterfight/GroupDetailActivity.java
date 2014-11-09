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
	
	// Server-Urls
	private static String url_get_groups = "http://counterfight.net/get_group_details.php";
	
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_COUNTER = "get_details";
	private static final String TAG_USER = "user";
	private static final String TAG_GROUPID = "groupId";
	private static final String TAG_COUNTERVALUE = "counterValue";
	private static final String TAG_GROUPNAME = "groupName";
	private static final String TAG_USERNAME = "userName";
	
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
		 
        contactList = new ArrayList<HashMap<String, String>>();
 
        ListView lv = getListView();
		
		Toast.makeText(this, "GroupDetailActivity", Toast.LENGTH_LONG).show();
		new LoadGroupUser().execute();
		
		registerForContextMenu(getListView());
	}

	
	
	
	
	
	
	class LoadGroupUser extends AsyncTask<String, String, String> {
		ProgressDialog pDialog;
		
		@Override
		protected String doInBackground(String... args) {
			// Building Parameters	
			
				/*
				//groupId aus AllGroupsActivity übergeben
				
				String username = null;
				sm = new SessionManager(getApplicationContext());
				if (sm.isLoggedIn() == true) {
					username = sm.getUsername();
				}
				
				final List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair(TAG_GROUPID, username));
				JSONObject json = null;
			
				try {
					json = jParser.makeHttpRequest(url_get_groups, "POST", params);
				} catch (Exception e){
					Log.e("GroupDetailActivity", "JSON (username POST): " + e.getMessage());
				}
				*/	
				
				
				String username = null;
				List<NameValuePair> params2 = new ArrayList<NameValuePair>();
				params2.add(new BasicNameValuePair(TAG_GROUPID, username));
				Log.d("GroupDetailActivity: ", params2.toString());
				
				JSONObject json2 = null;
				try {
					json2 = jParser.makeHttpRequest(url_get_groups, "GET", params2);
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
					
					
					
					
					/*
					
					 ListAdapter adapter = new SimpleAdapter(
							 GroupDetailActivity.this, contactList,
			                 R.layout.activity_group_detail,                                            
			                 new String[] {TAG_USERNAME, TAG_COUNTERVALUE},          
			                 new int[] {R.id.user_row_userName, R.id.user_countervalue});  

			         // Bind to our new adapter.
			         setListAdapter(adapter);
			         */
			         
			         
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
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
