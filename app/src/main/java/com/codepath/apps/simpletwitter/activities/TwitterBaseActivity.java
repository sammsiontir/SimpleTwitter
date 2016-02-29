package com.codepath.apps.simpletwitter.activities;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.simpletwitter.MyUtils;
import com.codepath.apps.simpletwitter.R;
import com.codepath.apps.simpletwitter.models.Tweet;
import com.codepath.apps.simpletwitter.models.User;

public abstract class TwitterBaseActivity extends AppCompatActivity {
    public abstract void addTweet(Tweet tweet);

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.action_tweet:
                MyUtils.openComposeDialog(this);
                return true;

            case R.id.action_profile:
                MyUtils.openProfileActivity(this, User.account);
                return true;

            case R.id.action_message:
                MyUtils.openRecipientsActivity(this);
                return true;

            case R.id.action_search:
                MyUtils.openSearchActivity(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
