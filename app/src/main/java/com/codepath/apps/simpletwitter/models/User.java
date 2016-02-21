package com.codepath.apps.simpletwitter.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    public String name;
    public String screen_name;
    public String profile_image_url;
    public boolean following;
    public int followers_count;
    public int friends_count;
    public long id;

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
        dest.writeInt(this.followers_count);
        dest.writeInt(this.friends_count);
        dest.writeLong(this.id);
    }

    protected User(Parcel in) {
        this.name = in.readString();
        this.screen_name = in.readString();
        this.profile_image_url = in.readString();
        this.following = in.readByte() != 0;
        this.followers_count = in.readInt();
        this.friends_count = in.readInt();
        this.id = in.readLong();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
