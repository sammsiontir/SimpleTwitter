package com.codepath.apps.simpletwitter.activities;

import android.app.Activity;
import android.os.Bundle;

import com.codepath.apps.simpletwitter.R;

public class MessageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
