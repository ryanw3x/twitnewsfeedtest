package com.example.apps.newsfeed.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.apps.newsfeed.TweetAdapter;
import com.example.apps.newsfeed.models.Tweet;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class TweetsListFragment extends Fragment {

    public final int REQUEST_CODE = 200;
    public static final String TAG = "TweetsListFragment";
    public long max_id = Long.MAX_VALUE; 

    
    public SwipeRefreshLayout swipeContainer;

    
    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    public RecyclerView rvTweets;

    private HomeTimelineFragment timelineFragment;
    private MentionsTimelineFragment mentionFragment;

    
    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

    
    Calendar CAL = Calendar.getInstance();
    SimpleDateFormat HMS = new SimpleDateFormat("HH:mm:ss");

    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        
        View v = inflater.inflate(com.example.apps.newsfeed.R.layout.fragments_tweets_list, container, false);
        rvTweets = (RecyclerView) v.findViewById(com.example.apps.newsfeed.R.id.rvTweet);

        
        swipeContainer = (SwipeRefreshLayout) v.findViewById(com.example.apps.newsfeed.R.id.swipeContainerRefresher);
        swipeContainer.setColorSchemeResources(com.example.apps.newsfeed.R.color.colorPrimaryDarkLight, com.example.apps.newsfeed.R.color.colorLightGrey, com.example.apps.newsfeed.R.color.colorAccent, com.example.apps.newsfeed.R.color.colorPrimaryLight);

        reinitializeTweetsAndAdapter();
        return v;
    }

    public void reinitializeTweetsAndAdapter(){
        tweets = new ArrayList<>(); 
        tweetAdapter = new TweetAdapter(tweets); 
        
        rvTweets.setLayoutManager(layoutManager); 
        rvTweets.setAdapter(tweetAdapter); 

        Log.d(TAG, String.format("Tweets reinitialized at %s", HMS.format(CAL.getTime())));
    }

    public void addItems(JSONArray response, boolean following){
        for (int i = 0; i < response.length(); i++){
            
            Tweet tweet;
            try {
                tweet = Tweet.fromJSON(response.getJSONObject(i));
                tweet.user.following = following;
                
                
                tweets.add(tweet);
                
                tweetAdapter.notifyItemInserted(tweets.size() - 1);
                

                
                max_id = tweet.uid < max_id ? tweet.uid : max_id;
                Log.d(TAG, String.format("PopulateTimeline | Tweet at index %s SUCCESS at %s", i,  HMS.format(CAL.getTime())));
            } catch (JSONException e) {
                Log.d(TAG, String.format("PopulateTimeline | Tweet at index %s FAILED at %s", i,  HMS.format(CAL.getTime())));
                e.printStackTrace();
            }

        }
        Log.d(TAG, String.format("PopulateTimeline SUCCESS at %s", HMS.format(CAL.getTime())));
    }

    public void addItemOne(Tweet tweet, int position) {
        tweets.add(position, tweet);
        tweetAdapter.notifyItemInserted(position);
        
        rvTweets.scrollToPosition(position);
    }

    public long getMaxId() {
        return max_id - 1;
    }
    public int getTweetSize() {
        return tweets.size();
    }

    public String concatTag(String TAG1, String TAG2) {
        return TAG1 + ": " + TAG2;
    }

    private HomeTimelineFragment getTimelineFragment() {
        if (timelineFragment == null) {
            timelineFragment = new HomeTimelineFragment();
        }
        return timelineFragment;
    }

    private MentionsTimelineFragment getMentionFragment() {
        if (timelineFragment == null) {
            mentionFragment = new MentionsTimelineFragment();
        }
        return mentionFragment;
    }
}
