package com.example.apps.newsfeed.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import static com.example.apps.newsfeed.models.Tweet.getRelativeTimeAgo;

@Parcel
public class User {
    
    public String name, screenName, profileImageUrl;
    public long uid;
    
    public String profileImageUrlHTTPS, profileBackgroundImageUrl, profileBackgroundImageUrlHTTPS, profileBannerUrl, profileBackgroundColor, createdAt, description;
    public long followersCount, followingsCount;
    public boolean verifiedUser, followsYou, following;

    public User() {}

    
    public static User fromJSON(JSONObject jsonObject) throws JSONException {
        User user = new User();

        
        user.name = jsonObject.getString("name");
        user.screenName = jsonObject.getString("screen_name");
        user.profileImageUrl = jsonObject.getString("profile_image_url").replace("_normal","");
        user.profileImageUrlHTTPS = jsonObject.getString("profile_image_url_https").replace("_normal","");
        user.uid = jsonObject.getLong("id");
        

        user.description = jsonObject.getString("description");
        user.profileBackgroundImageUrl = jsonObject.getString("profile_background_image_url").replace("_normal","");
        user.profileBackgroundColor = jsonObject.getString("profile_background_color");
        user.profileBannerUrl = jsonObject.getString("profile_banner_url");
        user.createdAt = getRelativeTimeAgo(jsonObject.getString("created_at")); 
        user.followersCount = jsonObject.getLong("followers_count");
        user.followingsCount = jsonObject.getLong("friends_count"); 
        user.verifiedUser = jsonObject.getBoolean("verified");
        user.followsYou = jsonObject.getBoolean("following");
        user.following = false; 

        return user;
    }
}
