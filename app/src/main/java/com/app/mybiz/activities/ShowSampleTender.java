package com.app.mybiz.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.mybiz.objects.Tenders;
import com.app.mybiz.PreferenceKeys;
import com.app.mybiz.R;
import com.bumptech.glide.Glide;

import java.util.concurrent.TimeUnit;

public class ShowSampleTender extends AppCompatActivity {
    Tenders sampleTender;
    ImageView profileImage;
    TextView name, town, expiryTimeLeft, requestDescription, cancelSuggestTv;
    LinearLayout cancelLayout;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_sample_tender);
        sampleTender = (Tenders) getIntent().getSerializableExtra("sampleTender");
        initViews();
        if (sampleTender != null)
            setInfo();
    }

    private void initViews() {
        profileImage = (ImageView) findViewById(R.id.profile);
        name = (TextView) findViewById(R.id.name);
        town = (TextView) findViewById(R.id.town);
        expiryTimeLeft = (TextView) findViewById(R.id.expiry_time_left);
        requestDescription = (TextView) findViewById(R.id.request_description);
        cancelSuggestTv = (TextView) findViewById(R.id.cancel_suggest_tv);
        cancelLayout = (LinearLayout) findViewById(R.id.cancel_layout);
        cancelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ShowSampleTender.this, IntroTendersActivity.class);
                startActivity(i);
                finish();
            }
        });
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.sample_tender));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(R.drawable.right_arrow_w);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setInfo() {
        Glide.with(getBaseContext()).load(sampleTender.getProfileUrl()).into(profileImage);
        name.setText(getSharedPreferences(PreferenceKeys.PREFERENCES, MODE_PRIVATE).getString(PreferenceKeys.NAME, ""));
        town.setText(sampleTender.getTown());
        requestDescription.setText(sampleTender.getRequest());

        long timeLeft = sampleTender.getExpiryTime() - System.currentTimeMillis();
        int days = (int) (timeLeft / (1000 * 60 * 60 * 24));
        int hours = (int) TimeUnit.HOURS.toHours(timeLeft - (days * (1000 * 60 * 60 * 24)));
        if (days >= 1)
            expiryTimeLeft.setText((days + 1) + " " + getString(R.string.days));
        else
            expiryTimeLeft.setText(1 + "");
    }
}
