package com.codepath.apps.simpletwitter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

public class RecipientsAdapter extends ArrayAdapter<Message> {
    public RecipientsAdapter(Context context, List<Message> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = getItem(position);

        RecipientViewHolder holder;
        if(convertView==null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_recipient, parent, false);
            holder = new RecipientViewHolder(convertView);
            convertView.setTag(holder);
        }
        else {
            holder =(RecipientViewHolder) convertView.getTag();
        }

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
        return convertView;
    }

    static class RecipientViewHolder {
        @Bind(R.id.ivProfilePicture) ImageView ivProfilePicture;
        @Bind(R.id.tvUsername) TextView tvUsername;
        @Bind(R.id.tvText) TextView tvText;
        @Bind(R.id.tvCreateTime) TextView tvCreateTime;

        public RecipientViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }
}
