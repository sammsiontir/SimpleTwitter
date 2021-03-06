package com.codepath.apps.simpletwitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.codepath.apps.simpletwitter.RESTAPI.TwitterApplication;
import com.codepath.apps.simpletwitter.models.Tweet;
import com.codepath.apps.simpletwitter.models.TweetDB;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class HomeTimelineFragment extends TweetsListFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get Home timeline
        List<Long> queryResults = TweetDB.getRecentTweets(100);
        if(queryResults != null && !queryResults.isEmpty()) {
            tweetsIdArray.addAll(queryResults);
            Toast.makeText(getActivity(), "Load from DB", Toast.LENGTH_LONG).show();
        }
        else {
            loadLatestTweets();
        }
    }

    @Override
    public void onScrollingDown() {
        loadPreviousTweets();
        Toast.makeText(getActivity(), "Load More", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPullToRefresh() {
        clearAllTweets();
        loadLatestTweets();
    }

    private void clearAllTweets() {
        // clear hashTweets
        Tweet.hashTweets.clear();
        // clear all tweets in DB
        new Delete().from(TweetDB.class).execute();
        // clear all tweets in local data member
        tweetsIdArray.clear();
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
                    // updateToDB moreTweets to DB
                    moreTweets.get(i).updateToDB();
                    // store data
                    tweetsIdArray.add(moreTweets.get(i).id);
                }
                // notify the adapter
                tweetsAdapter.notifyDataSetChanged();
                // clear refresh mark if calling by swipe to refresh
                listener.endPullToRefresh();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable
                    , JSONArray errorResponse) {
                throwable.printStackTrace();
                Log.e("REST_API_ERROR", errorResponse.toString());
            }
        });
    }

    private void loadPreviousTweets() {
        long max_id = 0;
        long since_id = 0;
        if (tweetsAdapter != null && tweetsAdapter.getItemCount() > 0) {
            max_id = tweetsAdapter.getTweets().get(tweetsAdapter.getItemCount() - 2);
        }
        TwitterApplication.getRestClient().getHomeTimeline(max_id, since_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Gson gson = new Gson();
                ArrayList<Tweet> moreTweets = gson.fromJson(response.toString(),
                        new TypeToken<ArrayList<Tweet>>() {
                        }.getType());

                for (int i = 0; i < moreTweets.size(); i++) {
                    // updateToDB moreTweets to DB
                    moreTweets.get(i).updateToDB();
                    // store data and notify the adapter
                    tweetsIdArray.add(moreTweets.get(i).id);
                }
                // notify the adapter
                int curSize = tweetsAdapter.getItemCount();
                tweetsAdapter.notifyItemRangeInserted(curSize, tweetsIdArray.size() - 1);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable
                    , JSONArray errorResponse) {
                throwable.printStackTrace();
                Log.e("REST_API_ERROR", errorResponse.toString());
            }
        });
    }
}
