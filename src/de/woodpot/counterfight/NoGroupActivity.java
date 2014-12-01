package de.woodpot.counterfight;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class NoGroupActivity extends ActionBarActivity {
	
	private TextView adviceTextView;
	private Button createGroupButton;
	private Button searchGroupButton;
	FragmentManager fm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_no_group);
		
		adviceTextView = (TextView)findViewById(R.id.textview_nogroupact_advice);
		createGroupButton = (Button)findViewById(R.id.button_nogroupact_createGroup);
		searchGroupButton = (Button)findViewById(R.id.button_nogroupact_searchGroup);
		
		fm = getSupportFragmentManager();
		
		createGroupButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {		
				CreateGroupDialog createGroupDialog = (CreateGroupDialog) Fragment.instantiate(getBaseContext(), CreateGroupDialog.class.getName(), null);
				createGroupDialog.show(fm, "CreateGroupDialog");			
			}
			
		});
		
		searchGroupButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {			
				SearchGroupDialog searchGroupDialog = (SearchGroupDialog) Fragment.instantiate(getBaseContext(), SearchGroupDialog.class.getName(), null);
				searchGroupDialog.show(fm, "SearchGroupDialog");
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.no_group, menu);
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
}
