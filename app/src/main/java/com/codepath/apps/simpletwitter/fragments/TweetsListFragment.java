package com.codepath.apps.simpletwitter.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.simpletwitter.R;
import com.codepath.apps.simpletwitter.adapter.EndlessRecyclerViewScrollListener;
import com.codepath.apps.simpletwitter.adapter.TweetsAdapter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class TweetsListFragment extends Fragment {
    protected TweetsAdapter tweetsAdapter;
    protected ArrayList<Long> homeTimelineTweets;

    @Bind(R.id.srHomeTimeline) SwipeRefreshLayout srHomeTimeline;
    @Bind(R.id.rvHomeTimeline) RecyclerView rvHomeTimeline;

    private TweetsListOnClickListener listener;

    // Define the events that the fragment will use to communicate
    public interface TweetsListOnClickListener {
        void onClickReply(Long tweetId);
        void onClickText(Long tweetId);
    }

    public abstract void onScrollingDown();
    public abstract void onPullToRefresh();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof TweetsListOnClickListener) {
            listener = (TweetsListOnClickListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement MyListFragment.OnItemSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweets_list, container, false);
        // Bind views
        ButterKnife.bind(this, view);

        // Bind with swipe to refresh
        srHomeTimeline.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onPullToRefresh();
            }
        });
        // Configure the refreshing colors
        srHomeTimeline.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // Setup adapter
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        // Bind Recycle view with tweets
        rvHomeTimeline.setAdapter(tweetsAdapter);
        rvHomeTimeline.setLayoutManager(linearLayoutManager);
        rvHomeTimeline.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                onScrollingDown();
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initial data and adapter
        homeTimelineTweets = new ArrayList<>();
        tweetsAdapter = new TweetsAdapter(homeTimelineTweets) {
            @Override
            public void onClickReply(Long tweetId) {
                listener.onClickReply(tweetId);
            }

            @Override
            public void onClickText(Long tweetId) {
                listener.onClickText(tweetId);
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    public void add(int position, Long tweetId) {
        homeTimelineTweets.add(position, tweetId);
        tweetsAdapter.notifyItemInserted(position);
    }

    public void scrollToPosition(int position) {
        rvHomeTimeline.scrollToPosition(position);
    }
}
