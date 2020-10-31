package com.mybiz;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.mybiz.Activities.AllServiceInfo;
import com.mybiz.Activities.ShareMyService;
import com.mybiz.Adapters.ListViewFormAdapter;
import com.mybiz.Objects.Service;
import com.mybiz.Objects.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServiceRegistrationActivityForm extends AppCompatActivity {
    public static Service newService;
    static double lat, lon;
    String TAG = "serviceRegistration";
    User newUser;
    View step1, step2, step3, step4, step5, step6;
    ListView listViewForm;
    TextView stepsToGo;
    ListViewFormAdapter adapter;
    ArrayList<FormItem> list;
    Toolbar toolbar;
    ImageView previewIcon;
    ProgressBar saving_service_prog;
    int positionChanged = -1;
    int count;
    int stepsLeft = 6;
    int stepsComplete = 0;
    String newServiceKey, addServiceKey;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    FormItem firstItem, secondItem, thirdItem, fourthItem, fifthItem, sixthItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_registration_form);

        String myService = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.MY_SERVICE, Constants.RANDOM_STRING);
        if (myService.equals(Constants.RANDOM_STRING)) {
            newService = new Service();
        } else {
            Gson gson = new Gson();
            try {
                JSONObject obj = new JSONObject(myService);
                newService = gson.fromJson(obj.toString(), Service.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        Log.d(TAG, "onCreate: initViews");
        saving_service_prog = (ProgressBar) findViewById(R.id.saving_service_prog);
        previewIcon = (ImageView) findViewById(R.id.preview_icon);
        previewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceRegistrationActivityForm.this, AllServiceInfo.class);
                Service service = newService;
                intent.putExtra("isMidReg", true);
                intent.putExtra("currentService", service);
                startActivity(intent);

            }
        });
        initViews();


    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ServiceRegistrationActivityForm.this);
        // set dialog message
        alertDialogBuilder
                .setMessage(getString(R.string.confirm_exit))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
//                         onBackPressed();
                        finish();

                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    private void updateStepper(int counter) {
        counter = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isComplete()) {
                counter = counter + 1;
                Log.d(TAG, "onResume: " + counter + "");
            }
        }
        if (counter != stepsComplete && ServiceRegistrationFragmentContainer.saveVsBack == ServiceRegistrationFragmentContainer.SAVE) {
            //animate preview icon
            Animation animation1 =
                    AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
            previewIcon.startAnimation(animation1);
        }
        stepsComplete = counter;
        stepsLeft = 6 - stepsComplete;
        Log.d(TAG, "updateStepper: " + stepsComplete);

        switch (counter) {
            case 6:
                step6.setBackgroundColor(Color.RED);
                step5.setBackgroundColor(Color.RED);
                step4.setBackgroundColor(Color.RED);
                step3.setBackgroundColor(Color.RED);
                step2.setBackgroundColor(Color.RED);
                step1.setBackgroundColor(Color.RED);
                break;
            case 5:
                step6.setBackgroundColor(Color.BLACK);
                step5.setBackgroundColor(Color.RED);
                step4.setBackgroundColor(Color.RED);
                step3.setBackgroundColor(Color.RED);
                step2.setBackgroundColor(Color.RED);
                step1.setBackgroundColor(Color.RED);
                break;
            case 4:
                step6.setBackgroundColor(Color.BLACK);
                step5.setBackgroundColor(Color.BLACK);
                step4.setBackgroundColor(Color.RED);
                step3.setBackgroundColor(Color.RED);
                step2.setBackgroundColor(Color.RED);
                step1.setBackgroundColor(Color.RED);
                break;
            case 3:
                step6.setBackgroundColor(Color.BLACK);
                step5.setBackgroundColor(Color.BLACK);
                step4.setBackgroundColor(Color.BLACK);
                step3.setBackgroundColor(Color.RED);
                step2.setBackgroundColor(Color.RED);
                step1.setBackgroundColor(Color.RED);
                break;
            case 2:
                step6.setBackgroundColor(Color.BLACK);
                step5.setBackgroundColor(Color.BLACK);
                step4.setBackgroundColor(Color.BLACK);
                step3.setBackgroundColor(Color.BLACK);
                step2.setBackgroundColor(Color.RED);
                step1.setBackgroundColor(Color.RED);
                break;
            case 1:
                step6.setBackgroundColor(Color.BLACK);
                step5.setBackgroundColor(Color.BLACK);
                step4.setBackgroundColor(Color.BLACK);
                step3.setBackgroundColor(Color.BLACK);
                step2.setBackgroundColor(Color.BLACK);
                step1.setBackgroundColor(Color.RED);
                break;
            case 0:
                step6.setBackgroundColor(Color.BLACK);
                step5.setBackgroundColor(Color.BLACK);
                step4.setBackgroundColor(Color.BLACK);
                step3.setBackgroundColor(Color.BLACK);
                step2.setBackgroundColor(Color.BLACK);
                step1.setBackgroundColor(Color.BLACK);
                break;

        }

        if (stepsLeft == 0 && newService.getPassword().length() >= 9 && android.util.Patterns.EMAIL_ADDRESS.matcher(newService.getEmail().toString()).matches()) {
            stepsToGo.setEnabled(true);
            stepsToGo.setAlpha(1.0f);
            stepsToGo.setText(getResources().getString(R.string.complete_service_registring));
            stepsToGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: " + stepsLeft + "___");
                    new AsyncTask() {

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            //show progress bar
                            saving_service_prog.setVisibility(View.VISIBLE);

                        }

                        @Override
                        protected Object doInBackground(Object[] params) {
                            getLocation("ישראל", newService.getTown(), newService.getAddress());
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Object o) {
                            super.onPostExecute(o);

                            if (!getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.APP_ID, Constants.RANDOM_STRING).equals(Constants.RANDOM_STRING)) {
                                Log.d(TAG, "onClick: " + 111);
                                addServiceToUser();
                            }
                            if (getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.APP_ID, Constants.RANDOM_STRING).equals(Constants.RANDOM_STRING)) {//first time, registering as service
                                Log.d(TAG, "onClick: " + 222);
                                registerBusiness();
                            }

                            //make progressbar disappear

                        }
                    }.execute();


                }
            });
        } else {
            if (stepsLeft > 0) {
                stepsToGo.setAlpha(.56f);
                stepsToGo.setEnabled(false);
                stepsToGo.setText(stepsLeft + "  " + getResources().getString(R.string.steps_to_go));
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newService.getEmail()).matches() || newService.getPassword().length() < 9) {
                Intent intent = new Intent(ServiceRegistrationActivityForm.this, ServiceRegistrationFragmentContainer.class);
                intent.putExtra("fragmentNumber", 5);
                startActivity(intent);
            }
        }


    }

    private void initViews() {
        step1 = findViewById(R.id.step1);
        step2 = findViewById(R.id.step2);
        step3 = findViewById(R.id.step3);
        step4 = findViewById(R.id.step4);
        step5 = findViewById(R.id.step5);
        step6 = findViewById(R.id.step6);
        listViewForm = (ListView) findViewById(R.id.list_view_form);
        stepsToGo = (TextView) findViewById(R.id.steps_to_go);
        stepsToGo.setText((stepsLeft - count) + "  " + getString(R.string.to_account));
        int areCompleted = 0;
        list = new ArrayList<>();
        firstItem = new FormItem(ifProfileExists(), "aaa");
        if (ifProfileExists())
            areCompleted = areCompleted + 1;
        secondItem = new FormItem(isFirstComplete(), "", getResources().getString(R.string.name_and_category));
        if (isFirstComplete())
            areCompleted = areCompleted + 1;
        thirdItem = new FormItem(isSecondComplete(), "", getResources().getString(R.string.service_location));
        if (isSecondComplete())
            areCompleted = areCompleted + 1;
        fourthItem = new FormItem(isThirdComplete(), "", getResources().getString(R.string.describe_your_service));
        if (isThirdComplete())
            areCompleted = areCompleted + 1;
        fifthItem = new FormItem(isFourthComplete(), "", getResources().getString(R.string.openning_hours_and_phone));
        if (isFourthComplete())
            areCompleted = areCompleted + 1;
        sixthItem = new FormItem(isFifthComplete(), "", getResources().getString(R.string.email_and_password));
        if (isFifthComplete())
            areCompleted = areCompleted + 1;

        list.add(firstItem);
        list.add(secondItem);
        list.add(thirdItem);
        list.add(fourthItem);
        list.add(fifthItem);
        list.add(sixthItem);
        Log.d(TAG, "initViews: " + areCompleted);
        Log.d(TAG, "initViews2: " + (list.size() - areCompleted));
        updateStepper(areCompleted);
        updateFooterStepsCounter(areCompleted);
        if (areCompleted < list.size()) {
            stepsToGo.setAlpha(.56f);
        } else {
            stepsToGo.setAlpha(1.0f);
        }
        adapter = new ListViewFormAdapter(getBaseContext(), list);
        listViewForm.setAdapter(adapter);
        listViewForm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                positionChanged = position;
                Log.d(TAG, "onItemClick: " + position);
                Intent intent = new Intent(ServiceRegistrationActivityForm.this, ServiceRegistrationFragmentContainer.class);
                intent.putExtra("fragmentNumber", position);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG + 1, "onStart: ...");
        Log.d(TAG, "onRestart: " + newService.toJson());
        Log.d(TAG + 1, "onRestart: ...");
//        if (positionChanged==0)
            ifProfileExists();
//        if (positionChanged==1)
            isFirstComplete();
//        if (positionChanged==2)
            isSecondComplete();
//        if (positionChanged==3)
            isThirdComplete();
//        if (positionChanged==4)
            isFourthComplete();
//        if (positionChanged==5)
            isFifthComplete();

        updateStepper(stepsComplete);
        updateFooterStepsCounter(stepsLeft);
    }


    private boolean ifProfileExists() {
        File directory = new File(getApplicationContext().getFilesDir() + "/profile.jpg");
        if (directory.exists()) {
            if (list.size() > 0) {
                FormItem tmp = list.get(0);
                tmp.setComplete(true);
                list.set(0, tmp);
                Log.d(TAG, "isFirstComplete2: ");
                adapter.notifyDataSetChanged();
                return true;
            }
            return true;
        } else {
            if (list.size() > 0) {
                FormItem tmp = list.get(0);
                tmp.setComplete(false);
                list.set(0, tmp);
                Log.d(TAG, "isFirstComplete2: ");
                adapter.notifyDataSetChanged();
            }
            return false;
        }
    }

    private boolean isFirstComplete() {
        Log.d(TAG, "isFirstComplete1: " + list.size());
        if (newService.getCategory() != null && !newService.getTitle().equals(Constants.DEFAULT_SERVICE_TITLE) && !newService.getCategory().equals(getResources().getString(R.string.choose_category))) {
            if (list.size() >= 1) {
                FormItem tmp = list.get(1);
                tmp.setComplete(true);
                list.set(1, tmp);
                Log.d(TAG, "isFirstComplete23: ");
                adapter.notifyDataSetChanged();
            }
            return true;
        } else {
            if (list.size() >= 1) {
                FormItem tmp = list.get(1);
                tmp.setComplete(false);
                list.set(1, tmp);
                Log.d(TAG, "isFirstComplete2: ");
                adapter.notifyDataSetChanged();
            }
            return false;
        }
    }

    private boolean isSecondComplete() {
        if (newService.getTown() != null && !newService.getTown().equals("") && !newService.getTown().equals(getResources().getString(R.string.choose_category))
                && newService.getAddress() != null && !newService.getAddress().equals("") && !newService.getAddress().equals(getResources().getString(R.string.choose_category))) {
            if (list.size() >= 2) {
                FormItem tmp = list.get(2);
                tmp.setComplete(true);
                list.set(2, tmp);
                Log.d(TAG, "isSecondComplete23: ");
                adapter.notifyDataSetChanged();
            }
            return true;
        } else {
            if (list.size() >= 2) {
                FormItem tmp = list.get(2);
                tmp.setComplete(false);
                list.set(2, tmp);
                Log.d(TAG, "isSecondComplete23: ");
                adapter.notifyDataSetChanged();
            }
            return false;
        }
    }

    private boolean isThirdComplete() {
        if (newService.getShortDescription() != null && !newService.getShortDescription().equals("")
                && !newService.getShortDescription().equals(getResources().getString(R.string.choose_category))) {
            if (list.size() >= 3) {
                FormItem tmp = list.get(3);
                tmp.setComplete(true);
                list.set(3, tmp);
                Log.d(TAG, "isSecondComplete23: ");
                adapter.notifyDataSetChanged();
            }
            return true;
        } else {
            if (list.size() >= 3) {
                FormItem tmp = list.get(3);
                tmp.setComplete(false);
                list.set(3, tmp);
                Log.d(TAG, "isThirdComplete23: ");
                adapter.notifyDataSetChanged();
            }
            return false;
        }
    }

    private boolean isFourthComplete() {
        if (newService.getPhoneNumber() != null && !newService.getPhoneNumber().equals("") && !newService.getPhoneNumber().equals(getResources().getString(R.string.choose_category))
                && newService.getOpeningHours() != null && !newService.getOpeningHours().equals("") && !newService.getOpeningHours().equals(getResources().getString(R.string.choose_category))) {
            if (list.size() >= 4) {
                FormItem tmp = list.get(4);
                tmp.setComplete(true);
                list.set(4, tmp);
                Log.d(TAG, "isSecondComplete23: ");
                adapter.notifyDataSetChanged();
            }
            return true;
        } else {
            if (list.size() >= 4) {
                FormItem tmp = list.get(4);
                tmp.setComplete(false);
                list.set(4, tmp);
                Log.d(TAG, "isThirdComplete23: ");
                adapter.notifyDataSetChanged();
            }
            return false;
        }

    }

    private boolean isFifthComplete() {
        if (newService.getEmail() != null && !newService.getEmail().equals("") && !newService.getEmail().equals(getResources().getString(R.string.choose_category))
                && newService.getPassword() != null && !newService.getPassword().equals("") && !newService.getPassword().equals(getResources().getString(R.string.choose_category))) {
            if (list.size() >= 5) {
                FormItem tmp = list.get(5);
                tmp.setComplete(true);
                list.set(5, tmp);
                Log.d(TAG, "isSecondComplete23: ");
                adapter.notifyDataSetChanged();
            }
            return true;
        } else {
            if (list.size() >= 5) {
                FormItem tmp = list.get(5);
                tmp.setComplete(false);
                list.set(5, tmp);
                Log.d(TAG, "isThirdComplete23: ");
                adapter.notifyDataSetChanged();
            }
            return false;
        }
    }

    private void updateFooterStepsCounter(int count) {
        if (count != 0) {
            stepsToGo.setText(count + " " + getString(R.string.to_account));
        } else {
            stepsToGo.setText(getResources().getString(R.string.complete_service_registring));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save all registration data for orientation changed
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

    @Override
    protected void onPause() {
        super.onPause();
        saving_service_prog.setVisibility(View.GONE);
        Log.d(TAG + 1, "onPause: ...");
        getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).edit().putString(Constants.MY_SERVICE, newService.toJson()).commit();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        Log.d(TAG, "onRestart: " + newService.toJson());
//        Log.d(TAG + 1, "onRestart: ...");
//        ifProfileExists();
//        isFirstComplete();
//        isSecondComplete();
//        isThirdComplete();
//        isFourthComplete();
//        isFifthComplete();
//        updateStepper(stepsComplete);
//        updateFooterStepsCounter(stepsLeft);
    }

    private void addServiceToUser() {
        if (newService.getEmail().endsWith("@gmail.com"))
            newService.setEmail(newService.getEmail().replace("@gmail.com", "@mybizzgmail.com"));
//        Log.d(TAG, "addServiceToUser: " + FirebaseAuth.getInstance().getCurrentUser().getProviders());


//        Intent confirmUserIntent = new Intent(ServiceRegistrationActivityForm.this, RegistrationConfirmationActivity.class);
//        if (FirebaseAuth.getInstance().getCurrentUser().getProviders().contains("google.com")) {
//            confirmUserIntent.putExtra("google", true);
//            Log.d(TAG, "addServiceToUser: google true");
//        }
//        if (FirebaseAuth.getInstance().getCurrentUser().getProviders().contains("facebook.com")) {
//            confirmUserIntent.putExtra("facebook", true);
//            Log.d(TAG, "addServiceToUser: facebook true");
//        }
//        startActivity(confirmUserIntent);

        //re-authenticate user,
        AuthCredential credential = null;
        if(FirebaseAuth.getInstance().getCurrentUser().getProviderId().contains("google.com")){
//        if (FirebaseAuth.getInstance().getCurrentUser().getProviders().contains("google.com")){
            //demi google login
            credential = GoogleAuthProvider.getCredential(getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString("googleToken", ""), null);
        }else{//user signed in with facebook
            credential = FacebookAuthProvider.getCredential(getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString("facebookToken", ""));
        }
        FirebaseAuth.getInstance().getCurrentUser().reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User re-authenticated1.");
                            //update user profile
                            //set email
                            FirebaseAuth.getInstance().getCurrentUser().updateEmail(newService.getEmail()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "User re-authenticated2: "+FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                    //set password
                                    FirebaseAuth.getInstance().getCurrentUser().updatePassword(newService.getPassword()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //update profile
                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(newService.getTitle())
                                                    .build();
                                            FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates);

                                            //set users profile
                                            FirebaseStorage storage = FirebaseStorage.getInstance();
                                            StorageReference storageRef = storage.getReferenceFromUrl("gs://mybizz-3bbe5.appspot.com");
                                            StorageReference mountainImagesRef = storageRef.child("images/" + System.currentTimeMillis() + ".png");
                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            loadImageFromStorage(getBaseContext().getFilesDir() + "/profile.jpg").compress(Bitmap.CompressFormat.PNG, 100, baos);
                                            byte[] data = baos.toByteArray();
                                            UploadTask uploadTask = mountainImagesRef.putBytes(data);
                                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {//if profile imge upload has succeeded
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    Uri downloadUrl = taskSnapshot.getUploadSessionUri();//.getDownloadUrl();
                                                    Log.d(TAG, "onSuccess: "+FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());
                                                    ServiceRegistrationActivityForm.newService.setProfileUrl(downloadUrl.toString());
                                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                            .setPhotoUri(downloadUrl)
                                                            .build();

                                                    FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates);
                                                    Log.d(TAG, "onSuccess: "+FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());
                                                    //add service info to correct places in firebase
                                                    String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                                    DatabaseReference usersRef = mRootRef.child("AllUsers").child("PrivateData").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                    addServiceKey = usersRef.child("services").push().getKey();
                                                    ServiceRegistrationActivityForm.newService.setKey(addServiceKey);
                                                    ServiceRegistrationActivityForm.newService.setUserUid(myUid);
                                                    ServiceRegistrationActivityForm.newService.setSubcategory(ServiceRegistrationActivityForm.newService.getCategory());

                                                    //add to user containsService
                                                    usersRef.child("containsService").setValue(true);
                                                    usersRef.child("services").child(addServiceKey).setValue(ServiceRegistrationActivityForm.newService);
                                                    mRootRef.child("AllUsers").child("PublicData").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("services").child(addServiceKey).setValue(ServiceRegistrationActivityForm.newService);
                                                    mRootRef.child("AllUsers").child("PublicData").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("containsService").setValue(true);
                                                    mRootRef.child("Services").child("PrivateData").child(addServiceKey).setValue(ServiceRegistrationActivityForm.newService);
                                                    mRootRef.child("Services").child("PublicData").child(addServiceKey).setValue(ServiceRegistrationActivityForm.newService);
                                                    try {
                                                        endRegistration(newService.getUserUid());
//                                                        SharedPreferences preferences = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE);
//                                                        SharedPreferences.Editor editor = preferences.edit();
//                                                        editor.putString(Constants.APP_ID, newService.getUserUid());
//                                                        editor.putString(Constants.NAME, newService.getTitle());
//                                                        editor.putString(Constants.EMAIL, newService.getEmail());
//                                                        editor.putBoolean(Constants.IS_SERVICE, true);
//                                                        editor.putString(Constants.MY_CATEGORY, newService.getCategory());
//                                                        editor.putString(Constants.MY_SUB_CATEGORY, newService.getSubcategory());
//                                                        editor.putString(Constants.MY_SERVICE, newService.toJson());
//                                                        editor.putBoolean(Constants.IS_ANONYMOUS, false);
//                                                        editor.commit();
//
//                                                        FileOutputStream fileOutputStream = openFileOutput("myService.json", MODE_PRIVATE);
//                                                        Log.d(TAG, "endRegistration: " + newService.toString());
//                                                        fileOutputStream.write(newService.toString().getBytes());
//                                                        fileOutputStream.close();
//                                                        MyApplication.addListener(newService.getUserUid());
//                                                        Intent intent = new Intent(ServiceRegistrationActivityForm.this, ShareMyService.class);
//                                                        startActivity(intent);
//                                                        saving_service_prog.setVisibility(View.GONE);
////        finish();
//
//                                                        String newToken = FirebaseInstanceId.getInstance().getToken();
//                                                        if (newToken != null) {
//                                                            Device device = new Device();
//                                                            device.setPushToken(newToken);
//                                                            device.setUid(newService.getUserUid());
//                                                            String android_id = Settings.Secure.getString(getContentResolver(),
//                                                                    Settings.Secure.ANDROID_ID);
//                                                            FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/").child("Devices").child(android_id).setValue(device);
//                                                        }
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }


                                                }
                                            });

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {//if password not set
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: ");
                                        }
                                    });
                                }
                            });
                            //endRegistration
                        }else{// if re-authentication failed
                            Log.d(TAG, "User re-authenticated3. "+task.getException().getMessage());
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ServiceRegistrationActivityForm.this);
                            alertDialogBuilder
                                    .setMessage(task.getException().getMessage())
                                    .setCancelable(false)
                                    .setPositiveButton("הבנתי", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                        }
                                    });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();

                        }
                    }

                });




    }

    private void showInfoDialog(String message, String posBtn, final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ServiceRegistrationActivityForm.this);
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(posBtn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        Intent intent = new Intent(ServiceRegistrationActivityForm.this, ServiceRegistrationFragmentContainer.class);
                        intent.putExtra("fragmentNumber", position);
                        startActivity(intent);
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        saving_service_prog.setVisibility(View.GONE);
    }

    private void registerBusiness() {
        Log.d(TAG, "registerBusiness: ");

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(newService.getEmail(), newService.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String uid = task.getResult().getUser().getUid();
                    //create user with service
                    newUser = new User(newService.getTitle(), newService.getEmail(), uid, newService.getProfileUrl(), true);
                    newService.setUserUid(uid);
                    newService.setSubcategory(newService.getCategory());

                    if (newService.getEmail().endsWith("@gmail.com"))
                        newService.setEmail(newService.getEmail().replace("@gmail.com", "@mybizzgmail.com"));

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
                    Log.d(TAG, "onComplete: " + newServiceKey);

                    //add uid to device
                    Device device = new Device();
                    device.setUid(uid);
                    String newToken = FirebaseInstanceId.getInstance().getToken();
                    if (newToken != null)
                        device.setPushToken(newToken);
                    device.setSerialNumber(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                    mRootRef.child("Devices").child(device.getSerialNumber()).setValue(device);
                    try {
                        endRegistration(uid);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                saving_service_prog.setVisibility(View.GONE);
                Log.d(TAG, "onFailure: "+ServiceRegistrationActivityForm.newService.getEmail());
                FirebaseDatabase.getInstance().getReference().child("Exceptions").child(ServiceRegistrationActivityForm.newService.getEmail().replace(".", "ll")).setValue(e.getMessage());
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ServiceRegistrationActivityForm.this);
                // set dialog message
                alertDialogBuilder
                        .setMessage(e.getMessage().toString())
                        .setCancelable(false)
                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
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
        editor.putBoolean(Constants.IS_ANONYMOUS, false);
        editor.commit();

        //upload profile and update fields
        uploadFile(loadImageFromStorage(getBaseContext().getFilesDir() + "/profile.jpg"));
        FileOutputStream fileOutputStream = openFileOutput("myService.json", MODE_PRIVATE);
        Log.d(TAG, "endRegistration: " + newService.toString());
        fileOutputStream.write(newService.toString().getBytes());
        fileOutputStream.close();
        MyApplication.addListener(mUid);
        Intent intent = new Intent(ServiceRegistrationActivityForm.this, ShareMyService.class);
        startActivity(intent);
        saving_service_prog.setVisibility(View.GONE);
//        finish();

        String newToken = FirebaseInstanceId.getInstance().getToken();
        if (newToken != null) {
            Device device = new Device();
            device.setPushToken(newToken);
            device.setUid(mUid);
            String android_id = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/").child("Devices").child(android_id).setValue(device);
        }
    }


    private void getLocation(String state, String city, String address) {
        Geocoder geocoder = new Geocoder(getBaseContext());
        try {
            List<Address> list = geocoder.getFromLocationName(state + ", " + city + " " + address, 1);
            if (list != null && list.size() > 0) {
                Address add = list.get(0);
                Log.d(TAG, "getLocation: lat: " + add.getLatitude());
                Log.d(TAG, "getLocation: long: " + add.getLongitude());
                lat = add.getLatitude();
                lon = add.getLongitude();
                Log.d(TAG, "getLocation: " + add.getLocality());
                Log.d(TAG, "getLocation: " + add);
                LocationUtils.setLocationReg(lat, lon);
                ArrayList location = new ArrayList();
                location.add(lat);
                location.add(lon);
                ServiceRegistrationActivityForm.newService.setL(location);
                ;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void uploadFile(Bitmap bitmap) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://mybizz-3bbe5.appspot.com");
        StorageReference mountainImagesRef = storageRef.child("images/" + System.currentTimeMillis() + ".png");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = mountainImagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

                Uri downloadUrl = taskSnapshot.getUploadSessionUri();//.getDownloadUrl();
                ServiceRegistrationActivityForm.newService.setProfileUrl(downloadUrl.toString());
//                sendMsg("" + downloadUrl, 2);
                Log.d(TAG, "downloadUrl-->" + downloadUrl);
                final HashMap<String, Object> updateProfileUrl = new HashMap();
                updateProfileUrl.put("profileUrl", downloadUrl.toString());
                //set the profile url ini all needed places
                //1. all users -->privateData
                //2. all users -->privateData -->services-->pictureUrl
                //3. all users -->publicData
                //4. all users -->publicData -->services-->pictureUrl
                //5. services -->privateData -->serviceKey --> profile url
                //6. services -->publicData -->serviceKey --> profile url
                String serviceUid = ServiceRegistrationActivityForm.newService.getUserUid();
                String serviceKey = ServiceRegistrationActivityForm.newService.getKey();
                final DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PrivateData").child(serviceUid);
                final DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PrivateData").child(serviceUid).child("services").child(serviceKey);
                final DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PublicData").child(serviceUid);
                final DatabaseReference ref4 = FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PublicData").child(serviceUid).child("services").child(serviceKey);
                final DatabaseReference ref5 = FirebaseDatabase.getInstance().getReference().child("Services").child("PrivateData").child(serviceKey);
                final DatabaseReference ref6 = FirebaseDatabase.getInstance().getReference().child("Services").child("PublicData").child(serviceKey);


                //must check if service has been saved already, only if so the following fields mudt be updated, o/w, should not.
                ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            //if saved already in private data, update url in private data
                            ref1.updateChildren(updateProfileUrl);
                            ref2.updateChildren(updateProfileUrl);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                ref4.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            //if saved already in private data, update url in private data
                            ref3.updateChildren(updateProfileUrl);
                            ref4.updateChildren(updateProfileUrl);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                ref5.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            dataSnapshot.getRef().updateChildren(updateProfileUrl);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                ref6.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            dataSnapshot.getRef().updateChildren(updateProfileUrl);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

            }
        });
    }

    private Bitmap loadImageFromStorage(String path) {
        Log.d(TAG, "loadImageFromStorage: " + path);
        Bitmap b = null;

        try {
            File f = new File(path);
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return b;
    }
}
