package com.codepath.apps.simpletwitter.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codepath.apps.simpletwitter.fragments.FavoriteTimelineFragment;
import com.codepath.apps.simpletwitter.fragments.UserTimelineFragment;
import com.codepath.apps.simpletwitter.models.User;

public class ProfilePagerAdapter extends FragmentPagerAdapter {
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
}