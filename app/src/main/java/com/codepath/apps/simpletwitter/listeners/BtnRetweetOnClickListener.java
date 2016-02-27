package com.codepath.apps.simpletwitter.listeners;

import android.util.Log;
import android.view.View;

import com.codepath.apps.simpletwitter.RESTAPI.TwitterApplication;
import com.codepath.apps.simpletwitter.models.Tweet;
import com.codepath.apps.simpletwitter.models.ViewHolderTweet;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

public class BtnRetweetOnClickListener implements View.OnClickListener {
    private Long tweetId;
    private ViewHolderTweet holder;

    public BtnRetweetOnClickListener(Long tweetId, ViewHolderTweet holder) {
        this.tweetId = tweetId;
        this.holder = holder;
    }

    @Override
    public void onClick(View v) {
        if (Tweet.hashTweets.get(this.tweetId).retweeted) {
            // updateToDB server
            postUnRetweet(this.tweetId);
            // updateToDB local database
            Tweet.hashTweets.get(this.tweetId).unretweet();
            // updateToDB views
            holder.ibRetweet.setSelected(false);
            holder.tvRetweetCount.setSelected(false);
            holder.tvRetweetCount.setText(Integer.toString(Tweet.hashTweets.get(this.tweetId).retweet_count));

        } else {
            // updateToDB server
            postRetweet(this.tweetId);
            // updateToDB local database
            Tweet.hashTweets.get(this.tweetId).retweet();
            // updateToDB views
            holder.ibRetweet.setSelected(true);
            holder.tvRetweetCount.setSelected(true);
            holder.tvRetweetCount.setText(Integer.toString(Tweet.hashTweets.get(this.tweetId).retweet_count));
        }
    }

    private void postRetweet(long id) {
        Log.d("DEBUG", "Send Retweet");
        TwitterApplication.getRestClient().postRetweet(id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson gson = new Gson();
                Tweet tweet = gson.fromJson(response.toString(), Tweet.class);
                //tweet.updateToDB();
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

    private void postUnRetweet(long id) {
        Log.d("DEBUG", "Send Un-retweet");
        TwitterApplication.getRestClient().postUnRetweet(id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson gson = new Gson();
                Tweet tweet = gson.fromJson(response.toString(), Tweet.class);
                tweet.updateToDB();
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
