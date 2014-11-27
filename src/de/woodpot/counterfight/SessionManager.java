package de.woodpot.counterfight;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {
	SharedPreferences sharedPrefs;
	Editor editor;
	Context context;
	
	public static final String PREFNAME = "SharedPrefs";
	public static final String IS_USER_LOGGED_IN = "IsUserLoggedIn";
	public static final String USERNAME_KEY = "username";
	public static final String PASSWORD_KEY = "password";
	
	public SessionManager(Context context){
		this.context = context;
		sharedPrefs = context.getSharedPreferences(PREFNAME, Context.MODE_PRIVATE);
		editor = sharedPrefs.edit();
	}
	
	public void createSession(String username, String password){
		editor.putBoolean(IS_USER_LOGGED_IN, true);
		editor.putString(USERNAME_KEY, username);
		editor.putString(PASSWORD_KEY, password);
		editor.commit();
	}
	
	public boolean isLoggedIn(){
		if (sharedPrefs.getBoolean(IS_USER_LOGGED_IN, false)){
			return true;
		} else {
			return false;
		}
	}
	
	public void clearSession(){
		editor.clear();
		editor.commit();
	}
	
	public String getUsername() {
		return sharedPrefs.getString(USERNAME_KEY, null);
	}
	
	public String getPassword() {
		return sharedPrefs.getString(PASSWORD_KEY, null);
	}
}
