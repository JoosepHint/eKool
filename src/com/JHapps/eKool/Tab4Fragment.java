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
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
 
public class Tab4Fragment extends ListFragment {

	
	
	String TAG = "Page:";
	Document doc;
	boolean failed = false;
	FileOutputStream fos;
	FileOutputStream fos2;
	String [] linksed;
    String linklocation;
    ArrayList<String> links;
    int length;
    File input;
    SecurePreferences preferences;
    
	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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
        View v = inflater.inflate(R.layout.tab4_grades, container, false);
        //setListAdapter(null);
        setRetainInstance(true);
    	preferences = new SecurePreferences(getActivity(),"ekool_preferences", true);
		final String user = preferences.getString("username");
		final String pass = preferences.getString("password");
		 final ArrayList<String> tableRowStrings = new ArrayList<String>();//Hinne
		 final ArrayList<String> grateStrings = new ArrayList<String>();//Tund
		 final ProgressBar load = (ProgressBar) v.findViewById(R.id.load);
	        final TextView empty = (TextView) v.findViewById(R.id.empty);
            input = new File(getActivity().getFilesDir().getPath() + "/Grades.html");
	 		if(preferences.getBool("offline") == false){
				input.delete();
			}
			empty.setVisibility(View.GONE);
		    Thread th = new Thread(new Runnable() {
		        public void run() {
		try {
			{
			    if(isOnline()){
					DefaultHttpClient httpClient = new DefaultHttpClient();
					HttpPost httpPost = new HttpPost(
							"https://ee.ekool.eu/mob?page=grades");
					
					String currentuser = (preferences.getString("currentuser").replace("events", "grades").replace("assignments", "grades"));
					HttpPost httpPost3 = new HttpPost(currentuser);
					UsernamePasswordCredentials creds = new UsernamePasswordCredentials(user, pass);
					httpPost.addHeader(new BasicScheme().authenticate(creds, httpPost));
					httpPost3.addHeader(new BasicScheme().authenticate(creds, httpPost));
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
					httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
					HttpResponse response = httpClient.execute(httpPost, httpContext);
					response.setHeader(new BasicScheme().authenticate(creds, httpPost));
					response.getEntity().consumeContent();
					response = httpClient.execute(httpPost, httpContext);
					response.getEntity().consumeContent();
					response = httpClient.execute(httpPost3, httpContext);
					response.getEntity().consumeContent();
					response = httpClient.execute(httpPost3, httpContext);
					
					int status = (response.getStatusLine()
	                            .getStatusCode()); 
					if(status >= 300){
						response = httpClient.execute(httpPost3, httpContext);
					}
					fos = getActivity().openFileOutput("Grades.html", Context.MODE_PRIVATE);
					InputStream ins = response.getEntity().getContent();
					String html2 = IOUtils.toString(ins);
					html2 = html2.trim();
					fos.write(html2.getBytes());
					fos.close();
				          response.getEntity().consumeContent();
				    }
						doc = Jsoup.parse(input, "UTF-8",
								"http://example.com/");
				}
		        Elements tableRows = doc.select("DIV.additional.main");
		        for (Element tableRow : tableRows){
		        	if (tableRow.hasText()){
		        	    String rowData = tableRow.text();
		        	        tableRowStrings.add(rowData);
		        	}
		        } 
		            Elements grateRows = doc.select("DIV.additional.upper");
		            for (Element grateRow : grateRows){
		            	if (grateRow.hasText()){
		            	    String rowData3 = grateRow.text();
		            	        grateStrings.add(rowData3);         	    	
		            	}
		            } 
		                Elements link = doc.select("a[href]");
		       		 links = new ArrayList<String>();
		             for (Element linked : link) {
		         	    String row = linked.attr("href");
		         	    if(row.contains("coursegrade")){
		         	    links.add(row);
		         	    }
		             }
		            final String[] array = tableRowStrings.toArray(new String[tableRowStrings.size()]);//Hinne
		            final String[] grade_type = grateStrings.toArray(new String[grateStrings.size()]);//Tund
		    		getActivity().runOnUiThread(new Runnable() {
					    public void run() {
					    	load.setVisibility(View.GONE);
		            setListAdapter(new TextAdapter(getActivity(), R.layout.grade, array, grade_type));
		    		if(preferences.getBool("offline") == false){
		    			input.delete();
		    		}
		if(getListAdapter().isEmpty()){
			empty.setVisibility(View.VISIBLE);
		}
					    }
					});
		    		
        }catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}catch(ArrayIndexOutOfBoundsException e){
    			e.printStackTrace();
    		}catch (NullPointerException e) {
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
        if(isOnline()){
    	String item = (String) getListAdapter().getItem(position);
        length = position;
        linklocation ="https://ee.ekool.eu"+ links.get(length);
        Intent i = new Intent(getActivity().getBaseContext(), Grade.class);
        i.putExtra("page", linklocation);
        i.putExtra("lesson", item);
        i.putExtra("current", preferences.getString("currentuser"));
        startActivity(i);
        }else{
        	Toast.makeText(getActivity(), "Kahjuks ei saa hinnet internetiühenduseta täpsemalt vaadata", Toast.LENGTH_LONG).show();
        }
    }

}
