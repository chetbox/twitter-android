package com.chetbox.twitter.android;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TimelineCursor;
import com.twitter.sdk.android.tweetui.TimelineResult;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Paging;
import twitter4j.TwitterObjectFactory;

import static com.chetbox.twitter.android.Twitter4JWrapper.twitter4j;

class FetchTweetsTask extends AsyncTask<Void, Void, List<Tweet>> {

    private static final Gson sGson = new Gson();

    private final Paging mPaging;
    private final Callback<TimelineResult<Tweet>> mCallback;

    public FetchTweetsTask(Paging paging, Callback<TimelineResult<Tweet>> callback) {
        mPaging = paging;
        mCallback = callback;
    }

    @Override
    protected List<Tweet> doInBackground(Void... _) {
        try {
            List<twitter4j.Status> tw4jTweets = twitter4j().getHomeTimeline(mPaging);
            List<Tweet> tweets = new ArrayList<>(tw4jTweets.size());
            for (twitter4j.Status tw4jTweet : tw4jTweets) {
                tweets.add(toTwitterKitTweet(tw4jTweet));
            }
            return tweets;
        } catch (twitter4j.TwitterException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Tweet> tweets) {
        if (tweets == null) {
            mCallback.failure(new TwitterException("Could not fetch tweets"));
            return;
        }

        Long minPosition = tweets.size() > 0 ? tweets.get(tweets.size() - 1).getId() : null;
        Long maxPosition = tweets.size() > 0 ? tweets.get(0).getId() : null;

        mCallback.success(new Result<>(
                new TimelineResult<>(new TimelineCursor(minPosition, maxPosition), tweets),
                null));
    }

    private static Tweet toTwitterKitTweet(twitter4j.Status tweet) {
        if (tweet == null) {
            return null;
        }

        // re-parse as a Twitter Kit object
        String tweetJson = TwitterObjectFactory.getRawJSON(tweet);
        return sGson.fromJson(tweetJson, Tweet.class);
    }
}

