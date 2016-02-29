package com.codepath.apps.simpletwitter.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.simpletwitter.MyUtils;
import com.codepath.apps.simpletwitter.R;
import com.codepath.apps.simpletwitter.adapter.TweetsPagerAdapter;
import com.codepath.apps.simpletwitter.fragments.TweetsListFragment;
import com.codepath.apps.simpletwitter.models.Tweet;
import com.codepath.apps.simpletwitter.models.User;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TimelineActivity extends TwitterBaseActivity
        implements TweetsListFragment.TweetsListOnClickListener  {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.fab) FloatingActionButton fab;
    @Bind(R.id.srTimeline) SwipeRefreshLayout srTimeline;
    @Bind(R.id.vpHMPager) ViewPager vpHMPager;
    @Bind(R.id.tabsHM) PagerSlidingTabStrip tabsHM;

    private TweetsPagerAdapter tweetsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        // Bind all views
        ButterKnife.bind(this);
        // Bind view with toolbar and floating button
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setIcon(R.drawable.ic_twitter_logo_white);

        // Get Current User account
        if(User.account == null) MyUtils.getMyAccount();

        // Bind vpHMPager & tabsHM
        tweetsPagerAdapter = new TweetsPagerAdapter(getSupportFragmentManager());
        vpHMPager.setAdapter(tweetsPagerAdapter);
        tabsHM.setViewPager(vpHMPager);

        // Set up swipe refresh behavior
        // Bind with swipe to refresh
        srTimeline.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // check internet connection
                if(MyUtils.isNetworkAvailable(getApplicationContext())) {
                    tweetsPagerAdapter.onPullToRefresh(vpHMPager);
                    //Snackbar.make(getCurrentFocus(), "Loading", Snackbar.LENGTH_LONG).show();
                }
                else {
                    srTimeline.setRefreshing(false);
                    Snackbar.make(getCurrentFocus(), "Connection lost", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        // Configure the refreshing colors
        srTimeline.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        // set floating button behavior
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tweetsPagerAdapter.scrollToPosition(vpHMPager, 0);
            }
        });
    }

    @Override
    public void onClickReply(Long tweetId) {
        MyUtils.openReplyDialog(this, tweetId);
    }
    @Override
    public void onClickText(Long tweetId) {
        MyUtils.openTweetDetailActivity(this, tweetId);
    }
    @Override
    public void onClickUser(User user) {
        MyUtils.openProfileActivity(this, user);
    }
    @Override
    public void endPullToRefresh() {
        srTimeline.setRefreshing(false);
    }

    @Override
    public void addTweet(Tweet tweet) {
        // updateToDB DB
        tweet.updateToDB();
        // updateToDB timeline
        tweetsPagerAdapter.add(vpHMPager, 0, tweet.id);
    }
}
