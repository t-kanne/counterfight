package de.woodpot.counterfight;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterFragment extends Fragment {
	private EditText usernameEditText;
	private EditText realnameEditText;
	private EditText password1EditText;
	private EditText password2EditText;
	private Button registerButton;
	
	private String usernameString;
	private String realnameString;
	private String password1String;
	private String password2String;
	
	// JSONParser Objekt erstellen
	JSONParser jParser = new JSONParser();
	
	// Server-Urls
	private static String url_create_user = "http://www.counterfight.net/create_user.php";
	private static String url_delete_user = "http://www.counterfight.net/delete_user.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_ERRORCODE = "errorcode";
	private static final String TAG_USERNAME = "username";
	private static final String TAG_REALNAME = "realname";
	private static final String TAG_PASSWORD = "password";
	
	// MYSQL Fehlercodes
	private static final String MYSQL_ERRORCODE_USER_ALREADY_EXISTS = "1062";
	
	// JSONArray für Counterdaten
	JSONArray userTable = null;
	
	Context context;
	


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		registerButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Strings erstellen
				usernameString = usernameEditText.getText().toString();
				realnameString = realnameEditText.getText().toString();
				password1String = password1EditText.getText().toString();
				password2String = password2EditText.getText().toString();
				
				if (checkEmptyFields() && checkPassword()){
					new RegisterUser().execute();
				}
			}
		});		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.activity_register, null);
		
		usernameEditText = (EditText)layout.findViewById(R.id.edittext_registeract_username);
		realnameEditText = (EditText)layout.findViewById(R.id.edittext_registeract_realname);
		password1EditText = (EditText)layout.findViewById(R.id.edittext_registeract_password1);
		password2EditText = (EditText)layout.findViewById(R.id.edittext_registeract_password2);
		registerButton = (Button)layout.findViewById(R.id.button_registeract_register);
		
		
		return layout;
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
	
	private boolean checkEmptyFields(){
		if (usernameString.isEmpty() || realnameString.isEmpty() || password1String.isEmpty() || password2String.isEmpty()){
			Toast.makeText(context, R.string.string_registeract_fillallfields, Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	private boolean checkPassword(){
		if (password1String.equals(password2String)){
			return true;
		}
		else {
			Toast.makeText(context, R.string.string_registeract_wrongpassword, Toast.LENGTH_SHORT).show();
			return false;
		}
	}
	
	class RegisterUser extends AsyncTask<String, String, String> {
		String errorString = null;
		
		@Override
		protected String doInBackground(String... args) {
			final String usernameString = usernameEditText.getText().toString();
			final String realnameString = realnameEditText.getText().toString();
			final String passwordString = password1EditText.getText().toString();
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(TAG_USERNAME, usernameString));
			params.add(new BasicNameValuePair(TAG_PASSWORD, passwordString));
			params.add(new BasicNameValuePair(TAG_REALNAME, realnameString));
			
			JSONObject json = null;
			final String mysqlError;
			
			try {
				json = jParser.makeHttpRequest(url_create_user, "POST", params);	
			} catch (Exception e){
				this.errorString = e.getMessage();
			}
			/*
			try {
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					RegisterFragment.this.runOnUiThread(new Runnable() {
						  public void run() {
							  Toast.makeText(RegisterFragment.this, "Username " + usernameString + " erstellt", Toast.LENGTH_SHORT).show();
						  }
					});
					Intent intent = new Intent(RegisterFragment.this, LoginActivity.class);
					startActivity(intent);
				}
				else {
					if (json.getString(TAG_ERRORCODE).equals(MYSQL_ERRORCODE_USER_ALREADY_EXISTS)){
						mysqlError = getString(R.string.mysqlerror_user_already_exists);
					}
					else {
						mysqlError = getString(R.string.mysqlerror_registration_failed);
					}						
					RegisterFragment.this.runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(RegisterFragment.this, mysqlError, Toast.LENGTH_LONG).show();
						}
					});					
				}
				
			} catch (JSONException e) {
				Log.e("RegisterActivity", e.getMessage());
			}
			*/
			return null;
		}
		
	}
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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
