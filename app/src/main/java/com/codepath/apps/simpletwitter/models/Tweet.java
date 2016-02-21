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

    // Quote tweet - may be null
    public long quoted_status_id;
    public Tweet quoted_status;

    // Retweet tweet - may be null
    public Tweet retweeted_status;

    // Entities - enhance feature


    // Update Database
    public void update() {
        // update hashTweets
        hashTweets.put(this.id, this);
        // update DB
        TweetDB updateTweetDB = convertToDB();
        updateTweetDB.save();
    }

    public TweetDB convertToDB() {
        TweetDB tweetDB = new TweetDB();
        tweetDB.id = this.id;

        Gson gson = new Gson();
        tweetDB.JSONObject = gson.toJson(this);
        return tweetDB;
    }

    // update status
    public void retweet() {
        this.retweet_count++;
        this.retweeted = true;
        this.update();
    }

    public void unretweet() {
        this.retweet_count--;
        this.retweeted = false;
        this.update();
    }

    public void favorite() {
        this.favorite_count++;
        this.favorited = true;
        this.update();
    }

    public void unfavorite() {
        this.favorite_count--;
        this.favorited = false;
        this.update();
    }

}


