package de.woodpot.counterfight;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginFragment extends Fragment {
	/*
	private EditText usernameEditText;
	private EditText passwordEditText;
	private Button loginButton;
	private TextView registerTextView;
	private ProgressDialog pDialog;
	
	private String usernameString;
	private String passwordString;
	private String groupIdIntent;
	private String groupNameIntent;
	
	// Objekt vom SessionManager erstellen
	SessionManager sessionManager;
	
	// JSONParser Objekt erstellen
	JSONParser jParser = new JSONParser();
	
	// Server-Urls
	private static String url_check_user = "http://www.counterfight.net/login_user.php";
	private static String url_count_user_groups = "http://www.counterfight.net/count_user_groups.php";
	
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_USER = "user";
	private static final String TAG_USERNAME = "username";
	private static final String TAG_PASSWORD = "password";
	private static final String TAG_GROUPID = "groupId";
	private static final String TAG_GROUPNAME = "groupName";
	
	// JSON Arrays
	JSONArray userData = null;
	String noOfGroups = null;
	Boolean correctUserdata = false;
	
	// JSON parser class
	JSONParser jsonParser = new JSONParser();
	
	Context context;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		sessionManager = new SessionManager(context);
		
		if (sessionManager.isLoggedIn() == false) {
			


			
			registerTextView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, RegisterActivity.class);
					startActivity(intent);
				}
			});
			
			loginButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// String-Variablen erzeugen
					usernameString = usernameEditText.getText().toString();
					passwordString = passwordEditText.getText().toString();	
					new GetUser().execute();
				}
			});
		} else {
			new CountUserGroups().execute();
		}
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_login, null);
		
		usernameEditText = (EditText)layout.findViewById(R.id.edittext_loginact_username);
		passwordEditText = (EditText)layout.findViewById(R.id.edittext_loginact_password);
		loginButton = (Button)layout.findViewById(R.id.button_loginact_login);
		registerTextView = (TextView)layout.findViewById(R.id.textview_loginact_register);

		return layout;
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
	
	class GetUser extends AsyncTask<String, String, String> {
		
		protected String doInBackground(String... args) {

			final List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(TAG_USERNAME, usernameString));
			params.add(new BasicNameValuePair(TAG_PASSWORD, passwordString)); 
			Log.d("LoginActivity", "username: " + usernameString + " password: " + passwordString);
			JSONObject json = null;
			
			try {
				json = jParser.makeHttpRequest(url_check_user, "POST", params);
			} catch (Exception e){
				Log.e("LoginActivity", "JSON: " + e.getMessage());
			}
			/*
			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					LoginFragment.this.runOnUiThread(new Runnable() {
						  public void run() {
						    Toast.makeText(LoginFragment.this, "Username " + usernameString + " gefunden", Toast.LENGTH_SHORT).show();
						  }
					});
					// Erstellen der Session
					sessionManager.createSession(usernameString, passwordString);
					
					groupIdIntent = json.getString("groupId");
					Log.d("LoginActivity: ", "groupId Json: " + groupIdIntent);
					groupNameIntent = json.getString("groupName");
					Log.d("LoginActivity: ", "groupName Json: " + groupNameIntent);
				}
				else {
					LoginFragment.this.runOnUiThread(new Runnable() {
						  public void run() {
						    Toast.makeText(LoginFragment.this, "Username oder Passwort falsch", Toast.LENGTH_LONG).show();
						  }
					});
				}
				
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String result){
			if (sessionManager.isLoggedIn() == true){
				new CountUserGroups().execute();
			}
		}	
		
	}
	
	class CountUserGroups extends AsyncTask <String, String, String> {
		/*
		protected void onPreExecute() {
			pDialog = new ProgressDialog(LoginFragment.this);
			pDialog.setMessage(LoginFragment.this.getString(R.string.string_loginact_loading));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		
		protected String doInBackground(String... args) {
			Log.d("LoginAcitivty: ", "CountUserGroups");
			final List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(TAG_USERNAME, sessionManager.getUsername()));
			JSONObject json = null;
			
			try {
				json = jParser.makeHttpRequest(url_count_user_groups, "POST", params);
				Log.d("LoginActivity", "JSON countusergroups post ");
			} catch (Exception e){
				Log.e("LoginActivity", "JSON (UserGroups): " + e.getMessage());
			}
			
			try {
				int success = json.getInt(TAG_SUCCESS);
				
				if (success == 1) {
					
					noOfGroups = json.getString("noOfGroups");
					Log.d("LoginActivity: ", "noOfGroups: " + noOfGroups + " for user " + sessionManager.getUsername());
				
				}
				else {
					LoginFragment.this.runOnUiThread(new Runnable() {
						  public void run() {
						    Toast.makeText(LoginFragment.this, "Anzahl der Gruppen nicht ermittelt", Toast.LENGTH_LONG).show();
						  }
					});
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return null;
		}
	/*	
		@Override
		protected void onPostExecute(String result){
			if (sessionManager.isLoggedIn() == true){
				pDialog.dismiss();
				startGroupDependingActivity(noOfGroups);
			}
		}
		
		
	}
	
	public void startGroupDependingActivity(String noOfGroups) {
		int noOfGroupsInt;
		
		try {
			noOfGroupsInt = Integer.valueOf(noOfGroups);
			Log.d("LoginActivity: ", "Anzahl Gruppen: " + noOfGroupsInt);
			
			if (noOfGroupsInt == 0) {
				Intent intent = new Intent(this, NoGroupActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);
				finish();
			}
			
			if (noOfGroupsInt == 1) {
				Intent intent = new Intent(this, GroupDetailActivity.class);
				intent.putExtra("groupId", groupIdIntent);
				intent.putExtra("groupName", groupNameIntent);
				Log.d("LoginActivity", "groupId: " + groupIdIntent +groupNameIntent);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);
				finish();
			}
			if (noOfGroupsInt > 1) {
				Intent intent = new Intent(this, AllGroupsActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);
				finish();
			}
		} catch (NumberFormatException e) {
			Intent intent = new Intent(this, NoGroupActivity.class);
			startActivity(intent);
			finish();
		}
				
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	*/
	
}