package de.woodpot.counterfight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

class LoadAllUserAsyncTask extends AsyncTask<String, String, ArrayList<HashMap<String, String>>>{

	// JSONParser Objekt erstellen
		JSONParser jParser = new JSONParser();
		SessionManager sm;
		
		CheckInternetConnection checkInternet;	
			
		 Context context;
		
		//private ProgressDialog pDialog;
			
		// Server-Urls
		private static String url_get_groups = "http://counterfight.net/get_all_groups.php";
			
		private String groupName;
		private String groupId;
							
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
			
		ArrayList<HashMap<String, String>> groupList = new ArrayList<HashMap<String, String>>();

		// JSON parser class
		JSONParser jsonParser = new JSONParser();
		
		
	
		public LoadAllUserAsyncTask(Context context){
			  this.context=context;
			}
			
			
		
		
		
		protected void onPreExecute() {
			
		}
		
		
		protected ArrayList<HashMap<String, String>> doInBackground(String... args) {
			
			
			
			return groupList;			
		}
					
		
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			
			//Übergabe an Main
			
			super.onPostExecute(result);
			
		}

		
		

		
	
	
	
	

}
