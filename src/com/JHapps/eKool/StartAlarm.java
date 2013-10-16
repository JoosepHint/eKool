package com.JHapps.eKool;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class StartAlarm {
	
	static Intent intent;
	
	public static void start(SecurePreferences preferences, Context ctx){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        if(preferences.getBool("sync")){
	       try {
        	   AlarmManager alarms = (AlarmManager) ctx
        	     .getSystemService(Context.ALARM_SERVICE);
        	   intent = new Intent(ctx,
        	     alarmReceiver.class);
        	   intent.putExtra(alarmReceiver.ACTION_ALARM,
        			   alarmReceiver.ACTION_ALARM);
        	 
        	   final PendingIntent pIntent = PendingIntent.getBroadcast(ctx,
        	     11002, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        	   int time = Integer.parseInt(prefs.getString("sync_frequency", "3600000"));
        	   alarms.setRepeating(AlarmManager.RTC_WAKEUP,
        	     System.currentTimeMillis(), time, pIntent);
        	  } catch (Exception e) {
        	   // TODO Auto-generated catch block
        	   e.printStackTrace();
        	  }
		}	
		
	}
	
	public static void stop(Context ctx){
		// TODO Congrat yourself for making it work the first time 
 	   AlarmManager alarms = (AlarmManager) ctx
      	     .getSystemService(Context.ALARM_SERVICE);
      	   intent = new Intent(ctx,
      	     alarmReceiver.class);
      	   intent.putExtra(alarmReceiver.ACTION_ALARM,
      			   alarmReceiver.ACTION_ALARM);
    	   final PendingIntent pIntent = PendingIntent.getBroadcast(ctx,
          	     11002, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarms.cancel(pIntent);
	}
	
	
}
