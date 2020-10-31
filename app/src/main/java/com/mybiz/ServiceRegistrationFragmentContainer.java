package com.mybiz;

import android.content.Intent;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;

public class ServiceRegistrationFragmentContainer extends AppCompatActivity implements View.OnClickListener {
    Intent i;
    static String userState = "";
    String TAG = "fragmentContainer";


    ServiceProfileImageFragment profileImageFragment;
    ServiceCategoryAndNameFragment categoryInfo;
    ServiceRegistrationLocationFragment locationInfo;
    ServiceRegistrationPhoneAndHours phoneAndHours;
    ServiceRegistrationAdditionalInfoFragment additionalInfo;
    ServiceRegistrationEmailAndPassword emailAndAddress;
    public static HashMap<String, Object> map = new HashMap<>();



    TextView toolbarTitle;
    LinearLayout fragmentContainer;
    ImageView previewIcon;
    int fragmentId;
    Toolbar toolbar;
    static TextView saveBtn;
    static int saveVsBack = -1;
    static int SAVE = 1;
    static int BACK = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        saveBtn = (TextView) findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);
        allowToSaveInfo(false);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setBackgroundColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                onBackPressed();
                saveVsBack = 0;
                Log.d(TAG, "onClick: ");
            }
        });
        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        additionalInfo = new ServiceRegistrationAdditionalInfoFragment();
        locationInfo = new ServiceRegistrationLocationFragment();
        emailAndAddress  = new ServiceRegistrationEmailAndPassword();
        profileImageFragment = new ServiceProfileImageFragment();
        phoneAndHours = new ServiceRegistrationPhoneAndHours();
        categoryInfo = new ServiceCategoryAndNameFragment();



        fragmentContainer = (LinearLayout) findViewById(R.id.fragment_container);
        i = getIntent();
        if (i.getBooleanExtra("isEdit", false))
            userState="isEdit";


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (i.getIntExtra("fragmentNumber", -1)==0){
            toolbarTitle.setText(getResources().getString(R.string.add_picture));
            toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            transaction.replace(R.id.fragment_container, profileImageFragment).commit();
            fragmentId = profileImageFragment.getId();
        }

        if (i.getIntExtra("fragmentNumber", -1)==1){
            toolbarTitle.setText(getResources().getString(R.string.choose_category));
            transaction.replace(R.id.fragment_container, categoryInfo).commit();
            fragmentId = categoryInfo.getId();
        }

        if (i.getIntExtra("fragmentNumber", -1)==2){
            toolbarTitle.setText(getResources().getString(R.string.add_location));
            transaction.replace(R.id.fragment_container, locationInfo).commit();
            fragmentId = locationInfo.getId();
        }

        if (i.getIntExtra("fragmentNumber", -1)==3){
            toolbarTitle.setText(getResources().getString(R.string.describe_your_service));
            transaction.replace(R.id.fragment_container, additionalInfo).commit();
            fragmentId = phoneAndHours.getId();
        }

        if (i.getIntExtra("fragmentNumber", -1)==4){
            toolbarTitle.setText(getResources().getString(R.string.openning_hours));
            transaction.replace(R.id.fragment_container, phoneAndHours).commit();
            fragmentId = phoneAndHours.getId();
        }

        if (i.getIntExtra("fragmentNumber", -1)==5){
            toolbarTitle.setText(getResources().getString(R.string.username));
            transaction.replace(R.id.fragment_container, emailAndAddress).commit();
            fragmentId = emailAndAddress.getId();
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.save_btn) {
            saveVsBack = 1;
//            if (userState.equals("isEdit")){
//                //update all fields in database
//                String serviceKey = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.APP_ID, Constants.RANDOM_STRING);
//                //refs to update
//                //update fields.
//                FirebaseDatabase.getInstance().getReference().child("Exceptions").child(serviceKey).updateChildren(ServiceRegistrationFragmentContainer.map);
//                Log.d(TAG, "onClick1112: "+ServiceRegistrationActivityForm.newService.toJson().toString());
//                Log.d(TAG, "onClick111: "+getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.MY_SERVICE, Constants.RANDOM_STRING));
//                getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).edit().putString(Constants.MY_SERVICE, ServiceRegistrationActivityForm.newService.toJson()).commit();
//                Log.d(TAG, "onClick111: "+getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.MY_SERVICE, Constants.RANDOM_STRING));
//            }
            onBackPressed();
        }
    }

    static  void allowToSaveInfo(boolean allow){
        if (allow){
            saveBtn.setAlpha(.87f);
            saveBtn.setEnabled(true);
        }else{
            saveBtn.setAlpha(.12f);
            saveBtn.setEnabled(false);
        }
    }


}
