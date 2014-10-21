package de.woodpot.counterfight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
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
import android.widget.ListAdapter;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	TextView counterDesc;
	TextView counter;
	Button refreshCounterButton;
	Button increaseCounterButton;
	Button showAllUsersOfGroupButton;
	
	// JSONParser Objekt erstellen
	JSONParser jParser = new JSONParser();
	
	// Server-Urls
	private static String url_read_counter = "http://www.dayvision.de/counterfight/get_counter.php";
	private static String url_update_counter = "http://www.dayvision.de/counterfight/update_counter.php";
	
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
		
		counter = (TextView) findViewById(R.id.txtMainCounter);
		refreshCounterButton = (Button) findViewById(R.id.btnRefreshCounter);
		increaseCounterButton = (Button) findViewById(R.id.btnCountUp);
		showAllUsersOfGroupButton = (Button) findViewById(R.id.btnShowAllCountersOfGroup);
		
		
		// Loading products in Background Thread
		new LoadCounter().execute();
		
		refreshCounterButton.setOnClickListener(new OnClickListener() {
						
			@Override
			public void onClick(View arg0) {
				new LoadCounter().execute();
				counter.setText(counterValue);
			}
		});
		
		increaseCounterButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String incrCounter = counter.getText().toString();
				int incrCounter_int = Integer.valueOf(incrCounter).intValue();
				incrCounter_int = incrCounter_int+1;
				incrCounter = ""+incrCounter_int;
				Log.d("MainActivity JSON: ", "New Counter: " + incrCounter);
				counter.setText(incrCounter);
				new UpdateCounter().execute();
			}
			
		});
		
		showAllUsersOfGroupButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				openAllUsersOfGroup();
				finish();
			}
			
		});
		
	}
	
	public void openAllUsersOfGroup(){
		Intent intent = new Intent(this.getApplicationContext(), ShowAllUsersOfGroupActivity.class);
		Log.d("MainActivity: ", "Intent: " + intent.toString());
		startActivity(intent);
	}
	
	class LoadCounter extends AsyncTask<String, String, String> {

	
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_read_counter, "GET", params);
			
			// Check your log cat for JSON reponse
			Log.d("MainActivity JSON: ", "JSONObject: " + json.toString());

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
						Log.d("MainActivity JSON: ", "JSONArray: " + c.toString());
						
						// Storing each json item in variable
						user = c.getString(TAG_USER);
						Log.d("MainActivity JSON: ", "Counter user: " + user);
						counterValue = c.getString(TAG_COUNTERVALUE);
						Log.d("MainActivity JSON: ", "Counter value: " + counterValue);
					}		
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		
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
			JSONObject json = jsonParser.makeHttpRequest(url_update_counter,
					"POST", params);

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
