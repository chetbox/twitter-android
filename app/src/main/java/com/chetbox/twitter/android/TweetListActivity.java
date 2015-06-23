package com.chetbox.twitter.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.tweetui.TimelineResult;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;


public class TweetListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ListView tweetList;
    private SwipeRefreshLayout swipeRefresh;
    private TweetTimelineListAdapter timelineAdapter;
    private Button composeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_list);

        tweetList = (ListView) findViewById(android.R.id.list);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        composeButton = (Button) findViewById(R.id.compose);

        String username = Twitter.getSessionManager().getActiveSession().getUserName();

        setTitle(username);

        UserTimeline userTimeline = new UserTimeline.Builder()
                .screenName(username)
                .build();
        timelineAdapter = new TweetTimelineListAdapter(this, userTimeline);
        tweetList.setAdapter(timelineAdapter);

        swipeRefresh.setOnRefreshListener(this);

        composeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TweetComposer.Builder(TweetListActivity.this).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tweet_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            Twitter.getSessionManager().clearActiveSession();

            Intent launchAppIntent = new Intent(this, LaunchActivity.class);
            startActivity(launchAppIntent);

            finish();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        swipeRefresh.setRefreshing(true);
        timelineAdapter.refresh(new Callback<TimelineResult<Tweet>>() {
            @Override
            public void success(Result result) {
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void failure(TwitterException e) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(TweetListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
