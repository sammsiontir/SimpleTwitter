package com.codepath.apps.simpletwitter.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.simpletwitter.MyUtils;
import com.codepath.apps.simpletwitter.R;
import com.codepath.apps.simpletwitter.models.Message;
import com.codepath.apps.simpletwitter.models.User;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RecipientsAdapter extends RecyclerView.Adapter<RecipientsAdapter.RecipientViewHolder> {
    private List<Message> recipients;
    View convertView;

    public RecipientsAdapter(List<Message> recipients) {
        this.recipients = recipients;
    }

    public List<Message> getRecipients() {
        return recipients;
    }

    @Override
    public RecipientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        convertView = inflater.inflate(R.layout.item_recipient, parent, false);
        RecipientViewHolder viewHolder = new RecipientViewHolder(convertView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecipientViewHolder holder, int position) {
        Message message = recipients.get(position);

        User recipient;
        if(User.account.id == message.sender.id) recipient = message.recipient;
        else recipient = message.sender;

        // Bind data with views
        Glide.with(convertView.getContext())
                .load(recipient.profile_image_url)
                .fitCenter()
                .into(holder.ivProfilePicture);

        holder.tvUsername.setText(recipient.name);
        holder.tvText.setText(message.text);
        holder.tvCreateTime.setText(MyUtils.getRelativeTimeAgo(message.created_at));
    }

    @Override
    public int getItemCount() {
        return recipients.size();
    }

    class RecipientViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.ivProfilePicture) ImageView ivProfilePicture;
        @Bind(R.id.tvUsername) TextView tvUsername;
        @Bind(R.id.tvText) TextView tvText;
        @Bind(R.id.tvCreateTime) TextView tvCreateTime;

        public RecipientViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
