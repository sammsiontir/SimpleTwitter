package com.codepath.apps.simpletwitter.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codepath.apps.simpletwitter.RESTAPI.TwitterApplication;
import com.codepath.apps.simpletwitter.models.User;
import com.codepath.apps.simpletwitter.models.UserList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FollowingListFragment extends UsersListFragment {
    private UserList userList;

    public static FollowingListFragment newInstance(Long user_id) {
        FollowingListFragment followingListFragment = new FollowingListFragment();
        Bundle args = new Bundle();
        args.putLong("user_id", user_id);
        followingListFragment.setArguments(args);
        return followingListFragment;
    }

    @Override
    public void onScrollingDown() {
        loadMoreUsers(current_user_id);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get current user id
        current_user_id = getArguments().getLong("user_id");
        // Get Home timeline
        userList = null;
        loadMoreUsers(current_user_id);
    }

    private void loadMoreUsers(Long current_user_id) {
        String next_cursor_str;
        if(userList == null) {
            next_cursor_str = "-1";
        }
        else {
            next_cursor_str = userList.next_cursor_str;
        }
        TwitterApplication.getRestClient().getFriendList(current_user_id, next_cursor_str, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson gson = new Gson();
                Log.d("DEBUG", response.toString());
                userList = gson.fromJson(response.toString(), UserList.class);
                Log.d("DEBUG", userList.toString());
                // Get users from List
                TwitterApplication.getRestClient().getUsersLookup(userList.toString(), new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        Gson gson = new Gson();
                        Log.d("DEBUG", response.toString());
                        ArrayList<User> moreUsers = gson.fromJson(response.toString(),
                                new TypeToken<ArrayList<User>>() {
                                }.getType());
                        users.addAll(moreUsers);
                        Log.d("DEBUG", Integer.toString(users.size()));
                        // notify the adapter
                        int curSize = usersAdapter.getItemCount();
                        usersAdapter.notifyItemRangeInserted(curSize, users.size() - 1);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable
                            , JSONArray errorResponse) {
                        throwable.printStackTrace();
                        Log.e("REST_API_ERROR", errorResponse.toString());
                    }
                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable
                    , JSONObject errorResponse) {
                throwable.printStackTrace();
                Log.e("REST_API_ERROR", errorResponse.toString());
            }
        });
    }

}
