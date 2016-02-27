package com.codepath.apps.simpletwitter.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.apps.simpletwitter.R;
import com.codepath.apps.simpletwitter.models.User;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProfileFragment extends Fragment {
    private User user;
    private ProfileOnClickListener listener;

    @Bind(R.id.tvUsername) TextView tvUsername;
    @Bind(R.id.tvScreenName) TextView tvScreenName;
    @Bind(R.id.tvFollowingCount) TextView tvFollowingCount;
    @Bind(R.id.tvLabelFollowing) TextView tvLabelFollowing;
    @Bind(R.id.tvFollowersCount) TextView tvFollowersCount;
    @Bind(R.id.tvLabelFollowers) TextView tvLabelFollowers;

    public interface ProfileOnClickListener {
        void onClickFollowing(Long userId);
        void onClickFollower(Long userId);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ProfileOnClickListener) {
            listener = (ProfileOnClickListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement ProfileFragment.ProfileOnClickListener");
        }
    }

    public static ProfileFragment newInstance(User user) {
        ProfileFragment profileFragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        profileFragment.setArguments(bundle);
        return profileFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = getArguments().getParcelable("user");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        // set content of view fields
        tvUsername.setText(user.name);
        tvScreenName.setText("@" + user.screen_name);
        tvFollowingCount.setText(Integer.toString(user.friends_count));
        tvFollowersCount.setText(Integer.toString(user.followers_count));

        // set onClickListener to clickable item
        tvFollowingCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickFollowing(user.id);
            }
        });
        tvLabelFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickFollowing(user.id);
            }
        });
        tvFollowersCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickFollower(user.id);
            }
        });
        tvLabelFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickFollower(user.id);
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
