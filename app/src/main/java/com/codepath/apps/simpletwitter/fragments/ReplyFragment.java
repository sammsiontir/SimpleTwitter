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
import com.codepath.apps.simpletwitter.MyUtils;
import com.codepath.apps.simpletwitter.R;
import com.codepath.apps.simpletwitter.models.Tweet;
import com.codepath.apps.simpletwitter.models.User;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ReplyFragment extends DialogFragment {
    private static final String REPLYSTATUSPREFIX = "In Reply to ";

    @Bind(R.id.ivProfilePicture) ImageView ivProfilePictureCompose;
    @Bind(R.id.tvUsernameCompose) TextView tvUsernameCompose;
    @Bind(R.id.tvScreenNameCompose) TextView tvScreenNameCompose;
    @Bind(R.id.ibCancelCompose) ImageButton ibCancelCompose;
    @Bind(R.id.tvReplyStatus) TextView tvReplyStatus;
    @Bind(R.id.etComposeText) EditText etComposeText;
    @Bind(R.id.tvCharLeft) TextView tvCharLeft;
    @Bind(R.id.btnTweet) Button btnTweet;

    public ReplyFragment() {
    }

    public static ReplyFragment newInstance(Long tweetId) {
        ReplyFragment reply = new ReplyFragment();
        // collect passing data
        Bundle data = new Bundle();
        data.putLong("tweetId", tweetId);
        // prepare return object
        reply.setArguments(data);
        return reply;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView = inflater.inflate(R.layout.fragment_reply, container, false);
        // Bind Views
        ButterKnife.bind(this, convertView);
        return convertView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Fetch passing data from activity
        User sender = User.account;
        final Long id =  getArguments().getLong("tweetId");
        Tweet tweet = Tweet.hashTweets.get(id);


        // Bind User information
        // Todo: radius the corner
        Glide.with(view.getContext())
                .load(sender.profile_image_url)
                .fitCenter()
                .into(ivProfilePictureCompose);
        tvUsernameCompose.setText(sender.name);
        tvScreenNameCompose.setText(sender.screen_name);

        // Bind ReplyStatus
        tvReplyStatus.setText(REPLYSTATUSPREFIX + tweet.user.name);

        // Set Text field
        String replyString = "@" + tweet.user.screen_name + " ";
        etComposeText.setText(replyString);
        setComposeTextField();

        // Bind Tweet button
        btnTweet.setEnabled(false);
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // remain characters must be in the range for submit tweet
                String tweetText = etComposeText.getText().toString();
                TweetReplyListener listener = (TweetReplyListener) getActivity();
                listener.onSubmitReply(tweetText, id);
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

    public interface TweetReplyListener {
        void onSubmitReply(String inputText, Long id);
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
                setTextFieldStatus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        setTextFieldStatus();
    }

    private void setTextFieldStatus() {
        int remain = MyUtils.setRemainingEditTextLength(etComposeText, tvCharLeft);
        // Set submit button
        if (remain <= 0 || remain == MyUtils.TWEET_MAX_LENGTH) {
            btnTweet.setEnabled(false);
        } else {
            btnTweet.setEnabled(true);
        }
    }
}
