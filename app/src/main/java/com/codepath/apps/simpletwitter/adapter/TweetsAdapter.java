package com.codepath.apps.simpletwitter.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.codepath.apps.simpletwitter.MyUtils;
import com.codepath.apps.simpletwitter.R;
import com.codepath.apps.simpletwitter.listeners.BtnFavoriteOnClickListener;
import com.codepath.apps.simpletwitter.listeners.BtnRetweetOnClickListener;
import com.codepath.apps.simpletwitter.models.Tweet;
import com.codepath.apps.simpletwitter.models.User;
import com.codepath.apps.simpletwitter.models.ViewHolderTweet;
import com.codepath.apps.simpletwitter.models.ViewHolderTweetMedia;

import java.util.List;

public abstract class TweetsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public List<Long> tweets;
    View convertView;

    private final int VIEW_PROG = 0;
    private final int VIEW_ITEM_TEXT = 1;
    private final int VIEW_ITEM_MEDIA = 2;

    // The minimum amount of items to have below your current scroll position before loading more.
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    public TweetsAdapter(List<Long> tweets, RecyclerView recyclerView) {
        this.tweets = tweets;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        // End has been reached
                        // Do something
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    public List<Long> getTweets() {
        return tweets;
    }

    @Override
    public int getItemViewType(int position) {
        if(tweets.get(position) == null) return VIEW_PROG;
        if(Tweet.hashTweets.get(tweets.get(position)).entities != null &&
                Tweet.hashTweets.get(tweets.get(position)).entities.media != null &&
                Tweet.hashTweets.get(tweets.get(position)).entities.media.get(0) != null)
            return VIEW_ITEM_MEDIA;
        return VIEW_ITEM_TEXT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        switch(viewType) {
            case VIEW_PROG:
                convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_progress, parent, false);

                holder = new ProgressViewHolder(convertView);
                return holder;
            case VIEW_ITEM_TEXT:
                convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_tweet, parent, false);

                holder = new ViewHolderTweet(convertView);
                return holder;
            case VIEW_ITEM_MEDIA:
                convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_tweet_media, parent, false);

                holder = new ViewHolderTweetMedia(convertView);
                return holder;
            default:
                convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_tweet, parent, false);

                holder = new ViewHolderTweet(convertView);
                return holder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch(viewType) {
            case VIEW_PROG:
                ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
                break;
            case VIEW_ITEM_TEXT:
                onBindTextTweet((ViewHolderTweet) holder, position);
                break;
            case VIEW_ITEM_MEDIA:
                onBindMedia((ViewHolderTweetMedia) holder, position);
                break;
            default:
                onBindTextTweet((ViewHolderTweet) holder, position);
                break;
        }
    }

    private void onBindTextTweet(ViewHolderTweet holder, int position) {
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
        if(User.account != null && tweet.user.id == User.account.id) {
            holder.ibRetweet.setEnabled(false);
            holder.ibRetweet.setAlpha((float) 0.5);
            holder.tvRetweetCount.setEnabled(false);
            holder.tvRetweetCount.setAlpha((float) 0.0);
        }
        else {
            holder.ibRetweet.setEnabled(true);
            holder.tvRetweetCount.setEnabled(true);
        }
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

    private void onBindMedia(ViewHolderTweetMedia holder, int position) {
        // Bind other stuff
        onBindTextTweet(holder, position);

        // bind image
        final Long tweetId = tweets.get(position);
        Tweet tweet = Tweet.hashTweets.get(tweetId);

        Glide.with(convertView.getContext())
                .load(tweet.entities.media.get(0).media_url)
                .fitCenter()
                .into(holder.ivTweetImage);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // Button OnClick Listener
    public abstract void onClickReply(Long tweetId);
    public abstract void onClickText(Long tweetId);
    public abstract void onClickUser(User user);


    public void setLoaded() {
        loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        }
    }
}
