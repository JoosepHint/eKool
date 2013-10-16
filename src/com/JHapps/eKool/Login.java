package com.JHapps.eKool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuInflater;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class Login extends SherlockActivity {
	String myString;
	String TAG = "Page:";
	FileOutputStream fos;
	FileOutputStream fos2;
	SecurePreferences preferences;
	String user;
	String pass;
	String html2;
	boolean failed = false;
	RelativeLayout loading;
	TextView fail;
	CheckBox auto;
	CheckBox remember;
	Document doc;
	 Thread th;
		String title;
		String school;
	 boolean us = false;
	 boolean incog;

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
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
		preferences = new SecurePreferences(getBaseContext(),"ekool_preferences", true);
		if(!preferences.containsKey("firstrun")){
		final ArrayList<String> languages = new ArrayList<String>();
		languages.add("et_EE");
		languages.add("en_GB");
		languages.add("ru_RU");
			preferences.put("firstrun", "true");
			AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
			builder.setTitle(R.string.language);
			builder.setCancelable(false);
			builder.setItems(R.array.pref_lang_frequency_titles, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int lang) {
	            	   String language = languages.get(lang);
	            	   prefs.edit().putString("lang_frequency", language).commit();
	            	   finish();
	            	   Intent i = new Intent(Login.this, Login.class);
	            	   startActivity(i);
	               }
	               });

			AlertDialog dialog = builder.create();
			dialog.show();
		}
		setContentView(R.layout.activity_login);
		final EditText username = (EditText) findViewById(R.id.username);
		final EditText password = (EditText) findViewById(R.id.password);
		final Button login = (Button) findViewById(R.id.button1);
		final LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
		fail = (TextView) findViewById(R.id.failed);
		loading = (RelativeLayout) findViewById(R.id.login_status);
		remember = (CheckBox) findViewById(R.id.remember);
		auto = (CheckBox) findViewById(R.id.automatic);
		fail.setText(null);
		loading.setVisibility(View.GONE);
		 final File input = new File(getBaseContext().getFilesDir().getPath() + "/Users.html");
		if(preferences.getBool("offline") == false){
			input.delete();
	}
		auto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			   @Override
			   public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				   if (isChecked){
					   remember.setChecked(true);
				   }
			   }
			});
		remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			   @Override
			   public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				   if (!isChecked){
					   auto.setChecked(false);
				   }
			   }
			});
		if(forgotten()){
			preferences.removeValue("username");
			preferences.removeValue("password");
		}
		if (preferences.containsKey("username")
				&& preferences.containsKey("password")) {
			username.setText(preferences.getString("username"));
			password.setText(preferences.getString("password"));
			remember.setChecked(true);
			if(!isOnline() && preferences.getBool("offline") == true){
				login.setText(R.string.logoffline);
				login.setTextSize(15.0f);
			}else{
				login.setText(R.string.login);
			}
		}      
		login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loading.setVisibility(View.VISIBLE);
				    	InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
				        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
				user = username.getText().toString();
				pass = password.getText().toString();
				{
			    th = new Thread(new Runnable() {
			        public void run() {
			            try {
					if (isOnline()) {
						DefaultHttpClient httpClient = new DefaultHttpClient();
						HttpPost httpPost2 = new HttpPost(
								"https://ee.ekool.eu/mob");
						HttpPost httpPost = new HttpPost(
								"https://ee.ekool.eu/mob?page=account");
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
						HttpResponse response = httpClient.execute(httpPost2);
						response = httpClient.execute(httpPost2);
						//response1.getEntity().consumeContent();
						response = httpClient.execute(httpPost);
						response = httpClient.execute(httpPost);
						InputStream ins = response.getEntity().getContent();
						html2 = IOUtils.toString(ins);
						fos =  openFileOutput("Users.html", Context.MODE_PRIVATE);
						fos.write(html2.getBytes());
						fos.close();
						response.getEntity().consumeContent();
					   if(!(html2.length() == 0)){
						if (html2.contains("eba�nnestus")) {//Kui on vale parool/kasutajanimi
							failed = true;
						} else {
							if(auto.isChecked() && remember.isChecked()){
								preferences.put("auto", "true");
								add();
							}else if(remember.isChecked()){
								add();
								preferences.removeValue("auto");
							}
							if(!remember.isChecked()){
								add();
								forget();
							}
							failed = false;
						}
					}
					if (failed == true) {
						Login.this.runOnUiThread(new Runnable() {
						    public void run() {
								fail.setText("eKooli sisse logimine eba�nnestus!");
								loading.setVisibility(View.GONE);
						    }
						});
					} else {
				        if(input.exists()){
				        doc = Jsoup.parse(input, "UTF-8", "http://example.com/");
				        Elements elem = doc.select("DIV.name");
				        Elements elem2 = doc.select("DIV.additional");
		                Elements link = doc.select("a[href]");
			       		 ArrayList<String> Name = new ArrayList<String>();
			       		 ArrayList<String> School = new ArrayList<String>();
		       		 ArrayList<String> Links = new ArrayList<String>();
		             for (Element linked : link) {
		            	 if (linked.attr("href").contains("assignments&acc")){
				         	    String row = linked.attr("href").replace("assignments","events");
				         	    Links.add(row);
		            	 }
		         	    }
		             for (Element ele : elem){
		            	 String add = ele.text();
		            	 Name.add(add);
		             }
		             for (Element ele2 : elem2){
		            	 String add2 = ele2.text();
		            	 School.add(add2);
		             }
					if(elem.isEmpty()){//Kui ei ole lisatud �htegi kasutajat
						Login.this.runOnUiThread(new Runnable() {
						    public void run() {
								Toast.makeText(getBaseContext(), "Sul ei ole veel �heski koolis kasutus�igust, tee arvuti eKoolis kooli taotlus ja proovi hiljem uuesti!", Toast.LENGTH_LONG).show();
								loading.setVisibility(View.GONE);
						    }
						});
					}else{
			 			title = elem.get(0).text();
						school = (elem2.get(0).text());
					preferences.put("pupil_name", title);
					preferences.put("pupil_school", school);
		             final String[] name = Name.toArray(new String[Name.size()]);//K�ik kasutajate nimed
		             final String[] school = School.toArray(new String[School.size()]);//Koolide nimed
		             final String[] links = Links.toArray(new String[Links.size()]);//Lingid Kasutajate eKoolidesse
						Intent i = new Intent(Login.this, MainActivity.class);
						i.putExtra("names", name);
						i.putExtra("school", school);
						i.putExtra("links", links);
						startActivity(i);
						finish();
					}
					}else{
						Login.this.runOnUiThread(new Runnable() {
						    public void run() {
								fail.setText("Interneti�hendus Puudub!");//Pole interneti ja andmed kust laadida
								loading.setVisibility(View.GONE);
						    }
						});
					}
					}
				}else{
			        if(input.exists() && preferences.getBool("offline") == true){//Logi Sisse v�rguta
			        try {
						doc = Jsoup.parse(input, "UTF-8", "http://example.com/");
			        Elements elem = doc.select("DIV.name");
			        Elements elem2 = doc.select("DIV.additional");
	                Elements link = doc.select("a[href]");
		       		 ArrayList<String> Name = new ArrayList<String>();
		       		 ArrayList<String> School = new ArrayList<String>();
	       		 ArrayList<String> Links = new ArrayList<String>();
	             for (Element linked : link) {
	            	 if (linked.attr("href").contains("assignments&acc")){
			         	    String row = linked.attr("href").replace("assignments","events");
			         	    Links.add(row);
	            	 }
	         	    }
	             for (Element ele : elem){
	            	 String add = ele.text();
	            	 Name.add(add);
	             }
	             for (Element ele2 : elem2){
	            	 String add2 = ele2.text();
	            	 School.add(add2);
	             }
		 			title = elem.get(0).text();
					school = (elem2.get(0).text());
				preferences.put("pupil_name", title);
				preferences.put("pupil_school", school);
	             final String[] name = Name.toArray(new String[Name.size()]);
	             final String[] school = School.toArray(new String[School.size()]);
	             final String[] links = Links.toArray(new String[Links.size()]);
					Intent i = new Intent(Login.this, MainActivity.class);
					i.putExtra("names", name);
					i.putExtra("school", school);
					i.putExtra("links", links);
					i.putExtra("incognito", incog);
					startActivity(i);
					finish();
		    		if(preferences.getBool("offline") == false){
	        			input.delete();
	    		}
					} catch (IOException e) {
						Toast.makeText(getBaseContext(), "Toimus Viga!", Toast.LENGTH_LONG).show();
						loading.setVisibility(View.GONE);
						e.printStackTrace();
					}
				}else{
		    		Login.this.runOnUiThread(new Runnable() {
					    public void run() {
					Toast.makeText(getBaseContext(), "Kahjuks ei saa eKooli internetita vaadata!(Kontrolli seadete men��st, kas v�rguta eKool on v�imaldatud)", Toast.LENGTH_LONG).show();
					loading.setVisibility(View.GONE);
					    }
		    		});
				}
				}
		    		Login.this.runOnUiThread(new Runnable() {
					    public void run() {
					loading.setVisibility(View.GONE);
					    }
		    		});
				} catch (final IOException e) {
		    		Login.this.runOnUiThread(new Runnable() {
					    public void run() {
							Toast.makeText(getBaseContext(), "Toimus Viga! "+e.toString(), Toast.LENGTH_LONG).show();
							loading.setVisibility(View.GONE);
					    }
		    		});
					e.printStackTrace();
				}
			    }

			    });
			    th.start();
				}
			}
		});
		Bundle extras = getIntent().getExtras();
		if(extras != null){
			us = getIntent().getExtras().getBoolean("noauto");
		}else{
			us = false;
		}
		if(preferences.containsKey("auto") && us == false){
			auto.setChecked(true);
			remember.setChecked(true);
			login.performClick();
		}
	}
	@Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.login, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onMenuItemSelected(int featureId,com.actionbarsherlock.view.MenuItem item) {
    	if (item.getItemId() == R.id.settings) {
    		finish();
	        	Intent i = new Intent(getApplicationContext(), Settings.class);
	        	startActivity(i);
	            return true;
	        }/* else if(item.getItemId() == R.id.incognito){
	    		finish();
	        	Intent i = new Intent(getApplicationContext(), Login.class);
	        	i.putExtra("incognito", true);
	        	startActivity(i);
	        	return true;
	        }*/else{
	            return super.onOptionsItemSelected(item);
	        }
	}
	private void add() {
		preferences.put("username", user);
		preferences.put("password", pass);
		preferences.put("remember", "checked");
		preferences.removeValue("temp");
	}
	private void forget(){
		preferences.put("temp", "true");
	}
	private boolean forgotten(){
		return preferences.containsKey("temp");
	}
}
