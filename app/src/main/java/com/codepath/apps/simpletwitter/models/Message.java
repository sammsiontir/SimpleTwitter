package com.codepath.apps.simpletwitter.models;

import java.util.ArrayList;
import java.util.HashMap;

public class Message {
    public static HashMap<Long, ArrayList<Message>> Recipients = new HashMap<>();

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
}

