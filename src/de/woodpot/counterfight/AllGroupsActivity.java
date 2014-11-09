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

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class AllGroupsActivity extends ListActivity  {

	// JSONParser Objekt erstellen
		JSONParser jParser = new JSONParser();
		SessionManager sm;
		
		// Server-Urls
		private static String url_get_groups = "http://counterfight.net/get_all_groups.php";
		
		private String usernameString;
		
		// JSON Node names
		private static final String TAG_SUCCESS = "success";
		private static final String TAG_COUNTER = "get_groups";
		private static final String TAG_USER = "user";
		private static final String TAG_GROUPID = "groupId";
		private static final String TAG_COUNTERVALUE = "counterValue";
		private static final String TAG_GROUPNAME = "groupName";
		private static final String TAG_USERFIRST = "user_first";
		private static final String TAG_OWNPLACE = "own_place";
		private static final String TAG_USERNAME = "username";
		
		// JSONArray für Counterdaten
		JSONArray counterData = null;
		private Map<String, String> users = new HashMap<String,String>(); 
		
		ArrayList<HashMap<String, String>> contactList = new ArrayList<HashMap<String, String>>();

		// JSON parser class
		JSONParser jsonParser = new JSONParser();
		

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
				
			if (savedInstanceState == null) {
			}
			Toast.makeText(this, "AllGroupsActivity", Toast.LENGTH_LONG).show();
			new LoadAllUserCounter().execute();
			
			registerForContextMenu(getListView());
			
			
		}


		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			int id = item.getItemId();
			if (id == R.id.action_settings) {
				return true;
			}
			return super.onOptionsItemSelected(item);
		}
		
		@Override
		public void onBackPressed() {
		    Intent startMain = new Intent(Intent.ACTION_MAIN);      
	        startMain.addCategory(Intent.CATEGORY_HOME);                        
	        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);          
	        startActivity(startMain); 
		}


		public static class PlaceholderFragment extends Fragment {

			public PlaceholderFragment() {
			}

			@Override
			public View onCreateView(LayoutInflater inflater, ViewGroup container,
					Bundle savedInstanceState) {
				View rootView = inflater
						.inflate(R.layout.fragment_show_all_users_of_group,
								container, false);
				return rootView;
			}
		}
		
		class LoadAllUserCounter extends AsyncTask<String, String, String> {
			ProgressDialog pDialog;
			
		
			@Override
			protected String doInBackground(String... args) {
				// Building Parameters	
					
					String username = null;
					sm = new SessionManager(getApplicationContext());
					if (sm.isLoggedIn() == true) {
						username = sm.getUsername();
					}
				
					final List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair(TAG_USERNAME, username));
					Log.d("AllGroupsActivity: ", params.toString());
					JSONObject json = null;
				
					try {
						json = jParser.makeHttpRequest(url_get_groups, "POST", params);
					} catch (Exception e){
						Log.e("AllGroupsActivity", "JSON (username POST): " + e.getMessage());
					}
				
								
					List<NameValuePair> params2 = new ArrayList<NameValuePair>();
					// getting JSON string from URL
					
					JSONObject json2 = null;
					try {
						json2 = jParser.makeHttpRequest(url_get_groups, "GET", params);
					} catch (Exception e){
						Log.e("AllGroupsActivity", "JSON: " + e.getMessage());
					}
					
					
					// Check your log cat for JSON reponse
					Log.d("AllGroupsActivityFragment JSON:: ", "JSONObject: " + json2.toString());

				
					// Checking for SUCCESS TAG
					
					try {
						int success = json2.getInt(TAG_SUCCESS);

					if (success == 1) {

						counterData = json2.getJSONArray(TAG_COUNTER);
						Log.d("AllGroupsActivityFragment JSON: ", "counterDataLenght: " + counterData.length());

						// looping through All items
						for (int i = 0; i < counterData.length(); i++) {
							JSONObject c = counterData.getJSONObject(i);
							Log.d("AllGroupsActivityFragment JSON: ", "JSONArray: " + c.toString());
							
							// Storing each json item in variable
							users.put(c.getString(TAG_GROUPNAME), c.getString(TAG_USERFIRST));
							Log.d("AllGroupsActivityFragment JSON: ", "COUNTER USER: " + users.toString());
							
							if (isCancelled()) break;
						}
						
						for (int i = 0; i < counterData.length(); i++) {
							JSONObject c = counterData.getJSONObject(i);
							
							String groupName = c.getString(TAG_GROUPNAME);
							String firstPlace = c.getString(TAG_USERFIRST);
							String ownPlace = c.getString(TAG_OWNPLACE);
							
							// tmp hashmap for single contact
							HashMap<String, String> contact = new HashMap<String, String>();

							// adding each child node to HashMap key => value
							contact.put(TAG_GROUPNAME, groupName);
							contact.put(TAG_USERFIRST, firstPlace);
							contact.put(TAG_OWNPLACE, ownPlace);
							
							// adding contact to contact list
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
				
				// dismiss the dialog after getting all products
				//pDialog.dismiss();
				// updating UI from Background Thread
				runOnUiThread(new Runnable() {
					public void run() {
						/**
						 * Updating parsed JSON data into ListView
						 * */
						ListAdapter adapter = new SimpleAdapter(
								AllGroupsActivity.this, contactList,
								R.layout.category_row_layout2, new String[] { TAG_GROUPNAME, TAG_USERFIRST,
										TAG_OWNPLACE }, new int[] { R.id.user_row_groupName,
										R.id.user_row_first_place, R.id.user_row_own_place });
						// updating listview
						Log.d("AllGroupsActivityFragment JSON: ", "Adapterusers: " + users.toString());
						setListAdapter (adapter);
					}
				}); 
			}
				
		}	
}
