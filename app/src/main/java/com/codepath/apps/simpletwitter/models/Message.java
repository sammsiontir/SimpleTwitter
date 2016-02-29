package com.codepath.apps.simpletwitter.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

public class Message implements Parcelable {
    public static transient HashMap<Long, ArrayList<Message>> Recipients = new HashMap<>();

    public String created_at;
    public Entity entities;
    public long id;
    public User recipient;
    public User sender;
    public String text;

    public Long getRecipientId() {
        if(User.account.id == recipient.id) return sender.id;
        else return recipient.id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.created_at);
        dest.writeParcelable(this.entities, 0);
        dest.writeLong(this.id);
        dest.writeParcelable(this.recipient, 0);
        dest.writeParcelable(this.sender, 0);
        dest.writeString(this.text);
    }

    public Message() {
    }

    protected Message(Parcel in) {
        this.created_at = in.readString();
        this.entities = in.readParcelable(Entity.class.getClassLoader());
        this.id = in.readLong();
        this.recipient = in.readParcelable(User.class.getClassLoader());
        this.sender = in.readParcelable(User.class.getClassLoader());
        this.text = in.readString();
    }

    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
        public Message createFromParcel(Parcel source) {
            return new Message(source);
        }

        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
}

