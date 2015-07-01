package com.chetbox.twitter.android;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.Timeline;
import com.twitter.sdk.android.tweetui.TimelineCursor;
import com.twitter.sdk.android.tweetui.TimelineResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit.client.Header;
import retrofit.client.Response;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.TwitterFactory;
import twitter4j.TwitterObjectFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class HomeTimeline implements Timeline<Tweet> {

    private final twitter4j.Twitter mTwitter;
    private final Gson mGson = new Gson();

    public HomeTimeline() {
        TwitterSession session = Twitter.getSessionManager().getActiveSession();

        Configuration twitterConfig = new ConfigurationBuilder()
                .setDebugEnabled(true)
                .setJSONStoreEnabled(true)
                .setOAuthConsumerKey(TwitterApplication.TWITTER_KEY)
                .setOAuthConsumerSecret(TwitterApplication.TWITTER_SECRET)
                .setOAuthAccessToken(session.getAuthToken().token)
                .setOAuthAccessTokenSecret(session.getAuthToken().secret)
                .build();

        mTwitter = new TwitterFactory(twitterConfig).getInstance();
    }

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

    private class FetchTweetsTask extends AsyncTask<Void, Void, List<Tweet>> {

        private final Paging mPaging;
        private final Callback<TimelineResult<Tweet>> mCallback;

        public FetchTweetsTask(Paging paging, Callback<TimelineResult<Tweet>> callback) {
            mPaging = paging;
            mCallback = callback;
        }

        @Override
        protected List<Tweet> doInBackground(Void... _) {
            try {
                List<twitter4j.Status> tw4jTweets = mTwitter.getHomeTimeline(mPaging);
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
                    dummyResponse()));
        }
    }

    private static Response dummyResponse() {
        return new Response("https://twitter.com", 200, "OK", new ArrayList<Header>(), null);
    }

    private Tweet toTwitterKitTweet(Status tweet) {
        if (tweet == null) {
            return null;
        }

        // re-parse as a Twitter Kit object
        String tweetJson = TwitterObjectFactory.getRawJSON(tweet);
        return mGson.fromJson(tweetJson, Tweet.class);
    }

}
