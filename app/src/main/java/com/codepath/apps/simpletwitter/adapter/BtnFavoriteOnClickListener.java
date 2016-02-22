package com.codepath.apps.simpletwitter.adapter;

import android.util.Log;
import android.view.View;

import com.codepath.apps.simpletwitter.RESTAPI.TwitterApplication;
import com.codepath.apps.simpletwitter.models.Tweet;
import com.codepath.apps.simpletwitter.models.ViewHolderTweet;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

public class BtnFavoriteOnClickListener implements View.OnClickListener {
    private Long tweetId;
    private ViewHolderTweet holder;

    public BtnFavoriteOnClickListener(Long tweetId, ViewHolderTweet holder) {
        this.tweetId = tweetId;
        this.holder = holder;
    }

    @Override
    public void onClick(View v) {
        if (Tweet.hashTweets.get(this.tweetId).favorited) {
            // update server
            postUnFavorite(this.tweetId);
            // update local database
            Tweet.hashTweets.get(this.tweetId).unfavorite();
            // update views
            holder.ibFavorite.setSelected(false);
            holder.tvFavoriteCount.setSelected(false);
            holder.tvFavoriteCount.setText(Integer.toString(Tweet.hashTweets.get(this.tweetId).favorite_count));

        } else {
            // update server
            postFavorite(this.tweetId);
            // update local database
            Tweet.hashTweets.get(this.tweetId).favorite();
            // update views
            holder.ibFavorite.setSelected(true);
            holder.tvFavoriteCount.setSelected(true);
            holder.tvFavoriteCount.setText(Integer.toString(Tweet.hashTweets.get(this.tweetId).favorite_count));
        }
    }

    private void postFavorite(long id) {
        Log.d("DEBUG", "Send Favorite");
        TwitterApplication.getRestClient().postFavorite(id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson gson = new Gson();
                Tweet tweet = gson.fromJson(response.toString(), Tweet.class);
                tweet.update();
                Log.d("DEBUG", response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable
                    , JSONObject errorResponse) {
                throwable.printStackTrace();
                Log.e("REST_API_ERROR", errorResponse.toString());
            }
        });
    }

    private void postUnFavorite(long id) {
        Log.d("DEBUG", "Send Un-favorite");
        TwitterApplication.getRestClient().postUnFavorite(id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson gson = new Gson();
                Tweet tweet = gson.fromJson(response.toString(), Tweet.class);
                tweet.update();
                Log.d("DEBUG", response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable
                    , JSONObject errorResponse) {
                throwable.printStackTrace();
                Log.e("REST_API_ERROR", errorResponse.toString());
            }
        });
    }
}
