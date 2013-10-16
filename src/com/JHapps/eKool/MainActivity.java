package com.JHapps.eKool;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.internal.app.ActionBarWrapper;
import com.actionbarsherlock.view.MenuInflater;

public class MainActivity extends SherlockFragmentActivity {

	View custom;
	String currentuser;
	int selectionCurrent;
    ActionBar mActionBar;
    ViewPager mPager;
    int currenttab;
	boolean CheckboxPreference;
	boolean CheckboxPreference2;
	TextView offline;

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	public class MyAdapter extends ArrayAdapter<String>{
		 String [] subs;
		 String[] strings;
        public MyAdapter(Context context, int textViewResourceId, String[] objects, String[] strings2) {
            super(context, textViewResourceId, objects);
            strings = objects;
            subs = strings2;
        }
        
        @Override
        public View getDropDownView(int position, View convertView,ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }
 
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }
 
        public View getCustomView(int position, View convertView, ViewGroup parent) {
        	//getApplicationContext().setTheme(R.style.Theme_Ekool);
            LayoutInflater inflater=getLayoutInflater();
            View row=inflater.inflate(R.layout.row, parent, false);
            TextView label=(TextView)row.findViewById(R.id.company);
            label.setText(strings[position]);
            label.setTextColor(getResources()
                    .getColorStateList(R.color.abs__primary_text_holo_dark));
            TextView sub=(TextView)row.findViewById(R.id.sub);
            sub.setText(subs[position]);
            sub.setTextColor(getResources()
                    .getColorStateList(R.color.abs__bright_foreground_disabled_holo_light));
            return row;
            }
        }

	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//getApplicationContext().setTheme(R.style.Theme_Ekool_dark);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		if(prefs.contains("lang_frequency")){
			String language = (prefs.getString("lang_frequency", "et_EE"));
			Locale locale = new Locale(language); 
			Locale.setDefault(locale);
			Configuration config = new Configuration();
			config.locale = locale;
			getApplicationContext().getResources().updateConfiguration(config, null);
		}else{
			Locale locale = new Locale("et_EE"); 
			Locale.setDefault(locale);
			Configuration config = new Configuration();
			config.locale = locale;
			getApplicationContext().getResources().updateConfiguration(config, null);
		}
		setContentView(R.layout.tabs_layout);
		offline = (TextView) findViewById(R.id.offline);
		if(!isOnline()){
			offline.setVisibility(View.VISIBLE);
		}else{
			offline.setVisibility(View.GONE);
		}
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		getWindow().setSoftInputMode(
			      WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		getSupportActionBar().setIcon(
				   new ColorDrawable(getResources().getColor(android.R.color.transparent)));  
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		//getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#5c6d39")));
		getSupportActionBar().setTitle(null);
		doTabs();
		final SecurePreferences preferences = new SecurePreferences(getBaseContext(),"ekool_preferences", true);
		Bundle extras = getIntent().getExtras();
		if(extras != null){
			final String[] names = extras.getStringArray("names");
			final String[] school = extras.getStringArray("school");
			final String[] links = extras.getStringArray("links");
			if (links.length >1){
        	currentuser = "https://ee.ekool.eu" + links[0];
        	preferences.put("currentuser" , currentuser);
			}else{
	        	preferences.put("currentuser" , "https://ee.ekool.eu/mob?page=events");
	        	
			}
			if(names.length < 2){
				custom = getLayoutInflater().inflate(R.layout.layout_actionbar, null);
				final TextView scho = (TextView)custom.findViewById(R.id.school);
				final TextView poop = (TextView)custom.findViewById(R.id.pupil);
		        new Handler().postDelayed(new Runnable() {
		            public void run() {
		        		String school = preferences.getString("pupil_school");
		        		String title = preferences.getString("pupil_name");
		        		scho.setText(school);
		        		poop.setText(title);
		        		getSupportActionBar().setCustomView(custom);
		            }
		        }, 500);
			}else{
				custom = getLayoutInflater().inflate(R.layout.layout_actionbar_alt, null);
				Spinner mySpinner = (Spinner)custom.findViewById(R.id.spinner);
		        mySpinner.setAdapter(new MyAdapter(MainActivity.this, R.layout.row, names, school));
		        selectionCurrent = mySpinner.getSelectedItemPosition();


		        mySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		        @Override
		        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		            if (selectionCurrent != position){
		            	currentuser = "https://ee.ekool.eu" + links[position];
						/*Toast.makeText(MainActivity.this,
								currentuser, Toast.LENGTH_SHORT).show();*/
			        	preferences.put("currentuser" , currentuser);
			        	doTabs();
						}
					selectionCurrent = position;
		        } 
		        

		        @Override
		        public void onNothingSelected(AdapterView<?> parentView) {
		            // your code here
		        }

		        });
        		getSupportActionBar().setCustomView(custom);
			}
		}
	}
	private void embeddedTabs(Object actionBar, Boolean embed_tabs) {
	    try {
	        if (actionBar instanceof ActionBarWrapper) {
	            // ICS and forward
	            try {
	                Field actionBarField = actionBar.getClass()
	                        .getDeclaredField("mActionBar");
	                actionBarField.setAccessible(true);
	                actionBar = actionBarField.get(actionBar);
	            } catch (Exception e) {
	                Log.e("", "Error enabling embedded tabs", e);
	            }
	        }
	        Method setHasEmbeddedTabsMethod = actionBar.getClass()
	                .getDeclaredMethod("setHasEmbeddedTabs", boolean.class);
	        setHasEmbeddedTabsMethod.setAccessible(true);
	        setHasEmbeddedTabsMethod.invoke(actionBar, embed_tabs);
	    } catch (Exception e) {
	        Log.e("", "Error marking actionbar embedded", e);
	    }
	}
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
	protected void doTabs() {
        mActionBar = getSupportActionBar();
        if (android.os.Build.VERSION.SDK_INT >= 11){
        embeddedTabs(mActionBar, false);
        }
		if(!isOnline()){
			offline.setVisibility(View.VISIBLE);
		}else{
			offline.setVisibility(View.GONE);
		}
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mPager = (ViewPager) findViewById(R.id.viewpager);
        mPager.setOffscreenPageLimit(5);
        if(mActionBar.getTabCount() > 0){
        	currenttab = mPager.getCurrentItem();
        }
        mActionBar.removeAllTabs();
        FragmentManager fm = getSupportFragmentManager();
        ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mActionBar.setSelectedNavigationItem(position);
            }
        };
        mPager.setOnPageChangeListener(pageChangeListener);
        MyFragmentPagerAdapter fragmentPagerAdapter = new MyFragmentPagerAdapter(fm);
        mPager.setAdapter(fragmentPagerAdapter);
 
        mActionBar.setDisplayShowTitleEnabled(true);
 
        /** Defining tab listener */
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
 
            @Override
            public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            }
 
            @Override
            public void onTabSelected(Tab tab, FragmentTransaction ft) {
                mPager.setCurrentItem(tab.getPosition());
            }
 
            @Override
            public void onTabReselected(Tab tab, FragmentTransaction ft) {
            }
        };
 
        Tab tab = mActionBar.newTab()
                .setText(R.string.news)
                .setTabListener(tabListener);
 
        mActionBar.addTab(tab);
 
        tab = mActionBar.newTab()
                .setText(R.string.exercise)
                .setTabListener(tabListener);
 
        mActionBar.addTab(tab);
        
        tab = mActionBar.newTab()
                .setText(R.string.distsipline)
                .setTabListener(tabListener);
 
        mActionBar.addTab(tab);
        
        tab = mActionBar.newTab()
                .setText(R.string.grades)
                .setTabListener(tabListener);
 
        mActionBar.addTab(tab);
        
        tab = mActionBar.newTab()
                .setText(R.string.full)
                .setTabListener(tabListener);
 
        mActionBar.addTab(tab);
        mPager.setCurrentItem(currenttab);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	@Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onMenuItemSelected(int featureId,com.actionbarsherlock.view.MenuItem item) {
	        if (item.getItemId() == R.id.exit) {
	        	finish();
	            return true;
	        } else if (item.getItemId() == R.id.logout) {
	        	finish();
	        	Intent i = new Intent(getApplicationContext(), Login.class);
	        	i.putExtra("noauto", true);
	        	startActivity(i);
	            return true;
	        }  else if (item.getItemId() == R.id.itemRefresh) {
	        	doTabs();
	            return true;
	        } else if (item.getItemId() == R.id.settings) {
	        	finish();
	        	Intent i = new Intent(getApplicationContext(), Settings.class);
	        	startActivity(i);
	            return true;
	        } else {
	            return super.onOptionsItemSelected(item);
	        }
	}
}
