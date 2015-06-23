package com.chetbox.twitter.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterSession;

public class LaunchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Class<? extends Activity> activity = isUserLoggedIn()
            ? TweetListActivity.class
            : LoginActivity.class;

        Intent newActivity = new Intent(this, activity);
        newActivity.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        startActivity(newActivity);

        finish();
    }

    private static boolean isUserLoggedIn() {
        TwitterSession session = Twitter.getSessionManager().getActiveSession();
        return session != null;
    }

}
