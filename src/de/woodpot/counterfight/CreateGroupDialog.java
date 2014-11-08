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

public class CreateGroupDialog extends ActionBarActivity {
	
	private TextView adviceTextView;
	private EditText groupNameEditText;
	private Button okayButton;
	SessionManager sm;
	
	// JSONParser Objekt erstellen
	JSONParser jParser = new JSONParser();
	
	// Server-Urls
	private static String url_create_group = "http://www.counterfight.net/create_group.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_GROUPNAME = "groupName";
	private static final String TAG_GROUPADMIN = "admin";
	
	// JSONArray für Counterdaten
	JSONArray groupTable = null;
	String groupName;
	
	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_group_dialog);
		
		adviceTextView = (TextView)findViewById(R.id.textview_createGroupDialog_advice);
		groupNameEditText = (EditText)findViewById(R.id.edittext_createGroupDialog_groupName);
		okayButton = (Button)findViewById(R.id.button_createGroupDialog_OK);
		
		okayButton.setOnClickListener(new OnClickListener() {
						
			@Override
			public void onClick(View v) {		
				int editTextLength = groupNameEditText.getText().toString().length();
				Log.d("CreateGroupDialog:","editTextLength: " + editTextLength);
				
				if (editTextLength >= 3) {
					new CreateGroup().execute();
				}
				else {
					Toast.makeText(getApplicationContext(), "Der Gruppenname muss aus mind. 3 Zeichen bestehen", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	
	class CreateGroup extends AsyncTask<String, String, String> {
		
		protected String doInBackground(String... args) {

			// Gruppenname vom EditText holen
			final String newGroup = groupNameEditText.getText().toString();

			// SessionManager nach aktuellen Usernamen fragen
			String username = null;
			sm = new SessionManager(getApplicationContext());
			if (sm.isLoggedIn() == true) {
				username = sm.getUsername();
			}

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(TAG_GROUPNAME, newGroup));
			params.add(new BasicNameValuePair(TAG_GROUPADMIN, username));

			Log.d("CreateGroupDialog: ", "Group name: " + newGroup + " with admin: " + username);

			// sending modified data through http request
			// Notice that update product url accepts POST method
			JSONObject json = null;

			// check json success tag
			try {
				json = jsonParser.makeHttpRequest(url_create_group, "POST", params);
				int success = json.getInt(TAG_SUCCESS);
				
				if (success == 1) {
					// successfully updated
					Log.d("CreateGroupDialog JSON: ", json.toString());
					
					CreateGroupDialog.this.runOnUiThread(new Runnable() {
						  public void run() {
						    Toast.makeText(CreateGroupDialog.this, "Gruppe " + newGroup + " erfolgreich erstellt.", Toast.LENGTH_SHORT).show();
						  }
					});
					finish();
					
				} else {
					CreateGroupDialog.this.runOnUiThread(new Runnable() {
						  public void run() {
						    Toast.makeText(CreateGroupDialog.this, "Gruppe konnte nicht erstellt werden.", Toast.LENGTH_SHORT).show();
						  }
					});
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
		getMenuInflater().inflate(R.menu.create_group_dialog, menu);
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
