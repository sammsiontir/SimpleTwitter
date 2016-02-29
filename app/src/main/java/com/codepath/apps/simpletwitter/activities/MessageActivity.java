package com.codepath.apps.simpletwitter.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.codepath.apps.simpletwitter.MyUtils;
import com.codepath.apps.simpletwitter.R;
import com.codepath.apps.simpletwitter.RESTAPI.TwitterApplication;
import com.codepath.apps.simpletwitter.adapter.MessagesAdapter;
import com.codepath.apps.simpletwitter.models.Message;
import com.codepath.apps.simpletwitter.models.User;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MessageActivity extends AppCompatActivity {
    private ArrayList<Message> messages;
    private MessagesAdapter messagesAdapter;
    private Message recipient;

    @Bind(R.id.rvMessage)
    RecyclerView rvMessage;
    @Bind(R.id.etMessage)
    EditText etMessage;
    @Bind(R.id.btnSend)
    Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        // Received data from caller
        recipient = getIntent().getParcelableExtra("recipient");
        final ArrayList<Message> temp = Message.Recipients.get(recipient.getRecipientId());
        messages = new ArrayList<>();
        for(int i = 0; i < temp.size(); i++) {
            messages.add(0, temp.get(i));
        }

        // Bind views
        ButterKnife.bind(this);

        // toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setIcon(R.drawable.ic_twitter_logo_white);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#55acee")));

        // Bind adapter with recyclerview
        messagesAdapter = new MessagesAdapter(messages);
        rvMessage.setAdapter(messagesAdapter);
        rvMessage.setLayoutManager(new LinearLayoutManager(this));

        // Send message
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = etMessage.getText().toString();
                // send message
                sendMessage(recipient.getRecipientId(), text);
                // clear etMessage
                etMessage.setText("");
            }
        });
        btnSend.setEnabled(false);
        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = etMessage.getText().toString();
                // Set submit button
                if (input.length() <= 0) {
                    btnSend.setEnabled(false);
                } else {
                    btnSend.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void sendMessage(Long recipientId, String text) {
        TwitterApplication.getRestClient().postMessageNew(recipientId, text, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson gson = new Gson();
                Message message = gson.fromJson(response.toString(), Message.class);
                messages.add(message);
                messagesAdapter.notifyItemInserted(messages.size() - 1);

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable
                    , JSONObject errorResponse) {
                throwable.printStackTrace();
                Log.d("REST_API_ERROR", errorResponse.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.action_tweet:
                MyUtils.openComposeDialog(this);
                return true;

            case R.id.action_profile:
                MyUtils.openProfileActivity(this, User.account);
                return true;

            case R.id.action_message:
                MyUtils.openRecipientsActivity(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



}
