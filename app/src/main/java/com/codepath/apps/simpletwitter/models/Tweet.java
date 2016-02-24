package com.codepath.apps.simpletwitter.models;


import com.google.gson.Gson;

import java.util.HashMap;

public class Tweet {
    public static final transient HashMap<Long, Tweet> hashTweets = new HashMap<>();

    public String created_at;
    public int favorite_count;
    public boolean favorited;
    public long id;
    public String text;
    public int retweet_count;
    public boolean retweeted;
    public User user;
    public Long in_reply_to_status_id;

    // Quote tweet - may be null
    public long quoted_status_id;
    public Tweet quoted_status;

    // Retweet tweet - may be null
    public Tweet retweeted_status;

    // Entities - enhance feature


    // Update Database
    public void updateToDB() {
        // updateToDB hashTweets
        this.update();
        // updateToDB DB
        TweetDB updateTweetDB = convertToDB();
        updateTweetDB.save();
    }

    public void update() {
        // updateToDB hashTweets
        hashTweets.put(this.id, this);
    }

    public TweetDB convertToDB() {
        TweetDB tweetDB = new TweetDB();
        tweetDB.id = this.id;

        Gson gson = new Gson();
        tweetDB.JSONObject = gson.toJson(this);
        return tweetDB;
    }

    // updateToDB status
    public void retweet() {
        this.retweet_count++;
        this.retweeted = true;
        this.updateToDB();
    }

    public void unretweet() {
        this.retweet_count--;
        this.retweeted = false;
        this.updateToDB();
    }

    public void favorite() {
        this.favorite_count++;
        this.favorited = true;
        this.updateToDB();
    }

    public void unfavorite() {
        this.favorite_count--;
        this.favorited = false;
        this.updateToDB();
    }

}


