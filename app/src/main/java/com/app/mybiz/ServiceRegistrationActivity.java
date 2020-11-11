package com.app.mybiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import androidx.annotation.NonNull;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.iid.FirebaseInstanceId;
import com.app.mybiz.Objects.Service;
import com.app.mybiz.Objects.User;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class ServiceRegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    String TAG = "oooo";
    public  static  Service newService;
    TextView backBtn, nextBtn, textview_toolbar;
    Button finish_btn;
    LinearLayout container;
    int fragmentNumber = 0;
    User newUser;
    String newServiceKey, addServiceKey;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    ServiceRegistrationAdditionalInfoFragment firstFragment;
    ServiceRegistrationPhoneAndHours secondFragment;
    ServiceCategoryAndNameFragment thirdFragment;
    ServiceRegistrationEmailAndPassword fourthFragment;
    ServiceProfileImageFragment fifthFragment;
    DatabaseReference usersRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_registration_view_pager);
        newService = new Service();
        if (FirebaseAuth.getInstance().getCurrentUser()!=null){
            newService.setUserUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
            usersRef = mRootRef.child("AllUsers").child("PrivateData").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            addServiceKey = usersRef.child("services").push().getKey();
            newService.setKey(addServiceKey);

        }
        //initialise layout items
        initViews();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        if (savedInstanceState == null) {//Activity first created
            fragmentTransaction.add(R.id.container, firstFragment);
            fragmentTransaction.commit();
            fragmentNumber = 1;
        } else {
            fragmentNumber = savedInstanceState.getInt("fragmentNumber");
        }
        setListeners();
    }

    private void initViews() {
        textview_toolbar = (TextView) findViewById(R.id.textview_toolbar);
        finish_btn = (Button) findViewById(R.id.finish_btn);
        backBtn = (TextView) findViewById(R.id.back_btn);
        nextBtn = (TextView) findViewById(R.id.next_btn);
        container = (LinearLayout) findViewById(R.id.container);

        firstFragment = new ServiceRegistrationAdditionalInfoFragment();
        secondFragment = new ServiceRegistrationPhoneAndHours();
        thirdFragment = new ServiceCategoryAndNameFragment();
        fourthFragment = new ServiceRegistrationEmailAndPassword();
        fifthFragment = new ServiceProfileImageFragment();
    }

    private void setListeners() {
        backBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        finish_btn.setOnClickListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                //collect info

                if (fragmentNumber > 1)
                    onBackPressed();
                else if (fragmentNumber == 1) {
                    Intent i = new Intent(ServiceRegistrationActivity.this, CreateAccountChoiceActivity.class);
                    startActivity(i);
                }
                if (fragmentNumber != 5) {
                    backBtn.setVisibility(View.VISIBLE);
                    nextBtn.setVisibility(View.VISIBLE);
                    finish_btn.setVisibility(View.GONE);

                }
                if (fragmentNumber == 5) {
                    backBtn.setVisibility(View.VISIBLE);
                    nextBtn.setVisibility(View.GONE);
                    finish_btn.setVisibility(View.VISIBLE);
                }
                Toast.makeText(getBaseContext(), "back was clicked__" + fragmentNumber, Toast.LENGTH_SHORT).show();

                break;

            case R.id.next_btn:
                //collect info
                //nextFragment
                nextFragment();
                Toast.makeText(getBaseContext(), "next was clicked__" + fragmentNumber, Toast.LENGTH_SHORT).show();


                break;

            case R.id.finish_btn:
                Log.d(TAG, "onClick: "+getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.APP_ID, Constants.RANDOM_STRING));
                if (!getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.APP_ID, Constants.RANDOM_STRING).equals(Constants.RANDOM_STRING)){
                    Log.d(TAG, "onClick: "+111);
                    addServiceToUser();
                }
                if (getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.APP_ID, Constants.RANDOM_STRING).equals(Constants.RANDOM_STRING)) {//first time, registering as service
                    Log.d(TAG, "onClick: "+222);
                    registerBusiness();
                }
                break;
            default:
                break;

        }
    }

    @Override
    public void onBackPressed() {
        if (fragmentNumber >= 0) {
            fragmentNumber--;
        }
        super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save all registration data for orientation changed
        outState.putInt("fragmentNumber", fragmentNumber);
        outState.putString("title", newService.getTitle());
        outState.putString("town", newService.getTown());
        outState.putString("address", newService.getAddress());
        outState.putBoolean("isService", newService.isService());
        outState.putString("phoneNumber", newService.getPhoneNumber());
        outState.putString("openningHours", newService.getOpeningHours());
        outState.putString("category", newService.getCategory());
        outState.putString("shortDescription", newService.getShortDescription());
        outState.putString("additionalInfo", newService.getAdditionalInfo());
        outState.putString("email", newService.getEmail());
        outState.putString("password", newService.getPassword());
        outState.putString("profileUrl", newService.getProfileUrl());

    }

    private void nextFragment() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        switch (fragmentNumber) {
            case 1:
                //collect info - done on stop of fragment
                if(((ServiceRegistrationAdditionalInfoFragment) getSupportFragmentManager().findFragmentById(firstFragment.getId())).isComplete()) {
                    nextBtn.setVisibility(View.VISIBLE);
                    backBtn.setVisibility(View.VISIBLE);
                    finish_btn.setVisibility(View.GONE);
                    fragmentNumber = 2;
                    transaction.replace(R.id.container, secondFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }else{
                    Toast.makeText(getBaseContext(), "You must complete required fields", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                //collect info
                if(((ServiceRegistrationPhoneAndHours) getSupportFragmentManager().findFragmentById(secondFragment.getId())).isComplete()) {
                nextBtn.setVisibility(View.VISIBLE);
                backBtn.setVisibility(View.VISIBLE);
                finish_btn.setVisibility(View.GONE);
                fragmentNumber = 3;
                transaction.replace(R.id.container, thirdFragment);
                transaction.replace(R.id.container, thirdFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                }else{
                    Toast.makeText(getBaseContext(), "You must complete required fields", Toast.LENGTH_SHORT).show();
                }
                break;
            case 3:
                if(((ServiceCategoryAndNameFragment) getSupportFragmentManager().findFragmentById(thirdFragment.getId())).isComplete()) {

                    //collect info
                fragmentNumber = 4;
                nextBtn.setVisibility(View.VISIBLE);
                backBtn.setVisibility(View.VISIBLE);
                finish_btn.setVisibility(View.GONE);
                transaction.replace(R.id.container, fourthFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                nextBtn.setVisibility(View.VISIBLE);
                finish_btn.setVisibility(View.GONE);
                }else{
                    Toast.makeText(getBaseContext(), "You must complete required fields", Toast.LENGTH_SHORT).show();
                }
                break;
            case 4:
                if(((ServiceRegistrationEmailAndPassword) getSupportFragmentManager().findFragmentById(fourthFragment.getId())).isComplete()) {

                    //collect info
                transaction.replace(R.id.container, fifthFragment);
                fragmentNumber = 5;
                transaction.addToBackStack(null);
                transaction.commit();
                nextBtn.setVisibility(View.GONE);
                backBtn.setVisibility(View.VISIBLE);
                finish_btn.setVisibility(View.VISIBLE);
                finish_btn.setEnabled(true);
                finish_btn.setFocusable(true);
                }else{
                    Toast.makeText(getBaseContext(), "You must complete required fields", Toast.LENGTH_SHORT).show();
                }
                break;
            case 5:
//                collect info
//                reset button

                break;
        }

    }

    private void addServiceToUser(){
        if(newService.getEmail().endsWith("@gmail.com"))
            newService.setEmail(newService.getEmail().replace("@gmail.com", "@mybizzgmail.com"));
        Log.d(TAG, "addServiceToUser: "+newService.getEmail()+"__"+newService.getPassword());
        AuthCredential credential = EmailAuthProvider.getCredential(newService.getEmail(), newService.getPassword());
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "onComplete: linked email");
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: " + task.getResult().getUser().getUid());
                    String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Log.d(TAG, "onComplete: " + myUid + "__" + task.getResult().getUser().getUid());
                    DatabaseReference usersRef = mRootRef.child("AllUsers").child("PrivateData").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    addServiceKey = usersRef.child("services").push().getKey();
                    newService.setKey(addServiceKey);
                    newService.setUserUid(myUid);
                    newService.setSubcategory(newService.getCategory());
                    //add to user containsService
                    usersRef.child("containsService").setValue(true);
                    usersRef.child("services").child(addServiceKey).setValue(newService);
                    mRootRef.child("AllUsers").child("PublicData").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("services").child(addServiceKey).setValue(newService);
                    mRootRef.child("AllUsers").child("PublicData").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("containsService").setValue(true);

                    mRootRef.child("Services").child("PrivateData").child(addServiceKey).setValue(newService);
                    mRootRef.child("Services").child("PublicData").child(addServiceKey).setValue(newService);
                    try {
                        endRegistration(myUid);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    Log.d(TAG, "onComplete: "+task.getException().getMessage());
                    Log.d(TAG, "onFailure: "+task.getException().toString());
                }
            }

        });
    }

    private void registerBusiness(){
        Log.d(TAG, "registerBusiness: ");

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(newService.getEmail(), newService.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    String uid = task.getResult().getUser().getUid();
                    //create user with service
                    newUser = new User(newService.getTitle(), newService.getEmail(), uid, newService.getProfileUrl(), true );
                    newService.setUserUid(uid);
                    newService.setSubcategory(newService.getCategory());

                    HashMap<String, String> devices = newUser.getDevices();
                    devices.put(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID), Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                    newUser.setDevices(devices);

                    //add service to services--> then add the user including key to users
                    newServiceKey = mRootRef.child("Services").child("PrivateData").push().getKey();
                    newService.setKey(newServiceKey);
                    mRootRef.child("Services").child("PrivateData").child(newServiceKey).setValue(newService);
                    mRootRef.child("Services").child("PublicData").child(newServiceKey).setValue(newService);


                    //ad user with service
                    HashMap<String, Service> services = newUser.getServices();
                    services.put(newServiceKey, newService);
                    newUser.setServices(services);
                    mRootRef.child("AllUsers").child("PrivateData").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(newUser);
                    mRootRef.child("AllUsers").child("PublicData").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(newUser);
                    Log.d(TAG, "onComplete: "+newServiceKey);

                    //add uid to device
                    Device device = new Device();
                    device.setUid(uid);
                    String newToken = "";//FirebaseInstanceId.getInstance().getToken();
                    if (newToken!=null)
                        device.setPushToken(newToken);
                    device.setSerialNumber(Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID));
                    mRootRef.child("Devices").child(device.getSerialNumber()).setValue(device);
                    try {
                        endRegistration(uid);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }
        });
    }

    private void endRegistration(String mUid) throws IOException {
        SharedPreferences preferences = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.APP_ID, mUid);
        editor.putString(Constants.NAME, newService.getTitle());
        editor.putString(Constants.EMAIL, newService.getEmail());
        editor.putBoolean(Constants.IS_SERVICE, true);
        editor.putString(Constants.MY_CATEGORY, newService.getCategory());
        editor.putString(Constants.MY_SUB_CATEGORY, newService.getSubcategory());
        editor.putString(Constants.MY_SERVICE, newService.toJson());
        editor.commit();
//        FileOutputStream f = new FileOutputStream(new File("myObjects.txt"));
//        ObjectOutputStream o = new ObjectOutputStream(f);
//        // Write objects to file
//        o.writeObject(newService);
//        o.close();
//        f.close();

        FileOutputStream fileOutputStream = openFileOutput("myService.json", MODE_PRIVATE);
        Log.d(TAG, "endRegistration: " + newService.toString());
        fileOutputStream.write(newService.toString().getBytes());
        fileOutputStream.close();
        MyApplication.addListener(mUid);
        Intent intent = new Intent(ServiceRegistrationActivity.this, TabsActivity.class);
        startActivity(intent);
//        finish();

        String newToken = "";//FirebaseInstanceId.getInstance().getToken();
        if (newToken != null) {
            Device device = new Device();
            device.setPushToken(newToken);
            device.setUid(mUid);
            String android_id = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/").child("Devices").child(android_id).setValue(device);
        }
    }
}
