package com.codepath.apps.simpletwitter.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
 * Created by chengfu_lin on 2/17/16.
 */
public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {
    public TweetsArrayAdapter(Context context, List<Tweet> objects) {
        super(context, 0, objects);
    }


    static class ViewHolder {
        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        @Bind(R.id.ivProfilePicture) ImageView ivProfilePicture;
        @Bind(R.id.tvUsername) TextView tvUsername;
        @Bind(R.id.tvCreateTime) TextView tvCreateTime;
        @Bind(R.id.tvTweetText) TextView tvTweetText;
        @Bind(R.id.ibReply) ImageView ibReply;
        @Bind(R.id.ibRetweet) ImageView ibRetweet;
        @Bind(R.id.tvRetweetCount) TextView tvRetweetCount;
        @Bind(R.id.ibFavorite) ImageView ibFavorite;
        @Bind(R.id.tvFavoriteCount) TextView tvFavoriteCount;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Tweet tweet = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder;
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_tweet, parent, false);

            // Initialize all the components in ListView layout
            viewHolder = new ViewHolder(convertView);
            // Add tag to viewHolder
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        Glide.with(convertView.getContext())
                .load(tweet.user.profile_image_url)
                .fitCenter()
                .into(viewHolder.ivProfilePicture);
        viewHolder.tvUsername.setText(tweet.user.name);
        viewHolder.tvCreateTime.setText(getRelativeTimeAgo(tweet.created_at));
        viewHolder.tvTweetText.setText(tweet.text);
        viewHolder.tvRetweetCount.setText(Integer.toString(tweet.retweet_count));
        viewHolder.tvFavoriteCount.setText(Integer.toString(tweet.favorite_count));



        // Return the completed view to render on screen
        return convertView;
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
