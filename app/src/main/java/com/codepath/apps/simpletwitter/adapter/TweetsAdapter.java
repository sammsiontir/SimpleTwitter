package com.codepath.apps.simpletwitter.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.codepath.apps.simpletwitter.MyUtils;
import com.codepath.apps.simpletwitter.R;
import com.codepath.apps.simpletwitter.models.Tweet;
import com.codepath.apps.simpletwitter.models.User;
import com.codepath.apps.simpletwitter.models.ViewHolderTweet;

import java.util.List;

public abstract class TweetsAdapter extends RecyclerView.Adapter<ViewHolderTweet> {
    public List<Long> tweets;
    View convertView;

    public TweetsAdapter(List<Long> tweets) {
        this.tweets = tweets;
    }
    public List<Long> getTweets() {
        return tweets;
    }

    @Override
    public ViewHolderTweet onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        convertView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolderTweet viewHolder = new ViewHolderTweet(convertView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolderTweet holder, int position) {
        // Get data from current position
        final Long tweetId = tweets.get(position);
        Tweet tweet = Tweet.hashTweets.get(tweetId);

        // Bind data with views
        Glide.with(convertView.getContext())
                .load(tweet.user.profile_image_url)
                .fitCenter()
                .into(holder.ivProfilePicture);
        holder.ivProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickUser(Tweet.hashTweets.get(tweetId).user);
            }
        });

        holder.tvUsername.setText(tweet.user.name);
        holder.tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickUser(Tweet.hashTweets.get(tweetId).user);
            }
        });
        holder.tvScreenName.setText("@" + tweet.user.screen_name);
        holder.tvScreenName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickUser(Tweet.hashTweets.get(tweetId).user);
            }
        });
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


    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // Button OnClick Listener
    public abstract void onClickReply(Long tweetId);
    public abstract void onClickText(Long tweetId);
    public abstract void onClickUser(User user);

}
