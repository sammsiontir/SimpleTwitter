package com.codepath.apps.simpletwitter.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

@Table(name = "MyTweetDB")
public class TweetDB extends Model {
    @Column(name = "Created_at")
    public String created_at;
    @Column(name = "TweetId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long id;
    @Column(name = "Json")
    public String JSONObject;


    public TweetDB() {
        super();
    }

    public TweetDB(String created_at, long id, String JSONObject) {
        super();
        this.created_at = created_at;
        this.id = id;
        this.JSONObject = JSONObject;
    }

    public static List<Tweet> getRecentTweets(Integer limit) {
        List<TweetDB> tweetDBs = new Select().from(TweetDB.class)
                .orderBy("TweetId DESC")
                .limit(limit.toString()).execute();
        ArrayList<Tweet> tweets = new ArrayList<>();
        for(int i = 0; i < tweetDBs.size(); i++) {
            Gson gson = new Gson();
            Tweet tweet = gson.fromJson(tweetDBs.get(i).JSONObject, Tweet.class);
            tweets.add(tweet);
        }
        return tweets;
    }


}
