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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class GroupDetailFragment extends ListFragment {

	// JSONParser Objekt erstellen
	JSONParser jParser = new JSONParser();
	SessionManager sm;
	
	ListAdapter adapter;
	Context context;
	
	Button increaseCounterButton;
	TextView groupNameTextView;
	
	CheckInternetConnection checkInternet;		
	
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
	static String groupIdIntent;
	static String groupNameIntent;
	
	// JSONArray für Counterdaten
	JSONArray counterData = null;
	private Map<String, String> users = new HashMap<String,String>(); 
	
	ArrayList<HashMap<String, String>> contactList = new ArrayList<HashMap<String, String>>();

	// JSON parser class
	JSONParser jsonParser = new JSONParser();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity();
		sm = new SessionManager(context);
		setHasOptionsMenu(true);
	
        contactList = new ArrayList<HashMap<String, String>>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_group_detail, container, false);
		groupNameTextView = (TextView) layout.findViewById(R.id.group_name);
		increaseCounterButton = (Button) layout.findViewById(R.id.increase_button);
		ListView ls = (ListView) layout.findViewById(android.R.id.list);
		registerForContextMenu(ls);
		
		Bundle extras = getArguments();
		if(extras != null){
			groupIdIntent = extras.getString("groupId");
			groupNameIntent = extras.getString("groupName");	
			Log.d("GroupDetailActivity", "intent groupId: " + groupIdIntent);
			Log.d("GroupDetailActivity,", "intent groupName: " + groupNameIntent);
			groupNameTextView.setText(groupNameIntent);
		}
		else{
			Log.d("GroupDetailActivity, Intent von AllGroups:", "extras: fail");	
		}
		
		new LoadGroupUser().execute();
		
		increaseCounterButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GroupDetailFragment gdf = new GroupDetailFragment();
				new UpdateCounterValue().execute();	
			}	
		});
		
		return layout;
	}
	
	

	class LoadGroupUser extends AsyncTask<String, String, String> {
	
		@Override
		protected void onPreExecute() {
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage(GroupDetailFragment.this.getString(R.string.string_allact_loading));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		 
		}
		
		@Override
		protected String doInBackground(String... args) {
			// Building Parameters	
			
				//groupId aus AllGroupsActivity übergeben
				String username = null;
				String groupId = null;
				
				sm = new SessionManager(getActivity().getApplicationContext());
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
						sm = new SessionManager(getActivity().getApplicationContext());
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
			GroupDetailFragment.this.getActivity().runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					Log.d("GroupDetailActivity JSON: ", "onPostExecute ausgeführt");
					BaseAdapter adapter = new SimpleAdapter(
							GroupDetailFragment.this.getActivity(), contactList,
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
			//String groupId = null;
			
			sm = new SessionManager(context);
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
			GroupDetailFragment.this.getActivity().runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					if (refresh() == true) {
						showCountConfirmation();
					}					
				}
			}); 
		}
		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.reload_groups, menu);
	    return;
	}

	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
	switch (item.getItemId()) {
		case R.id.action_settings:
			Intent intent = new Intent(getActivity(), SettingsActivity.class);
			startActivity(intent);
			return true;
        
		case R.id.action_reload:
			refresh();
			
		default:
	        return super.onOptionsItemSelected(item);
	
		}			
	}
	
	public boolean refresh() {
		checkInternet = new CheckInternetConnection();	
		if(checkInternet.haveNetworkConnection(context)){
			Log.d("GroupDetailActivity: ", "hasConnection() true!");	
			
			new LoadGroupUser().execute();
			BaseAdapter adapter = new SimpleAdapter(
				GroupDetailFragment.this.getActivity(), contactList,
                R.layout.group_detail_list_item, new String[] { TAG_USERNAME, TAG_COUNTERVALUE }, 
                new int[] { R.id.user_row_username, R.id.user_countervalue });

			contactList.clear();
			Log.d("GroupDetailActivity: ", "alte ListView gecleart");
			adapter.notifyDataSetChanged();
			Log.d("GroupDetailActivity: ", "neue ListView erstellt");
			return true;
		}
		else{
			Log.d("GroupDetailActivity: ", "hasConnection() false!");	
			showFailConnection();
			return false;
		}
	}
	
	public void showCountConfirmation(){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
		alertDialogBuilder.setTitle(R.string.string_groupdetailact_alerttitle);
		alertDialogBuilder.setMessage(R.string.string_groupdetailact_alerttext);
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setPositiveButton(R.string.string_groupdetailact_alertokay, null); 
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	
	public void showFailConnection(){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
		alertDialogBuilder.setTitle(R.string.string_groupdetailact_fail_alerttitle);
		alertDialogBuilder.setMessage(R.string.string_groupdetailact_fail_alerttext);
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setPositiveButton(R.string.string_groupdetailact_fail_alertokay, null); 
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	
	
	
}
