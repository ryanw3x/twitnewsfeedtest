package com.example.apps.newsfeed.models;

import android.text.format.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

@Parcel
public class Tweet {
    public String body, createdAt;
    public long uid; 
    public User user;
    public Media media;
    public int retweetCount, likeCount;
    public boolean tweetLiked, tweetRetweeted, mediaFound;

    
    public Tweet() {}

    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException{
        Tweet tweet = new Tweet();

        
        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = getRelativeTimeAgo(jsonObject.getString("created_at"));
        
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        try {
            tweet.media = Media.fromJSON(jsonObject.getJSONObject("entities").getJSONArray("media"));
            tweet.mediaFound = true;
        } catch (JSONException e) {
            tweet.media = null;
            tweet.mediaFound = false;
        }

        tweet.retweetCount = jsonObject.getInt("retweet_count");
        tweet.likeCount = jsonObject.getInt("favorite_count");
        tweet.tweetRetweeted = jsonObject.getBoolean("retweeted");
        tweet.tweetLiked = jsonObject.getBoolean("favorited");

        return tweet;
    }

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

        return relativeDate;
    }
}
