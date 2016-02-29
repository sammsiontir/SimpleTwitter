package com.codepath.apps.simpletwitter.activities;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.simpletwitter.R;
import com.codepath.apps.simpletwitter.RESTAPI.TwitterApplication;
import com.codepath.apps.simpletwitter.adapter.TweetsAdapter;
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

public class SearchActivity extends AppCompatActivity {
    private ArrayList<Long> tweets;
    private TweetsAdapter tweetsAdapter;
    
    @Bind(R.id.rvList) RecyclerView rvList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.ic_twitter_logo_white);
        getSupportActionBar().setTitle("    Search tweets");

        // Bind view
        ButterKnife.bind(this);

        // set rvList
        tweets = new ArrayList<>();
        rvList.setLayoutManager(new LinearLayoutManager(this));
        tweetsAdapter = new TweetsAdapter(tweets, rvList) {
            @Override
            public void onClickReply(Long tweetId) {

            }

            @Override
            public void onClickText(Long tweetId) {

            }

            @Override
            public void onClickUser(User user) {

            }
        };
        rvList.setAdapter(tweetsAdapter);
        tweetsAdapter.setOnLoadMoreListener(new TweetsAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        // For Search bar
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String keywords) {
                // clear previous results
                clearResults();
                // perform query here
                search(keywords);
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // clear previous results
                clearResults();
                return false;
            }
        });

        return true;
    }

    private void search(String query) {
        long max_id = 0;
        long since_id = 0;
        TwitterApplication.getRestClient().getTweetWithScreenName(query, max_id, since_id, new JsonHttpResponseHandler() {
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
                    // updateToDB moreTweets to DB
                    moreTweets.get(i).update();
                    // store data and notify the adapter
                    tweets.add(moreTweets.get(i).id);
                }
                // notify the adapter
                tweetsAdapter.notifyDataSetChanged();
                Log.d("DEBUG", Integer.toString(tweets.size()));
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

    private void clearResults() {
        tweets.clear();
        tweetsAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }




}
