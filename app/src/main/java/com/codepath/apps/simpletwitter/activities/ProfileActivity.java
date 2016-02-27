package com.codepath.apps.simpletwitter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.astuetz.PagerSlidingTabStrip;
import com.bumptech.glide.Glide;
import com.codepath.apps.simpletwitter.MyUtils;
import com.codepath.apps.simpletwitter.R;
import com.codepath.apps.simpletwitter.RESTAPI.TwitterApplication;
import com.codepath.apps.simpletwitter.adapter.ProfilePagerAdapter;
import com.codepath.apps.simpletwitter.fragments.ProfileFragment;
import com.codepath.apps.simpletwitter.fragments.TweetsListFragment;
import com.codepath.apps.simpletwitter.models.Tweet;
import com.codepath.apps.simpletwitter.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProfileActivity extends TwitterBaseActivity
        implements TweetsListFragment.TweetsListOnClickListener, ProfileFragment.ProfileOnClickListener {
    private User user;
    private String bannerUrl;
    private ProfilePagerAdapter profilePagerAdapter;

    @Bind(R.id.tabsProfile) PagerSlidingTabStrip tabsProfile;
    @Bind(R.id.vpProfilePager) ViewPager vpProfilePager;
    @Bind(R.id.ivProfileBanner) ImageView ivProfileBanner;
    @Bind(R.id.ivProfilePicture) ImageView ivProfilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplication(), MessageActivity.class);
                startActivity(i);
                Snackbar.make(view, "Message", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        // Get user information from previous activity
        user = getIntent().getParcelableExtra("user");

        // Bind all views
        ButterKnife.bind(this);

        // Bind vpHMPager & tabsHM
        profilePagerAdapter = new ProfilePagerAdapter(getSupportFragmentManager(), user);
        vpProfilePager.setAdapter(profilePagerAdapter);
        tabsProfile.setViewPager(vpProfilePager);

        // open profile fragment on the top
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ProfileFragment profileFragment = ProfileFragment.newInstance(user);
        ft.replace(R.id.flProfileHolder, profileFragment);
        ft.commit();

        // Bind profile background image
        getProfileBannerUrl(user.id, this);
//        bannerUrlTemp = "https://pbs.twimg.com/profile_banners/215318899/1456062009/mobile";

        Glide.with(this)
                .load(user.profile_image_url)
                .fitCenter()
                .into(ivProfilePicture);
        ivProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
    public void onClickUser(User selectedUser) {
        // Do nothing if we are already in this user's profile page
        if(selectedUser.id != user.id) {
            MyUtils.openProfileActivity(this, selectedUser);
        }
    }

    @Override
    public void endPullToRefresh() {

    }

    @Override
    public void onClickFollowing(Long userId) {
        // open following list
        Intent i = new Intent(this, UsersListActivity.class);
        i.putExtra("user", user);
        i.putExtra("mode", 0);
        startActivity(i);
    }

    @Override
    public void onClickFollower(Long userId) {
        // open follower list
        Intent i = new Intent(this, UsersListActivity.class);
        i.putExtra("user", user);
        i.putExtra("mode", 1);
        startActivity(i);
    }

    public void onClickProfilePicture() {
        // open profile picture
    }

    public void getProfileBannerUrl(Long userId, final AppCompatActivity activity) {
        TwitterApplication.getRestClient().getProfileBanner(userId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Log.d("DEBUG", response.toString());
                    bannerUrl = response.getJSONObject("sizes").getJSONObject("mobile").getString("url");
                    Log.d("DEBUG", bannerUrl);
                    Glide.with(activity)
                            .load(bannerUrl)
                            .centerCrop()
                            .into(ivProfileBanner);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable
                    , JSONObject errorResponse) {
                throwable.printStackTrace();
                Log.e("REST_API_ERROR", errorResponse.toString());
            }
        });
    }

    @Override
    public void addTweet(Tweet tweet) {
        // updateToDB DB
        tweet.updateToDB();
        // updateToDB timeline
        profilePagerAdapter.add(vpProfilePager, 0, tweet.id);
    }
}
