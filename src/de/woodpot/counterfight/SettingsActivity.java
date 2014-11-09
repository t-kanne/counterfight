package de.woodpot.counterfight;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

@SuppressWarnings("deprecation")

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.pref_settings);

        Preference deleteUser = findPreference("account_delete");
        deleteUser.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference deleteUser){
            	deleteAccount();
            	return true;
            }
         
        });
        
        /*
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        
        //Einstellungen für die Bewegungsempfindlichkeit aufrufen und verarbeiten
        ListPreference listPref = (ListPreference) findPreference("sensitivity_key");
        String listPrefSelected = sp.getString("sensitivity_key", null);
        String listPrefEntry = (String) listPref.getEntry();
        listPref.setSummary(listPrefEntry);
        
        //Einstellungen für die Countdowndauer aufrufen und verarbeiten
        listPref = (ListPreference) findPreference("countdown_key");
        listPrefSelected = sp.getString("countdown_key", null);
        listPrefEntry = (String) listPref.getEntry();
        listPref.setSummary(listPrefEntry);
         */
    }
	
	public void deleteAccount() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
        builder.setTitle(R.string.pref_account_warning_header);
        builder.setPositiveButton(R.string.pref_account_warning_positive_button, null); 
        builder.setNegativeButton(R.string.pref_account_warning_negative_button, null);
        builder.setMessage(R.string.pref_account_warning_explanation);
        AlertDialog errorDialog = builder.create();
        errorDialog.show();
		
	}
	
	protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

	protected void onPause() {
        super.onPause();	
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
	
	public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
	    Log.i("settings", "onSharedPreferenceChanged aufgerufen"); 
		Preference pref = findPreference(key);
	        if (pref instanceof ListPreference) {
	            ListPreference listPref = (ListPreference) pref;
	            listPref.setSummary(listPref.getEntry());
	        }
	        
	        String lockKey = "pref_lockscreen_mode_key";
	        Log.i("settings", "lockscreen mode geändert");
	        CheckBoxPreference lockPref = (CheckBoxPreference) findPreference(lockKey);
	        boolean lockPrefValue = lockPref.isChecked();
	        Log.i("settings", "lockscreenOn: " + lockPrefValue);
	        if (key.equals(lockKey) && lockPrefValue == true) {
                AlertDialog.Builder builder = 
                   new AlertDialog.Builder(this);
	                //builder.setTitle(R.string.notification);
	                //builder.setPositiveButton(R.string.okay, null); 
	                //builder.setMessage(R.string.pref_lockscreen_mode_information);
	                AlertDialog errorDialog = builder.create();
	                errorDialog.show();
	        }
	        
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            // app icon in action bar clicked; go home
	            finish();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
}

