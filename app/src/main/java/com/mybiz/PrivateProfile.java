package com.mybiz;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mybiz.Objects.User;

import de.hdodenhof.circleimageview.CircleImageView;

public class PrivateProfile extends AppCompatActivity {

    Intent intent;
    User user;
    de.hdodenhof.circleimageview.CircleImageView imageProfile;
    TextView privateName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_profile);
        imageProfile = (CircleImageView) findViewById(R.id.private_profile);
        privateName = (TextView) findViewById(R.id.private_name);

        intent = getIntent();
        String id = intent.getStringExtra("otherId");

        FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/AllUsers/PublicData/"+id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                privateName.setText(user.getmName());
                Glide.with(getBaseContext()).load(user.getProfileUrl()).into(imageProfile);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
