package com.mybiz.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import androidx.annotation.RequiresApi;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.mybiz.Adapters.CommentRecyclerAdapter;
import com.mybiz.ChatActivity;
import com.mybiz.Constants;
import com.mybiz.CreateAccountChoiceActivity;
import com.mybiz.Objects.Comment;
import com.mybiz.Objects.Service;
import com.mybiz.R;
import com.mybiz.ServiceRegistrationActivityForm;
import com.mybiz.ServiceRegistrationFragmentContainer;
import com.mybiz.views.MyBizzRatingBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;


/*
this class is used in a number of situations:
    1. while service registration
    2. showing user a specific service
    3. users edited info
*/

public class AllServiceInfo extends AppCompatActivity implements View.OnClickListener {
    String TAG = "AllServiceInfo",myName, myId, homeNumber;
    Service currentService;
    ImageView serviceProfile, openHoursEdit,addressEdit, phoneNumberEdit, infoEdit, additionalInfoEdit, profileImageEdit;
    TextView openningHoursInfo, addressInfo, phoneNumberInfo, serviceInfo, serviceAdditionalInfo, totalAverage, noReviewers;
    FloatingActionButton addCommentFab;
    TextView noRatings, avrgRating;
    RatingBar allRatings;
    Toolbar myToolbar;
    DatabaseReference serviceRef, ref ;
    CollapsingToolbarLayout collapsingToolbar;
    ViewSwitcher displayMyComment;
    SharedPreferences pref;
    RecyclerView allComments;
    CommentRecyclerAdapter commentsAdapter;
    ArrayList<Comment> commentsList;
    HashMap<String, Comment> commentMap;
    LinearLayout rating_container;
    boolean isAnonymous;
    ValueEventListener serviceChangeListener;
    AppBarLayout barLayout;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_service_info_);
        pref = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE);
        isAnonymous = pref.getBoolean(Constants.IS_ANONYMOUS, false);
        barLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        barLayout.setExpanded(true);

        //set status bar to be transparent in available versions
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(Color.parseColor("#12000000"));

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setFitsSystemWindows(true);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        myToolbar.setNavigationIcon(R.drawable.right_arrow_w);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        myId = pref.getString(Constants.APP_ID, Constants.RANDOM_STRING);
        myName = pref.getString(Constants.NAME, Constants.RANDOM_STRING);
        allComments = (RecyclerView) findViewById(R.id.all_comments);
        commentsList = new ArrayList<>();
        commentMap = new HashMap<>();
        commentsAdapter = new CommentRecyclerAdapter(getBaseContext(), commentsList);
        allComments.setAdapter(commentsAdapter);
        allComments.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
        currentService = (Service) getIntent().getSerializableExtra("currentService");
        myToolbar.setTitle(currentService.getTitle());
        collapsingToolbar.setTitle(currentService.getTitle());

        //init views
        serviceProfile = (ImageView) findViewById(R.id.service_profile);
        openHoursEdit = (ImageView) findViewById(R.id.open_hours_edit);
        addressEdit = (ImageView) findViewById(R.id.address_edit);
        phoneNumberEdit = (ImageView) findViewById(R.id.phone_number_edit);
        infoEdit = (ImageView) findViewById(R.id.info_edit);
        additionalInfoEdit = (ImageView) findViewById(R.id.additional_info_edit);
        profileImageEdit = (ImageView) findViewById(R.id.profile_image_edit);
        rating_container = (LinearLayout) findViewById(R.id.rating_container);
        openningHoursInfo = (TextView) findViewById(R.id.openning_hours_info);
        addressInfo = (TextView) findViewById(R.id.address_info);
        phoneNumberInfo = (TextView) findViewById(R.id.phone_number_info);
        serviceInfo = (TextView) findViewById(R.id.service_info);
        serviceAdditionalInfo = (TextView) findViewById(R.id.service_additional_info);
        addCommentFab = (FloatingActionButton) findViewById(R.id.add_comment_fab);
        addCommentFab.setOnClickListener(this);
        displayMyComment = (ViewSwitcher) findViewById(R.id.display_my_comment);
        avrgRating = (TextView) findViewById(R.id.avrg_rating);
        allRatings = (RatingBar) findViewById(R.id.all_ratings);

        //init references
        ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/Services/PublicData/" + currentService.getKey());
        serviceRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/Services/PublicData/" + currentService.getUserUid());

        ServiceRegistrationActivityForm.newService = currentService;

        //here we set the correct view per user. if user is the owner of service (enable edit) or not (disable edit).
        if (currentService.getUserUid().equals(getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.APP_ID, Constants.RANDOM_STRING))){
            openHoursEdit.setOnClickListener(this);
            addressEdit.setOnClickListener(this);
            phoneNumberEdit.setOnClickListener(this);
            infoEdit.setOnClickListener(this);
            additionalInfoEdit.setOnClickListener(this);
            profileImageEdit.setOnClickListener(this);
            if (getIntent().getBooleanExtra("isMidReg", false)){
                openHoursEdit.setVisibility(View.GONE);
                addressEdit.setVisibility(View.GONE);
                phoneNumberEdit.setVisibility(View.GONE);
                infoEdit.setVisibility(View.GONE);
                additionalInfoEdit.setVisibility(View.GONE);
                profileImageEdit.setVisibility(View.GONE);
            }else{
                openHoursEdit.setVisibility(View.VISIBLE);
                addressEdit.setVisibility(View.VISIBLE);
                phoneNumberEdit.setVisibility(View.VISIBLE);
                infoEdit.setVisibility(View.VISIBLE);
                additionalInfoEdit.setVisibility(View.VISIBLE);
                profileImageEdit.setVisibility(View.VISIBLE);
            }
        }else{
            openHoursEdit.setVisibility(View.GONE);
            addressEdit.setVisibility(View.GONE);
            phoneNumberEdit.setVisibility(View.GONE);
            infoEdit.setVisibility(View.GONE);
            additionalInfoEdit.setVisibility(View.GONE);
//            profileImageEdit.setVisibility(View.GONE);
        }


        //if service registration his not complete,  then show image from saved file
        if (getIntent().getBooleanExtra("isMidReg", false)){
            rating_container.setVisibility(View.GONE);
            allComments.setVisibility(View.GONE);
            File f=new File(getFilesDir()+"/profile.jpg");
            Log.d(TAG, "onCreate: "+f.exists());
            Bitmap b = null;
            try {
                if (f.exists())
                b = BitmapFactory.decodeStream(new FileInputStream(f));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            serviceProfile.setImageBitmap(b);
        }else {
            if (currentService.getUserUid().equals(getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.APP_ID, Constants.RANDOM_STRING))) {
                File f=new File(getFilesDir()+"/profile.jpg");
                Log.d(TAG, "onCreate: "+f.exists());
                Bitmap b = null;
                try {
                    b = BitmapFactory.decodeStream(new FileInputStream(f));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                serviceProfile.setImageBitmap(b);

            }else{
                Glide.with(AllServiceInfo.this).load(currentService.getProfileUrl()).into(serviceProfile);
            }
        }

        //if service owner, then make option of chatting with himself false.
        if (currentService.getUserUid().equals(pref.getString(Constants.APP_ID, Constants.RANDOM_STRING)))
            addCommentFab.setVisibility(View.GONE);


        //summary of ratings
        LayerDrawable stars = (LayerDrawable) allRatings.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        allRatings.setStepSize(0.5f);
        noRatings = (TextView) findViewById(R.id.no_ratings);
        avrgRating.setText(new DecimalFormat("##.##").format(currentService.getAverageRating()));
        allRatings.setRating(currentService.getAverageRating());
        Log.d(TAG, "onCreate: noRating"+currentService.getNoReviewers());


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getIntent().getBooleanExtra("isMidReg", false)){
            Log.d(TAG, "onCreate: is anonymous");
            currentService = ServiceRegistrationActivityForm.newService;
            rating_container = (LinearLayout) findViewById(R.id.rating_container);
            rating_container.setVisibility(View.GONE);
            allComments.setVisibility(View.GONE);
            addCommentFab.setVisibility(View.GONE);

            File f=new File(getFilesDir()+"/profile.jpg");
            Log.d(TAG, "onCreate: "+f.exists());
            Bitmap b = null;
            try {
                if (f.exists())
                b = BitmapFactory.decodeStream(new FileInputStream(f));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (f.exists())
                serviceProfile.setImageBitmap(b);
            openningHoursInfo.setText(""+ServiceRegistrationActivityForm.newService.getOpeningHours());
            homeNumber = ServiceRegistrationActivityForm.newService.getServiceHomeNumber();
            if (homeNumber==null)
                homeNumber = "";
            addressInfo.setText(ServiceRegistrationActivityForm.newService.getAddress()+"  "+homeNumber+"  "+ServiceRegistrationActivityForm.newService.getTown());
            phoneNumberInfo.setText(ServiceRegistrationActivityForm.newService.getPhoneNumber());
            serviceInfo.setText(ServiceRegistrationActivityForm.newService.getShortDescription());
            serviceAdditionalInfo.setText(ServiceRegistrationActivityForm.newService.getAdditionalInfo());
        }else {
            Glide.with(AllServiceInfo.this).load(currentService.getProfileUrl()).into(serviceProfile);

        Log.d(TAG, "onStart: hours"+currentService.getOpeningHours());
        openningHoursInfo.setText(""+currentService.getOpeningHours());
        homeNumber = currentService.getServiceHomeNumber();
        if (homeNumber==null)
            homeNumber = "";
        Log.d(TAG, "onStart: address"+ currentService.getAddress()+"  "+homeNumber+"  "+currentService.getTown());
        addressInfo.setText(currentService.getAddress()+"  "+homeNumber+"  "+currentService.getTown());
        Log.d(TAG, "onStart: phone"+currentService.getPhoneNumber());
        phoneNumberInfo.setText(currentService.getPhoneNumber());
        serviceInfo.setText(currentService.getShortDescription());
        serviceAdditionalInfo.setText(currentService.getAdditionalInfo());
        }

        ref.child("reviews").child(myId).addValueEventListener(inflateMessageListener);

        ref.addValueEventListener(serviceChangeListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentService = dataSnapshot.getValue(Service.class);
                if (currentService!=null) {
                    if (currentService.getNoReviewers() == 0) {
                        allRatings.setVisibility(View.GONE);
                        avrgRating.setVisibility(View.GONE);
                        noRatings.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                        noRatings.setText(getResources().getString(R.string.no_ratings));
                    } else {
                        allRatings.setVisibility(View.VISIBLE);
                        avrgRating.setVisibility(View.VISIBLE);
                        noRatings.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                        noRatings.setText("(" + currentService.getNoReviewers() + ")");
                    }
                }
//                openningHoursInfo.setText(ServiceRegistrationActivityForm.newService.getOpeningHours());
//                homeNumber = currentService.getServiceHomeNumber();
//                if (homeNumber==null)
//                    homeNumber = "";
//                addressInfo.setText(currentService.getAddress()+"  "+homeNumber);
//                phoneNumberInfo.setText(" "+currentService.getPhoneNumber());
//                serviceInfo.setText(currentService.getShortDescription());
//                serviceAdditionalInfo.setText(currentService.getAdditionalInfo());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ref.addValueEventListener(serviceRatingListener);

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        ref.addValueEventListener(serviceChangeListener);
        Log.d(TAG, "onRestart: ");
        if (getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.APP_ID, Constants.RANDOM_STRING).equals(currentService.getUserUid())){
//            if (ServiceRegistrationActivityForm.newService!=null) {
//                currentService = ServiceRegistrationActivityForm.newService;
//                Log.d(TAG, "onRestart: " + currentService.getOpeningHours());
//            }
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        ref.removeEventListener(serviceChangeListener);
        ref.removeEventListener(inflateMessageListener);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResumeService: "+ServiceRegistrationActivityForm.newService.toString());
        Log.d(TAG, "onResume: ");
        if (!(getIntent().getBooleanExtra("isMidReg", true)) &&getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.APP_ID, Constants.RANDOM_STRING).equals(currentService.getUserUid())){
            Gson gson= new Gson();
            Log.d(TAG, "onResume2: ");
            String s = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.MY_SERVICE, Constants.RANDOM_STRING);
            try {
                JSONObject obj = new JSONObject(s);
                currentService = gson.fromJson(obj.toString(), Service.class);
                openningHoursInfo.setText(currentService.getOpeningHours());
                String homeNumber = currentService.getServiceHomeNumber();
                if (homeNumber==null)
                    homeNumber="";
                addressInfo.setText(currentService.getAddress()+" "+homeNumber+"  "+currentService.getTown());
                phoneNumberInfo.setText(currentService.getPhoneNumber());
                serviceInfo.setText(currentService.getShortDescription());
                serviceAdditionalInfo.setText(currentService.getAdditionalInfo());

                Log.d(TAG, "onRestart: "+currentService.getOpeningHours());
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }else{
            //if came from edit
            if (getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.APP_ID, Constants.RANDOM_STRING).equals(currentService.getUserUid())){
                openningHoursInfo.setText(ServiceRegistrationActivityForm.newService.getOpeningHours());
                String homeNumber = ServiceRegistrationActivityForm.newService.getServiceHomeNumber();
                if (homeNumber==null)
                    homeNumber="";
                addressInfo.setText(ServiceRegistrationActivityForm.newService.getAddress()+" "+homeNumber+"  "+ServiceRegistrationActivityForm.newService.getTown());
                phoneNumberInfo.setText(ServiceRegistrationActivityForm.newService.getPhoneNumber());
                serviceInfo.setText(ServiceRegistrationActivityForm.newService.getShortDescription());
                serviceAdditionalInfo.setText(ServiceRegistrationActivityForm.newService.getAdditionalInfo());
            }
        }
        FirebaseDatabase.getInstance().getReference().child("Services").child("PublicData").child(currentService.getKey()).child("reviews").addChildEventListener(commentsListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (currentService!=null)
        FirebaseDatabase.getInstance().getReference().child("Services").child("PublicData").child(currentService.getKey()).child("reviews").removeEventListener(commentsListener);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isAnonymous)
        ref.removeEventListener(serviceRatingListener);
    }




    @Override
    public void onClick(View v) {

        if (v.getId()==R.id.open_hours_edit || v.getId()==R.id.phone_number_edit){
            String s = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.MY_SERVICE, Constants.RANDOM_STRING);
            try {
                JSONObject obj = new JSONObject(s);
                Gson gson = new Gson();
                ServiceRegistrationActivityForm.newService = gson.fromJson(obj.toString(),  com.mybiz.Objects.Service.class );
                Intent intent4 = new Intent(AllServiceInfo.this, ServiceRegistrationFragmentContainer.class);
                intent4.putExtra("fragmentNumber", 4);
                intent4.putExtra("isEdit", true);
                startActivity(intent4);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(v.getId()==R.id.additional_info_edit || v.getId()==R.id.info_edit) {
            String s = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.MY_SERVICE, Constants.RANDOM_STRING);
            try {
                JSONObject obj = new JSONObject(s);
                Gson gson = new Gson();
                ServiceRegistrationActivityForm.newService = gson.fromJson(obj.toString(),  com.mybiz.Objects.Service.class );
                Intent intent3 = new Intent(AllServiceInfo.this, ServiceRegistrationFragmentContainer.class);
                intent3.putExtra("fragmentNumber", 3);
                intent3.putExtra("isEdit", true);
                startActivity(intent3);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else if (v.getId()==R.id.profile_image_edit) {
            String s = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.MY_SERVICE, Constants.RANDOM_STRING);
            try {
                JSONObject obj = new JSONObject(s);
                Gson gson = new Gson();
                ServiceRegistrationActivityForm.newService = gson.fromJson(obj.toString(),  com.mybiz.Objects.Service.class );
                Intent intent0 = new Intent(AllServiceInfo.this, ServiceRegistrationFragmentContainer.class);
                intent0.putExtra("fragmentNumber", 0);
                intent0.putExtra("isEdit", true);
                startActivity(intent0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if(v.getId()==R.id.address_edit){
            String s = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.MY_SERVICE, Constants.RANDOM_STRING);
            try {
                JSONObject obj = new JSONObject(s);
                Gson gson = new Gson();
                ServiceRegistrationActivityForm.newService = gson.fromJson(obj.toString(),  com.mybiz.Objects.Service.class );
                Intent intent2 = new Intent(AllServiceInfo.this, ServiceRegistrationFragmentContainer.class);
                intent2.putExtra("fragmentNumber", 2);
                intent2.putExtra("isEdit", true);
                startActivity(intent2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        else
        {
            switch (v.getId()) {

                case R.id.add_comment_fab:
//                    if (!getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).getString(Constants.APP_ID, Constants.RANDOM_STRING).equals(Constants.RANDOM_STRING)) {
                        Intent intent = new Intent(AllServiceInfo.this, ChatActivity.class);
                        intent.putExtra("otherID", currentService.getUserUid());
                        intent.putExtra("otherName", currentService.getTitle());
                        intent.putExtra("isService", true);
                        intent.putExtra("currentService", currentService);
                        startActivity(intent);
//                    } else {
//
//                    }
                    break;
                default:
                    break;
            }
        }
    }

    //if user has rated already current service or not
    ValueEventListener inflateMessageListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {//user has commented already
                Log.d(TAG, "onDataChange: user has commented");
                final Comment comment1 = dataSnapshot.getValue(Comment.class);
                displayMyComment = (ViewSwitcher) findViewById(R.id.display_my_comment);//where you want to add/inflate a view as a child
                View child = displayMyComment.getChildAt(1);
//                displayMyComment.addView(child);
                displayMyComment.showNext();

                ImageView profile = (ImageView) child.findViewById(R.id.profile);
                Glide.with(getBaseContext()).load(comment1.getUrl()).into(profile);

                TextView writer = (TextView) child.findViewById(R.id.writer);
                writer.setText(comment1.getWriter());

                final RatingBar ratingBar = (RatingBar) child.findViewById(R.id.ratingBar);
                ratingBar.setIsIndicator(true);
                ratingBar.setStepSize(1.0f);
                LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
                stars.getDrawable(2).setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
                ratingBar.setAlpha(.56f);
                ratingBar.setRating(comment1.getReview());
                TextView myReview = (TextView) child.findViewById(R.id.my_review);
                myReview.setText(comment1.getComment());

                String dateString = "";
                int gmtOffset = TimeZone.getDefault().getRawOffset();
                long msgTime1 = comment1.getDate() + gmtOffset;

//                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
//                dateString = formatter.format(new Date(msgTime1));
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
//                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                dateString = formatter.format(new Date(comment1.getDate()));
                Log.d(TAG, "onDataChange: " + dateString);


                TextView dateReview = (TextView) child.findViewById(R.id.review_date);
                dateReview.setText(dateString);

                ImageView editComment = (ImageView) child.findViewById(R.id.edit_comment);
                TextView edit = (TextView) child.findViewById(R.id.edit);
                editComment.setColorFilter(getResources().getColor(R.color.colorPrimary));
                editComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            Intent intent = new Intent(AllServiceInfo.this, AddCommentActivity.class);
//                            Intent intent = new Intent(AllServiceInfo.this, AddComment.class);
                            intent.putExtra("isEdit", true);
                            intent.putExtra("currentService", currentService);
                            intent.putExtra("myReview", comment1.getComment());
                            Log.d(TAG, "onClick: "+ratingBar.getRating());
                            intent.putExtra("ratingAmount", (int)ratingBar.getRating());
                            startActivity(intent);
                            finish();
                    }
                });
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AllServiceInfo.this, AddCommentActivity.class);
//                        Intent intent = new Intent(AllServiceInfo.this, AddComment.class);
                        intent.putExtra("isEdit", true);
                        intent.putExtra("currentService", currentService);
                        intent.putExtra("myReview", comment1.getComment());
                        Log.d(TAG, "onClick: "+ratingBar.getRating());
                        intent.putExtra("ratingAmount", (int)ratingBar.getRating());
                        startActivity(intent);
                        finish();
                    }
                });

            } else {
                Log.d(TAG, "onDataChange: user has not commented");
                displayMyComment = (ViewSwitcher) findViewById(R.id.display_my_comment);//where you want to add/inflate a view as a child
                if (!currentService.getUserUid().equals(pref.getString(Constants.APP_ID, Constants.RANDOM_STRING)))
                {
                    View child = displayMyComment.getChildAt(0);
                    TextView writer = (TextView) child.findViewById(R.id.writer);
                    TextView des = (TextView) child.findViewById(R.id.describe);
                    String w = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.NAME, Constants.RANDOM_STRING);
                    writer.setText(w);
                    des.setText(getResources().getString(R.string.describe_experience)+" "+currentService.getTitle());
//                    displayMyComment.addView(child);

                    final MyBizzRatingBar  myRating = (MyBizzRatingBar) child.findViewById(R.id.ratingBar2);
                    myRating.setOnRatingBarChangeListener(new MyBizzRatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(int rating, boolean fromUser) {
                            if (fromUser) {
                                if (!isAnonymous) {
                                    Intent intent = new Intent(AllServiceInfo.this, AddCommentActivity.class);
//                                    Intent intent = new Intent(AllServiceInfo.this, AddComment.class);
                                    intent.putExtra("currentService", currentService);
                                    intent.putExtra("ratingAmount", rating);
                                    Log.d(TAG, "onRatingChanged: "+rating);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(AllServiceInfo.this);
                                    builder.setTitle(getResources().getString(R.string.for_rating))
                                            .setPositiveButton(getString(R.string.sign_up), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    Intent intent = new Intent(AllServiceInfo.this, CreateAccountChoiceActivity.class);
                                                    startActivity(intent);

                                                }
                                            }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            myRating.setRating(-1);
                                        }
                                    });
                                    builder.setCancelable(false);
                                    builder.create().show();
                                }
                            }
                            else{
                                //
                                Log.d(TAG, "onRatingChanged: "+rating);
                            }
                        }
                    });
                }else{
                    displayMyComment.setVisibility(View.GONE);
                    findViewById(R.id.sixth_view).setVisibility(View.GONE);
                }
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    //follows any change to rating of users to current service
    ValueEventListener serviceRatingListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Service service = dataSnapshot.getValue(Service.class);
            if (service != null){
                avrgRating.setText(new DecimalFormat("##.#").format(service.getAverageRating()));
            allRatings.setRating(service.getAverageRating());
            noRatings.setText("(" + service.getNoReviewers() + ")");

//            get number of reviews from firebase
            if (currentService.getNoReviewers() == 0) {
                avrgRating.setVisibility(View.GONE);
                allRatings.setVisibility(View.GONE);
                noRatings.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                noRatings.setText(getResources().getString(R.string.no_ratings));
            } else {
                avrgRating.setVisibility(View.VISIBLE);
                allRatings.setVisibility(View.VISIBLE);
                avrgRating.setText(new DecimalFormat("##.#").format(service.getAverageRating()));
                allRatings.setRating(service.getAverageRating());
                noRatings.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);
                noRatings.setText("(" + currentService.getNoReviewers() + ")");
            }
        }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }

    };

    //run through all comments add them to list to be displayed.
    ChildEventListener commentsListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Log.d(TAG, "onChildAdded: "+dataSnapshot.getValue());
            Comment c = dataSnapshot.getValue(Comment.class);
            if (!c.getWriterUid().equals(getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.APP_ID, Constants.RANDOM_STRING))) {
                if (commentMap.get(c.getWriterUid())==null) {
                    commentMap.put(c.getWriterUid(), c);
                    commentsList.add(c);
                    Log.d(TAG, "onChildAdded: " + commentsList.size());
                    Log.d(TAG, "onChildAdded: " + commentsList.toArray().toString());
                    commentsAdapter.notifyDataSetChanged();
                }
            }


            if (dataSnapshot.getChildrenCount()==0){
                avrgRating.setVisibility(View.GONE);
                allRatings.setVisibility(View.GONE);
                noRatings.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                noRatings.setText(getResources().getString(R.string.no_ratings));
            }else{
                avrgRating.setVisibility(View.VISIBLE);
                allRatings.setVisibility(View.VISIBLE);
                avrgRating.setText(new DecimalFormat("##.#").format(currentService.getAverageRating()));
                allRatings.setRating(currentService.getAverageRating());
                noRatings.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);
                noRatings.setText("("+currentService.getNoReviewers()+")");
            }

//            if (.getNoReviewers()==0){
//                avrgRating.setVisibility(View.GONE);
//                allRatings.setVisibility(View.GONE);
//                noRatings.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
//                noRatings.setText(getResources().getString(R.string.no_ratings));
//            }else{
//                avrgRating.setVisibility(View.VISIBLE);
//                allRatings.setVisibility(View.VISIBLE);
//                avrgRating.setText(new DecimalFormat("##.#").format(service.getAverageRating()));
//                allRatings.setRating(service.getAverageRating());
//                noRatings.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);
//                noRatings.setText("("+currentService.getNoReviewers()+")");
//            }

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}

