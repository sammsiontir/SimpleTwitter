package com.codepath.apps.simpletwitter.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.simpletwitter.R;
import com.codepath.apps.simpletwitter.RESTAPI.TwitterApplication;
import com.codepath.apps.simpletwitter.models.Tweet;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    public List<Long> tweets;
    View convertView;

    public TweetsAdapter(List<Long> tweets) {
        this.tweets = tweets;
    }

    public List<Long> getTweets() {
        return tweets;
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
        final Long tweetId = tweets.get(position);
        Tweet tweet = Tweet.hashTweets.get(tweetId);

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

        // Bind Reply button
        holder.ibReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open reply dialog
            }
        });
        // Bind Retweet button
        holder.ibRetweet.setEnabled(true);
        holder.ibRetweet.setSelected(tweet.retweeted);
        holder.tvRetweetCount.setSelected(tweet.retweeted);
        holder.ibRetweet.setOnClickListener(new BtnRetweetOnClickListener(tweetId, holder));

        // Bind Favorite button
        holder.ibFavorite.setSelected(tweet.favorited);
        holder.tvFavoriteCount.setSelected(tweet.favorited);
        holder.ibFavorite.setOnClickListener(new BtnFavoriteOnClickListener(tweetId, holder));
    }


    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.ivProfilePictureCompose) ImageView ivProfilePicture;
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

        // Special Case: Yesterday
        if (relativeDate.equals("Yesterday")) {
            String res = "1 d";
            //Log.d("DEBUG: relativeDate = ", relativeDate);
            //Log.d("DEBUG: relativeDate = ", res);
            return res;
        }

        // Case: X days ago
        //       X hours ago
        //       X minutes ago
        //       X seconds ago
        int pos;
        for(pos = 0; pos < relativeDate.length(); pos++) {
            if (( relativeDate.charAt(pos) == 'd' ||
                  relativeDate.charAt(pos) == 'h' ||
                  relativeDate.charAt(pos) == 'm' ||
                  relativeDate.charAt(pos) == 's')
                 && pos + 1 < relativeDate.length()) {
                //Log.d("DEBUG: relativeDate = ", relativeDate);
                //Log.d("DEBUG: relativeDate = ", relativeDate.substring(0, pos+1));
                return relativeDate.substring(0, pos+1);
            }
        }

        // Case: more than 7 days ago
        SimpleDateFormat fromUser = new SimpleDateFormat("MMM dd, yyyy");
        SimpleDateFormat myFormat = new SimpleDateFormat("MM/dd/yy");
        String reformattedStr = "";
        try {
            reformattedStr = myFormat.format(fromUser.parse(relativeDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Log.d("DEBUG: relativeDate = ", relativeDate);
        //Log.d("DEBUG: reformatted = ", reformattedStr);
        return reformattedStr;
    }

    // REST API
    private void postRetweet(long id) {
        Toast.makeText(convertView.getContext(), "Retweet", Toast.LENGTH_LONG).show();
        TwitterApplication.getRestClient().postRetweet(id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson gson = new Gson();
                Tweet tweet = gson.fromJson(response.toString(), Tweet.class);
                //tweet.update();
                Log.d("DEBUG", response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable
                    , JSONObject errorResponse) {
                throwable.printStackTrace();
                Log.e("REST_API_ERROR", errorResponse.toString());
            }
        });
    }

    private void postUnRetweet(long id) {
        Toast.makeText(convertView.getContext(), "Un-retweet", Toast.LENGTH_LONG).show();
        TwitterApplication.getRestClient().postUnRetweet(id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson gson = new Gson();
                Tweet tweet = gson.fromJson(response.toString(), Tweet.class);
                tweet.update();
                Log.d("DEBUG", response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable
                    , JSONObject errorResponse) {
                throwable.printStackTrace();
                Log.e("REST_API_ERROR", errorResponse.toString());
            }
        });
    }

    private void postFavorite(long id) {
        Toast.makeText(convertView.getContext(), "Favorite", Toast.LENGTH_LONG).show();
        TwitterApplication.getRestClient().postFavorite(id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson gson = new Gson();
                Tweet tweet = gson.fromJson(response.toString(), Tweet.class);
                tweet.update();
                Log.d("DEBUG", response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable
                    , JSONObject errorResponse) {
                throwable.printStackTrace();
                Log.e("REST_API_ERROR", errorResponse.toString());
            }
        });
    }

    private void postUnFavorite(long id) {
        Toast.makeText(convertView.getContext(), "Un-favorite", Toast.LENGTH_LONG).show();
        TwitterApplication.getRestClient().postUnFavorite(id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson gson = new Gson();
                Tweet tweet = gson.fromJson(response.toString(), Tweet.class);
                tweet.update();
                Log.d("DEBUG", response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable
                    , JSONObject errorResponse) {
                throwable.printStackTrace();
                Log.e("REST_API_ERROR", errorResponse.toString());
            }
        });
    }

    private void getTweet(long id, final int position) {
        Toast.makeText(convertView.getContext(), "Fetch single tweet", Toast.LENGTH_LONG).show();
        TwitterApplication.getRestClient().getMyAccount(id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson gson = new Gson();
                Tweet tweet = gson.fromJson(response.toString(), Tweet.class);
                Log.d("DEBUG", response.toString());
                // update timeline
                tweet.update();
                notifyItemChanged(position);
                // update DB
                tweet.update();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable
                    , JSONObject errorResponse) {
                throwable.printStackTrace();
                Log.e("REST_API_ERROR", errorResponse.toString());
            }
        });
    }

    public class BtnRetweetOnClickListener implements View.OnClickListener {
        private Long tweetId;
        private TweetsAdapter.ViewHolder holder;

        public BtnRetweetOnClickListener(Long tweetId, TweetsAdapter.ViewHolder holder) {
            this.tweetId = tweetId;
            this.holder = holder;
        }

        @Override
        public void onClick(View v) {
            if (Tweet.hashTweets.get(this.tweetId).retweeted) {
                // update server
                postUnRetweet(this.tweetId);
                // update local database
                Tweet.hashTweets.get(this.tweetId).unretweet();
                // update views
                holder.ibRetweet.setSelected(false);
                holder.tvRetweetCount.setSelected(false);
                holder.tvRetweetCount.setText(Integer.toString(Tweet.hashTweets.get(this.tweetId).retweet_count));

            } else {
                // update server
                postRetweet(this.tweetId);
                // update local database
                Tweet.hashTweets.get(this.tweetId).retweet();
                // update views
                holder.ibRetweet.setSelected(true);
                holder.tvRetweetCount.setSelected(true);
                holder.tvRetweetCount.setText(Integer.toString(Tweet.hashTweets.get(this.tweetId).retweet_count));
            }
        }
    }

    // Button OnClick Listener
    public class BtnFavoriteOnClickListener implements View.OnClickListener {
        private Long tweetId;
        private TweetsAdapter.ViewHolder holder;

        public BtnFavoriteOnClickListener(Long tweetId, TweetsAdapter.ViewHolder holder) {
            this.tweetId = tweetId;
            this.holder = holder;
        }

        @Override
        public void onClick(View v) {
            if (Tweet.hashTweets.get(this.tweetId).favorited) {
                // update server
                postUnFavorite(this.tweetId);
                // update local database
                Tweet.hashTweets.get(this.tweetId).unfavorite();
                // update views
                holder.ibFavorite.setSelected(false);
                holder.tvFavoriteCount.setSelected(false);
                holder.tvFavoriteCount.setText(Integer.toString(Tweet.hashTweets.get(this.tweetId).favorite_count));

            } else {
                // update server
                postFavorite(this.tweetId);
                // update local database
                Tweet.hashTweets.get(this.tweetId).favorite();
                // update views
                holder.ibFavorite.setSelected(true);
                holder.tvFavoriteCount.setSelected(true);
                holder.tvFavoriteCount.setText(Integer.toString(Tweet.hashTweets.get(this.tweetId).favorite_count));
            }
        }
    }
}
