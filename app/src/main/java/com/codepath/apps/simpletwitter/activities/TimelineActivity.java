package com.codepath.apps.simpletwitter.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.codepath.apps.simpletwitter.R;
import com.codepath.apps.simpletwitter.RESTAPI.TwitterApplication;
import com.codepath.apps.simpletwitter.adapter.EndlessRecyclerViewScrollListener;
import com.codepath.apps.simpletwitter.adapter.TweetsAdapter;
import com.codepath.apps.simpletwitter.models.Tweet;
import com.codepath.apps.simpletwitter.models.TweetDB;
import com.codepath.apps.simpletwitter.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TimelineActivity extends AppCompatActivity
        implements TweetFragment.TweetComposeListener, ReplyFragment.TweetReplyListener  {
    public static User myself;
    private TweetsAdapter tweetsAdapter;
    private ArrayList<Long> homeTimelineTweets;

    @Bind(R.id.rvHomeTimeline) RecyclerView rvHomeTimeline;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.fab) FloatingActionButton fab;
    @Bind(R.id.srHomeTimeline) SwipeRefreshLayout srHomeTimeline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        // Bind all views
        ButterKnife.bind(this);
        // Bind view with toolbar and floating button
        setSupportActionBar(toolbar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Get data from DB", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        // Bind with swipe to refresh
        srHomeTimeline.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                clearAllTweets();
                loadLatestTweets();
            }
        });
        // Configure the refreshing colors
        srHomeTimeline.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // Initial data and adapter
        homeTimelineTweets = new ArrayList<>();
        tweetsAdapter = new TweetsAdapter(homeTimelineTweets) {
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
        rvHomeTimeline.setAdapter(tweetsAdapter);
        rvHomeTimeline.setLayoutManager(linearLayoutManager);
        rvHomeTimeline.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadPreviousTweets();
                Toast.makeText(getApplicationContext(), "Load More", Toast.LENGTH_LONG).show();
            }
        });

        // Get Current User account
        getMyAccount();
        // Get Home timeline
        List<Long> queryResults = TweetDB.getRecentTweets(100);
        if(queryResults != null && !queryResults.isEmpty()) {
            homeTimelineTweets.addAll(queryResults);
            tweetsAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Load from DB", Toast.LENGTH_LONG).show();
        }
        else {
            loadLatestTweets();
        }
    }

    private void clearAllTweets() {
        // clear hashTweets
        Tweet.hashTweets.clear();
        // clear all tweets in DB
        new Delete().from(TweetDB.class).execute();
        // clear all tweets in local data member
        homeTimelineTweets.clear();
    }

    private void getMyAccount() {
        TwitterApplication.getRestClient().getMyAccount(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson gson = new Gson();
                myself = gson.fromJson(response.toString(), User.class);
                //myself.update();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable
                    , JSONObject errorResponse) {
                throwable.printStackTrace();
                Log.e("REST_API_ERROR", errorResponse.toString());
            }
        });
    }

    private void loadLatestTweets() {
        long max_id = 0;
        long since_id = 0;
        if (tweetsAdapter != null && tweetsAdapter.getItemCount() > 0) {
            since_id = tweetsAdapter.getTweets().get(0);
        }
        TwitterApplication.getRestClient().getHomeTimeline(max_id, since_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Gson gson = new Gson();
                ArrayList<Tweet> moreTweets = gson.fromJson(response.toString(),
                        new TypeToken<ArrayList<Tweet>>() {
                        }.getType());

                for (int i = 0; i < moreTweets.size(); i++) {
                    // update moreTweets to DB
                    moreTweets.get(i).update();
                    // store data
                    homeTimelineTweets.add(moreTweets.get(i).id);
                }
                // notify the adapter
                tweetsAdapter.notifyDataSetChanged();
                // clear refresh mark if calling by swipe to refresh
                srHomeTimeline.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable
                    , JSONObject errorResponse) {
                throwable.printStackTrace();
                Log.e("REST_API_ERROR", errorResponse.toString());
            }
        });
    }

    private void loadPreviousTweets() {
        long max_id = 0;
        long since_id = 0;
        if (tweetsAdapter != null && tweetsAdapter.getItemCount() > 0) {
            max_id = tweetsAdapter.getTweets().get(tweetsAdapter.getItemCount() - 1);
        }
        TwitterApplication.getRestClient().getHomeTimeline(max_id, since_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Gson gson = new Gson();
                ArrayList<Tweet> moreTweets = gson.fromJson(response.toString(),
                        new TypeToken<ArrayList<Tweet>>() {
                        }.getType());

                for (int i = 0; i < moreTweets.size(); i++) {
                    // update moreTweets to DB
                    moreTweets.get(i).update();
                    // store data and notify the adapter
                    homeTimelineTweets.add(moreTweets.get(i).id);
                }
                // notify the adapter
                int curSize = tweetsAdapter.getItemCount();
                tweetsAdapter.notifyItemRangeInserted(curSize, homeTimelineTweets.size() - 1);
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
                homeTimelineTweets.add(0, tweet.id);
                tweetsAdapter.notifyItemInserted(0);
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
                homeTimelineTweets.add(0, tweet.id);
                tweetsAdapter.notifyItemInserted(0);
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
        TweetFragment composeTweet = TweetFragment.newInstance(myself);
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
