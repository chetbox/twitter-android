package com.chetbox.twitter.android;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.Timeline;
import com.twitter.sdk.android.tweetui.TimelineResult;

import twitter4j.Paging;

public class HomeTimeline implements Timeline<Tweet> {

    @Override
    public void next(Long since, Callback<TimelineResult<Tweet>> callback) {
        Paging paging = (since == null) ? new Paging() : new Paging(since);
        new FetchTweetsTask(paging, callback).execute();
    }

    @Override
    public void previous(Long maxId, Callback<TimelineResult<Tweet>> callback) {
        Paging paging = (maxId == null) ? new Paging() : new Paging(maxId - 1L);
        new FetchTweetsTask(paging, callback).execute();
    }

}
