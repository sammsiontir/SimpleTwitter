package com.codepath.apps.simpletwitter.activities;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.simpletwitter.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TweetFragment extends DialogFragment {
    private static final int TWEET_MAX_LENGTH = 140;

    @Bind(R.id.ivProfilePictureCompose) ImageView ivProfilePictureCompose;
    @Bind(R.id.ibCancelCompose) ImageButton ibCancelCompose;
    @Bind(R.id.etComposeText) EditText etComposeText;
    @Bind(R.id.tvCharLeft) TextView tvCharLeft;
    @Bind(R.id.btnTweet) Button btnTweet;

    public TweetFragment() {
    }

    public static TweetFragment newInstance(String profilePictureUrl) {
        TweetFragment compose = new TweetFragment();
        // collect passing data
        Bundle data = new Bundle();
        data.putString("thumbnail", profilePictureUrl);
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
        String profilePictureUrl = getArguments().getString("thumbnail");

        // Bind Profile picture
        // Todo: add profile picture

        // Set Text field
        setComposeTextField();

        // Bind Tweet button
        btnTweet.setEnabled(false);
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // remain characters must be in the range for submit tweet
                String tweetText = etComposeText.getText().toString();
                TweetComposeListener listener = (TweetComposeListener) getActivity();
                listener.onClickTweet(tweetText);
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

    public interface TweetComposeListener {
        void onClickTweet(String inputText);
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
                int remain = TWEET_MAX_LENGTH - count;
                if (remain <= 0 || remain == TWEET_MAX_LENGTH) {
                    Log.d("DEBUG_false", Integer.toString(remain));
                    btnTweet.setEnabled(false);
                } else {
                    Log.d("DEBUG_true", Integer.toString(remain));
                    btnTweet.setEnabled(true);
                }
                tvCharLeft.setText(Integer.toString(remain));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
