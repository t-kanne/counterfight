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
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	TextView counterDesc;
	TextView counter;
	Button refreshCounterButton;
	Button increaseCounterButton;
	Button showAllUsersOfGroupButton;
	Button openNoGroupActivity;
	Button loginActivity;
	Button openAllGroupsActivity;
	Button logoutButton;
	Button openGroupDetailActivity;
	
	// JSONParser Objekt erstellen
	JSONParser jParser = new JSONParser();
	
	// Server-Urls
	private static String url_read_counter = "http://www.counterfight.net/get_counter.php";
	private static String url_update_counter = "http://www.counterfight.net/update_counter.php";
	
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_COUNTER = "counter";
	private static final String TAG_USER = "user";
	private static final String TAG_COUNTERVALUE = "counterValue";
	
	// JSONArray für Counterdaten
	JSONArray counterData = null;
	String user;
	String counterValue = null;
	
	// JSON parser class
	JSONParser jsonParser = new JSONParser();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		refreshCounterButton = (Button) findViewById(R.id.btnRefreshCounter);
		showAllUsersOfGroupButton = (Button) findViewById(R.id.btnShowAllCountersOfGroup);
		openNoGroupActivity = (Button) findViewById(R.id.btnOpenNoGroupActivity);
		loginActivity = (Button) findViewById(R.id.btnLoginActivity);
		openAllGroupsActivity = (Button) findViewById(R.id.btnOpenAllGroupsActivity);
		logoutButton = (Button) findViewById(R.id.btnLogout);
		openGroupDetailActivity = (Button) findViewById(R.id.btnOpenGroupDetailActivity);
		
		// Loading products in Background Thread
			
		showAllUsersOfGroupButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				openAllUsersOfGroup();
				finish();
			}
			
		});
		
		openNoGroupActivity.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), NoGroupActivity.class);
				startActivity(intent);				
			}
		});
		
		loginActivity.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
				startActivity(intent);		
			}
		});
		
		openGroupDetailActivity.setOnClickListener(new OnClickListener() {

			 @Override
			 public void onClick(View v) {
			 
			 Intent intent = new Intent(getApplicationContext(), GroupDetailActivity.class);
			 startActivity(intent); 
			 }
			 
		});
		
		openAllGroupsActivity.setOnClickListener(new OnClickListener() {

			 @Override
			 public void onClick(View v) {
			 
			 Intent intent = new Intent(getApplicationContext(), AllGroupsActivity.class);
			 startActivity(intent); 
			 }
			 
		});
		
		logoutButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SessionManager sm = new SessionManager(getApplicationContext());
				sm.clearSession();
			}
			
		});
		
	}
	
	public void openAllUsersOfGroup(){
		Intent intent = new Intent(this.getApplicationContext(), ShowAllUsersOfGroupActivity.class);
		Log.d("MainActivity: ", "Intent: " + intent.toString());
		startActivity(intent);
	}
	
	
	
	
	
	class UpdateCounter extends AsyncTask<String, String, String> {


		protected String doInBackground(String... args) {

			// getting updated data from EditTexts
			String newCounter = counter.getText().toString();

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(TAG_USER, "thom-ass"));
			params.add(new BasicNameValuePair(TAG_COUNTERVALUE, newCounter));
			Log.d("MainActivity JSON: ", "New Counter value: " + newCounter);

			// sending modified data through http request
			// Notice that update product url accepts POST method
			JSONObject json = null;

			try {
				json = jsonParser.makeHttpRequest(url_update_counter, "POST", params);
			} catch (Exception e){
				e.printStackTrace();
			}


			// check json success tag
			try {
				int success = json.getInt(TAG_SUCCESS);
				
				if (success == 1) {
					// successfully updated
					Log.d("MainActivity JSON: ", json.toString());
					
				} else {
					// failed to update product
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
		getMenuInflater().inflate(R.menu.main, menu);
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
