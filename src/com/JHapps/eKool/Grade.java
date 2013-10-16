package com.JHapps.eKool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.MenuItem;

public class Grade extends SherlockListActivity {
	String TAG = "Page:";
	FileOutputStream fos;
	StringBuffer storedString = new StringBuffer();
	Document doc;
	int length;
	String [] linksed;
    String linklocation;
    ArrayList<String> links;
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grade);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setListAdapter(null);
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			Intent i = new Intent(this, MainActivity.class);
			startActivity(i);
		    return;
		    }
		// Get data via the key
		String value1 = extras.getString("page");
		String title = extras.getString("lesson");
		setTitle(title);
		if (value1 != null) {
			linklocation = value1;
			performcheck();
		} else{
			Intent i = new Intent(this, MainActivity.class);
			startActivity(i);
		}
		performcheck();
	}
	public void performcheck(){
	    Thread th = new Thread(new Runnable() {
	        public void run() {
		 ArrayList<String> tableRowStrings = new ArrayList<String>();
		 ArrayList<String> grateStrings = new ArrayList<String>();
		 ArrayList<String> gradeStrings = new ArrayList<String>();
		SecurePreferences preferences = new SecurePreferences(getBaseContext(),"ekool_preferences", true);
		String user = preferences.getString("username");
		String pass = preferences.getString("password");
		String currentuser = preferences.getString("currentuser");
		//TextView text = (TextView) findViewById(R.id.text);
		try {
		    if(isOnline()){
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(linklocation);
			HttpPost httpPost2 = new HttpPost(currentuser);
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
			HttpResponse response1 = httpClient.execute(httpPost, httpContext);
			response1.getEntity().consumeContent();
			HttpResponse response2 = httpClient.execute(httpPost2, httpContext);
			response2.getEntity().consumeContent();
			HttpResponse response = httpClient.execute(httpPost, httpContext);
			fos = openFileOutput("Grade.html", Context.MODE_PRIVATE);
			InputStream ins = response.getEntity().getContent();
			String html2 = IOUtils.toString(ins);
			html2 = html2.trim();
			fos.write(html2.getBytes());
			fos.close();
			response.getEntity().consumeContent();
		    }
	            File input = new File(getBaseContext().getFilesDir().getPath() +"/Grade.html");
	            doc = Jsoup.parse(input, "UTF-8", "http://example.com/");
       Elements tableRows = doc.select("DIV.add.focus");
       for (Element tableRow : tableRows){
       	if (tableRow.hasText()){
       	    String rowData = tableRow.text();
       	        tableRowStrings.add(rowData);
       	}
           Elements grateRows = doc.select("DIV.additional.upper");
           for (Element grateRow : grateRows){
           	if (grateRow.hasText()){
           	    String rowData3 = grateRow.text();
           	        grateStrings.add(rowData3);         	    	
           	}
            Elements gradeRows = doc.select("DIV.add.grade");
            for (Element grade : gradeRows){
            	if (grade.hasText()){
            	    String rowDat3 = grade.text();
            	    gradeStrings.add(rowDat3);         	    	
            	}
           final String[] grades = gradeStrings.toArray(new String[gradeStrings.size()]);
           final String[] array = tableRowStrings.toArray(new String[tableRowStrings.size()]);
           final String[] grade_type = grateStrings.toArray(new String[grateStrings.size()]);
   		Grade.this.runOnUiThread(new Runnable() {
		    public void run() {
   		setListAdapter(new GradeAdapter(getBaseContext(), R.layout.list2, array, grade_type, grades));
   		
		    }
   		});
       }
       }
		}
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(NullPointerException e){
			performcheck();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       }
	    });
	    th.start();
	}
}

