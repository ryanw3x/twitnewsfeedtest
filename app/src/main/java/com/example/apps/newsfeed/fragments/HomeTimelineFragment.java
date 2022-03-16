package com.example.apps.newsfeed.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.apps.newsfeed.R;
import com.example.apps.newsfeed.TwitterApp;
import com.example.apps.newsfeed.TwitterClient;
import com.example.apps.newsfeed.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class HomeTimelineFragment extends TweetsListFragment {
    private TwitterClient client;
    int totalTweets = 10;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = TwitterApp.getRestClient();
        populateTimeline(totalTweets);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        
        View v = inflater.inflate(R.layout.fragments_tweets_list, container, false);
        rvTweets = (RecyclerView) v.findViewById(R.id.rvTweet);

        
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainerRefresher);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                
                 reinitializeTweetsAndAdapter();
                 populateTimeline(getTweetSize());
                swipeContainer.setRefreshing(false);
            }
        });
        swipeContainer.setColorSchemeResources(R.color.colorPrimaryDarkLight, R.color.colorLightGrey, R.color.colorAccent, R.color.colorPrimaryLight);

        
        rvTweets.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    int lastPosition = layoutManager.findLastVisibleItemPosition();
                    if (lastPosition > tweets.size() - 2) {
                        addToTimeline();
                    }
                }
            }
        });

        reinitializeTweetsAndAdapter();
        return v;

    }

    public void populateTimeline(int count) {
        
        
        

        
        client.getHomeTimeline(count, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                
                Log.d(client.TAG, response.toString());
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                
                Log.d(TAG, String.format("PopulateTimeline | RESPONSE LENGTH %s SUCCESS at %s", response.length(),  HMS.format(CAL.getTime())));
                addItems(response, true); 
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(client.TAG, String.format("REGULAR FAILURE: %s" , responseString));
                throwable.printStackTrace();

                Toast.makeText(getContext(), String.format("An error occurred while acquiring the data. Please try again in 2 minutes."), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(client.TAG, String.format("JSON OBJECT FAILURE: %s" ,errorResponse.toString()));
                throwable.printStackTrace();

                Toast.makeText(getContext(), String.format("An error occurred while acquiring the data. Please try again in 2 minutes."), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e(client.TAG, String.format("JSON ARRAY FAILURE: %s" ,errorResponse.toString()));
                throwable.printStackTrace();

                Toast.makeText(getContext(), String.format("An error occurred while acquiring the data. Please try again in 2 minutes."), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addToTimeline() {
        Log.d(TAG, String.format("addToTimeline at %s", HMS.format(CAL.getTime())));

        client.addToTimeline(max_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                addItems(response, true); 
                totalTweets = getTweetSize(); 
                Log.d(TAG, String.format("tweet size: %s, and maxid: %s, PopulateTimeline Home at %s", totalTweets, max_id, HMS.format(CAL.getTime())));
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(concatTag(TAG, client.TAG), response.toString());
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(concatTag(TAG, client.TAG), errorResponse.toString() + "Damn 1");
                throwable.printStackTrace();

                Toast.makeText(getContext(), String.format("An error occurred. Unable to load more tweets."), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e(concatTag(TAG, client.TAG), errorResponse.toString() + "Damn 2");
                throwable.printStackTrace();

                Toast.makeText(getContext(), String.format("An error occurred. Unable to load more tweets."), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(concatTag(TAG, client.TAG), responseString + "Damn 3");
                throwable.printStackTrace();

                Toast.makeText(getContext(), String.format("An error occurred. Unable to load more tweets."), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addTweet(Tweet tweet){
        addItemOne(tweet, 0);
    }
}
