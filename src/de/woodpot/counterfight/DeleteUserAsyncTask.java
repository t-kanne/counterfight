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

class DeleteUserAsyncTask extends AsyncTask<String, Boolean, Boolean> {
	private Context context;
	private ProgressDialog pDialog;
	
	// Tag-Strings
	private static String TAG_USERNAME = "username";
	private static String TAG_PASSWORD = "password";
	private static String TAG_SUCCESS = "success";
	
	SessionManager sm;
	
	// Url-String(s)
	private static String url_delete_user = "http://www.counterfight.net/delete_user.php";
	
	// JSON
	JSONParser jParser = new JSONParser();
	
	public DeleteUserAsyncTask(Context context) {
		this.context = context;
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
		params.add(new BasicNameValuePair(TAG_PASSWORD, sm.getPassword()));
		
		JSONObject json = null;
		final String mysqlError;
				
		try {
			json = jParser.makeHttpRequest(url_delete_user, "POST", params);
			Log.d("DeleteUserAsyncTask: ", "json: " + json);
			int success = json.getInt(TAG_SUCCESS);
			if (success == 1) {
				// Session beenden, damit User ausgeloggt wird
				Log.d("DeleteUserAsyncTask: ", "User erfolgreich gelöscht");
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
		Log.d("DeleteUserAsyncTask", "onPostExecute() ausgeführt");
		pDialog.dismiss();
		
		if (result == true) {
			sm.clearSession();
			Intent intent = new Intent(context, LoginActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
			Log.d("DeleteUserAsyncTask", "onPostExecute() result: " +result);
		} else {
			Toast.makeText(context, R.string.string_registeract_userdeletefailed, Toast.LENGTH_LONG).show();
		}
	}
}