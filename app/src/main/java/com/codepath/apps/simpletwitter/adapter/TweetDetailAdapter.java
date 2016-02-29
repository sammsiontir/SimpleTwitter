package com.codepath.apps.simpletwitter.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.codepath.apps.simpletwitter.helpers.MyUtils;
import com.codepath.apps.simpletwitter.R;
import com.codepath.apps.simpletwitter.listeners.BtnFavoriteOnClickListener;
import com.codepath.apps.simpletwitter.listeners.BtnRetweetOnClickListener;
import com.codepath.apps.simpletwitter.models.Tweet;
import com.codepath.apps.simpletwitter.models.ViewHolderTweet;

import java.util.List;

public abstract class TweetDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Long> tweets;
    private Long topTweet;
    View convertView;

    private static final int TOP_TWEET = 0;
    private static final int COMMENT_TWEET = 1;

    public TweetDetailAdapter(List<Long> tweets, Long topTweet) {
        this.tweets = tweets;
        this.topTweet = topTweet;
    }

    public List<Long> getTweets() {
        return tweets;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case TOP_TWEET:
                convertView = inflater.inflate(R.layout.item_tweet_detail, parent, false);
                viewHolder = new ViewHolderTweet(convertView);
                break;
            case COMMENT_TWEET:
            default:
                convertView = inflater.inflate(R.layout.item_tweet, parent, false);
                viewHolder = new ViewHolderTweet(convertView);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TOP_TWEET:
                ViewHolderTweet viewHolderTop = (ViewHolderTweet) holder;
                bindViewHolderTop(viewHolderTop);
                break;
            case COMMENT_TWEET:
            default:
                ViewHolderTweet viewHolder = (ViewHolderTweet) holder;
                bindViewHolderComment(viewHolder, position-1);
                break;
        }
    }

    private void bindViewHolderComment(ViewHolderTweet holder, int position) {
        final Long tweetId = tweets.get(position);
        Tweet tweet = Tweet.hashTweets.get(tweetId);

        // Bind data with views
        Glide.with(convertView.getContext())
                .load(tweet.user.profile_image_url)
                .fitCenter()
                .into(holder.ivProfilePicture);
        holder.tvUsername.setText(tweet.user.name);
        holder.tvScreenName.setText("@" + tweet.user.screen_name);
        holder.tvCreateTime.setText(MyUtils.getRelativeTimeAgo(tweet.created_at));
        // Bind text field
        holder.tvTweetText.setText(tweet.text);
        holder.tvTweetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickText(tweetId);
            }
        });
        // Bind Reply button
        holder.ibReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickReply(tweetId);
            }
        });
        // Bind Retweet button
        holder.ibRetweet.setEnabled(true);
        holder.ibRetweet.setSelected(tweet.retweeted);
        holder.tvRetweetCount.setSelected(tweet.retweeted);
        holder.tvRetweetCount.setText(Integer.toString(tweet.retweet_count));
        holder.ibRetweet.setOnClickListener(new BtnRetweetOnClickListener(tweetId, holder));

        // Bind Favorite button
        holder.ibFavorite.setSelected(tweet.favorited);
        holder.tvFavoriteCount.setSelected(tweet.favorited);
        holder.tvFavoriteCount.setText(Integer.toString(tweet.favorite_count));
        holder.ibFavorite.setOnClickListener(new BtnFavoriteOnClickListener(tweetId, holder));
    }

    private void bindViewHolderTop(ViewHolderTweet holder) {
        final Long tweetId = topTweet;
        Tweet tweet = Tweet.hashTweets.get(tweetId);

        // Bind data with views
        Glide.with(convertView.getContext())
                .load(tweet.user.profile_image_url)
                .fitCenter()
                .into(holder.ivProfilePicture);
        holder.tvUsername.setText(tweet.user.name);
        holder.tvScreenName.setText("@" + tweet.user.screen_name);
        holder.tvCreateTime.setText(tweet.created_at);
        // Bind text field
        holder.tvTweetText.setText(tweet.text);
        holder.tvTweetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickText(tweetId);
            }
        });
        // Bind Reply button
        holder.ibReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickReply(tweetId);
            }
        });
        // Bind Retweet button
        holder.ibRetweet.setEnabled(true);
        holder.ibRetweet.setSelected(tweet.retweeted);
        holder.tvRetweetCount.setSelected(tweet.retweeted);
        holder.tvRetweetCount.setText(Integer.toString(tweet.retweet_count));
        holder.ibRetweet.setOnClickListener(new BtnRetweetOnClickListener(tweetId, holder));

        // Bind Favorite button
        holder.ibFavorite.setSelected(tweet.favorited);
        holder.tvFavoriteCount.setSelected(tweet.favorited);
        holder.tvFavoriteCount.setText(Integer.toString(tweet.favorite_count));
        holder.ibFavorite.setOnClickListener(new BtnFavoriteOnClickListener(tweetId, holder));

    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) return TOP_TWEET;
        else return COMMENT_TWEET;
    }

    @Override
    public int getItemCount() {
        return tweets.size()+1;
    }

    // Button OnClick Listener
    public abstract void onClickReply(Long tweetId);
    public abstract void onClickText(Long tweetId);
}
