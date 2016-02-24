package com.codepath.apps.simpletwitter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
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
import com.codepath.apps.simpletwitter.adapter.TweetsPagerAdapter;
import com.codepath.apps.simpletwitter.fragments.HomeTimelineFragment;
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

public class TimelineActivity extends AppCompatActivity
        implements TweetFragment.TweetComposeListener, ReplyFragment.TweetReplyListener, HomeTimelineFragment.TweetsListOnClickListener  {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.fab) FloatingActionButton fab;
    @Bind(R.id.vpHMPager) ViewPager vpHMPager;
    @Bind(R.id.tabsHM) PagerSlidingTabStrip tabsHM;

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


        // Bind vpHMPager & tabsHM
        vpHMPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager()));
        tabsHM.setViewPager(vpHMPager);

        // set floating button behavior
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // homeTimelineFragment.scrollToPosition(0);
            }
        });

        // Get Current User account
        if(User.account == null) MyUtils.getMyAccount();
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
                // homeTimelineFragment.add(0, tweet.id);
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
                // create fragment manager
                FragmentManager fragmentManager = getSupportFragmentManager();
                // pass user information to dialog
                TweetFragment composeTweet = TweetFragment.newInstance(User.account);
                // create compose tweet dialog
                composeTweet.show(fragmentManager, "compose_tweet");
                return true;

            case R.id.action_profile:
                Intent profileIntent = new Intent(this, ProfileActivity.class);
                profileIntent.putExtra("user", User.account);
                startActivity(profileIntent);
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
        // create fragment manager
        FragmentManager fragmentManager = getSupportFragmentManager();
        // pass user information to dialog
        ReplyFragment replyTweet = ReplyFragment.newInstance(tweetId);
        // create compose tweet dialog
        replyTweet.show(fragmentManager, "reply_tweet");
    }

    @Override
    public void onClickText(Long tweetId) {
        Intent tweetDetailIntent = new Intent(this, TweetDetailActivity.class);
        tweetDetailIntent.putExtra("myself", User.account);
        tweetDetailIntent.putExtra("topTweetId", tweetId);
        startActivity(tweetDetailIntent);
    }
}
