package com.codepath.apps.simpletwitter.models;

import android.view.View;
import android.widget.ImageView;

import com.codepath.apps.simpletwitter.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ViewHolderTweetMedia extends ViewHolderTweet{
    @Bind(R.id.ivTweetImage)
    public ImageView ivTweetImage;

    public ViewHolderTweetMedia(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
