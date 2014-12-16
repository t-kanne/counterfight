package de.woodpot.counterfight;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

public class CreateGroupDialog extends DialogFragment {
	
	private TextView adviceTextView;
	private EditText groupNameEditText;
	private Button okayButton;
	SessionManager sm;
	Context context;
	FragmentSwitcher fragmentSwitcher;
	
	// JSONParser Objekt erstellen
	JSONParser jParser = new JSONParser();
	
	// Server-Urls
	private static String url_create_group = "http://www.counterfight.net/create_group.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_GROUPNAME = "groupName";
	private static final String TAG_GROUPADMIN = "admin";
	private static final String TAG_GROUPID = "groupId";
	
	// JSONArray für Counterdaten
	JSONArray groupTable = null;
	String groupName, newGroup;
	String groupId;
	
	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		context = getActivity();
		setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_create_group_dialog, container, false);
		
		// thisDialog = getDialog();
		//thisDialog.setTitle("Gruppe erstellen");
		
		adviceTextView = (TextView)layout.findViewById(R.id.textview_creategroupdialog_advice);
		groupNameEditText = (EditText)layout.findViewById(R.id.edittext_creategroupdialog_groupname);
		okayButton = (Button)layout.findViewById(R.id.button_creategroupdialog_ok);
		
		okayButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {	
				int editTextLength = groupNameEditText.getText().toString().length();
				Log.d("CreateGroupDialog:","editTextLength: " + editTextLength);
				
				if (editTextLength >= 3) {
					new CreateGroup().execute();
				}
				else {
					Toast.makeText(context, "Der Gruppenname muss aus mind. 3 Zeichen bestehen", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		return layout;
	}
	
	@Override
	public void onActivityCreated(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onActivityCreated(arg0);
		
		fragmentSwitcher = (FragmentSwitcher) getActivity();
	}
	
	class CreateGroup extends AsyncTask<String, String, String> {
		
		protected String doInBackground(String... args) {

			// Gruppenname vom EditText holen
			newGroup = groupNameEditText.getText().toString();

			// SessionManager nach aktuellen Usernamen fragen
			String username = null;
			sm = new SessionManager(context);
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
				groupId = json.getString(TAG_GROUPID);
				
				if (success == 1) {
					// successfully updated
					Log.d("CreateGroupDialog JSON: ", json.toString());
					
					CreateGroupDialog.this.getActivity().runOnUiThread(new Runnable() {
						  public void run() {
							  Toast.makeText(CreateGroupDialog.this.getActivity(), "Gruppe " + newGroup + " erfolgreich erstellt.", Toast.LENGTH_SHORT).show();
							  goToNextFragment();
						  }
					});
					dismiss();
					
				} else {
					CreateGroupDialog.this.getActivity().runOnUiThread(new Runnable() {
						  public void run() {
						    Toast.makeText(CreateGroupDialog.this.getActivity(), "Gruppe konnte nicht erstellt werden.", Toast.LENGTH_SHORT).show();
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
	
	public void goToNextFragment() {	
		Bundle fragmentData = new Bundle();
		//groupId = String.format("%05d", groupId);
		
		fragmentData.putString("groupId", groupId);
		fragmentData.putString("groupName", newGroup);
		
		GroupDetailFragment fragment = new GroupDetailFragment();
		fragmentSwitcher.replaceFragment(fragmentData, fragment); 
	}
	
}
