package com.codepath.apps.simpletwitter.activities;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.simpletwitter.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TweetFragment extends DialogFragment {

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
        //etComposeText.requestFocus();
        //getDialog().getWindow().setSoftInputMode(
        //        WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        // Bind Tweet button
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TweetComposeListener listener = (TweetComposeListener) getActivity();
                listener.onClickTweet(etComposeText.getText().toString());
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

}
