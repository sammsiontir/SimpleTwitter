package com.codepath.apps.simpletwitter.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.codepath.apps.simpletwitter.R;
import com.codepath.apps.simpletwitter.RESTAPI.TwitterApplication;
import com.codepath.apps.simpletwitter.adapter.RecipientsAdapter;
import com.codepath.apps.simpletwitter.models.Message;
import com.codepath.apps.simpletwitter.models.Tweet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RecipientsListActivity extends TwitterBaseActivity {
    private ArrayList<Message>  recipients;
    private RecipientsAdapter recipientsAdapter;
    @Bind(R.id.lvRecipient) ListView lvRecipient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setIcon(R.drawable.ic_twitter_logo_white);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        // Bind views
        ButterKnife.bind(this);

        // Set adapter
        recipients = new ArrayList<>();
        recipientsAdapter = new RecipientsAdapter(this, recipients);
        lvRecipient.setAdapter(recipientsAdapter);

        // Fetch recipients
        loadLatestRecipients();
    }

    private void loadLatestRecipients() {
        long max_id = 0;
        long since_id = 0;
        if (recipients != null && recipients.size() > 0) {
            since_id = recipients.get(0).id;
        }
        TwitterApplication.getRestClient().getMessageRecipient(max_id, since_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Gson gson = new Gson();
                ArrayList<Message> moreRecipients = gson.fromJson(response.toString(),
                        new TypeToken<ArrayList<Message>>() {
                        }.getType());
                for(int i = 0; i < moreRecipients.size(); i++) {
                    Message m = moreRecipients.get(i);
                    if(Message.Recipients.get(m.getRecipientId()) == null) {
                        ArrayList<Message> ms = new ArrayList<>();
                        ms.add(m);
                        Message.Recipients.put(m.getRecipientId(), ms);
                        // Only add latest message to the adapter
                        recipientsAdapter.add(m);
                    }
                    else {
                        Message.Recipients.get(m.getRecipientId()).add(m);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable
                    , JSONObject errorResponse) {
                throwable.printStackTrace();
                Log.d("REST_API_ERROR", errorResponse.toString());
            }
        });
    }

    private void loadPreviousRecipients() {
        long max_id = 0;
        long since_id = 0;
        if (recipients != null && recipients.size() > 0) {
            max_id = recipients.get(recipients.size() - 1).id;
        }
        TwitterApplication.getRestClient().getMessageRecipient(max_id, since_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Gson gson = new Gson();
                ArrayList<Message> moreRecipients = gson.fromJson(response.toString(),
                        new TypeToken<ArrayList<Message>>() {
                        }.getType());
                recipientsAdapter.addAll(moreRecipients);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable
                    , JSONArray errorResponse) {
                throwable.printStackTrace();
                Log.e("REST_API_ERROR", errorResponse.toString());
            }
        });
    }

    @Override
    public void addTweet(Tweet tweet) {

    }
}
