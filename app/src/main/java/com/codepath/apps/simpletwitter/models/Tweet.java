package com.codepath.apps.simpletwitter.models;


public class Tweet {
    private String created_at;
    private int favorite_count;
    private boolean favorited;
    private long id;
    private String text;
    private int retweet_count;
    private boolean retweeted;
     private User user;

    // Quote tweet - may be null
    private long quoted_status_id;
    private Tweet quoted_status;

    // Retweet tweet - may be null
    private Tweet retweeted_status;

    // Entities - enhance feature
}


