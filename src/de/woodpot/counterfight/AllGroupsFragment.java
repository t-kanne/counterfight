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
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class AllGroupsFragment extends ListFragment  {

	// JSONParser Objekt erstellen
		JSONParser jParser = new JSONParser();
		SessionManager sm;
		Context context;
		
		ListAdapter adapter;
		
		CheckInternetConnection checkInternet;	
		
		private ProgressDialog pDialog;
		
		ArrayList<String> searchList = new ArrayList<String>();
		
		// Server-Urls
		private static String url_get_groups = "http://counterfight.net/get_all_groups.php";
		
		private String groupName;
		private String groupId;
		
		TextView testNameTextView;
		ListView ls;
			
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
		
		private static final String TAG_GROUPNAMELAYOUT = "groupName";
		private static final String TAG_USERFIRSTLAYOUT = "user_first";
		private static final String TAG_OWNPLACELAYOUT = "own_place";
	
		
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
			
			new LoadAllUserCounter().execute();
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			View layout = inflater.inflate(R.layout.fragment_all_groups, null);
			
			ls = (ListView) layout.findViewById(android.R.id.list);
			testNameTextView = (TextView)layout.findViewById(R.id.group_name);
			
			registerForContextMenu(ls);
			
			return layout;
		}
		
		
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			
		checkInternet = new CheckInternetConnection();		
		
		
			//ohne Funktion
			switch (item.getItemId()) {
				case R.id.action_settings:
				Intent intent = new Intent(context, SettingsActivity.class);
				startActivity(intent);
		        return true;
		            
				case R.id.action_reload:
					
					if(checkInternet.haveNetworkConnection(context)){
						Log.d("GroupDetailActivity: ", "hasConnection() true!");	
							
						//Intent intent2 = new Intent(this, AllGroupsFragment.class);
						//finish();
						//startActivity(intent2);
						return true;
				}
				else{
					Log.d("GroupDetailActivity: ", "hasConnection() false!");	
					showFailConnection();
				}
				
				default:
				
		        return super.onOptionsItemSelected(item);	
		    	
			}
				
		}
		
		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			inflater.inflate(R.menu.reload_groups, menu);
			super.onCreateOptionsMenu(menu, inflater);
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
			
			protected void onPreExecute() {
				pDialog = new ProgressDialog(getActivity());
				pDialog.setMessage(AllGroupsFragment.this.getString(R.string.string_allact_loading));
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(false);
				pDialog.show();
			}
			@Override
			protected String doInBackground(String... args) {
				
				// Building Parameters	
					
					String username = null;
					if (sm.isLoggedIn() == true) {
						username = sm.getUsername();
					}
				
					final ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair(TAG_USERNAME, username));
					Log.d("AllGroupsActivity post: ", params.toString());
					JSONObject json = null;
					
					 
					
					try {
						json = jParser.makeHttpRequest(url_get_groups, "POST", params);
						Log.d("AllGroupsActivity post", "JSON post variablen: " + params);
					} catch (Exception e){
						Log.d("AllGroupsActivity post", "JSON (username POST): " + e.getMessage());
					}
				
								
					ArrayList<NameValuePair> params2 = new ArrayList<NameValuePair>();
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
							
							//Parameter für Übergabe an GroupDetailActivity
							groupId = c.getString(TAG_GROUPID);
							groupName = c.getString(TAG_GROUPNAME);
							String firstPlace = c.getString(TAG_USERFIRST);
							String ownPlace = c.getString(TAG_OWNPLACE);
							
							//int addItem = Integer.parseInt(groupId);
							searchList.add(groupId);
								
							// tmp hashmap for single contact
							HashMap<String, String> contact = new HashMap<String, String>();

							// adding each child node to HashMap key => value
							contact.put(TAG_GROUPID, groupId);
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
				pDialog.dismiss();
				// updating UI from Background Thread
				AllGroupsFragment.this.getActivity().runOnUiThread(new Runnable() {
					public void run() {
						/**
						 * Updating parsed JSON data into ListView
						 * */
						BaseAdapter adapter = new SimpleAdapter(
								AllGroupsFragment.this.getActivity(), contactList,
								R.layout.all_groups_list_item, new String[] { TAG_GROUPNAME, TAG_USERFIRST,
										TAG_OWNPLACE }, new int[] { R.id.user_row_groupName,
										R.id.user_row_first_place, R.id.user_row_own_place });
						// updating listview
						//Log.d("AllGroupsActivityFragment JSON: ", "Adapterusers: " + users.toString());
						setListAdapter (adapter);
						
						pDialog.dismiss();
					}
				}); 
			}
		}
					
						
		public void onListItemClick(ListView list, View view, int position, long id) {
		    super.onListItemClick(list, view, position, id);	
			Context context = getActivity();

            @SuppressWarnings("unchecked")
			HashMap<String,String> map=(HashMap<String, String>) list.getItemAtPosition(position);
            Log.d("GroupDetailActivity:", "hashmap map:" + map);
            groupId = map.get(TAG_GROUPID);
            groupName = map.get(TAG_GROUPNAME);
            
            Log.d("GroupDetailActivity:", "groupId: " + groupId);
            Log.d("GroupDetailActivity:", "groupName: " + groupName);
            Intent intent = new Intent(context, GroupDetailActivity.class);
		    intent.putExtra("groupId", groupId);
		    intent.putExtra("groupName", groupName);
	
			startActivity(intent);	
			    
		}
		

		public void showFailConnection(){
			Context context = getActivity();
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
			alertDialogBuilder.setTitle(R.string.string_groupdetailact_fail_alerttitle);
			alertDialogBuilder.setMessage(R.string.string_groupdetailact_fail_alerttext);
			alertDialogBuilder.setCancelable(false);
			alertDialogBuilder.setPositiveButton(R.string.string_groupdetailact_fail_alertokay, null); 
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}
		    
   
		}
