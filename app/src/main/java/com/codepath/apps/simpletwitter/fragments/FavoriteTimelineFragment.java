package com.codepath.apps.simpletwitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.codepath.apps.simpletwitter.RESTAPI.TwitterApplication;
import com.codepath.apps.simpletwitter.models.Tweet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.ArrayList;

public class FavoriteTimelineFragment extends TweetsListFragment {
    public Long current_user_id;

    public static FavoriteTimelineFragment newInstance(Long user_id) {
        FavoriteTimelineFragment favoriteTimelineFragment = new FavoriteTimelineFragment();
        Bundle args = new Bundle();
        args.putLong("user_id", user_id);
        favoriteTimelineFragment.setArguments(args);
        return favoriteTimelineFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get current user id
        current_user_id = getArguments().getLong("user_id");
        // Get Home timeline
        loadLatestTweets(current_user_id);
    }

    @Override
    public void onScrollingDown() {
        loadPreviousTweets(current_user_id);
        Toast.makeText(getActivity(), "Load More", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPullToRefresh() {
        clearAllTweets();
        loadLatestTweets(current_user_id);
    }

    private void clearAllTweets() {
        // clear all tweets in local data member
        tweetsIdArray.clear();
    }

    private void loadLatestTweets(Long user_id) {
        long max_id = 0;
        long since_id = 0;
        if (tweetsAdapter != null && tweetsAdapter.getItemCount() > 0) {
            since_id = tweetsAdapter.getTweets().get(0);
        }
        TwitterApplication.getRestClient().getFavoriteTimeline(user_id, max_id, since_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Gson gson = new Gson();
                ArrayList<Tweet> moreTweets = gson.fromJson(response.toString(),
                        new TypeToken<ArrayList<Tweet>>() {
                        }.getType());

                for (int i = 0; i < moreTweets.size(); i++) {
                    // updateToDB moreTweets to DB
                    moreTweets.get(i).update();
                    // store data
                    tweetsIdArray.add(moreTweets.get(i).id);
                }
                // notify the adapter
                tweetsAdapter.notifyDataSetChanged();
                // clear refresh mark if calling by swipe to refresh
                srTimeline.setRefreshing(false);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable
                    , JSONArray errorResponse) {
                throwable.printStackTrace();
                Log.e("REST_API_ERROR", errorResponse.toString());
            }
        });
    }

    private void loadPreviousTweets(Long user_id) {
        long max_id = 0;
        long since_id = 0;
        if (tweetsAdapter != null && tweetsAdapter.getItemCount() > 0) {
            max_id = tweetsAdapter.getTweets().get(tweetsAdapter.getItemCount() - 1);
        }
        TwitterApplication.getRestClient().getFavoriteTimeline(user_id, max_id, since_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Gson gson = new Gson();
                ArrayList<Tweet> moreTweets = gson.fromJson(response.toString(),
                        new TypeToken<ArrayList<Tweet>>() {
                        }.getType());

                for (int i = 0; i < moreTweets.size(); i++) {
                    // updateToDB moreTweets to DB
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
