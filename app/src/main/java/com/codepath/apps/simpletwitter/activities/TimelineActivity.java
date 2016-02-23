package com.codepath.apps.simpletwitter.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.simpletwitter.R;
import com.codepath.apps.simpletwitter.RESTAPI.TwitterApplication;
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
        implements TweetFragment.TweetComposeListener, ReplyFragment.TweetReplyListener  {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.fab) FloatingActionButton fab;

    HomeTimelineFragment homeTimelineFragment;


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


        // Bind with Home Timeline Fragment
        if(savedInstanceState == null) {
            homeTimelineFragment = (HomeTimelineFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_home_timeline);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeTimelineFragment.scrollToPosition(0);
            }
        });

        // Get Current User account
        if(User.account == null) getMyAccount();
    }

    private void getMyAccount() {
        TwitterApplication.getRestClient().getMyAccount(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson gson = new Gson();
                User.account = gson.fromJson(response.toString(), User.class);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable
                    , JSONObject errorResponse) {
                throwable.printStackTrace();
                Log.e("REST_API_ERROR", errorResponse.toString());
            }
        });
    }

    private void postTweet(String status) {
        TwitterApplication.getRestClient().postStatusUpdate(status, 0, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson gson = new Gson();
                Tweet tweet = gson.fromJson(response.toString(), Tweet.class);
                Log.d("DEBUG", response.toString());
                // update DB
                tweet.update();
                // update timeline
                homeTimelineFragment.add(0, tweet.id);
                // update moreTweets to DB
                tweet.update();
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
                // update DB
                tweet.update();
                // update timeline
                homeTimelineFragment.add(0, tweet.id);
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

        if (id == R.id.action_tweet) {
            composeTweet();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Open compose dialog
    private void composeTweet() {
        // create fragment manager
        FragmentManager fragmentManager = getSupportFragmentManager();
        // pass user information to dialog
        TweetFragment composeTweet = TweetFragment.newInstance(User.account);
        // create compose tweet dialog
        composeTweet.show(fragmentManager, "compose_tweet");
    }

    private void replyTweet(User sender, User recipient, String status, Long id) {
        // create fragment manager
        FragmentManager fragmentManager = getSupportFragmentManager();
        // pass user information to dialog
        ReplyFragment replyTweet = ReplyFragment.newInstance(sender, recipient, status, id);
        // create compose tweet dialog
        replyTweet.show(fragmentManager, "reply_tweet");
    }

    @Override
    public void onClickTweet(String inputText) {
        postTweet(inputText);
    }

    @Override
    public void onClickReply(String inputText, Long id) {
        replyTweet(inputText, id);
    }
}
