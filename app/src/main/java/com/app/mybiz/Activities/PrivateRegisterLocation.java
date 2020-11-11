package com.app.mybiz.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.app.mybiz.Fragments.PrivateInsertAddressFragment;
import com.app.mybiz.Fragments.RequestLocationPermissions;
import com.app.mybiz.R;
import com.app.mybiz.TabsActivity;

/**
 * Created by itzikalgrisi on 21/02/2017.
 */

public class PrivateRegisterLocation extends AppCompatActivity implements  RequestLocationPermissions.OnLocationOptionsListener {

    String TAG = "PrivateRegisterLocation";
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.private_register_location);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.choose_location));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

//        findViewById(R.id.insert).setOnClickListener(this);
//        findViewById(R.id.useLocation).setOnClickListener(this);


        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container,requestLocationPermissions = new RequestLocationPermissions().setListener(this)).commit();
        }
//        if(savedInstanceState == null){
//            requestLocationPermissions = new RequestLocationPermissions().setListener(this);
//            requestLocationPermissions.show(getSupportFragmentManager(), "ab");
//        }
    }

    RequestLocationPermissions requestLocationPermissions;
    protected void onStart() {
//        mGoogleApiClient.connect();
        super.onStart();

    }

    protected void onStop() {
//        mGoogleApiClient.disconnect();
        super.onStop();
    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1000 && requestLocationPermissions != null)
            requestLocationPermissions.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setAddress(RequestLocationPermissions fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new PrivateInsertAddressFragment()).addToBackStack("a").commit();
    }

    @Override
    public void tryEnableLocation(RequestLocationPermissions fragment) {

    }

    @Override
    public void requestPermission(boolean per, RequestLocationPermissions fragment) {
//        fragment.dismiss();
    }

    @Override
    public void onEnable(RequestLocationPermissions fragment) {
        try {
//            fragment.dismiss();

            fragment.getLastLocation();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationUpdate(com.app.mybiz.Objects.Location location, RequestLocationPermissions fragment) {
        if(FirebaseAuth.getInstance().getCurrentUser()!= null) {

//            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Intent intent = new Intent(this, TabsActivity.class);
            startActivity(intent);
//            finish();
        }
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.useLocation:
//
//                break;
//            case R.id.insert:
//
//                break;
//        }
//    }
}
