package com.codepath.apps.simpletwitter.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class UserMention implements Parcelable {
    Long id;
    ArrayList<Integer> indices;
    String name;
    String screen_name;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeList(this.indices);
        dest.writeString(this.name);
        dest.writeString(this.screen_name);
    }

    public UserMention() {
    }

    protected UserMention(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.indices = new ArrayList<Integer>();
        in.readList(this.indices, List.class.getClassLoader());
        this.name = in.readString();
        this.screen_name = in.readString();
    }

    public static final Parcelable.Creator<UserMention> CREATOR = new Parcelable.Creator<UserMention>() {
        public UserMention createFromParcel(Parcel source) {
            return new UserMention(source);
        }

        public UserMention[] newArray(int size) {
            return new UserMention[size];
        }
    };
}
