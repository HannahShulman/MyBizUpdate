package com.app.mybiz;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.app.mybiz.Activities.LogInAccount;

public class MidLoginActivity extends AppCompatActivity {
    Toolbar toolbar;
    Button privateRegBtn, serviceRegBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mid_login);
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(R.drawable.right_arrow_w);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.log_in_account);
//        getSupportActionBar().set
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        privateRegBtn = (Button) findViewById(R.id.private_reg_btn);
        serviceRegBtn = (Button) findViewById(R.id.service_reg_btn);
        privateRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MidLoginActivity.this, PersonalRegistrationActivity.class);
                startActivity(intent);
            }
        });
        serviceRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MidLoginActivity.this, LogInAccount.class);
                startActivity(intent1);
            }
        });
    }
}
