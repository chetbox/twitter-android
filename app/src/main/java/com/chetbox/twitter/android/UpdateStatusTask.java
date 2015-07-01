package com.chetbox.twitter.android;

import android.os.AsyncTask;

import twitter4j.TwitterException;

import static com.chetbox.twitter.android.Twitter4JWrapper.twitter4j;

public class UpdateStatusTask extends AsyncTask<Void, Void, Void> {

    private final String mText;

    public UpdateStatusTask(String text) {
        mText = text;
    }

    @Override
    protected Void doInBackground(Void... _) {
        try {
            twitter4j().updateStatus(mText);
        } catch (TwitterException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

}
