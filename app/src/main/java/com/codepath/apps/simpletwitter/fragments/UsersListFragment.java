package com.codepath.apps.simpletwitter.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.simpletwitter.R;
import com.codepath.apps.simpletwitter.adapter.EndlessRecyclerViewScrollListener;
import com.codepath.apps.simpletwitter.adapter.UsersAdapter;
import com.codepath.apps.simpletwitter.models.User;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class UsersListFragment extends Fragment {
    protected Long current_user_id;
    protected ArrayList<User> users;
    protected UsersAdapter usersAdapter;
    private UsersListOnClickListener listener;
    @Bind(R.id.rvList) protected RecyclerView rvUsersList;

    public interface UsersListOnClickListener {
        void onFriendSubmit(Long userId);
        void onUnFriendSubmit(Long userId);
        void onUserClick(User user);
    }

    public abstract void onScrollingDown();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof UsersListOnClickListener) {
            listener = (UsersListOnClickListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement UsersListFragment.UsersListOnClickListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create data members
        users = new ArrayList<>();
        usersAdapter = new UsersAdapter(users) {
            @Override
            public void onUserClick(User user) {
                listener.onUserClick(user);
            }

            @Override
            public void onFriendSubmit(Long userId) {
                listener.onFriendSubmit(userId);
            }

            @Override
            public void onUnFriendSubmit(Long userId) {
                listener.onUnFriendSubmit(userId);
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, view);
        // Create RecyclerView Layout manager and connect with adapter
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvUsersList.setAdapter(usersAdapter);
        rvUsersList.setLayoutManager(linearLayoutManager);
        rvUsersList.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                onScrollingDown();
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
