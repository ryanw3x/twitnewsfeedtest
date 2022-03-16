package com.example.apps.newsfeed;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.codepath.oauth.OAuthLoginActionBarActivity;

public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient> {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}


	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	
	
	@Override
	public void onLoginSuccess() {
		 Intent timeline = new Intent(this, TimelineActivity.class);
		 startActivity(timeline);
	}

	
	
	@Override
	public void onLoginFailure(Exception e) {
		e.printStackTrace();
	}

	
	
	
	public void loginToRest(View view) {
		getClient().connect();
	}

}
