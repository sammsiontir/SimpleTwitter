package com.codepath.apps.simpletwitter.models;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.simpletwitter.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ViewHolderTweet extends RecyclerView.ViewHolder{
    @Bind(R.id.ivProfilePictureCompose)
    public ImageView ivProfilePicture;
    @Bind(R.id.tvUsername)
    public TextView tvUsername;
    @Bind(R.id.tvScreenName)
    public TextView tvScreenName;
    @Bind(R.id.tvCreateTime)
    public TextView tvCreateTime;
    @Bind(R.id.tvTweetText)
    public TextView tvTweetText;
    @Bind(R.id.ibReply)
    public ImageView ibReply;
    @Bind(R.id.ibRetweet)
    public ImageView ibRetweet;
    @Bind(R.id.tvRetweetCount)
    public TextView tvRetweetCount;
    @Bind(R.id.ibFavorite)
    public ImageView ibFavorite;
    @Bind(R.id.tvFavoriteCount)
    public TextView tvFavoriteCount;

    public ViewHolderTweet(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}