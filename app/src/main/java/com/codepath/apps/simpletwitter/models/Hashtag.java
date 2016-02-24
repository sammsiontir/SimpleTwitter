package com.codepath.apps.simpletwitter.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Hashtag implements Parcelable {
    ArrayList<Integer> indices;
    String text;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.indices);
        dest.writeString(this.text);
    }

    public Hashtag() {
    }

    protected Hashtag(Parcel in) {
        this.indices = new ArrayList<Integer>();
        in.readList(this.indices, List.class.getClassLoader());
        this.text = in.readString();
    }

    public static final Parcelable.Creator<Hashtag> CREATOR = new Parcelable.Creator<Hashtag>() {
        public Hashtag createFromParcel(Parcel source) {
            return new Hashtag(source);
        }

        public Hashtag[] newArray(int size) {
            return new Hashtag[size];
        }
    };
}