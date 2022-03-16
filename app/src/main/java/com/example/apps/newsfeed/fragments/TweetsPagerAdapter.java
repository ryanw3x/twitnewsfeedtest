package com.example.apps.newsfeed.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TweetsPagerAdapter extends FragmentPagerAdapter {

    
    private String[] tabTitles = new String[]{"Home", "Mentions"};
    private Context context;

    private HomeTimelineFragment timelineFragment;
    private MentionsTimelineFragment mentionsFragment;

    public TweetsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        timelineFragment = new HomeTimelineFragment();
        mentionsFragment = new MentionsTimelineFragment();
        this.context = context;
    }

    
    @Override
    public int getCount() {
        return 2;
    }

    
    @Override
    public Fragment getItem(int position) {
        
        

        if (position == 0) return timelineFragment;
        else if (position == 1) return mentionsFragment;
        else return null;
    }

    
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
