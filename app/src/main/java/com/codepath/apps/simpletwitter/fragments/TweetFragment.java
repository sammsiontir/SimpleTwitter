package com.codepath.apps.simpletwitter.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.simpletwitter.helpers.MyUtils;
import com.codepath.apps.simpletwitter.R;
import com.codepath.apps.simpletwitter.activities.TwitterBaseActivity;
import com.codepath.apps.simpletwitter.models.User;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TweetFragment extends DialogFragment {
    @Bind(R.id.ivProfilePicture) ImageView ivProfilePictureCompose;
    @Bind(R.id.tvUsernameCompose) TextView tvUsernameCompose;
    @Bind(R.id.tvScreenNameCompose) TextView tvScreenNameCompose;
    @Bind(R.id.ibCancelCompose) ImageButton ibCancelCompose;
    @Bind(R.id.etComposeText) EditText etComposeText;
    @Bind(R.id.tvCharLeft) TextView tvCharLeft;
    @Bind(R.id.btnTweet) Button btnTweet;

    public TweetFragment() {
    }

    public static TweetFragment newInstance(User myself) {
        TweetFragment compose = new TweetFragment();
        // collect passing data
        Bundle data = new Bundle();
        data.putParcelable("user", myself);
        // prepare return object
        compose.setArguments(data);
        return compose;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView = inflater.inflate(R.layout.fragment_compose, container, false);
        // Bind Views
        ButterKnife.bind(this, convertView);
        return convertView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Fetch passing data from activity
        User user = getArguments().getParcelable("user");

        // Bind User information
        // Todo: radius the corner
        Glide.with(view.getContext())
                .load(user.profile_image_url)
                .fitCenter()
                .into(ivProfilePictureCompose);
        tvUsernameCompose.setText(user.name);
        tvScreenNameCompose.setText(user.screen_name);

        // Set Text field
        setComposeTextField();

        // Bind Tweet button
        btnTweet.setEnabled(false);
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // remain characters must be in the range for submit tweet
                String tweetText = etComposeText.getText().toString();
                MyUtils.postTweet((TwitterBaseActivity) getActivity(), tweetText);
                dismiss();
            }
        });

        // Bind Cancel button
        ibCancelCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void setComposeTextField() {
        // call virtual keyboard
        etComposeText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        // calculate remain characters
        etComposeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                MyUtils.setEditTextLimitation(etComposeText, tvCharLeft, btnTweet);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        MyUtils.setEditTextLimitation(etComposeText, tvCharLeft, btnTweet);
    }
}
