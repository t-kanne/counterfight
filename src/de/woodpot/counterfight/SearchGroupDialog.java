package de.woodpot.counterfight;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class SearchGroupDialog extends ActionBarActivity {
	
	private TextView adviceTextView;
	private EditText groupIdEditText;
	private Button okayButton;
	SessionManager sm;
	private String groupIdString;
	
	// JSONParser Objekt erstellen
	JSONParser jParser = new JSONParser();
	
	// Server-Urls
	private static String url_search_group = "http://www.counterfight.net/search_group.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_USERNAME = "username";
	private static final String TAG_GROUP_ID = "groupId";
	private static final String TAG_GROUPNAME = "groupName";
	private static final String TAG_GROUPDETAILS = "groupDetails";
	private static final String TAG_ERRORCODE = "errorcode";
	
	// MYSQL Fehlercodes
	private static final String MYSQL_ERRORCODE_GROUP_ALREADY_JOINED = "1062";
	private static final String MYSQL_ERRORCODE_GROUP_DOES_NOT_EXIST = "0";
	
	
	// JSONArray für Counterdaten
	JSONArray groupTable = null;
	String groupName = null;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_group_dialog);
		
		adviceTextView = (TextView)findViewById(R.id.textview_searchgroupdialog_advice);
		groupIdEditText = (EditText)findViewById(R.id.edittext_searchgroupdialog_groupid);
		okayButton = (Button)findViewById(R.id.button_searchgroupdialog_ok);
		
		okayButton.setOnClickListener(new OnClickListener() {
						
			@Override
			public void onClick(View v) {	
				groupIdString = groupIdEditText.getText().toString();
				
				if (checkGroupIdEditText()) {
					new SearchGroup().execute();
				}
			}
		});
	}
	
	public boolean checkGroupIdEditText() {
		if (groupIdString.length() != 5){
			Toast.makeText(this,R.string.string_searchgroupdialog_wronggroupid, Toast.LENGTH_SHORT).show();
			return false;
		} else {
			return true;
		}
	}
	
	class SearchGroup extends AsyncTask<String, String, String> {
		
		protected String doInBackground(String... args) {
			// SessionManager nach aktuellen Usernamen fragen
			String usernameString = null;
			final String groupName;
			
			sm = new SessionManager(getApplicationContext());
			if (sm.isLoggedIn() == true) {
				usernameString = sm.getUsername();
			}
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(TAG_GROUP_ID, groupIdString));
			params.add(new BasicNameValuePair(TAG_USERNAME, usernameString));

			Log.d("SearchGroupDialog: ", "Group Id: " + groupIdString + " with user: " + usernameString);

			JSONObject json = null;
			final String mysqlError;

			// check json success tag
			try {
				json = jParser.makeHttpRequest(url_search_group, "POST", params);
				int success = json.getInt(TAG_SUCCESS);
				
				if (success == 1) {
					Log.d("SearchGroupDialog JSON: ", json.toString());
					groupName = json.getJSONArray(TAG_GROUPDETAILS).getString(0);
					
					SearchGroupDialog.this.runOnUiThread(new Runnable() {
						  public void run() {
						    Toast.makeText(SearchGroupDialog.this, "Gruppe " + groupName + " erfolgreich beigetreten.", Toast.LENGTH_SHORT).show();
						  }
					});
					finish();
					
				} else {
					mysqlError = json.getString(TAG_ERRORCODE);
					if (mysqlError.equals(MYSQL_ERRORCODE_GROUP_ALREADY_JOINED)) {
						SearchGroupDialog.this.runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(SearchGroupDialog.this, R.string.mysqlerror_group_already_joined, Toast.LENGTH_SHORT).show();
							}
						});
					}
					if (mysqlError.equals(MYSQL_ERRORCODE_GROUP_DOES_NOT_EXIST)) {
						SearchGroupDialog.this.runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(SearchGroupDialog.this, R.string.mysqlerror_group_does_not_exist, Toast.LENGTH_SHORT).show();
							}
						});
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search_group_dialog, menu);
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
