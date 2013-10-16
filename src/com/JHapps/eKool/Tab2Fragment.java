package com.JHapps.eKool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Tab2Fragment extends ListFragment {

	
	String TAG = "Page:";
	Document doc;
	boolean failed = false;
	FileOutputStream fos;
	FileOutputStream fos2;
	View v;
	SecurePreferences preferences;
	HttpResponse response;
	HttpResponse response1;
	HttpResponse response2;
	String[] array;
	ArrayList<String> tableRowStrings;
	ArrayList<String> grateStrings;
	ArrayList<String> dateRowStrings;
	Context context;

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}
		v = inflater.inflate(R.layout.tab2_assigns, container, false);
		// setListAdapter(null);
		setRetainInstance(true);
		context = getActivity();
		preferences = new SecurePreferences(getActivity(), "ekool_preferences", true);
		final String user = preferences.getString("username");
		final String pass = preferences.getString("password");
		final ProgressBar load = (ProgressBar) v.findViewById(R.id.load);
		tableRowStrings = new ArrayList<String>();//Ülesanne
		dateRowStrings = new ArrayList<String>();//Kuupäev
		grateStrings = new ArrayList<String>();//Tund
		final File input = new File(getActivity().getFilesDir().getPath()
				+ "/Assigns.html");
		if (preferences.getBool("offline") == false) {
			input.delete();
		}
		// setListAdapter(null);
		final TextView empty = (TextView) v.findViewById(R.id.empty);
		empty.setVisibility(View.GONE);
		// TextView text = (TextView) findViewById(R.id.text);
		Thread th = new Thread(new Runnable() {
			public void run() {
				try {
					if (isOnline()) {
						DefaultHttpClient httpClient = new DefaultHttpClient();
						HttpPost httpPost = new HttpPost(
								"https://ee.ekool.eu/mob");
						String currentuser = (preferences.getString(
								"currentuser").replace("events", "assignments")
								.replace("grades", "assignments"));
						HttpPost httpPost3 = new HttpPost(currentuser);
						UsernamePasswordCredentials creds = new UsernamePasswordCredentials(
								user, pass);
						httpPost.addHeader(new BasicScheme().authenticate(
								creds, httpPost));
						httpPost3.addHeader(new BasicScheme().authenticate(
								creds, httpPost));
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
								2);
						nameValuePairs.add(new BasicNameValuePair("username",
								user));
						nameValuePairs.add(new BasicNameValuePair("password",
								pass));
						httpPost.setEntity(new UrlEncodedFormEntity(
								nameValuePairs, "UTF-8"));
						httpPost3.setEntity(new UrlEncodedFormEntity(
								nameValuePairs, "UTF-8"));
						HttpContext httpContext = new BasicHttpContext();
						CookieStore cookieStore = new BasicCookieStore();
						httpContext.setAttribute(ClientContext.COOKIE_STORE,
								cookieStore);
						response = httpClient.execute(httpPost, httpContext);
						response.setHeader(new BasicScheme().authenticate(
								creds, httpPost));
						response.getEntity().consumeContent();
						response = httpClient.execute(httpPost, httpContext);
						response.getEntity().consumeContent();
						response = httpClient.execute(httpPost3, httpContext);
						response.getEntity().consumeContent();
						response = httpClient.execute(httpPost3, httpContext);
						int status = (response.getStatusLine().getStatusCode());
						if (status >= 300) {
							response = httpClient.execute(httpPost3,
									httpContext);
						}
						fos = getActivity().openFileOutput("Assigns.html",
								Context.MODE_PRIVATE);
						InputStream ins = response.getEntity().getContent();
						String html2 = IOUtils.toString(ins);
						html2 = html2.trim();
						fos.write(html2.getBytes());
						fos.close();
						response.getEntity().consumeContent();

					}
					doc = Jsoup.parse(input, "UTF-8", "http://example.com/");
					Elements tableRows = doc.select("DIV.additional.main");
					for (Element tableRow : tableRows) {
						if (tableRow.hasText()) {
							String rowData = tableRow.text();
							tableRowStrings.add(rowData);
						}
					}
					/*
					 * Elements dateRows = doc.select("H3.daymarker"); for
					 * (Element dateRow : dateRows){ String rowData2 =
					 * dateRow.text(); if(dateRowStrings.contains(rowData2) &&
					 * !rowData2.equals("")){ dateRowStrings.add(""); }else{
					 * dateRowStrings.add(rowData2); } }
					 */
					Elements grateRows = doc
							.select("h3.listheader, DIV.additional.upper");
					for (Element grateRow : grateRows) {
						if (grateRow.hasText()) {
							String rowData3 = grateRow.text();
							if (grateRow.hasClass("listheader")) {
								dateRowStrings.add(rowData3);
							} else if (grateRow.hasClass("upper")) {
								grateStrings.add(rowData3);
								if (dateRowStrings.size() >= grateStrings
										.size()) {
								} else {
									dateRowStrings.add("");
								}
							}
						}
					}
					final String[] array = tableRowStrings
							.toArray(new String[tableRowStrings.size()]);//Ülesanne
					final String[] date = dateRowStrings
							.toArray(new String[dateRowStrings.size()]);//Kuupäev
					final String[] grade_type = grateStrings
							.toArray(new String[grateStrings.size()]);//Tund
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							setListAdapter(new AssignAdapter(getActivity(),
									R.layout.assign, array, grade_type, date));
							if (getListAdapter().isEmpty()) {
								empty.setVisibility(View.VISIBLE);
							} else {
								empty.setVisibility(View.GONE);

							}
							load.setVisibility(View.GONE);
							if (preferences.getBool("offline") == false) {
								input.delete();
							}
						}
					});
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
				} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (AuthenticationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		th.start();
		return v;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		/*
		 * final String combined = (tableRowStrings.get(position) +
		 * grateStrings.get(position)); final CheckBox done = (CheckBox)
		 * v.findViewById(R.id.done); final SharedPreferences prefs =
		 * PreferenceManager.getDefaultSharedPreferences(context); try {
		 * JSONArray dones = JSONSharedPreferences.loadJSONArray(prefs, "done");
		 * if(dones.toString().contains(combined)){ done.setChecked(true); } }
		 * catch (JSONException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } if(done.isChecked()){ Toast.makeText(context,
		 * combined, Toast.LENGTH_LONG).show(); try { JSONArray dones =
		 * JSONSharedPreferences.loadJSONArray(prefs, "done");
		 * if(!dones.toString().contains(combined)){ dones.put(combined);
		 * JSONSharedPreferences.saveJSONArray(prefs, "done", dones); } } catch
		 * (JSONException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } }else{ try { JSONArray dones =
		 * JSONSharedPreferences.loadJSONArray(prefs, "done");
		 * if(dones.toString().contains(combined)){ int i; for(i = 0; i <
		 * dones.length(); i++){ if (dones.getString(i).equals(combined)){
		 * dones.getJSONObject(i).remove(combined);
		 * JSONSharedPreferences.saveJSONArray(prefs, "done", dones); } } } }
		 * catch (JSONException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } }
		 */

	}
}