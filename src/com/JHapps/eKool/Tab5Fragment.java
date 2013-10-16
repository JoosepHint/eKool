package com.JHapps.eKool;
 
import java.lang.reflect.InvocationTargetException;

import org.apache.http.util.EncodingUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.widget.ZoomButtonsController;

public class Tab5Fragment extends Fragment {
	
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        View v = inflater.inflate(R.layout.tab5_norm, container, false);
        final WebView mWebview = (WebView) v.findViewById(R.id.webView);
        mWebview.setVisibility(View.GONE);
        mWebview.clearHistory();
        mWebview.clearCache(true);
        Context mContext = getActivity().getApplicationContext();
        mWebview.clearFormData();
        WebSettings webSettings = mWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);
		SecurePreferences preferences = new SecurePreferences(getActivity(),"ekool_preferences", true);
		final String user = preferences.getString("username");
		final String pass = preferences.getString("password");
		mWebview.loadUrl("https://ee.ekool.eu/mob?page=out");
        final String postData = "username="+user+"&password="+pass;
        mWebview.getSettings().setSupportZoom(true);
        mWebview.getSettings().setBuiltInZoomControls(true);
        mWebview.getSettings().setDefaultZoom(ZoomDensity.FAR);
        mWebview.getSettings().setUseWideViewPort(true);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
          // Use the API 11+ calls to disable the controls
          // Use a seperate class to obtain 1.6 compatibility
          new Runnable() {
            @SuppressLint("NewApi")
			public void run() {
            	mWebview.getSettings().setDisplayZoomControls(false);
            }
          }.run();
        } else {
          ZoomButtonsController zoom_controll;
		try {
			zoom_controll = (ZoomButtonsController) mWebview.getClass().getMethod("getZoomButtonsController").invoke(mWebview, null);
	          zoom_controll.getContainer().setVisibility(View.GONE);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        }
        mWebview.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
            	if(url.equals("https://ee.ekool.eu/mob") || url.equals("https://ee.ekool.eu/mob?page=out")){
                    mWebview.postUrl("https://ee.ekool.eu/mob?page=events", EncodingUtils.getBytes(postData, "BASE64"));
            	}
            	if(url.equals("https://ee.ekool.eu/mob?page=events")){
            		mWebview.loadUrl("https://ee.ekool.eu/index_en.html?#/?screenId=p.main.show");
                    mWebview.setVisibility(View.VISIBLE);
            	}
            }
        });
        return v;
    }
}