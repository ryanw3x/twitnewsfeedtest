package com.example.apps.newsfeed;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


public class TwitterClient extends OAuthBaseClient {
    private static final BaseApi REST_API_INSTANCE = TwitterApi.instance(); 
    private static final String REST_URL = "https://api.twitter.com/1.1"; 
    private static final String REST_CONSUMER_KEY = "PGNEF4cG2sLomVBz9QhlUK3sx"; 
    private static final String REST_CONSUMER_SECRET = "xVTBtXsxBItin7z1qHg6l9EMa6dMFHe6wgLDpzGVxptxhMdAYk"; 

    
    public static final String TAG = "TwitterClient";

    
    private static final String FALLBACK_URL = "https://placeholder.com";

    
    private static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

    public TwitterClient(Context context) {
        super(context, REST_API_INSTANCE,
                REST_URL,
                REST_CONSUMER_KEY, 
                REST_CONSUMER_SECRET,
                String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
                        context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
    }

    
    public void getHomeTimeline(int count, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        
        RequestParams params = new RequestParams();
        params.put("count", count);
        params.put("include_entities", true);
        params.put("exclude_replies", false);
        
        
        client.get(apiUrl, params, handler);
    }

    public void addToTimeline(long maxid, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", 15);
        params.put("max_id", maxid - 1);
        params.put("include_entities", true);
        client.get(apiUrl, params, handler);
    }

    public void getMentionsTimeline(int count, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        
        RequestParams params = new RequestParams();
        params.put("count", count);
        params.put("include_entities", true);
        client.get(apiUrl, params, handler);
    }

    public void addToMentionsTimeline(long maxid, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", 15);
        params.put("max_id", maxid - 1);
        params.put("include_entities", true);
        client.get(apiUrl, params, handler);
    }

    
    public void sendTweet(String message, long reply_uid, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", message);
        params.put("in_reply_to_status_id", reply_uid);
        client.post(apiUrl, params, handler);
    }

    public void retweetTweet(long uid, AsyncHttpResponseHandler handler) {
        
        String apiUrl = getApiUrl("statuses/retweet/" + Long.toString(uid) + ".json");
        
        
        client.post(apiUrl, null, handler);
    }

    public void unretweetTweet(long uid, AsyncHttpResponseHandler handler) {

        
        String apiUrl = getApiUrl("statuses/unretweet/" + Long.toString(uid) + ".json"); 
        
        
        
        client.post(apiUrl, null, handler);
    }


    public void likeTweet(long uid, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("favorites/create.json");
        RequestParams params = new RequestParams();
        params.put("id", uid);
        client.post(apiUrl, params, handler);
    }

    public void unlikeTweet(long uid, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("favorites/destroy.json");
        RequestParams params = new RequestParams();
        params.put("id", uid);
        client.post(apiUrl, params, handler);
    }

    public void getUsingUser(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("account/verify_credentials.json");
        RequestParams params = new RequestParams();
        params.put("include_entities", true);
        client.get(apiUrl, params, handler);
    }

    public void user(long uid, String screenName, AsyncHttpResponseHandler handler) {
        
        String apiUrl = getApiUrl("users/show.json");
        RequestParams params = new RequestParams();
        params.put("user_id", uid);
        params.put("screen_name", screenName);
        params.put("include_entities", true);
        client.get(apiUrl, params, handler);
    }

    public void getUserTimeline(String screenName, long count, AsyncHttpResponseHandler handler) {
        
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        RequestParams params = new RequestParams();
        params.put("screen_name", screenName);
        params.put("count", count); 
        
        
        client.get(apiUrl, params, handler);
    }

    public void addToUserTimeline(String screenName, long maxId, AsyncHttpResponseHandler handler) {
        
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        RequestParams params = new RequestParams();
        params.put("max_id", maxId); 
        params.put("screen_name", screenName);
        params.put("count", 30); 
        params.put("include_rts", true);
        params.put("exclude_replies", false);
        client.get(apiUrl, params, handler);
    }

    public void userLookup(String screenNames, AsyncHttpResponseHandler handler) {
        
        
        String apiUrl = getApiUrl("friendships/lookup.json");
        RequestParams params = new RequestParams();
        params.put("screen_name", screenNames);
        client.get(apiUrl, params, handler);
    }

    public void followToggle(String screenName, boolean following, AsyncHttpResponseHandler handler) {
        String apiUrl;
        if (!following) apiUrl = getApiUrl("friendships/create.json");
        else apiUrl = getApiUrl("friendships/destroy.json");
        RequestParams params = new RequestParams();
        params.put("screen_name", screenName);
        client.post(apiUrl, params, handler);
    }

    public void userFollowers() {
        
        
        String apiUrl = getApiUrl("followers/list.json");
        RequestParams params = new RequestParams();
    }

    public void userFollowings() {
        
        
        String apiUrl = getApiUrl("friends/list.json");
        RequestParams params = new RequestParams();
    }



	
}
