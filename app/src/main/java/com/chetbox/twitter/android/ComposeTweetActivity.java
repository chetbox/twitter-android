package com.chetbox.twitter.android;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterSession;

public class ComposeTweetActivity extends AppCompatActivity implements TextWatcher, View.OnClickListener {

    public static final String UPDATE_TEXT = "update_text";

    private TextView mUpdateText;
    private Button mUpdateButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_tweet);

        TwitterSession session = Twitter.getSessionManager().getActiveSession();
        setTitle(session.getUserName());

        mUpdateText = (TextView) findViewById(R.id.update_content);
        mUpdateText.addTextChangedListener(this);

        mUpdateButton = (Button) findViewById(R.id.update_button);
        mUpdateButton.setOnClickListener(this);

        // sets the initial enabled state of mUpdateButton
        onTextChanged(mUpdateText.getText(), 0, 0, 0);
    }

    // mUpdateButton
    @Override
    public void onClick(View v) {
        Intent updateData = new Intent();
        updateData.putExtra(UPDATE_TEXT, mUpdateText.getText().toString());
        setResult(RESULT_OK, updateData);
        finish();
    }

    // mUpdateText
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    // mUpdateText
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (isValidTweet(s) && !mUpdateButton.isEnabled()) {
            mUpdateButton.setEnabled(true);
        }
        if (!isValidTweet(s) && mUpdateButton.isEnabled()) {
            mUpdateButton.setEnabled(false);
        }
    }

    // mUpdateText
    @Override
    public void afterTextChanged(Editable s) {}


    private static boolean isValidTweet(CharSequence text) {
        return text != null
                && text.length() > 0
                && text.length() <= 140;
    }
}
