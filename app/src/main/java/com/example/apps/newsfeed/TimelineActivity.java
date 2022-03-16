package com.example.apps.newsfeed;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.apps.newsfeed.fragments.HomeTimelineFragment;
import com.example.apps.newsfeed.fragments.TweetsPagerAdapter;
import com.example.apps.newsfeed.models.Tweet;
import com.example.apps.newsfeed.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TimelineActivity extends AppCompatActivity {

    
    Toolbar home_toolbar;
    TextView home_toolbar_title;
    ImageView home_toolbar_image;
    User using_user;

    
    private TwitterClient client;
    public ViewPager vpPager;
    public TweetsPagerAdapter pagerAdapter;

    private final int REQUEST_CODE = 200;

    public FloatingActionButton fabCompose;


    private ProgressBar pb; 

    public static final String TAG = "TimelineActivityTAG";

    
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        
        client = TwitterApp.getRestClient();

        
        home_toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(home_toolbar);
        home_toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        getSupportActionBar().setTitle(""); 
        home_toolbar_title = (TextView) findViewById(R.id.home_toolbar_title);
        home_toolbar_image = (ImageView) findViewById(R.id.home_toolbar_image);
        getUsingUser();
        
        home_toolbar_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onProfileView();
            }
        });

        
        pb = (ProgressBar) findViewById(R.id.pbLoading);
        pb.setVisibility(ProgressBar.INVISIBLE); 
        fabCompose = (FloatingActionButton) findViewById(R.id.fabCompose);

        
        vpPager = (ViewPager) findViewById(R.id.viewpager);
        
        pagerAdapter = new TweetsPagerAdapter(getSupportFragmentManager(), this);
        vpPager.setAdapter(pagerAdapter);
        
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(vpPager);
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getBaseContext(), R.color.colorAccent));
        tabLayout.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
        tabLayout.setTabTextColors(ColorStateList.valueOf(ContextCompat.getColor(getBaseContext(), R.color.colorWhite)));

        fabCompose.setOnClickListener(new View.OnClickListener() {
        @Override
            public void onClick(View v) {
                composeMessage(null);
            }
        });
    }

    public void getUsingUser(){
        client.getUsingUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    using_user = User.fromJSON(response);
                    
                    Glide.with(getBaseContext()).load(using_user.profileImageUrl)
                            .bitmapTransform(new RoundedCornersTransformation(getBaseContext(), 2000, 0))
                            .placeholder(R.drawable.ic_person_v1_svg)
                            .error(R.drawable.ic_person_v1_svg)
                            .override(2048, 2048)
                            .into(home_toolbar_image);
                } catch (JSONException e) {
                    Toast.makeText(getBaseContext(), String.format("Error occurred."), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG +" : "+ client.TAG, String.format("Error JSONObject: %s" , errorResponse.toString()));
                throwable.printStackTrace();

                Toast.makeText(getBaseContext(), String.format("An error occurred."), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e(TAG +" : "+ client.TAG, String.format("Error JSONArray: %s" , errorResponse.toString()));
                throwable.printStackTrace();

                Toast.makeText(getBaseContext(), String.format("An error occurred."), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG +" : "+ client.TAG, String.format("Error String: %s" , responseString));
                throwable.printStackTrace();

                Toast.makeText(getBaseContext(), String.format("An error occurred."), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onProfileView() {
        
        Intent i = new Intent(TimelineActivity.this, UserProfileActivity.class);
        i.putExtra("user_uid", using_user.uid);
        i.putExtra("screenName", using_user.screenName);
        i.putExtra("using_user", true);
        startActivity(i);
    }

    public void composeMessage(String text) {
        Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
        i.putExtra("text", text);
        startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        
        Log.d(TAG, String.format("%s %s %s RESULT_OK: %s", "TIMELINE START", resultCode, requestCode, RESULT_OK));
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            
            ((HomeTimelineFragment) pagerAdapter.getItem(vpPager.getCurrentItem())).addTweet(tweet);
            
            pb.setVisibility(ProgressBar.INVISIBLE); 
        }
    }

    private void hideViews() {
        
        home_toolbar.animate().translationY(-home_toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
    }

    private void showViews() {
        
        home_toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }
    

    

    
}
