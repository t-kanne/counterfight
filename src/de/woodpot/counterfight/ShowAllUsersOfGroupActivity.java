package de.woodpot.counterfight;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.os.Build;

public class ShowAllUsersOfGroupActivity extends ListActivity {
	
	// JSONParser Objekt erstellen
	JSONParser jParser = new JSONParser();
	
	// Server-Urls
	private static String url_read_counter = "http://www.dayvision.de/counterfight/get_counter.php";
	
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_COUNTER = "counter";
	private static final String TAG_USER = "user";
	private static final String TAG_COUNTERVALUE = "counterValue";
	
	// JSONArray f�r Counterdaten
	JSONArray counterData = null;
	String[] user;
	ArrayList<String[]> users;
	ShowAllUsersOfGroupAdapter adapter;
	
	// JSON parser class
	JSONParser jsonParser = new JSONParser();
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {

		}
		users = new ArrayList<String[]>();
		//String[] erster = {"thomas", "55"};
		//String [] zweiter = {"richard", "22"};
		//users.add(erster);
		//users.add(zweiter);
		Toast.makeText(this, "Test", Toast.LENGTH_LONG).show();
		new LoadAllUserCounter().execute();
		
		//adapter = new ShowAllUsersOfGroupAdapter(this, users);
		//this.setListAdapter(adapter);
		//registerForContextMenu(getListView());
		
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
		protected String doInBackground(String... args) {
			// Building Parameters	
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			Log.d("ShowAllUsersOfGroupActivity: ", params.toString());
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_read_counter, "GET", params);
			
			// Check your log cat for JSON reponse
			Log.d("ShowAllUsersOfGroupFragment JSON: ", "JSONObject: " + json.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// products found
					// Getting Array of Products
					counterData = json.getJSONArray(TAG_COUNTER);

					// looping through All items
					for (int i = 0; i < counterData.length(); i++) {
						JSONObject c = counterData.getJSONObject(i);
						Log.d("ShowAllUsersOfGroupFragment JSON: ", "JSONArray: " + c.toString());
						
						// Storing each json item in variable
						user[i] = c.getString(TAG_USER);
						Log.d("ShowAllUsersOfGroupFragment JSON: ", "Counter user: " + user[i]);
						user[i+1] = c.getString(TAG_COUNTERVALUE);
						Log.d("ShowAllUsersOfGroupFragment JSON: ", "Counter value: " + user[i+1]);
						users.add(user);
					}		
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					ShowAllUsersOfGroupAdapter adapter = new ShowAllUsersOfGroupAdapter(
							ShowAllUsersOfGroupActivity.this, users);
					// updating listview
					setListAdapter(adapter);
				}
			});

		}
		
	}
	
}