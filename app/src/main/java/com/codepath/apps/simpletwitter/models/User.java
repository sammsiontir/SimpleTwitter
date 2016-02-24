package com.codepath.apps.simpletwitter.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    public static transient User account = null;

    public String name;
    public String screen_name;
    public String profile_image_url;
    public boolean following;
    public boolean follow_request_sent;
    public int followers_count;
    public int friends_count;
    public long id;
    public Entity entities;
    public String profile_background_image_url;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.screen_name);
        dest.writeString(this.profile_image_url);
        dest.writeByte(following ? (byte) 1 : (byte) 0);
        dest.writeByte(follow_request_sent ? (byte) 1 : (byte) 0);
        dest.writeInt(this.followers_count);
        dest.writeInt(this.friends_count);
        dest.writeLong(this.id);
        dest.writeParcelable(this.entities, flags);
        dest.writeString(this.profile_background_image_url);
    }

    public User() {
    }

    protected User(Parcel in) {
        this.name = in.readString();
        this.screen_name = in.readString();
        this.profile_image_url = in.readString();
        this.following = in.readByte() != 0;
        this.follow_request_sent = in.readByte() != 0;
        this.followers_count = in.readInt();
        this.friends_count = in.readInt();
        this.id = in.readLong();
        this.entities = in.readParcelable(Entity.class.getClassLoader());
        this.profile_background_image_url = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
