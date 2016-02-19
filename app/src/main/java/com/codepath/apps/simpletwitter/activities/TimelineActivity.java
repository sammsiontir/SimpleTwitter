package com.codepath.apps.simpletwitter.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.simpletwitter.R;
import com.codepath.apps.simpletwitter.RESTAPI.TwitterApplication;
import com.codepath.apps.simpletwitter.RESTAPI.TwitterClient;
import com.codepath.apps.simpletwitter.adapter.EndlessRecyclerViewScrollListener;
import com.codepath.apps.simpletwitter.adapter.TweetsAdapter;
import com.codepath.apps.simpletwitter.models.Tweet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TimelineActivity extends AppCompatActivity {

    private TwitterClient client;
    private TweetsAdapter tweetsAdapter;
    private ArrayList<Tweet> homeTimelineTweets;

    @Bind(R.id.rvHomeTimeline) RecyclerView rvHomeTimeline;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.fab) FloatingActionButton fab;

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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Initial data and adapter
        homeTimelineTweets = new ArrayList<>();
        tweetsAdapter = new TweetsAdapter(homeTimelineTweets);
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

        client = TwitterApplication.getRestClient();
        loadLatestTweets();
    }

    private void loadLatestTweets() {
        long max_id = 0;
        long since_id = 0;
        if (tweetsAdapter != null && tweetsAdapter.getItemCount() > 0) {
            since_id = tweetsAdapter.getTweets().get(0).id;
        }
        client.getHomeTimeline(max_id, since_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Gson gson = new Gson();
                ArrayList<Tweet> moreTweets = gson.fromJson(response.toString(),
                        new TypeToken<ArrayList<Tweet>>() {}.getType());
                // store data and notify the adapter
                homeTimelineTweets.addAll(moreTweets);
                tweetsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable
                    , JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    private void loadPreviousTweets() {
        long max_id = 0;
        long since_id = 0;
        if (tweetsAdapter != null && tweetsAdapter.getItemCount() > 0) {
            max_id = tweetsAdapter.getTweets().get(tweetsAdapter.getItemCount() - 1).id;
        }
        client.getHomeTimeline(max_id, since_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Gson gson = new Gson();
                ArrayList<Tweet> moreTweets = gson.fromJson(response.toString(),
                        new TypeToken<ArrayList<Tweet>>() {}.getType());
                // store data and notify the adapter
                homeTimelineTweets.addAll(moreTweets);
                int curSize = tweetsAdapter.getItemCount();
                tweetsAdapter.notifyItemRangeInserted(curSize, homeTimelineTweets.size() - 1);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable
                    , JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
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

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
