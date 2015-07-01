package com.chetbox.twitter.android;

import com.twitter.sdk.android.core.TwitterSession;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class Twitter4JWrapper {

    private static Twitter sTwitter;

    public static Twitter twitter4j() {
        if (sTwitter == null) {
            TwitterSession session = com.twitter.sdk.android.Twitter.getSessionManager().getActiveSession();

            Configuration twitterConfig = new ConfigurationBuilder()
                    .setDebugEnabled(true)
                    .setJSONStoreEnabled(true)
                    .setOAuthConsumerKey(TwitterApplication.TWITTER_KEY)
                    .setOAuthConsumerSecret(TwitterApplication.TWITTER_SECRET)
                    .setOAuthAccessToken(session.getAuthToken().token)
                    .setOAuthAccessTokenSecret(session.getAuthToken().secret)
                    .build();

            sTwitter = new TwitterFactory(twitterConfig).getInstance();
        }
        return sTwitter;
    }

}
