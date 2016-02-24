package com.codepath.apps.simpletwitter;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;

import com.codepath.apps.simpletwitter.RESTAPI.TwitterApplication;
import com.codepath.apps.simpletwitter.activities.ProfileActivity;
import com.codepath.apps.simpletwitter.activities.TweetDetailActivity;
import com.codepath.apps.simpletwitter.fragments.ReplyFragment;
import com.codepath.apps.simpletwitter.fragments.TweetFragment;
import com.codepath.apps.simpletwitter.models.User;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class MyUtils {
    public static void openProfileActivity(AppCompatActivity activity, User user) {
        Intent profileIntent = new Intent(activity, ProfileActivity.class);
        profileIntent.putExtra("user", user);
        activity.startActivity(profileIntent);
    }

    public static void openTweetDetailActivity(AppCompatActivity activity, Long tweetId) {
        Intent tweetDetailIntent = new Intent(activity, TweetDetailActivity.class);
        tweetDetailIntent.putExtra("topTweetId", tweetId);
        activity.startActivity(tweetDetailIntent);
    }

    public static void openReplyDialog(AppCompatActivity activity, Long tweetId) {
        // create fragment manager
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        // pass user information to dialog
        ReplyFragment replyTweet = ReplyFragment.newInstance(tweetId);
        // create compose tweet dialog
        replyTweet.show(fragmentManager, "reply_tweet");
    }

    public static void openComposeDialog(AppCompatActivity activity) {
        // create fragment manager
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        // pass user information to dialog
        TweetFragment composeTweet = TweetFragment.newInstance(User.account);
        // create compose tweet dialog
        composeTweet.show(fragmentManager, "compose_tweet");
    }

    // Get current user account
    public static void getMyAccount() {
        TwitterApplication.getRestClient().getMyAccount(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson gson = new Gson();
                User.account = gson.fromJson(response.toString(), User.class);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable
                    , JSONObject errorResponse) {
                throwable.printStackTrace();
                Log.e("REST_API_ERROR", errorResponse.toString());
            }
        });
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Special Case: Yesterday
        if (relativeDate.equals("Yesterday")) {
            String res = "1 d";
            //Log.d("DEBUG: relativeDate = ", relativeDate);
            //Log.d("DEBUG: relativeDate = ", res);
            return res;
        }

        // Case: X days ago
        //       X hours ago
        //       X minutes ago
        //       X seconds ago
        int pos;
        for(pos = 0; pos < relativeDate.length(); pos++) {
            if (( relativeDate.charAt(pos) == 'd' ||
                    relativeDate.charAt(pos) == 'h' ||
                    relativeDate.charAt(pos) == 'm' ||
                    relativeDate.charAt(pos) == 's')
                    && pos + 1 < relativeDate.length()) {
                //Log.d("DEBUG: relativeDate = ", relativeDate);
                //Log.d("DEBUG: relativeDate = ", relativeDate.substring(0, pos+1));
                return relativeDate.substring(0, pos+1);
            }
        }

        // Case: more than 7 days ago
        SimpleDateFormat myFormat = new SimpleDateFormat("MM/dd/yy");
        String reformattedStr = "";
        try {
            reformattedStr = myFormat.format(sf.parse(rawJsonDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Log.d("DEBUG: relativeDate = ", relativeDate);
        //Log.d("DEBUG: reformatted = ", reformattedStr);
        return reformattedStr;
    }
}
