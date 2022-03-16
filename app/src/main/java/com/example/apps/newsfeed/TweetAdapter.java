package com.example.apps.newsfeed;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.apps.newsfeed.models.Tweet;
import com.example.apps.newsfeed.models.TweetText;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
    static private List<Tweet> mTweets;
    private Context context;
    public long max_id = Long.MAX_VALUE; 
    
    private static TwitterClient client; 

    
    public TweetAdapter(List<Tweet> tweets) {
        mTweets = tweets;
    }
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        client = TwitterApp.getRestClient(); 

        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        
        final Tweet tweet = mTweets.get(position);
        
        holder.tvUserName.setText(tweet.user.name);
        
        TweetText finalTweet = new TweetText(tweet.body);

        holder.tvBody.setText(Html.fromHtml(finalTweet.finalText));
        holder.tvCreatedAt.setText(tweet.createdAt);
        holder.tvScreenName.setText("@"+tweet.user.screenName);
        holder.tvRetweets.setText(String.valueOf(tweet.retweetCount)); 
        if (!tweet.tweetRetweeted) holder.ivRetweet.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_retweet_svg));
        else holder.ivRetweet.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_retweet_green_svg));
        if (!tweet.tweetLiked) holder.ivLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_clear_light_blue_svg));
        else holder.ivLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_solid_red_svg));
        holder.tvLikes.setText(String.valueOf(tweet.likeCount)); 
        if (tweet.mediaFound) {
            holder.ivTweetedImage.setVisibility(View.VISIBLE);
            Glide.with(context).load(tweet.media.urlHTTPS)
                    .bitmapTransform(new RoundedCornersTransformation(context, 20, 0))
                    .placeholder(R.drawable.ic_picture_placeholder_svg)
                    .error(R.drawable.ic_picture_placeholder_svg)
                    .into(holder.ivTweetedImage);
        } else {
            holder.ivTweetedImage.setVisibility(View.GONE);
        }
        Glide.with(context).load(tweet.user.profileImageUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, 2000, 0))
                .placeholder(R.drawable.ic_person_v1_svg)
                .error(R.drawable.ic_person_v1_svg)
                .override(2048, 2048)
                .into(holder.ivProfileImage);

        
        max_id = tweet.uid < max_id ? tweet.uid : max_id;

        
        holder.ibReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                
                Intent i = new Intent(context, ComposeActivity.class);
                i.putExtra("text", tweet.user.screenName);
                i.putExtra("reply_uid", tweet.uid);
                context.startActivity(i);
            }
        });

        
        holder.ivRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tweet.tweetRetweeted){
                    client.retweetTweet(tweet.uid, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            tweet.retweetCount += 1;
                            holder.tvRetweets.setText(Long.toString(tweet.retweetCount));
                            tweet.tweetRetweeted = true;
                            holder.ivRetweet.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_retweet_green_svg));
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(context, "Error occured while processing retweet action", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    client.unretweetTweet(tweet.uid, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            tweet.retweetCount -= 1;
                            holder.tvRetweets.setText(Long.toString(tweet.retweetCount));
                            tweet.tweetRetweeted = false;
                            holder.ivRetweet.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_retweet_svg));
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(context, "Error occured while processing unretweet action", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        
        holder.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tweet.tweetLiked){
                    client.likeTweet(tweet.uid, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            tweet.likeCount += 1;
                            holder.tvLikes.setText(Long.toString(tweet.likeCount));
                            tweet.tweetLiked = true;
                            holder.ivLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_solid_red_svg));
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(context, "Error occured while processing retweet action", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    client.unlikeTweet(tweet.uid, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            tweet.likeCount -= 1;
                            holder.tvLikes.setText(Long.toString(tweet.likeCount));
                            tweet.tweetLiked = false;
                            holder.ivLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_clear_light_blue_svg));
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(context, "Error occured while processing unretweet action", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        holder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, UserProfileActivity.class);
                i.putExtra("user_uid", tweet.uid);
                i.putExtra("screenName", tweet.user.screenName);
                i.putExtra("using_user", false);
                context.startActivity(i);
            }
        });
    }

    
    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView ivProfileImage;
        public TextView tvUserName, tvBody, tvCreatedAt, tvScreenName;
        public TextView tvRetweets, tvLikes;
        public ImageView ibReply, ivRetweet, ivLike;
        public ImageView ivTweetedImage;

        public ViewHolder (View itemView) {
            super(itemView);

             
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBodyD);
            tvCreatedAt = (TextView) itemView.findViewById(R.id.tvCreatedAtD);
            tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
            ibReply = (ImageView) itemView.findViewById(R.id.ibReply);

            tvRetweets = (TextView) itemView.findViewById(R.id.tvRetweets);
            tvLikes = (TextView) itemView.findViewById(R.id.tvLikes);
            ivRetweet = (ImageView) itemView.findViewById(R.id.ivRetweet);
            ivLike = (ImageView) itemView.findViewById(R.id.ivLoveTweet);

            ivTweetedImage = (ImageView) itemView.findViewById(R.id.ivTweetedImage);

            tvScreenName.setOnClickListener(this);
            ivProfileImage.setOnClickListener(this);
            tvUserName.setOnClickListener(this);
            tvBody.setOnClickListener(this);
            tvCreatedAt.setOnClickListener(this);
            ibReply.setOnClickListener(this); 
            ivRetweet.setOnClickListener(this);
            ivLike.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            final Context context = v.getContext();
            int position = getAdapterPosition();

            if (v.getId() == tvUserName.getId() || v.getId() == tvBody.getId() || v.getId() == tvScreenName.getId() || v.getId() == ivTweetedImage.getId()) {
                
                Intent i = new Intent(context, TweetDetailsActivity.class);
                
                String tName, tScreenName, tBody, tCreatedAt, tProfileImageUrl, tLikes, tRetweets;

                
                tName = mTweets.get(getAdapterPosition()).user.name;
                tScreenName = mTweets.get(getAdapterPosition()).user.screenName;
                tBody =  mTweets.get(getAdapterPosition()).body;
                tCreatedAt = mTweets.get(getAdapterPosition()).createdAt;
                tProfileImageUrl = mTweets.get(getAdapterPosition()).user.profileImageUrl;
                tLikes = String.valueOf(mTweets.get(getAdapterPosition()).likeCount);
                tRetweets = String.valueOf(mTweets.get(getAdapterPosition()).retweetCount);

                
                i.putExtra("tName", tName);
                i.putExtra("tScreenName", tScreenName);
                i.putExtra("tBody", tBody);
                i.putExtra("tCreatedAt", tCreatedAt);
                i.putExtra("tProfileImageUrl", tProfileImageUrl);
                i.putExtra("tLikes", Long.valueOf(tLikes));
                i.putExtra("tRetweets", Long.valueOf(tRetweets));
                i.putExtra("tUid", mTweets.get(getAdapterPosition()).uid);
                i.putExtra("tRetweeted", mTweets.get(getAdapterPosition()).tweetRetweeted);
                i.putExtra("tLiked", mTweets.get(getAdapterPosition()).tweetLiked);
                i.putExtra("tPosition", getAdapterPosition());
                
                if (mTweets.get(getAdapterPosition()).mediaFound){
                    i.putExtra("tMediaFound", mTweets.get(getAdapterPosition()).mediaFound);
                    i.putExtra("tMediaUrl", mTweets.get(getAdapterPosition()).media.url);
                    i.putExtra("tMediaUrlHTTPS", mTweets.get(getAdapterPosition()).media.urlHTTPS);
                } else {
                    i.putExtra("tMediaFound", (Boolean) false);
                    i.putExtra("tMediaUrl", (String) null);
                    i.putExtra("tMediaUrlHTTPS", (String) null);
                }

                
                context.startActivity(i);
            } else {
                Toast.makeText(v.getContext(), "ITEM CLICKED NOT IMPLEMENTED AT POSITION " + String.valueOf(getAdapterPosition()), Toast.LENGTH_LONG).show();
            }
        }
    }

    
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    
    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }
}
