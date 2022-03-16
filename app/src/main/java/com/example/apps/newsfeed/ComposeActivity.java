package com.example.apps.newsfeed;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.apps.newsfeed.models.Tweet;
import com.example.apps.newsfeed.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ComposeActivity extends AppCompatActivity {
    private TwitterClient client;
    

    public TextView tvCharacterCount;
    public EditText etTweet;
    public Button bTweet;
    private ProgressBar pb;
    public String text;
    public long reply_uid;

    public static final String TAG = "ComposeActivity";

    
    Toolbar compose_toolbar;
    TextView compose_toolbar_title;
    ImageView compose_toolbar_image, compose_toolbar_cancel_button;
    User using_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        
        client = TwitterApp.getRestClient();

        
        tvCharacterCount = (TextView) findViewById(R.id.tvCharacterCount);
        etTweet = (EditText) findViewById(R.id.etTweet);
        bTweet = (Button) findViewById(R.id.bTweet);
        pb = (ProgressBar) findViewById(R.id.pbLoading);


        
        text = getIntent().getStringExtra("text");
        
        compose_toolbar = (Toolbar) findViewById(R.id.compose_toolbar);
        setSupportActionBar(compose_toolbar);
        compose_toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        getSupportActionBar().setTitle(""); 
        compose_toolbar_title = (TextView) findViewById(R.id.compose_toolbar_title);
        compose_toolbar_cancel_button = (ImageView) findViewById(R.id.compose_toolbar_cancel_button);
        compose_toolbar_image = (ImageView) findViewById(R.id.compose_toolbar_image);
        
        compose_toolbar_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getUsingUser();

        if (text != null){
            
            
            etTweet.setText("@"+text+" ");
            
            etTweet.setSelection(etTweet.getText().length());
            reply_uid = getIntent().getLongExtra("reply_uid", 0);
            
            compose_toolbar_title.setText("In reply to "+ text);
        }



        etTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = 140 - s.length();
                tvCharacterCount.setText(Integer.toString(length));
                

                int redLight = ContextCompat.getColor(getApplicationContext(), R.color.colorRedLight);
                int redDark = ContextCompat.getColor(getApplicationContext(), R.color.colorRed);
                int blueLight = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDarkLight);
                int whiteColor = ContextCompat.getColor(getApplicationContext(), R.color.colorWhite);

                
                if (length < 20) {
                    tvCharacterCount.setTextColor(redLight);
                    if (length < 0) {
                        bTweet.setEnabled(false);
                        bTweet.setTextColor(redLight);
                        bTweet.setBackgroundColor(redDark); 
                    } else {
                        bTweet.setEnabled(true);
                        bTweet.setTextColor(whiteColor);
                        bTweet.setBackgroundColor(blueLight); 
                    }
                } else {
                    tvCharacterCount.setTextColor(whiteColor);
                }
            }
        });
    }

    public void onSubmit(View v) {
        pb.setVisibility(ProgressBar.VISIBLE); 
        EditText tweet = (EditText) findViewById(R.id.etTweet);
        


        client.sendTweet(tweet.getText().toString(), reply_uid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                

                Tweet tweet;
                
                try {
                    Intent data = new Intent();
                    tweet = Tweet.fromJSON(response);

                    
                    data.putExtra("tweet", Parcels.wrap(tweet));
                    if (text != null) {
                        
                        Intent timelineIntent = new Intent(ComposeActivity.this, TimelineActivity.class);
                        startActivity(timelineIntent);
                    } else {
                        setResult(RESULT_OK, data);
                        finish();
                    }
                } catch (JSONException e) {
                    
                    
                    e.printStackTrace();
                }

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                 super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                
                Log.e(client.TAG, "ERRR ! !" + errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                
                Log.e(client.TAG, "ERRR ( (" + errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                
                Log.e(client.TAG, "ERRR < <" + responseString.toString());
                throwable.printStackTrace();
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
                            .into(compose_toolbar_image);
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
}
