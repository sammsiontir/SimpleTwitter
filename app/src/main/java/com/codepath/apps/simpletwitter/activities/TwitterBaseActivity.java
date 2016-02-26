package com.codepath.apps.simpletwitter.activities;

import android.support.v7.app.AppCompatActivity;

import com.codepath.apps.simpletwitter.models.Tweet;

public abstract class TwitterBaseActivity extends AppCompatActivity {
    public abstract void addTweet(Tweet tweet);
}
