package com.codepath.apps.simpletwitter.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.simpletwitter.R;
import com.codepath.apps.simpletwitter.models.User;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {
    List<User> users;
    View convertView;

    public abstract void onUserClick(User user);
    public abstract void onFriendSubmit(Long userId);
    public abstract void onUnFriendSubmit(Long userId);

    public UsersAdapter(List<User> users) {
        this.users = users;
    }

    public List<User> getUsers() {
        return users;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        convertView = inflater.inflate(R.layout.item_user, parent, false);
        UserViewHolder viewHolder = new UserViewHolder(convertView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final UserViewHolder holder, int position) {
        final User user = users.get(position);

        // Bind with each views
        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUserClick(user);
            }
        });

        Glide.with(convertView.getContext())
                .load(user.profile_image_url)
                .fitCenter()
                .into(holder.ivProfilePicture);

        holder.tvUsername.setText(user.name);
        holder.tvScreenName.setText("@" + user.screen_name);
        holder.ibAddFriend.setSelected(true);
        holder.ibAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.ibAddFriend.isSelected()) {
                    // we are friend before click, so unfriend
                    onUnFriendSubmit(user.id);
                    holder.ibAddFriend.setSelected(false);
                }
                else {
                    // we are not friend before click, so friend
                    onFriendSubmit(user.id);
                    holder.ibAddFriend.setSelected(true);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    class UserViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.ivProfilePicture)
        ImageView ivProfilePicture;
        @Bind(R.id.tvUsername)
        TextView tvUsername;
        @Bind(R.id.tvScreenName)
        TextView tvScreenName;
        @Bind(R.id.ibAddFriend)
        ImageButton ibAddFriend;
        @Bind(R.id.card_view)
        CardView card_view;

        public UserViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
