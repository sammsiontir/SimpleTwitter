package com.codepath.apps.simpletwitter.models;

public class User {
    public String name;
    public String screen_name;
    public String profile_image_url;
    public boolean following;
    public int followers_count;
    public int friends_count;
    public long id;
}


/*
 "user": {
      "name": "OAuth Dancer",
      "profile_sidebar_fill_color": "DDEEF6",
      "profile_background_tile": true,
      "profile_sidebar_border_color": "C0DEED",
      "profile_image_url": "http://a0.twimg.com/profile_images/730275945/oauth-dancer_normal.jpg",
      "created_at": "Wed Mar 03 19:37:35 +0000 2010",
      "location": "San Francisco, CA",
      "follow_request_sent": false,
      "id_str": "119476949",
      "is_translator": false,
      "profile_link_color": "0084B4",
      "entities": {
        "url": {
          "urls": [
            {
              "expanded_url": null,
              "url": "http://bit.ly/oauth-dancer",
              "indices": [
                0,
                26
              ],
              "display_url": null
            }
          ]
        },
        "description": null
      },
      "default_profile": false,
      "url": "http://bit.ly/oauth-dancer",
      "contributors_enabled": false,
      "favourites_count": 7,
      "utc_offset": null,
      "profile_image_url_https": "https://si0.twimg.com/profile_images/730275945/oauth-dancer_normal.jpg",
      "id": 119476949,
      "listed_count": 1,
      "profile_use_background_image": true,
      "profile_text_color": "333333",
      "followers_count": 28,
      "lang": "en",
      "protected": false,
      "geo_enabled": true,
      "notifications": false,
      "description": "",
      "profile_background_color": "C0DEED",
      "verified": false,
      "time_zone": null,
      "profile_background_image_url_https": "https://si0.twimg.com/profile_background_images/80151733/oauth-dance.png",
      "statuses_count": 166,
      "profile_background_image_url": "http://a0.twimg.com/profile_background_images/80151733/oauth-dance.png",
      "default_profile_image": false,
      "friends_count": 14,
      "following": false,
      "show_all_inline_media": false,
      "screen_name": "oauth_dancer"
    },
 */