package com.codepath.apps.simpletwitter.models;

import java.util.ArrayList;

public class UserList {
    public ArrayList<String> ids;
    public String next_cursor_str;


    public String toString() {
        String res = "";
        if(ids.isEmpty()) return res;
        res = ids.get(0);
        for( int i = 1; i < ids.size(); i++) {
            res = res + "," + ids.get(i);
        }
        return res;
    }
}

