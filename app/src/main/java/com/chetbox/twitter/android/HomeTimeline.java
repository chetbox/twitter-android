package com.chetbox.twitter.android;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Identifiable;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.tweetui.Timeline;
import com.twitter.sdk.android.tweetui.TimelineCursor;
import com.twitter.sdk.android.tweetui.TimelineResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import retrofit.client.Header;
import retrofit.client.Response;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class HomeTimeline implements Timeline<Tweet> {

    private static final SimpleDateFormat DATE_TIME_RFC822 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);

    private final twitter4j.Twitter mTwitter;
    private final Context mContext;

    public HomeTimeline(Context context) {
        mContext = context;

        TwitterSession session = Twitter.getSessionManager().getActiveSession();

        Configuration twitterConfig = new ConfigurationBuilder().setDebugEnabled(true)
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
                    tweets.add(toFabricTweet(tw4jTweet));
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

    private static Tweet toFabricTweet(Status tweet) {
        if (tweet == null) {
            return null;
        }
        return new Tweet(
                null, // TODO: coordinates
                DATE_TIME_RFC822.format(tweet.getCreatedAt()),
                null, // TODO: currentUserRetweet
                null, // TODO: entities
                tweet.getFavoriteCount(),
                tweet.isFavorited(),
                null, // TODO: filterLevel
                tweet.getId(),
                Long.toString(tweet.getId()),
                tweet.getInReplyToScreenName(),
                tweet.getInReplyToStatusId(),
                Long.toString(tweet.getInReplyToStatusId()),
                tweet.getInReplyToUserId(),
                Long.toString(tweet.getInReplyToUserId()),
                tweet.getLang(),
                null, // TODO: place
                tweet.isPossiblySensitive(),
                null, // TODO: scopes
                tweet.getRetweetCount(),
                tweet.isRetweeted(),
                toFabricTweet(tweet.getRetweetedStatus()),
                tweet.getSource(),
                tweet.getText(),
                tweet.isTruncated(),
                toFabricUser(tweet.getUser()),
                false, // TODO: withheldCopyright
                null, // TODO: witheldInCountries,
                null); // TODO: witheldScope
    }

    private static User toFabricUser(twitter4j.User user) {
        if (user == null) {
            return null;
        }
        return new User(
                user.isContributorsEnabled(),
                DATE_TIME_RFC822.format(user.getCreatedAt()),
                user.isDefaultProfile(),
                user.isDefaultProfileImage(),
                user.getDescription(),
                null, // TODO: emailAddress?
                null, // TODO: entities
                user.getFavouritesCount(),
                user.isFollowRequestSent(),
                user.getFollowersCount(),
                user.getFriendsCount(),
                user.isGeoEnabled(),
                user.getId(),
                Long.toString(user.getId()),
                user.isTranslator(),
                user.getLang(),
                user.getListedCount(),
                user.getLocation(),
                user.getName(),
                user.getProfileBackgroundColor(),
                user.getProfileBackgroundImageURL(),
                user.getProfileBackgroundImageUrlHttps(),
                user.isProfileBackgroundTiled(),
                user.getProfileBannerURL(),
                user.getProfileImageURL(),
                user.getProfileImageURLHttps(),
                user.getProfileLinkColor(),
                user.getProfileSidebarBorderColor(),
                user.getProfileSidebarFillColor(),
                user.getProfileTextColor(),
                user.isProfileUseBackgroundImage(),
                user.isProtected(),
                user.getScreenName(),
                user.isShowAllInlineMedia(),
                toFabricTweet(user.getStatus()),
                user.getStatusesCount(),
                user.getTimeZone(),
                user.getURL(),
                user.getUtcOffset(),
                user.isVerified(),
                null, // TODO: witheldInCountries,
                null); // TODO: witheldScope
    }

}
