package com.codepath.apps.simpletwitter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codepath.apps.simpletwitter.MyUtils;
import com.codepath.apps.simpletwitter.R;
import com.codepath.apps.simpletwitter.fragments.FollowerListFragment;
import com.codepath.apps.simpletwitter.fragments.FollowingListFragment;
import com.codepath.apps.simpletwitter.fragments.UsersListFragment;
import com.codepath.apps.simpletwitter.models.User;

public class UsersListActivity extends AppCompatActivity implements UsersListFragment.UsersListOnClickListener{
    private int mode; // mode 0 is following list, 1 is follower list
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_twitter_logo_white);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get user information from previous activity
        mode = getIntent().getIntExtra("mode", 0);
        user = getIntent().getParcelableExtra("user");

        if(mode == 0) {
            // open following list
            getSupportActionBar().setTitle("Following");
            FollowingListFragment fragment = FollowingListFragment.newInstance(user.id);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flUserListHolder, fragment)
                    .commit();
        }
        else {
            getSupportActionBar().setTitle("Follower");
            FollowerListFragment fragment = FollowerListFragment.newInstance(user.id);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flUserListHolder, fragment)
                    .commit();
        }
    }

    @Override
    public void onFriendSubmit(Long userId) {
        MyUtils.postFollow(userId);
    }

    @Override
    public void onUnFriendSubmit(Long userId) {
        MyUtils.postUnFollow(userId);
    }

    @Override
    public void onUserClick(User user) {
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra("user", user);
        startActivity(i);
    }

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

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
