package de.woodpot.counterfight;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class NoGroupFragment extends Fragment {
	
	private TextView adviceTextView;
	private Button createGroupButton;
	private Button searchGroupButton;
	FragmentManager fm;
	FragmentSwitcher fragmentSwitcher;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_no_group, null);
		
		adviceTextView = (TextView)layout.findViewById(R.id.textview_nogroupact_advice);
		createGroupButton = (Button)layout.findViewById(R.id.button_nogroupact_createGroup);
		searchGroupButton = (Button)layout.findViewById(R.id.button_nogroupact_searchGroup);
		
		fm = getFragmentManager();
		
		createGroupButton.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View arg0) {		
				CreateGroupDialog createGroupDialog = (CreateGroupDialog) Fragment.instantiate(getActivity(), CreateGroupDialog.class.getName(), null);
				createGroupDialog.show(fm, "CreateGroupDialog");			
			}
			
		});
		
		searchGroupButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {			
				SearchGroupDialog searchGroupDialog = (SearchGroupDialog) Fragment.instantiate(getActivity(), SearchGroupDialog.class.getName(), null);
				searchGroupDialog.show(fm, "SearchGroupDialog");
			}
		});
		
		return layout;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.no_group, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent(this.getActivity(), SettingsActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
