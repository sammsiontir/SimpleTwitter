package com.codepath.apps.simpletwitter.models;


import com.google.gson.Gson;

public class Tweet {
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
        TweetDB updateTweetDB = convertToDB();
        updateTweetDB.save();
    }

    public TweetDB convertToDB() {
        TweetDB tweetDB = new TweetDB();
        tweetDB.created_at = this.created_at;
        tweetDB.id = this.id;

        Gson gson = new Gson();
        tweetDB.JSONObject = gson.toJson(this);
        return tweetDB;
    }
}


