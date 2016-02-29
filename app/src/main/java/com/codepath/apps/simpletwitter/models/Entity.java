package com.codepath.apps.simpletwitter.models;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Entity implements Parcelable {
    public ArrayList<Hashtag> hashtags;
    public ArrayList<Media> media;
    public ArrayList<UserMention> user_mentions;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(hashtags);
        dest.writeTypedList(media);
        dest.writeTypedList(user_mentions);
    }

    public Entity() {
    }

    protected Entity(Parcel in) {
        this.hashtags = in.createTypedArrayList(Hashtag.CREATOR);
        this.media = in.createTypedArrayList(Media.CREATOR);
        this.user_mentions = in.createTypedArrayList(UserMention.CREATOR);
    }

    public static final Parcelable.Creator<Entity> CREATOR = new Parcelable.Creator<Entity>() {
        public Entity createFromParcel(Parcel source) {
            return new Entity(source);
        }

        public Entity[] newArray(int size) {
            return new Entity[size];
        }
    };
}
