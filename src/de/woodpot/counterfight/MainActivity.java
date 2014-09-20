package de.woodpot.counterfight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//import org.apache.http.NameValuePair;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;

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
	Button refreshCounter;
	
	// JSONParser Objekt erstellen
	JSONParser jParser = new JSONParser();
	
	// Server-Url, um Counter abzurufen
	private static String url_counter = "http://localhost/counterfight/get_counter.php";
	
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_COUNTER = "counter";
	private static final String TAG_BENUTZER = "benutzer";
	private static final String TAG_COUNTERSTAND = "counterstand";
	
	// JSONArray für Counterdaten
//	JSONArray counterData = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		counter = (TextView) findViewById(R.id.txtMainCounter);
		refreshCounter = (Button) findViewById(R.id.btnRefreshCounter);
		
		// Loading products in Background Thread
		//new LoadCounter().execute();
		
		refreshCounter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				counter.getText();
				
			}
		});
	}
	/*
	class LoadCounter extends AsyncTask<String, String, String> {

		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_counter, "GET", params);
			
			// Check your log cat for JSON reponse
			Log.d("Counter: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// products found
					// Getting Array of Products
					counterData = json.getJSONArray(TAG_COUNTER);

					// looping through All Products
					for (int i = 0; i < counterData.length(); i++) {
						JSONObject c = counterData.getJSONObject(i);

						// Storing each json item in variable
						String user = c.getString(TAG_BENUTZER);
						String counterValue = c.getString(TAG_COUNTERSTAND);
						
						counter.setText(counterValue);

					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}
		
	}
	*/
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
