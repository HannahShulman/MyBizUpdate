package com.app.mybiz;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.app.mybiz.Objects.User;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfile extends AppCompatActivity {
    String myId, myName, myEmail, myProfile;
    de.hdodenhof.circleimageview.CircleImageView myProfilePicture;
    ImageView camera, gallery;
    DatabaseReference myProfileRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/AllUsers/PublicData/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        myProfilePicture = (CircleImageView) findViewById(R.id.private_profile);
        camera = (ImageView) findViewById(R.id.camera);
        gallery = (ImageView) findViewById(R.id.gallery);
        SharedPreferences preferences = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE);
        myId = preferences.getString(Constants.APP_ID, Constants.RANDOM_STRING);
        myName = preferences.getString(Constants.NAME, Constants.RANDOM_STRING);
        myEmail = preferences.getString(Constants.EMAIL, Constants.RANDOM_STRING);


        if(!myId.equals(Constants.RANDOM_STRING)){
            myProfileRef.child(myId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User myProfileInfo = dataSnapshot.getValue(User.class);
                    myProfile = myProfileInfo.getProfileUrl();
                    Glide.with(MyProfile.this).load(myProfile).into(myProfilePicture);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
