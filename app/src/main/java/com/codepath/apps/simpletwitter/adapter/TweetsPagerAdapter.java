package com.codepath.apps.simpletwitter.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.codepath.apps.simpletwitter.fragments.HomeTimelineFragment;
import com.codepath.apps.simpletwitter.fragments.MentionsTimelineFragment;

public class TweetsPagerAdapter extends SmartFragmentStatePagerAdapter {
    private String tabTitles[] = { "Home", "Mentions" };

    public TweetsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return new HomeTimelineFragment();
            case 1:
                return new MentionsTimelineFragment();
            default:
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

    public void add(ViewPager viewPager, int position, Long tweetId){
        Object obj = this.getRegisteredFragment(viewPager.getCurrentItem());
        if (obj instanceof HomeTimelineFragment) {
            ((HomeTimelineFragment) obj).add(position, tweetId);
        }
        else if (obj instanceof MentionsTimelineFragment) {
            ((MentionsTimelineFragment) obj).add(position, tweetId);
        }
        else {
            // do nothing but log debug message
            Log.d("DEBUG", "TweetsPagerAdapter choose instanceof error");
        }
    }

    public void scrollToPosition(ViewPager viewPager, int position) {
        Object obj = this.getRegisteredFragment(viewPager.getCurrentItem());
        if(obj instanceof HomeTimelineFragment) {
            ((HomeTimelineFragment) obj).scrollToPosition(position);
        }
        else if (obj instanceof MentionsTimelineFragment) {
            ((MentionsTimelineFragment) obj).scrollToPosition(position);
        }
        else {
            // do nothing but log debug message
            Log.d("DEBUG", "TweetsPagerAdapter choose instanceof error");
        }
    }

    public void onPullToRefresh(ViewPager viewPager) {
        Object obj = this.getRegisteredFragment(viewPager.getCurrentItem());
        if(obj instanceof HomeTimelineFragment) {
            ((HomeTimelineFragment) obj).onPullToRefresh();
        }
        else if (obj instanceof MentionsTimelineFragment) {
            ((MentionsTimelineFragment) obj).onPullToRefresh();
        }
        else {
            // do nothing but log debug message
            Log.d("DEBUG", "TweetsPagerAdapter choose instanceof error");
        }
    }
}