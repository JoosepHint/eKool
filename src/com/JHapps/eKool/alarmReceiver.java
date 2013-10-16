package com.JHapps.eKool;

import java.util.Calendar;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class alarmReceiver extends BroadcastReceiver {
	public static String ACTION_ALARM = "com.JHapps.alarmReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {

		Log.i("Alarm Receiver", "Entered");
		//Toast.makeText(context, "Entered", Toast.LENGTH_SHORT).show();
		Bundle bundle = intent.getExtras();
		String action = bundle.getString(ACTION_ALARM);
		if (action.equals(ACTION_ALARM)) {
			Log.i("Alarm Receiver", "If loop");
			Intent inService = new Intent(context, TaskService.class);
			context.startService(inService);
		} else {
			Log.i("Alarm Receiver", "Else loop");
			Toast.makeText(context, "Else loop", Toast.LENGTH_SHORT).show();
		}
	}
}
