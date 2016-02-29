package com.codepath.apps.simpletwitter.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.simpletwitter.R;
import com.codepath.apps.simpletwitter.models.Message;
import com.codepath.apps.simpletwitter.models.User;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int SENDER = 0;
    private static final int RECIPIENT = 1;

    private List<Message> messages;
    View convertView;

    public MessagesAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if(viewType == SENDER) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_my_message, parent, false);
            holder = new MyMessageViewHolder(convertView);
        }
        else {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recipient_message, parent, false);
            holder = new RecipientMessageViewHolder(convertView);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);

        if(holder instanceof MyMessageViewHolder) {
            ((MyMessageViewHolder)holder).tvMessage.setText(message.text);
        }
        else {
            ((RecipientMessageViewHolder)holder).tvMessage.setText(message.text);
            Glide.with(convertView.getContext())
                    .load(message.sender.profile_image_url)
                    .fitCenter()
                    .into(((RecipientMessageViewHolder)holder).ivProfilePicture);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(messages.get(position).sender.id == User.account.id) {
            return SENDER;
        }
        else return RECIPIENT;
    }

    public static class MyMessageViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tvMessage) TextView tvMessage;

        public MyMessageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class RecipientMessageViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.ivProfilePicture) ImageView ivProfilePicture;
        @Bind(R.id.tvMessage) TextView tvMessage;

        public RecipientMessageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
