package de.woodpot.counterfight;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

class LeaveGroupAsyncTask extends AsyncTask<String, Boolean, Boolean> {
	private Context context;
	private ProgressDialog pDialog;
	private String groupId;
	
	// Tag-Strings
	private static String TAG_USERNAME = "username";
	private static String TAG_GROUPID = "groupId";
	private static String TAG_SUCCESS = "success";
	
	SessionManager sm;
	
	// Url-String(s)
	private static String url_leave_group = "http://www.counterfight.net/leave_group.php";
	
	// JSON
	JSONParser jParser = new JSONParser();
	
	public LeaveGroupAsyncTask(Context context, String groupId) {
		this.context = context;
		this.groupId = groupId;
		Log.d("LeaveGroupAsyncTask", "Gruppen-Id: " + this.groupId);
		sm = new SessionManager(this.context.getApplicationContext());
		pDialog = new ProgressDialog(this.context);
	};
	
	@Override
	protected void onPreExecute() {
			
		pDialog.setMessage(this.context.getResources().getString(R.string.string_allact_loading));
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		pDialog.show();
	}
	
	@Override
	protected Boolean doInBackground(String... args) {
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(TAG_USERNAME, sm.getUsername()));
		params.add(new BasicNameValuePair(TAG_GROUPID, groupId));
		
		JSONObject json = null;
		final String mysqlError;
				
		try {
			json = jParser.makeHttpRequest(url_leave_group, "POST", params);
			Log.d("LeaveGroupAsyncTask: ", "json: " + json);
			int success = json.getInt(TAG_SUCCESS);
			if (success == 1) {
				// Session beenden, damit User ausgeloggt wird
				Log.d("LeaveGroupAsyncTask: ", "Gruppe erfolgreich verlassen");
				return true;
			}
			else {				
				return false;
			}
			
		} catch (JSONException e) {
			Log.e("RegisterActivity", e.getMessage());
		}
		return false;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		Log.d("LeaveGroupAsyncTask", "onPostExecute() ausgeführt");
		pDialog.dismiss();
		
		if (result == true) {
			Toast.makeText(context, R.string.string_leavegroupasynctask_success, Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(context, R.string.string_leavegroupasynctask_fail, Toast.LENGTH_SHORT).show();
		}
		
		
	}
}