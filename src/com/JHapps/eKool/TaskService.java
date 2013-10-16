package com.JHapps.eKool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class TaskService extends IntentService {
	FileOutputStream fos;
	String user;
	String pass;
	String html3;
	boolean doit = false;
	int mId = 689348;
	 public TaskService() {
	  super("TaskService");
	  // TODO Auto-generated constructor stub
	 }
		public boolean isOnline() {
		    ConnectivityManager cm =
		        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo netInfo = cm.getActiveNetworkInfo();
		    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
		        return true;
		    }
		    return false;
		}
	 @Override
	 protected void onHandleIntent(Intent arg0) {
			SecurePreferences preferences = new SecurePreferences(this,"ekool_preferences", true);
			 final ArrayList<String> tableRowStrings = new ArrayList<String>();
			 final ArrayList<String> dateRowStrings = new ArrayList<String>();
			 final ArrayList<String> grateStrings = new ArrayList<String>();
			 final ArrayList<String> grate2Strings = new ArrayList<String>();
			 final ArrayList<String> gradeStrings = new ArrayList<String>();
				final String user = preferences.getString("username");
				final String pass = preferences.getString("password");
			try {
				if(isOnline()){
					DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost2 = new HttpPost("https://ee.ekool.eu/mob");
			HttpPost httpPost = new HttpPost(preferences.getString("currentuser"));
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
					2);
			nameValuePairs.add(new BasicNameValuePair("username",
					user));
			nameValuePairs.add(new BasicNameValuePair("password",
					pass));
			httpPost.setEntity(new UrlEncodedFormEntity(
					nameValuePairs, "UTF-8"));
			httpPost2.setEntity(new UrlEncodedFormEntity(
					nameValuePairs, "UTF-8"));
			HttpContext httpContext = new BasicHttpContext();
			CookieStore cookieStore = new BasicCookieStore();
			httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
			HttpResponse response1 = httpClient.execute(httpPost2, httpContext);
	          response1.getEntity().consumeContent();
				response1 = httpClient.execute(httpPost2, httpContext);
		          response1.getEntity().consumeContent();
				HttpResponse response3 = httpClient.execute(httpPost, httpContext);
		          response3.getEntity().consumeContent();
					response3 = httpClient.execute(httpPost, httpContext);
			          response3.getEntity().consumeContent();
			HttpResponse response = httpClient.execute(httpPost, httpContext);
	          response.getEntity().consumeContent();
			response = httpClient.execute(httpPost, httpContext);
			fos =  getApplicationContext().openFileOutput("New.html", Context.MODE_PRIVATE);
			InputStream ins = response.getEntity().getContent();
			String html2 = IOUtils.toString(ins);
			html2 = html2.trim();
			fos.write(html2.getBytes());
			fos.close();
		          response.getEntity().consumeContent();
		          response1.getEntity().consumeContent();
		          response3.getEntity().consumeContent();
        final File input = new File(this.getFilesDir().getPath() + "/New.html");
        Document doc;
        doc = Jsoup.parse(input, "UTF-8", "http://example.com/");
        /*Elements tableRows = doc.select("DIV.add.focus");
        for (Element tableRow : tableRows){
        	if (tableRow.hasText()){
        	    String rowData = tableRow.text();
        	    if(rowData.contains("Eemaldatud")){
        	       tableRowStrings.add(rowData+".removed");
        	    }else{
        	        tableRowStrings.add(rowData);
        	    }
        	}
        }*/
            Elements grateRows = doc.select("h3.daymarker, DIV.add.grade, DIV.additional.upper, DIV.additional.main");
            for (Element grateRow : grateRows){
            	if (grateRow.hasText()){
            	    String rowData3 = grateRow.text();
            	    if(grateRow.hasClass("daymarker")){
            	        dateRowStrings.add(rowData3);
            	    }else if(grateRow.hasClass("upper")){
                	    Elements ups = grateRow.getAllElements();
                	    Element up = ups.last();
            	        grate2Strings.add(up.text());  
            	        grateStrings.add(rowData3);  
            	        if(dateRowStrings.size() < grateStrings.size()){
                	        dateRowStrings.add("");
            	       }
            	    }else if(grateRow.hasClass("main")){
            	    Elements news = grateRow.children();
            	    for(Element newer : news){
            	    	if(newer.hasText()){
            	    		String row = newer.text();
            	    		if(newer.hasClass("grade")){
                    	    	gradeStrings.add(row);
                    	    }else if(newer.hasClass("focus")){
                        	    if(row.contains("Eemaldatud")){
                          	       tableRowStrings.add(row+".removed");
                          	    }else{
                          	        tableRowStrings.add(row);
                          	    }
                    	    	if(tableRowStrings.size() - gradeStrings.size() >= 1){
                       	    	   gradeStrings.add("");
                      	       }
                     	        if(dateRowStrings.size() < tableRowStrings.size()){
                         	        dateRowStrings.add("");
                     	       }
                     	    }else if(newer.hasClass("add") && !newer.hasClass("grade") && !newer.hasClass("focus")){
                        	    if(row.contains("Eemaldatud")){
                           	       tableRowStrings.add(row+".removed");
                           	    }else{
                           	        tableRowStrings.add(row);
                           	    }
                     	    	if(tableRowStrings.size() - gradeStrings.size() >= 1){
                        	    	   gradeStrings.add("");
                       	       }
                      	        if(dateRowStrings.size() < tableRowStrings.size()){
                          	        dateRowStrings.add("");
                      	       }
                      	    }
            	    	}
            	    }
            	   }
            	}
            }
            if(grateStrings.size() - gradeStrings.size() >=1){
   	    	   gradeStrings.add("");
            }
        	doit = false;
            String replacer = preferences.getString("news");
            String replace1 = (tableRowStrings.get(0)+grateStrings.get(0)+gradeStrings.get(0));
            String replace2;
            if(tableRowStrings.size() > 1){
                replace2 = (tableRowStrings.get(1)+grateStrings.get(1)+gradeStrings.get(1));
            }else{
            	replace2 = replacer;
            }
			String more;
			more = "";
			if(!replace1.equals(replacer)){
    			more = "";
            	doit = true;
            	if(!replace2.equals(replacer)){
        			more = "Ja veel muud...";
                	doit = true;
                	Log.i("TaskService","Replaced Nothing");
    			}else{
        			more = "";
    			}
			}else{
            	more = "";
            	doit = false;
            	Log.i("TaskService","Replaced ALL");
			}
/*            if(replace1.equals("")){
            	more = "";
            	doit = false;
            	Log.i("TaskService","Replaced 1st");
            }else if(replace2.equals("")){
    			more = "";
            	doit = true;
            	Log.i("TaskService","Replaced 2nd");
            }else{
    			more = "Ja veel muud...";
            	doit = true;
            	Log.i("TaskService","Replaced Nothing");
            }*/ //FAULTY CHECK
            if(doit == true){
				String title = grate2Strings.get(0) + " " + gradeStrings.get(0);
				String bottom = tableRowStrings.get(0);
				Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
				NotificationCompat.Builder mBuilder =
				        new NotificationCompat.Builder(this)
				        .setSmallIcon(R.drawable.ic_launcher)
				        .setContentTitle(title)
				        .setContentText(bottom)
				        .setContentInfo(more)
				        .setAutoCancel(true)
				        .setSound(alarmSound);
				Intent resultIntent = new Intent(this, Login.class);
				TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
				stackBuilder.addParentStack(Login.class);
				stackBuilder.addNextIntent(resultIntent);
				PendingIntent resultPendingIntent =
				        stackBuilder.getPendingIntent(
				            0,
				            PendingIntent.FLAG_UPDATE_CURRENT
				        );
				mBuilder.setContentIntent(resultPendingIntent);
				NotificationManager mNotificationManager =
				    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				mNotificationManager.notify(mId, mBuilder.build());
            }
		}
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (NullPointerException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (IndexOutOfBoundsException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	Log.i("TaskService","Service running");
	 }
}