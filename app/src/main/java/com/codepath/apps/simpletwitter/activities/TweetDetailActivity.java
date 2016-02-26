package com.codepath.apps.simpletwitter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.simpletwitter.MyUtils;
import com.codepath.apps.simpletwitter.R;
import com.codepath.apps.simpletwitter.RESTAPI.TwitterApplication;
import com.codepath.apps.simpletwitter.adapter.EndlessRecyclerViewScrollListener;
import com.codepath.apps.simpletwitter.adapter.TweetDetailAdapter;
import com.codepath.apps.simpletwitter.fragments.ReplyFragment;
import com.codepath.apps.simpletwitter.models.Tweet;
import com.codepath.apps.simpletwitter.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TweetDetailActivity extends AppCompatActivity {
    public static User myself;
    private TweetDetailAdapter tweetDetailAdapter;
    private Tweet topTweet;
    private ArrayList<Long> tweetComments;

    @Bind(R.id.rvTweetDetail) RecyclerView rvTweetDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        // Bind all views
        ButterKnife.bind(this);
        // Bind view with toolbar and floating button
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvTweetDetail.scrollToPosition(0);
            }
        });

        // Get passing data
        myself = User.account;
        Long topTweetId = getIntent().getLongExtra("topTweetId", 0);
        topTweet = Tweet.hashTweets.get(topTweetId);
        getSupportActionBar().setTitle("@"+topTweet.user.screen_name);

        // Initial data and adapter
        tweetComments = new ArrayList<>();
        tweetDetailAdapter = new TweetDetailAdapter(tweetComments, topTweet.id) {
            @Override
            public void onClickReply(Long tweetId) {
                replyTweet(tweetId);
            }

            @Override
            public void onClickText(Long tweetId) {
                Intent tweetDetailIntent = new Intent(TweetDetailActivity.this, TweetDetailActivity.class);
                tweetDetailIntent.putExtra("myself", myself);
                tweetDetailIntent.putExtra("topTweetId", tweetId);
                startActivity(tweetDetailIntent);
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
            }
        });

        loadLatestTweets(topTweet.user.screen_name);
    }

    private void loadLatestTweets(String screenName) {
        long max_id = 0;
        long since_id = 0;
        if (tweetComments != null && tweetComments.size() > 0) {
            since_id = tweetComments.get(0);
        }
        TwitterApplication.getRestClient().getTweetWithScreenName(screenName, max_id, since_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson gson = new Gson();
                ArrayList<Tweet> moreTweets = null;
                try {
                    String res = response.getJSONArray("statuses").toString();
                    Log.d("DEBUG", res);
                    moreTweets = gson.fromJson(res, new TypeToken<ArrayList<Tweet>>() {}.getType());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < moreTweets.size(); i++) {
                    // check if in_reply_to_status_id == topTweet.id
                    if(moreTweets.get(i).in_reply_to_status_id != null
                            && moreTweets.get(i).in_reply_to_status_id == topTweet.id) {
                        tweetComments.add(moreTweets.get(i).id);
                        Tweet.hashTweets.put(moreTweets.get(i).id, moreTweets.get(i));
                    }
                }
                // notify the adapter
                tweetDetailAdapter.notifyDataSetChanged();
                // clear refresh mark if calling by swipe to refresh
                // srHomeTimeline.setRefreshing(false);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable
                    , JSONObject errorResponse) {
                throwable.printStackTrace();
                Log.e("REST_API_ERROR", errorResponse.toString());
            }
        });
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
                // updateToDB DB
                tweet.updateToDB();
                // updateToDB timeline
                tweetComments.add(tweet.id);
                // notify change on the last item
                tweetDetailAdapter.notifyItemInserted(tweetDetailAdapter.getItemCount());
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
    private void replyTweet(Long id) {
        MyUtils.openReplyDialog(this, id);
        // create fragment manager
        FragmentManager fragmentManager = getSupportFragmentManager();
        // pass user information to dialog
        ReplyFragment replyTweet = ReplyFragment.newInstance(id);
        // create compose tweet dialog
        replyTweet.show(fragmentManager, "reply_tweet");
    }

}
