package com.codepath.apps.simpletwitter;

import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class MyUtils {
    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Special Case: Yesterday
        if (relativeDate.equals("Yesterday")) {
            String res = "1 d";
            //Log.d("DEBUG: relativeDate = ", relativeDate);
            //Log.d("DEBUG: relativeDate = ", res);
            return res;
        }

        // Case: X days ago
        //       X hours ago
        //       X minutes ago
        //       X seconds ago
        int pos;
        for(pos = 0; pos < relativeDate.length(); pos++) {
            if (( relativeDate.charAt(pos) == 'd' ||
                    relativeDate.charAt(pos) == 'h' ||
                    relativeDate.charAt(pos) == 'm' ||
                    relativeDate.charAt(pos) == 's')
                    && pos + 1 < relativeDate.length()) {
                //Log.d("DEBUG: relativeDate = ", relativeDate);
                //Log.d("DEBUG: relativeDate = ", relativeDate.substring(0, pos+1));
                return relativeDate.substring(0, pos+1);
            }
        }

        // Case: more than 7 days ago
        SimpleDateFormat fromUser = new SimpleDateFormat("MMM dd, yyyy");
        SimpleDateFormat myFormat = new SimpleDateFormat("MM/dd/yy");
        String reformattedStr = "";
        try {
            reformattedStr = myFormat.format(fromUser.parse(relativeDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Log.d("DEBUG: relativeDate = ", relativeDate);
        //Log.d("DEBUG: reformatted = ", reformattedStr);
        return reformattedStr;
    }
}
