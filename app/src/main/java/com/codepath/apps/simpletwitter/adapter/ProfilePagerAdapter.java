package com.codepath.apps.simpletwitter.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.codepath.apps.simpletwitter.fragments.FavoriteTimelineFragment;
import com.codepath.apps.simpletwitter.fragments.UserTimelineFragment;
import com.codepath.apps.simpletwitter.models.User;

public class ProfilePagerAdapter extends SmartFragmentStatePagerAdapter {
    private String tabTitles[] = { "Tweets", "Likes" };
    private User user;

    public ProfilePagerAdapter(FragmentManager fm, User user) {
        super(fm);
        this.user = user;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return UserTimelineFragment.newInstance(user.id);
            case 1:
                // return Likes Timeline
                return FavoriteTimelineFragment.newInstance(user.id);
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
        if (obj instanceof UserTimelineFragment) {
            ((UserTimelineFragment) obj).add(position, tweetId);
        }
        else if (obj instanceof FavoriteTimelineFragment) {
            ((FavoriteTimelineFragment) obj).add(position, tweetId);
        }
        else {
            // do nothing but log debug message
            Log.d("DEBUG", "TweetsPagerAdapter choose instanceof error");
        }
    }

    public void scrollToPosition(ViewPager viewPager, int position) {
        Object obj = this.getRegisteredFragment(viewPager.getCurrentItem());
        if(obj instanceof UserTimelineFragment) {
            ((UserTimelineFragment) obj).scrollToPosition(position);
        }
        else if (obj instanceof FavoriteTimelineFragment) {
            ((FavoriteTimelineFragment) obj).scrollToPosition(position);
        }
        else {
            // do nothing but log debug message
            Log.d("DEBUG", "TweetsPagerAdapter choose instanceof error");
        }
    }
}