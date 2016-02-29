package com.codepath.apps.simpletwitter.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Media implements Parcelable {
    public String display_url;
    public Long id;
    public ArrayList<Integer> indices;
    public String media_url;
    public Long source_status_id;
    public String type;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.display_url);
        dest.writeValue(this.id);
        dest.writeList(this.indices);
        dest.writeString(this.media_url);
        dest.writeValue(this.source_status_id);
        dest.writeString(this.type);
    }

    public Media() {
    }

    protected Media(Parcel in) {
        this.display_url = in.readString();
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.indices = new ArrayList<Integer>();
        in.readList(this.indices, List.class.getClassLoader());
        this.media_url = in.readString();
        this.source_status_id = (Long) in.readValue(Long.class.getClassLoader());
        this.type = in.readString();
    }

    public static final Parcelable.Creator<Media> CREATOR = new Parcelable.Creator<Media>() {
        public Media createFromParcel(Parcel source) {
            return new Media(source);
        }

        public Media[] newArray(int size) {
            return new Media[size];
        }
    };
}
