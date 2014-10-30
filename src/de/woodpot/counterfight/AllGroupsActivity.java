package de.woodpot.counterfight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.woodpot.counterfight.ShowAllUsersOfGroupActivity.LoadAllUserCounter;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class AllGroupsActivity extends ListActivity  {

	// JSONParser Objekt erstellen
		JSONParser jParser = new JSONParser();
		
		// Server-Urls
		private static String url_read_user = "http://counterfight.net/get_all_groups.php";
		
		// JSON Node names
		private static final String TAG_SUCCESS = "success";
		private static final String TAG_COUNTER = "counter";
		private static final String TAG_COUNTER2 = "counter2";
		private static final String TAG_USER = "user";
		private static final String TAG_GROUPNAME = "groupName";
		private static final String TAG_COUNTERVALUE = "counterValue";
		
		// JSONArray für Counterdaten
		JSONArray counterData = null;
		private Map<String, String> users = new HashMap<String,String>(); 
		//ArrayList<String> usersAL;
		ShowAllUsersOfGroupAdapter adapter;
		
		// JSON parser class
		JSONParser jsonParser = new JSONParser();
		

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			if (savedInstanceState == null) {
			}
			Toast.makeText(this, "AllGroupsActivity", Toast.LENGTH_LONG).show();
			new LoadAllUserCounter().execute();
			
			//adapter = new ShowAllUsersOfGroupAdapter(this, users);
			//this.setListAdapter(adapter);
			registerForContextMenu(getListView());
			
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
			
		/*	@Override
			protected void onPreExecute() {
				super.onPreExecute();
				pDialog = new ProgressDialog(ShowAllUsersOfGroupActivity.this);
				pDialog.setMessage("Loading products. Please wait...");
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(false);
				pDialog.show();
			}
		*/	
			@Override
			protected String doInBackground(String... args) {
				// Building Parameters	
				try {
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					Log.d("AllGroupsActivity: ", params.toString());
					// getting JSON string from URL
					JSONObject json = jParser.makeHttpRequest(url_read_user, "GET", params);
					
					// Check your log cat for JSON reponse
					Log.d("AllGroupsActivityFragment JSON: ", "JSONObject: " + json.toString());

				
					// Checking for SUCCESS TAG
					int success = json.getInt(TAG_SUCCESS);

					if (success == 1) {
						// products found
						// Getting Array of Products
						counterData = json.getJSONArray(TAG_COUNTER);
						Log.d("AllGroupsActivityFragment JSON: ", "counterDataLenght: " + counterData.length());

						// looping through All items
						for (int i = 0; i < counterData.length(); i++) {
							JSONObject c = counterData.getJSONObject(i);
							Log.d("AllGroupsActivityFragment JSON: ", "JSONArray: " + c.toString());
							
							// Storing each json item in variable
							users.put(c.getString(TAG_GROUPNAME), c.getString(TAG_COUNTERVALUE));
							Log.d("AllGroupsActivityFragment JSON: ", "Counter user: " + users.toString());
							
							if (isCancelled()) break;
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
						AllGroupsActivityAdapter adapter = new AllGroupsActivityAdapter(
								AllGroupsActivity.this, users);
						// updating listview
						Log.d("AllGroupsActivityFragment JSON: ", "Adapterusers: " + users.toString());
						setListAdapter((ListAdapter) adapter);
					}
				}); 
			}
				
		}	
}
