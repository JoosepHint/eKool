package com.JHapps.eKool;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.KeyEvent;
 
public class Settings extends PreferenceActivity {
	@Override
	protected void onStop(){
		final SecurePreferences preferences = new SecurePreferences(getBaseContext(),"ekool_preferences", true);
        if(preferences.getBool("sync")){
        	StartAlarm.start(preferences, getBaseContext());
        	}
        super.onStop();
	}
	@Override
	protected void onPause(){
		final SecurePreferences preferences = new SecurePreferences(getBaseContext(),"ekool_preferences", true);
        if(preferences.getBool("sync")){
        	StartAlarm.start(preferences, getBaseContext());
        	}
        super.onPause();
	}
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
        	finish();
        	Intent i = new Intent(Settings.this, Login.class);
        	startActivity(i);
        }
		return false;
    }
		@SuppressWarnings("deprecation")
		@Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                addPreferencesFromResource(R.xml.preferences);
                // Get the custom preference
        		final SecurePreferences preferences = new SecurePreferences(Settings.this,"ekool_preferences", true);
        		
                final CheckBoxPreference customPref = (CheckBoxPreference) findPreference("checkboxPref");
                customPref.setOnPreferenceClickListener(new CheckBoxPreference.OnPreferenceClickListener() {
					
					@Override
					public boolean onPreferenceClick(Preference preference) {
						if (customPref.isChecked()){
							preferences.putBoolean("offline", true);
						}else{
							preferences.putBoolean("offline", false);
						}
						
						return false;
					}
				});
                
                final CheckBoxPreference customPref2 = (CheckBoxPreference) findPreference("checkboxPref2");
                customPref2.setOnPreferenceClickListener(new CheckBoxPreference.OnPreferenceClickListener() {
					
					@Override
					public boolean onPreferenceClick(Preference preference) {
						if (customPref2.isChecked()){
							preferences.putBoolean("sync", true);
				        	StartAlarm.start(preferences, getBaseContext());
						}else{
							preferences.putBoolean("sync", false);
				        	StartAlarm.stop(getBaseContext());
						}
						
						return false;
					}
				});
        }
}