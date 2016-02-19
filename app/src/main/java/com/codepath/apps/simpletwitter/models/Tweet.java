package com.codepath.apps.simpletwitter.models;


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
}


