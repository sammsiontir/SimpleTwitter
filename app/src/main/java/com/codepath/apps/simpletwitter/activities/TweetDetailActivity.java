package com.codepath.apps.simpletwitter.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.simpletwitter.R;
import com.codepath.apps.simpletwitter.RESTAPI.TwitterApplication;
import com.codepath.apps.simpletwitter.adapter.EndlessRecyclerViewScrollListener;
import com.codepath.apps.simpletwitter.adapter.TweetDetailAdapter;
import com.codepath.apps.simpletwitter.models.Tweet;
import com.codepath.apps.simpletwitter.models.User;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TweetDetailActivity extends AppCompatActivity
        implements ReplyFragment.TweetReplyListener {
    public static User myself;
    private TweetDetailAdapter tweetDetailAdapter;
    private Tweet topTweet;
    private ArrayList<Long> tweetComments;

    @Bind(R.id.rvTweetDetail) RecyclerView rvTweetDetail;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.fab) FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        // Bind all views
        ButterKnife.bind(this);
        // Bind view with toolbar and floating button
        setSupportActionBar(toolbar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get passing data
        myself = getIntent().getParcelableExtra("myself");
        Long topTweetId = getIntent().getLongExtra("topTweetId", 0);
        topTweet = Tweet.hashTweets.get(topTweetId);

        // Initial data and adapter
        tweetComments = new ArrayList<>();
        tweetDetailAdapter = new TweetDetailAdapter(tweetComments, topTweet.id) {
            @Override
            public void onClickReply(Long tweetId) {
                replyTweet(myself, Tweet.hashTweets.get(tweetId).user, Tweet.hashTweets.get(tweetId).text, tweetId);
            }

            @Override
            public void onClickText(Long tweetId) {

            }
        };
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        // Bind Recycle view with tweets
        rvTweetDetail.setAdapter(tweetDetailAdapter);
        rvTweetDetail.setLayoutManager(linearLayoutManager);
        rvTweetDetail.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadPreviousComments();
                Toast.makeText(getApplicationContext(), "Load More", Toast.LENGTH_LONG).show();
            }
        });

        loadLatestTweets();
    }

    private void loadLatestTweets() {

    }

    private void loadPreviousComments() {

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
                tweetComments.add(0, tweet.id);
                tweetDetailAdapter.notifyItemInserted(1);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable
                    , JSONObject errorResponse) {
                throwable.printStackTrace();
                Log.e("REST_API_ERROR", errorResponse.toString());
            }
        });
    }

    // Open compose dialog
    private void replyTweet(User sender, User recipient, String status, Long id) {
        // create fragment manager
        FragmentManager fragmentManager = getSupportFragmentManager();
        // pass user information to dialog
        ReplyFragment replyTweet = ReplyFragment.newInstance(sender, recipient, status, id);
        // create compose tweet dialog
        replyTweet.show(fragmentManager, "reply_tweet");
    }


    @Override
    public void onClickReply(String inputText, Long id) {
        replyTweet(inputText, id);
    }

}
