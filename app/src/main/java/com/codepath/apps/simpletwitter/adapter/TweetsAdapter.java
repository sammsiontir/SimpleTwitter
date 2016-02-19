package com.codepath.apps.simpletwitter.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.simpletwitter.R;
import com.codepath.apps.simpletwitter.models.Tweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by chengfu_lin on 2/18/16.
 */
public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    private List<Tweet> tweets;
    View convertView;

    public TweetsAdapter(List<Tweet> tweets) {
        this.tweets = tweets;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        convertView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(convertView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get data from current position
        Tweet tweet = tweets.get(position);

        // Bind data with views
        Glide.with(convertView.getContext())
                .load(tweet.user.profile_image_url)
                .fitCenter()
                .into(holder.ivProfilePicture);
        holder.tvUsername.setText(tweet.user.name);
        holder.tvScreenName.setText("@" + tweet.user.screen_name);
        holder.tvCreateTime.setText(getRelativeTimeAgo(tweet.created_at));
        holder.tvTweetText.setText(tweet.text);
        holder.tvRetweetCount.setText(Integer.toString(tweet.retweet_count));
        holder.tvFavoriteCount.setText(Integer.toString(tweet.favorite_count));
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.ivProfilePicture) ImageView ivProfilePicture;
        @Bind(R.id.tvUsername) TextView tvUsername;
        @Bind(R.id.tvScreenName) TextView tvScreenName;
        @Bind(R.id.tvCreateTime) TextView tvCreateTime;
        @Bind(R.id.tvTweetText) TextView tvTweetText;
        @Bind(R.id.ibReply) ImageView ibReply;
        @Bind(R.id.ibRetweet) ImageView ibRetweet;
        @Bind(R.id.tvRetweetCount) TextView tvRetweetCount;
        @Bind(R.id.ibFavorite) ImageView ibFavorite;
        @Bind(R.id.tvFavoriteCount) TextView tvFavoriteCount;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}
