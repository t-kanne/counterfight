package de.woodpot.counterfight;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class SearchGroupDialog extends DialogFragment {
	
	private TextView adviceTextView;
	private EditText groupIdEditText;
	private Button okayButton;
	SessionManager sm;
	private String groupIdString;
	Context context;
	
	FragmentSwitcher fragmentSwitcher;
	private GroupDetailFragment groupDetailFragment;
	
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		context = getActivity();
		setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
	}
	
	@Override
	public void onActivityCreated(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onActivityCreated(arg0);
		
		fragmentSwitcher = (FragmentSwitcher) getActivity();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_search_group_dialog, container, false);
		
		adviceTextView = (TextView)layout.findViewById(R.id.textview_searchgroupdialog_advice);
		groupIdEditText = (EditText)layout.findViewById(R.id.edittext_searchgroupdialog_groupid);
		okayButton = (Button)layout.findViewById(R.id.button_searchgroupdialog_ok);
		
		okayButton.setOnClickListener(new OnClickListener() {
						
			@Override
			public void onClick(View v) {	
				groupIdString = groupIdEditText.getText().toString();
				
				if (checkGroupIdEditText()) {
					new SearchGroup().execute();
				}
			}
		});
		
		return layout;
	}
	
	
	public boolean checkGroupIdEditText() {
		if (groupIdString.length() != 5){
			Toast.makeText(this.getActivity(),R.string.string_searchgroupdialog_wronggroupid, Toast.LENGTH_SHORT).show();
			return false;
		} else {
			return true;
		}
	}
	
	class SearchGroup extends AsyncTask<String, Boolean, Boolean> {
		
		protected Boolean doInBackground(String... args) {
			// SessionManager nach aktuellen Usernamen fragen
			String usernameString = null;
			
			sm = new SessionManager(context);
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
					
					SearchGroupDialog.this.getActivity().runOnUiThread(new Runnable() {
						  public void run() {
						    Toast.makeText(SearchGroupDialog.this.getActivity(), "Gruppe " + groupName + " erfolgreich beigetreten.", Toast.LENGTH_SHORT).show();
						    goToNextFragment();
						  }
					});
					// Hier soll der Nutzer direkt in die GroupDetails zu der entsprechenden Gruppe gelangen
					dismiss();
					
					
					return true;
					
				} else {
					mysqlError = json.getString(TAG_ERRORCODE);
					if (mysqlError.equals(MYSQL_ERRORCODE_GROUP_ALREADY_JOINED)) {
						SearchGroupDialog.this.getActivity().runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(SearchGroupDialog.this.getActivity(), R.string.mysqlerror_group_already_joined, Toast.LENGTH_SHORT).show();
							}
						});
					}
					if (mysqlError.equals(MYSQL_ERRORCODE_GROUP_DOES_NOT_EXIST)) {
						SearchGroupDialog.this.getActivity().runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(SearchGroupDialog.this.getActivity(), R.string.mysqlerror_group_does_not_exist, Toast.LENGTH_SHORT).show();
							}
						});
					}
					return false;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}
		
		@Override
		protected void onPostExecute (Boolean result) {
			super.onPostExecute(result);
			
		}		
	}
	
	public void goToNextFragment() {
		Bundle fragmentData = new Bundle();
		fragmentData.putString("groupId", groupIdString);
		fragmentData.putString("groupName", groupName);
		
		GroupDetailFragment fragment = new GroupDetailFragment();
		fragmentSwitcher.replaceFragment(fragmentData, fragment); 
	}
}
