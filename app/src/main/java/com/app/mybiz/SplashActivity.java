package com.app.mybiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.app.mybiz.Objects.Category;

import org.jetbrains.annotations.NotNull;


public class SplashActivity extends AppCompatActivity {
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setStatusBarColor(Color.parseColor("#e5e5e5"));
        FirebaseDatabase.getInstance().getReference().child("Categories")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        Log.d("TAG", "onDataChange: ");
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            Log.d("TAG", "onDataChange: "+dataSnapshot);
                            for (DataSnapshot s : snap.getChildren()) {
                                Category category = s.getValue(Category.class);
                                if (!UpdatesFromServer.categoryList.contains(category.getTitle()))
                                    UpdatesFromServer.categoryList.add(category.getTitle());
                            }
                        }

                        if (signedIn()) {
                            i = new Intent(SplashActivity.this, TabsActivity.class);
                            startActivity(i);
                            finish();

                        } else {
                            i = new Intent(SplashActivity.this, CreateAccountChoiceActivity.class);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(3000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    startActivity(i);
                                    finish();

                                }
                            }).start();

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private boolean signedIn() {
        String muid = null;
        if (FirebaseAuth.getInstance().getCurrentUser() != null && !FirebaseAuth.getInstance().getCurrentUser().isAnonymous())
            muid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        SharedPreferences prefs = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE);
        String uid = prefs.getString(Constants.APP_ID, Constants.RANDOM_STRING);
        if (uid.equals(Constants.RANDOM_STRING)) {
            if (muid != null) {
                prefs.edit().putString(Constants.APP_ID, muid).apply();
                return true;
            }
            return false;
        }
        return true;
    }


}
