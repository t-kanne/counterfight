package de.woodpot.counterfight;

import android.app.AlertDialog;
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
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

@SuppressWarnings("deprecation")

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    SessionManager sm;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sm = new SessionManager(this);
        
        addPreferencesFromResource(R.xml.pref_settings);
        
        Preference deleteUserPref = findPreference("account_delete");
        Preference loginLogoutUserPref = findPreference("account_loginlogout");
        Preference loginActPref = findPreference("settings_activity_login");
        
        if (sm.isLoggedIn() == false) {
        	deleteUserPref.setEnabled(false);
        	loginLogoutUserPref.setTitle(R.string.pref_account_login);
        } else {
        	loginLogoutUserPref.setTitle(R.string.pref_account_logout);	 
        	
            deleteUserPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference deleteUser){
                	deleteAccount();
                	return true;
                }
             
            });
        }
        
    	loginLogoutUserPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				if (sm.isLoggedIn() == true) {
					sm.clearSession();
				} 
				Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);
				return true;
			}
    	});
    	
    	loginActPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				
				Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
				startActivity(intent);
				return true;
			}
    	});
 
    }
	
	public void deleteAccount() {
		sm = new SessionManager(this);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		// Edittext für die Sicherheits-Passwortabfrage
		final EditText pw = new EditText(this);
		pw.setTransformationMethod(PasswordTransformationMethod.getInstance());
		pw.setHint(R.string.string_loginact_passwordhint);
    	
		builder.setView(pw);
        builder.setTitle(R.string.pref_account_warning_header);
        builder.setPositiveButton(R.string.pref_account_warning_positive_button, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String value = pw.getText().toString();
				
				if(value.equals(sm.getPassword())){
					DeleteUserAsyncTask delUser = new DeleteUserAsyncTask(SettingsActivity.this);
					delUser.execute();
				} else {
					Toast.makeText(getBaseContext(), R.string.string_registeract_wrongpassword, Toast.LENGTH_SHORT).show();
				}
			}
		}); 
        builder.setNegativeButton(R.string.pref_account_warning_negative_button, null);
        builder.setMessage(R.string.pref_account_warning_explanation);
        AlertDialog warningDialog = builder.create();
        warningDialog.show();

        
		
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

