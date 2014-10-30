package de.woodpot.counterfight;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends ActionBarActivity {
	private EditText usernameEditText;
	private EditText passwordEditText;
	private Button loginButton;
	private TextView registerTextView;
	
	private String usernameString;
	private String passwordString;
		
	// JSONParser Objekt erstellen
	JSONParser jParser = new JSONParser();
	
	// Server-Urls
	private static String url_check_user = "http://www.counterfight.net/login_user.php";
	
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_USER = "user";
	private static final String TAG_USERNAME = "username";
	private static final String TAG_PASSWORD = "password";
	
	// JSONArray f�r Counterdaten
	JSONArray userData = null;
	Boolean correctUserdata = false;
	
	// JSON parser class
	JSONParser jsonParser = new JSONParser();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		usernameEditText = (EditText)findViewById(R.id.edittext_loginact_username);
		passwordEditText = (EditText)findViewById(R.id.edittext_loginact_password);
		loginButton = (Button)findViewById(R.id.button_loginact_login);
		registerTextView = (TextView)findViewById(R.id.textview_loginact_register);
		
		registerTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
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

			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					LoginActivity.this.runOnUiThread(new Runnable() {
						  public void run() {
						    Toast.makeText(LoginActivity.this, "Username " + usernameString + " gefunden", Toast.LENGTH_SHORT).show();
						  }
					});
					finish();
				}
				else {
					LoginActivity.this.runOnUiThread(new Runnable() {
						  public void run() {
						    Toast.makeText(LoginActivity.this, "Username oder Passwort falsch", Toast.LENGTH_LONG).show();
						  }
					});
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
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
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}