package com.codepath.apps.simpletwitter.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.simpletwitter.MyUtils;
import com.codepath.apps.simpletwitter.R;
import com.codepath.apps.simpletwitter.RESTAPI.TwitterApplication;
import com.codepath.apps.simpletwitter.adapter.ProfilePagerAdapter;
import com.codepath.apps.simpletwitter.fragments.HomeTimelineFragment;
import com.codepath.apps.simpletwitter.fragments.ProfileFragment;
import com.codepath.apps.simpletwitter.fragments.ReplyFragment;
import com.codepath.apps.simpletwitter.fragments.TweetFragment;
import com.codepath.apps.simpletwitter.models.Tweet;
import com.codepath.apps.simpletwitter.models.User;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity
        implements TweetFragment.TweetComposeListener, ReplyFragment.TweetReplyListener
        , HomeTimelineFragment.TweetsListOnClickListener, ProfileFragment.ProfileOnClickListener {
    private User user;
    private ProfilePagerAdapter profilePagerAdapter;

    @Bind(R.id.tabsProfile) PagerSlidingTabStrip tabsProfile;
    @Bind(R.id.vpProfilePager) ViewPager vpProfilePager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
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
    }

    private void postTweet(String status) {
        TwitterApplication.getRestClient().postStatusUpdate(status, 0, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson gson = new Gson();
                Tweet tweet = gson.fromJson(response.toString(), Tweet.class);
                Log.d("DEBUG", response.toString());
                // updateToDB DB
                tweet.updateToDB();
                // updateToDB timeline
                // homeTimelineFragment.add(0, tweet.id);
                // updateToDB moreTweets to DB
                tweet.updateToDB();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable
                    , JSONObject errorResponse) {
                throwable.printStackTrace();
                Log.e("REST_API_ERROR", errorResponse.toString());
            }
        });
    }

    private void replyTweet(String status, long id) {
        Toast.makeText(this, "Reply", Toast.LENGTH_LONG).show();
        TwitterApplication.getRestClient().postStatusUpdate(status, id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson gson = new Gson();
                Tweet tweet = gson.fromJson(response.toString(), Tweet.class);
                Log.d("DEBUG", response.toString());
                // updateToDB DB
                tweet.updateToDB();
                // updateToDB timeline
                profilePagerAdapter.add(vpProfilePager, 0, tweet.id);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.action_tweet:
                MyUtils.openComposeDialog(this);
                return true;

            case R.id.action_profile:
                if(user.id != User.account.id) {
                    MyUtils.openProfileActivity(this, User.account);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSubmitTweet(String inputText) {
        postTweet(inputText);
    }

    @Override
    public void onSubmitReply(String inputText, Long id) {
        replyTweet(inputText, id);
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
    public void onClickFollowing(Long userId) {
        // open following list
    }

    @Override
    public void onClickFollower(Long userId) {
        // open follower list
    }

    @Override
    public void onClickProfilePicture(Long userId) {
        // open profile picture
    }
}
