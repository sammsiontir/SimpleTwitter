package com.codepath.apps.simpletwitter.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.simpletwitter.R;
import com.codepath.apps.simpletwitter.models.User;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProfileFragment extends Fragment {
    private User user;
    private ProfileOnClickListener listener;

    @Bind(R.id.ivProfilePicture) ImageView ivProfilePicture;
    @Bind(R.id.tvUsername) TextView tvUsername;
    @Bind(R.id.tvScreenName) TextView tvScreenName;
    @Bind(R.id.tvFollowingCount) TextView tvFollowingCount;
    @Bind(R.id.tvFollowersCount) TextView tvFollowersCount;

    public interface ProfileOnClickListener {
        void onClickFollowing(Long userId);
        void onClickFollower(Long userId);
        void onClickProfilePicture(Long userId);
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
        Glide.with(view.getContext())
                .load(user.profile_image_url)
                .fitCenter()
                .into(ivProfilePicture);
        tvUsername.setText(user.name);
        tvScreenName.setText(user.screen_name);
        tvFollowingCount.setText(Integer.toString(user.friends_count));
        tvFollowersCount.setText(Integer.toString(user.followers_count));

        // set onClickListener to clickable item
        ivProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickProfilePicture(user.id);
            }
        });
        tvFollowingCount.setOnClickListener(new View.OnClickListener() {
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

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
