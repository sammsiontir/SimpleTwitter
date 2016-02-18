package com.codepath.apps.simpletwitter;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.codepath.apps.simpletwitter.models.Tweet;

import java.util.List;

/**
 * Created by chengfu_lin on 2/17/16.
 */
public class TweetsArrayAdatper extends ArrayAdapter<Tweet> {
    public TweetsArrayAdatper(Context context, List<Tweet> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }
}
