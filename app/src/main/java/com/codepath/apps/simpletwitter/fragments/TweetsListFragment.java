package com.codepath.apps.simpletwitter.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.simpletwitter.R;
import com.codepath.apps.simpletwitter.adapter.TweetsAdapter;
import com.codepath.apps.simpletwitter.models.User;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class TweetsListFragment extends Fragment {
    protected TweetsAdapter tweetsAdapter;
    protected ArrayList<Long> tweetsIdArray;
    protected TweetsListOnClickListener listener;
    protected Handler handler;

    @Bind(R.id.rvList) RecyclerView rvTimeline;

    public interface TweetsListOnClickListener {
        void onClickReply(Long tweetId);
        void onClickText(Long tweetId);
        void onClickUser(User user);
        void endPullToRefresh();
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
                    + " must implement TweetsListFragment.TweetsListOnClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        // Bind views
        ButterKnife.bind(this, view);

        // Setup adapter
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        // Bind Recycle view with tweets
        rvTimeline.setLayoutManager(linearLayoutManager);
        tweetsAdapter = new TweetsAdapter(tweetsIdArray, rvTimeline) {
            @Override
            public void onClickReply(Long tweetId) {
                listener.onClickReply(tweetId);
            }
            @Override
            public void onClickText(Long tweetId) {
                listener.onClickText(tweetId);
            }
            @Override
            public void onClickUser(User user) {
                listener.onClickUser(user);
            }

        };

        rvTimeline.setAdapter(tweetsAdapter);
        tweetsAdapter.setOnLoadMoreListener(new TweetsAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //add progress item
                int progress_position = tweetsIdArray.size();
                tweetsIdArray.add(null);
                tweetsAdapter.notifyItemInserted(progress_position);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //remove progress item
                        tweetsIdArray.remove(tweetsIdArray.size() - 1);
                        tweetsAdapter.notifyItemRemoved(tweetsIdArray.size());
                        //add items one by one
                        onScrollingDown();
                        tweetsAdapter.setLoaded();
                        //or you can add all at once but do not forget to call mAdapter.notifyDataSetChanged();
                    }
                }, 2000);
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initial data and adapter
        tweetsIdArray = new ArrayList<>();
        handler = new Handler();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    public void add(int position, Long tweetId) {
        tweetsIdArray.add(position, tweetId);
        tweetsAdapter.notifyItemInserted(position);
    }

    public void scrollToPosition(int position) {
        rvTimeline.scrollToPosition(position);
    }
}
