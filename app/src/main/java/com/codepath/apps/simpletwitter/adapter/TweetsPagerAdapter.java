package com.codepath.apps.simpletwitter.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.apps.simpletwitter.fragments.HomeTimelineFragment;
import com.codepath.apps.simpletwitter.fragments.MentionsTimelineFragment;

public class TweetsPagerAdapter extends FragmentPagerAdapter {
    private String tabTitles[] = { "Home", "Mentions" };

    public TweetsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if( position == 0 ) {
            return new HomeTimelineFragment();
        }
        else if ( position == 1 ) {
            return new MentionsTimelineFragment();
        }
        else {
            return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }
}