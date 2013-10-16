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
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
 
public class Tab1Fragment extends ListFragment {
    /** (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
	String TAG = "Page:";
	Document doc;
	boolean failed = false;
	FileOutputStream fos;
	FileOutputStream fos2;
    View v;
    String language;
	
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
        v = inflater.inflate(R.layout.tab1_news, container, false);
        //setListAdapter(null);
        setRetainInstance(true);
		final SecurePreferences preferences = new SecurePreferences(getActivity(),"ekool_preferences", true);

		final String user = preferences.getString("username");
		final String pass = preferences.getString("password");
		 final ProgressBar load = (ProgressBar) v.findViewById(R.id.load);
		 final ArrayList<String> tableRowStrings = new ArrayList<String>();//Tund
		 final ArrayList<String> dateRowStrings = new ArrayList<String>();//Kuupäev
		 final ArrayList<String> grateStrings = new ArrayList<String>();//Hinde tüüp(tunnihinne, kontrolltöö jne)
		 final ArrayList<String> gradeStrings = new ArrayList<String>();//Hinne
		 //setListAdapter(null);
	        final TextView empty = (TextView) v.findViewById(R.id.empty);
			empty.setVisibility(View.GONE);
		 Thread th = new Thread(new Runnable() {
		        public void run() {
		try {
				if(isOnline()){
					
					DefaultHttpClient httpClient = new DefaultHttpClient();
					UsernamePasswordCredentials creds = new UsernamePasswordCredentials(user, pass);
				HttpPost httpPost2 = new HttpPost("https://ee.ekool.eu/mob");
			HttpPost httpPost = new HttpPost(preferences.getString("currentuser"));
			httpPost.addHeader(new BasicScheme().authenticate(creds, httpPost));
			httpPost2.addHeader(new BasicScheme().authenticate(creds, httpPost));
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
			HttpResponse response = httpClient.execute(httpPost2, httpContext);
			response.setHeader(new BasicScheme().authenticate(creds, httpPost));
	          response.getEntity().consumeContent();
				response = httpClient.execute(httpPost, httpContext);
		          response.getEntity().consumeContent();
				response = httpClient.execute(httpPost2, httpContext);
		          response.getEntity().consumeContent();
				response = httpClient.execute(httpPost, httpContext);
		          response.getEntity().consumeContent();
			response = httpClient.execute(httpPost, httpContext);
			int status = (response.getStatusLine()
                    .getStatusCode()); 
		if(status >= 300){
			response = httpClient.execute(httpPost, httpContext);
		}
			fos =  getActivity().openFileOutput("News.html", Context.MODE_PRIVATE);
			InputStream ins = response.getEntity().getContent();
			String html2 = IOUtils.toString(ins);
			html2 = html2.trim();
			fos.write(html2.getBytes());
			fos.close();
		          response.getEntity().consumeContent();
			}
        final File input = new File(getActivity().getFilesDir().getPath() + "/News.html");//TODO Encrypt data
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
            	        grateStrings.add(rowData3);  
            	        if(dateRowStrings.size() < grateStrings.size()){//Kui hinne ei ole otseselt kuupäeva all
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
   	    	   gradeStrings.add("");//Juhul kui viimasel uudisel ei ole hinnet lisatud
            }
            if(tableRowStrings.size() > 0){
                preferences.put("news", tableRowStrings.get(0)+grateStrings.get(0)+gradeStrings.get(0));//Sünkroonimise jaoks
            }else{
            	preferences.put("news", "");
            }
            //preferences.put("news2nd", tableRowStrings.get(1)+grateStrings.get(1)+gradeStrings.get(1)); 
            final String[] array = tableRowStrings.toArray(new String[tableRowStrings.size()]);//Tund
            final String[] date = dateRowStrings.toArray(new String[dateRowStrings.size()]);//Kuupäev
            final String[] grade_type = grateStrings.toArray(new String[grateStrings.size()]);//Hinde tüüp(tunnihinne, kontrolltöö jne)
            final String[] grade = gradeStrings.toArray(new String[gradeStrings.size()]);//Hinne
    		getActivity().runOnUiThread(new Runnable() {
			    public void run() {
    		setListAdapter(new NewsAdapter(getActivity(), R.layout.news, array, grade_type, date, grade));
    		if(getListAdapter().isEmpty()){
    			empty.setVisibility(View.VISIBLE);
    		}else{
    			empty.setVisibility(View.GONE);
    		}
    		load.setVisibility(View.GONE);
    		if(preferences.getBool("offline") == false && preferences.getBool("sync") == false){
        			input.delete();
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
      // Do something with the data

    }
}