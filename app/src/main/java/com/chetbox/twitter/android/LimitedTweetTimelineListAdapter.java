package com.chetbox.twitter.android;

import android.content.Context;

import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.Timeline;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;

public class LimitedTweetTimelineListAdapter extends TweetTimelineListAdapter {

    private final int mMaxTweets;

    public LimitedTweetTimelineListAdapter(Context context, Timeline<Tweet> timeline, int maxTweets) {
        super(context, timeline);
        mMaxTweets = maxTweets;
    }

    @Override
    public int getCount() {
        return Math.min(20, super.getCount());
    }
}
