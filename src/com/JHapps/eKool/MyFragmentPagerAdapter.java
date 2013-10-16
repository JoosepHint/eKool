package com.JHapps.eKool;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
 
public class MyFragmentPagerAdapter extends FragmentPagerAdapter{
 
    final int PAGE_COUNT = 5;
 
    /** Constructor of the class */
    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }
 
    /** This method will be invoked when a page is requested to create */
    @Override
    public Fragment getItem(int arg0) {
        Bundle data = new Bundle();
        switch(arg0){
            case 0:
                Tab1Fragment news = new Tab1Fragment();
                data.putInt("current_page", arg0+1);
                news.setArguments(data);
                return news;
 
            case 1:
            	Tab2Fragment assignments = new Tab2Fragment();
                data.putInt("current_page", arg0+1);
                assignments.setArguments(data);
                return assignments;
                
            case 2:
            	Tab3Fragment distsipline = new Tab3Fragment();
                data.putInt("current_page", arg0+1);
                distsipline.setArguments(data);
                return distsipline;
            case 3:
            	Tab4Fragment grades = new Tab4Fragment();
                data.putInt("current_page", arg0+1);
                grades.setArguments(data);
                return grades;
            case 4:
            	Tab5Fragment regular = new Tab5Fragment();
                data.putInt("current_page", arg0+1);
                regular.setArguments(data);
                return regular;
        }
        return null;
    }
 
    /** Returns the number of pages */
    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}