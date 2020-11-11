//package com.app.mybiz;
//
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Bundle;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import android.util.Log;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.TextView;
//import com.bumptech.glide.Glide;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.ChildEventListener;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.app.mybiz.Activities.AllServiceInfo;
//import com.app.mybiz.Objects.Comment;
//import com.app.mybiz.Objects.Service;
//import com.app.mybiz.views.MyBizzRatingBar;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//
//import de.hdodenhof.circleimageview.CircleImageView;
///*
//this class enables the user to add a comment or edit a comment that has already been made by him
// */
//
//
//
//
//public class AddComment extends AppCompatActivity {
//    //comment from shimon
//    String TAG = "tag";
//    Toolbar toolbar;
//    Service service;
//    DatabaseReference serviceRef;
//    MyBizzRatingBar ratingBar;
//    EditText myComment;
//    TextView voteName, vote, voteTitle;
//    CircleImageView profileImage;
//    int prevRating;
//    ChildEventListener commentsUpdate;
//    DatabaseReference mServiceRootRef = FirebaseDatabase.getInstance().getReference().child("Services");
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_comment);
//        service = (Service)getIntent().getSerializableExtra("currentService");
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        toolbar.setTitleTextAppearance(this, R.style.MyTitleTextApperance);
//        setSupportActionBar(toolbar);
//        voteTitle = (TextView) findViewById(R.id.vote_title);
//        voteTitle.setText(service.getTitle());
//        toolbar.setTitle(service.getTitle());
//        toolbar.setNavigationIcon(R.drawable.right_arrow_w);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
//        profileImage = (CircleImageView) findViewById(R.id.circleImageView);
//        //// TODO: 24/03/2017 get profile from internal memory
////        Glide.with(getBaseContext()).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(profileImage);
//        FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PublicData").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("profileUrl").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String s = dataSnapshot.getValue(String.class);
//                Glide.with(getBaseContext()).load(s).into(profileImage);
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        serviceRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/Services/PublicData/"+service.getKey());
//        myComment = (EditText) findViewById(R.id.my_comment);
//        myComment.setHint(getResources().getString(R.string.describe_experience)+" "+service.getTitle());
//        myComment.setText(getIntent().getStringExtra("myReview"));
//        voteName = (TextView) findViewById(R.id.vote_name);
//        voteName.setText(getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.NAME, Constants.RANDOM_STRING));
//        ratingBar = (MyBizzRatingBar) findViewById(R.id.ratingBar);
//        ratingBar.setRating(1);
//        ratingBar.setRating(getIntent().getIntExtra("ratingAmount", 0)-1);
//        if (getIntent().getBooleanExtra("isEdit", false)){
//            prevRating = getIntent().getIntExtra("ratingAmount", 0);
//        }
//
//        vote = (TextView) findViewById(R.id.vote);
//
//        vote.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //add comment to service
//                FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PrivateData").child(getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.APP_ID, Constants.RANDOM_STRING)).child("profileUrl").addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        String userUrl = dataSnapshot.getValue(String.class);
//                        Comment comment = new Comment();
//                        comment.setWriter(getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.NAME, Constants.RANDOM_STRING));
//                        comment.setWriterUid(getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.APP_ID, Constants.RANDOM_STRING));
//                        comment.setDate(System.currentTimeMillis());
//                        comment.setReview((int)ratingBar.getRating()+1);
//                        Log.d(TAG, "onDataChangeRating: "+ratingBar.getRating());
//                        comment.setComment(myComment.getText().toString());
//                        comment.setUrl(userUrl);
//
//                        serviceRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/Services/PublicData/"+service.getKey()+"/reviews");
//                        serviceRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(comment);
//
//                        //update no of comments and average
//                        int noOfComments = service.getNoReviewers();
//                        float averageComments = (float) 0.0;
//                        if (getIntent().getBooleanExtra("isEdit", false)){
//                            //do not add user, but update average
//                            averageComments = service.getAverageRating();
//                            Log.d(TAG, "nDataChange:newAvg_commentsChange0: "+averageComments);
//                            float newAverage = ((noOfComments*averageComments)-prevRating+(ratingBar.getRating()+1))/noOfComments;
//                            mServiceRootRef.child("PrivateData").child(service.getKey()).child("averageRating").setValue(newAverage);
//                            mServiceRootRef.child("PublicData").child(service.getKey()).child("averageRating").setValue(newAverage);
//
//                        }else{
//                            //add user
//                            averageComments = service.getAverageRating();
//                            float newAverage = ((((noOfComments)*averageComments)+(ratingBar.getRating()+1))/(noOfComments+1));
//                            mServiceRootRef.child("PrivateData").child(service.getKey()).child("averageRating").setValue(newAverage);
//                            mServiceRootRef.child("PrivateData").child(service.getKey()).child("noReviewers").setValue(noOfComments+1);
//                            mServiceRootRef.child("PublicData").child(service.getKey()).child("averageRating").setValue(newAverage);
//                            mServiceRootRef.child("PublicData").child(service.getKey()).child("noReviewers").setValue(noOfComments+1);
//
//                        }
//                        serviceRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/Services/PrivateData/"+service.getKey()+"/reviews");
//                        DatabaseReference serviceRefPublic = FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/Services/PrivateData/"+service.getKey()+"/reviews");
//                        serviceRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(comment);
//                        serviceRefPublic.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(comment);
//                        Intent intent = new Intent(AddComment.this, AllServiceInfo.class);
//                        intent.putExtra("currentService", service);
//                        Log.d(TAG, "onDataChange: "+comment.getReview());
//                        finish();
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) { }
//                });
//            }
//        });
//    }
//
//}
