package com.codepath.apps.simpletwitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codepath.apps.simpletwitter.RESTAPI.TwitterApplication;
import com.codepath.apps.simpletwitter.models.Tweet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.ArrayList;

public class MentionsTimelineFragment extends TweetsListFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get Mentions timeline
        loadLatestTweets();
    }

    @Override
    public void onScrollingDown() {
        loadPreviousTweets();
    }

    @Override
    public void onPullToRefresh() {
        clearAllTweets();
        loadLatestTweets();
    }

    private void clearAllTweets() {
        // clear all tweets in local data member
        tweetsIdArray.clear();
    }

    private void loadLatestTweets() {
        long max_id = 0;
        long since_id = 0;
        if (tweetsAdapter != null && tweetsAdapter.getItemCount() > 0) {
            since_id = tweetsAdapter.getTweets().get(0);
        }
        TwitterApplication.getRestClient().getMentionsTimeline(max_id, since_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Gson gson = new Gson();
                ArrayList<Tweet> moreTweets = gson.fromJson(response.toString(),
                        new TypeToken<ArrayList<Tweet>>() {
                        }.getType());

                for (int i = 0; i < moreTweets.size(); i++) {
                    // update moreTweets to hashTweets only
                    moreTweets.get(i).update();
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
            max_id = tweetsAdapter.getTweets().get(tweetsAdapter.getItemCount() - 1);
        }
        TwitterApplication.getRestClient().getMentionsTimeline(max_id, since_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Gson gson = new Gson();
                ArrayList<Tweet> moreTweets = gson.fromJson(response.toString(),
                        new TypeToken<ArrayList<Tweet>>() {
                        }.getType());

                for (int i = 0; i < moreTweets.size(); i++) {
                    // update moreTweets to hashTweets only
                    moreTweets.get(i).update();
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
