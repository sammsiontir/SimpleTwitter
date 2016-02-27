package com.codepath.apps.simpletwitter.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.simpletwitter.ItemClickSupport;
import com.codepath.apps.simpletwitter.R;
import com.codepath.apps.simpletwitter.RESTAPI.TwitterApplication;
import com.codepath.apps.simpletwitter.adapter.RecipientsAdapter;
import com.codepath.apps.simpletwitter.listeners.EndlessRecyclerViewScrollListener;
import com.codepath.apps.simpletwitter.models.Message;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MessageActivity extends AppCompatActivity {
    private ArrayList<Message>  recipients;
    private RecipientsAdapter recipientsAdapter;
    private boolean noMorerecipient;
    @Bind(R.id.rvList) RecyclerView rvList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
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
        noMorerecipient = false;
        recipients = new ArrayList<>();
        recipientsAdapter = new RecipientsAdapter(recipients);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvList.setAdapter(recipientsAdapter);
        rvList.setLayoutManager(linearLayoutManager);
        rvList.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if(noMorerecipient == false) {
                    //loadPreviousRecipients();
                }
            }
        });

        // Fetch recipients
        loadLatestRecipients();

        // Set onClick Listener
        ItemClickSupport.addTo(rvList).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                Toast.makeText(getApplication()
                        , "Click " + Integer.toString(position) + ": " + Long.toString(recipients.get(position).id)
                        , Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadLatestRecipients() {
        long max_id = 0;
        long since_id = 0;
        if (recipientsAdapter != null && recipientsAdapter.getItemCount() > 0) {
            since_id = recipientsAdapter.getRecipients().get(0).id;
        }
        TwitterApplication.getRestClient().getMessageRecipient(max_id, since_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Gson gson = new Gson();
                ArrayList<Message> moreRecipients = gson.fromJson(response.toString(),
                        new TypeToken<ArrayList<Message>>() {
                        }.getType());

                recipients.addAll(moreRecipients);
                recipientsAdapter.notifyDataSetChanged();
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
        if (recipientsAdapter != null && recipientsAdapter.getItemCount() > 0) {
            max_id = recipientsAdapter.getRecipients().get(recipientsAdapter.getItemCount() - 1).id;
        }
        TwitterApplication.getRestClient().getMessageRecipient(max_id, since_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Gson gson = new Gson();
                ArrayList<Message> moreRecipients = gson.fromJson(response.toString(),
                        new TypeToken<ArrayList<Message>>() {
                        }.getType());

                recipients.addAll(moreRecipients);
                // notify the adapter
                int curSize = recipientsAdapter.getItemCount();
                recipientsAdapter.notifyItemRangeInserted(curSize, recipients.size() - 1);

                if(moreRecipients.size() < 25) {
                    noMorerecipient = true;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable
                    , JSONArray errorResponse) {
                throwable.printStackTrace();
                Log.e("REST_API_ERROR", errorResponse.toString());
            }
        });
    }



}
