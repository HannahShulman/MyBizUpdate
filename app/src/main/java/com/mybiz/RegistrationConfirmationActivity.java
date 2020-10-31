package com.mybiz;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mybiz.Activities.PrivateRegisterLocation;
import com.mybiz.Activities.ShareMyService;
import com.mybiz.Objects.Service;
import com.mybiz.Objects.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;



public class RegistrationConfirmationActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static int RC_SIGN_IN = 0;
    private static int FB_SIGN_IN = 1;
    private static String TAG = "RegistActivity";
    DatabaseReference ref;
    TextView google_sign_in_btn, termsAndConTextView;
    FrameLayout facebook_reg_layout;
    String addServiceKey;
    ImageView backIcon;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    Button facebookCoverBtn;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private CallbackManager callbackManager;
    TextView instruction;
    Toolbar toolbar;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_confirmation);
        facebook_reg_layout = (FrameLayout) findViewById(R.id.facebook_reg_layout);
        google_sign_in_btn = (TextView) findViewById(R.id.google_sign_in_btn);
        facebookCoverBtn = (Button) findViewById(R.id.facebook_cover_btn);
        backIcon = (ImageView) findViewById(R.id.back_icon);
        instruction = (TextView) findViewById(R.id.slogen);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.confirm_id));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        pb = (ProgressBar) findViewById(R.id.pb);
        ref = FirebaseDatabase.getInstance().getReferenceFromUrl(Constants.FIREBASE_URL);
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d("AUTH", "user logged in  " + user.getEmail());
                }
                else
                    Log.d("AUTH", "user logged out");
            }
        };


        //google sign in methods
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("960396926680-7g45c3kdn4n0lf6gaoagdpffkl7ubifs.apps.googleusercontent.com")
                .requestId()
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        google_sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: google");
                pb.setVisibility(View.VISIBLE);
                signIn();
            }
        });


        if (getIntent().getBooleanExtra("facebook", false)) {
            google_sign_in_btn.setVisibility(View.GONE);
//            instruction.setText(R.string.facebook_instruction);
            facebookCoverBtn.performClick();
        }
        else if (getIntent().getBooleanExtra("google", false)) {
            facebook_reg_layout.setVisibility(View.GONE);
            facebookCoverBtn.setVisibility(View.GONE);
//            instruction.setText(R.string.google_instruction);
            google_sign_in_btn.performClick();
        }

        //facebook sign in methods
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        final LoginButton loginButton = (LoginButton) findViewById(R.id.facebook_sign_in_btn);
        facebookCoverBtn = (Button) findViewById(R.id.facebook_cover_btn);
        facebookCoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.performClick();
            }
        });
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                signInWithFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                FirebaseDatabase.getInstance().getReference().child("Exceptions")
                        .child("FacebookLoginSignIn").child(Settings.Secure.getString(getContentResolver(),
                        Settings.Secure.ANDROID_ID)).setValue("PersonalRegistrationActivity/154:99"+"facebook canceled: ");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                FirebaseDatabase.getInstance().getReference().child("Exceptions")
                        .child("FacebookLoginSignIn").child(Settings.Secure.getString(getContentResolver(),
                        Settings.Secure.ANDROID_ID)).setValue("PersonalRegistrationActivity/159:99"+"facebook error: "+error);
            }
        });


    }


    //facebook methods
    private void signInWithFacebook(AccessToken token) {
        Log.d(TAG, "signInWithFacebook:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            signInAndContinue(task);
                        } else {
                            Log.d(TAG, "onFailure: " + task.getException().getMessage());
                            mAuth = FirebaseAuth.getInstance();
                            FirebaseDatabase.getInstance().getReference().child("Exceptions")
                                    .child("FacebookLoginSignIn").child(Settings.Secure.getString(getContentResolver(),
                                    Settings.Secure.ANDROID_ID)).setValue("PersonalRegistrationActivity/184:99"+"facebook reg error: "+task.getException().getMessage());

                        }
                    }
                });
    }


    //google sign in methods.
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            final GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                //link with given email and passward

                final AuthCredential credential = EmailAuthProvider.getCredential(ServiceRegistrationActivityForm.newService.getEmail(), ServiceRegistrationActivityForm.newService.getPassword());
                FirebaseAuth.getInstance().getCurrentUser().linkWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        FirebaseDatabase.getInstance().getReference().child("Exception").child("RegistratioConfirmationActivity").child();

                        if (task.isSuccessful()) {
//                            if (task.getResult().getUser().getUid().equals(getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.APP_ID, Constants.RANDOM_STRING))) {
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
                                    endRegistration(myUid);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    FirebaseDatabase.getInstance().getReference().child("Exception").child("RegistratioConfirmationActivity").child(e.getMessage().toString());
                                }
                            pb.setVisibility(View.GONE);

                        }

                        //if linking doesn't work
                        else{

                            if (task.getException().getMessage().toString().contains("CREDENTIAL_TOO_OLD_LOGIN_AGAIN")){
//                                FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        //if user has been deleted
//                                        if (task.isSuccessful()) {
//                                            //sign out google
////                                           FirebaseAuth.getInstance()=null;
////                                            google_sign_in_btn.setVisibility(View.VISIBLE);
//                                        }
//                                        if (!task.isSuccessful()){
//                                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegistrationConfirmationActivity.this);
//                                            alertDialogBuilder.setTitle(getString(R.string.demo_tender));
//                                            alertDialogBuilder
//                                                    .setMessage(task.getException().getMessage())
//                                                    .setCancelable(false)
//                                                    .setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
//                                                        public void onClick(DialogInterface dialog, int id) {
//
//                                                            dialog.dismiss();
//
//                                                        }
//                                                    });
//                                            AlertDialog alertDialog = alertDialogBuilder.create();
//                                            alertDialog.show();
//                                        }
//
//                                        //if user has not been deleted
//
//                                    }
//                                });

                                //re-authenticate user,
                                AuthCredential credential = null;
                                if (FirebaseAuth.getInstance().getCurrentUser().getProviderId().contains("google.com")){
//                                if (FirebaseAuth.getInstance().getCurrentUser().getProviders().contains("google.com")){
                                    //demi google login
                                    credential = GoogleAuthProvider.getCredential(getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString("googleToken", ""), null);
                                }else{//user signed in with facebook

                                }
                                FirebaseAuth.getInstance().getCurrentUser().reauthenticate(credential)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "User re-authenticated1.");
                                                    //update user profile
                                                    FirebaseAuth.getInstance().getCurrentUser().updateEmail(ServiceRegistrationActivityForm.newService.getEmail()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d(TAG, "User re-authenticated2: "+FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                                            FirebaseAuth.getInstance().getCurrentUser().updatePassword(ServiceRegistrationActivityForm.newService.getPassword()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    //update profile

                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
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

                                                }
                                            }

                                        });

                            }else {

                                Toast.makeText(getBaseContext(), "task un-successful", Toast.LENGTH_SHORT).show();
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegistrationConfirmationActivity.this);
                                alertDialogBuilder.setTitle(getString(R.string.demo_tender));
                                alertDialogBuilder
                                        .setMessage("some")
                                        .setCancelable(false)
                                        .setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                dialog.dismiss();

                                            }
                                        });
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }
                        }

                    }
                });
            }

            if (result.getStatus().isCanceled()){
                Toast.makeText(getBaseContext(), "task un-successful", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegistrationConfirmationActivity.this);
                alertDialogBuilder.setTitle(getString(R.string.demo_tender));
                alertDialogBuilder
                        .setMessage("oopsss... registration was canceled, please try again")
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }

            if (result.getStatus().isInterrupted()){
                Toast.makeText(getBaseContext(), "task un-successful", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegistrationConfirmationActivity.this);
                alertDialogBuilder.setTitle(getString(R.string.demo_tender));
                alertDialogBuilder
                        .setMessage("oops..., registration was interrupted, please try again")
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                Log.d(TAG, "google signing failed");
            }

        } else {//if request is not from google
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }



    private void endRegistration(String mUid) throws IOException {
        SharedPreferences preferences = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.APP_ID, mUid);
        editor.putString(Constants.NAME, ServiceRegistrationActivityForm.newService.getTitle());
        editor.putString(Constants.EMAIL, ServiceRegistrationActivityForm.newService.getEmail());
        editor.putBoolean(Constants.IS_SERVICE, true);
        editor.putString(Constants.MY_CATEGORY, ServiceRegistrationActivityForm.newService.getCategory());
        editor.putString(Constants.MY_SUB_CATEGORY, ServiceRegistrationActivityForm.newService.getSubcategory());
        editor.putString(Constants.MY_SERVICE, ServiceRegistrationActivityForm.newService.toJson());
        editor.putBoolean(Constants.IS_ANONYMOUS, false);
        editor.commit();

        //upload profile and update fields
        uploadFile(loadImageFromStorage(getBaseContext().getFilesDir()+"/profile.jpg"));
        FileOutputStream fileOutputStream = null;

            fileOutputStream = openFileOutput("myService.json", MODE_PRIVATE);
            fileOutputStream.write(ServiceRegistrationActivityForm.newService.toString().getBytes());
            fileOutputStream.close();
        Log.d(TAG, "endRegistration: " + ServiceRegistrationActivityForm.newService.toString());

        MyApplication.addListener(mUid);
        Intent intent = new Intent(RegistrationConfirmationActivity.this, ShareMyService.class);
        startActivity(intent);
//        saving_service_prog.setVisibility(View.GONE);
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
                Log.d(TAG , "downloadUrl-->" + downloadUrl);
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
                        if (dataSnapshot.exists()){
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
                        if (dataSnapshot.exists()){
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
                        if (dataSnapshot.exists()){
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
                        if (dataSnapshot.exists()){
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

    private Bitmap loadImageFromStorage(String path)
    {
        Log.d(TAG, "loadImageFromStorage: "+ path);
        Bitmap b = null;

        try {
            File f=new File(path);
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return b;
    }

    private void signInAndContinue(Task<AuthResult> task) {
        Log.d(TAG, "signInAndContinue: "+task.getResult().getUser().getUid());
        Log.d(TAG, "signInAndContinue: "+task.getResult().getUser().getDisplayName());
        Log.d(TAG, "signInAndContinue: "+task.getResult().getUser().getEmail());
        Log.d(TAG, "signInAndContinue: "+task.getResult().getUser().getPhotoUrl().toString());
        //basic info into
        final String uid = task.getResult().getUser().getUid();
        String name = task.getResult().getUser().getDisplayName();
        String email = task.getResult().getUser().getEmail();

        //user is online..


        SharedPreferences prefs = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE);
        prefs.edit().putString(Constants.APP_ID, uid)
                .putString(Constants.EMAIL, email)
                .putBoolean(Constants.IS_SERVICE, false)
                .putBoolean(Constants.IS_ANONYMOUS, false)
                .putString(Constants.NAME, name).commit();

        final User user = new User(name, email, uid, Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID));
        user.setOpenedAs("private");
        user.setProfileUrl(task.getResult().getUser().getPhotoUrl().toString());
        //add to users

        mRootRef.child("AllUsers").child("PublicData").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //check change
                boolean hasAddress = false;
                Log.d(TAG, "onDataChange: "+dataSnapshot.getValue());
                if (!dataSnapshot.exists() || ! dataSnapshot.hasChild("mName")) {
                    Log.d(TAG, "onDataChange: "+user.toString());
                    mRootRef.child("AllUsers").child("PublicData").child(uid).setValue(user);
                    mRootRef.child("AllUsers").child("PrivateData").child(uid).setValue(user);
                }else{
                    User user1 = dataSnapshot.getValue(User.class);
                    hasAddress= true;
                    Log.d(TAG, "onDataChange: "+user1);
                    if (user1.getServices().size()!=0){

                        Iterator it = user1.getServices().values().iterator();
                        Service service = (Service) user1.getServices().values().toArray()[0];

//                        Service service = user1.getServices().get(user1.getServices().values().iterator().next());
                        getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).edit()
                                .putBoolean(Constants.IS_ANONYMOUS, false)
                                .putString(Constants.NAME, service.getTitle())
                                .putString(Constants.MY_SERVICE, service.toJson())
                                .putString(Constants.MY_CATEGORY, service.getCategory())
                                .putString(Constants.MY_SUB_CATEGORY, service.getSubcategory())
                                .putString(Constants.EMAIL, service.getEmail())
                                .putBoolean(Constants.IS_SERVICE, true).commit();

                        if(service.l != null && service.l.size() > 1){
                            LocationUtils.setLocationReg(service.l.get(0), service.l.get(1));
                        }else {
                            if(user1.getLocation() != null && user1.getLocation().getLatitude() != 0){
                                LocationUtils.setLocationReg(user1.getLocation().getLatitude(), user1.getLocation().getLongitude());
                            }
                        }

                    }else {
                        if(user1.getLocation() != null && user1.getLocation().getLatitude() != 0){
                            LocationUtils.setLocationReg(user1.getLocation().getLatitude(), user1.getLocation().getLongitude());
                        }
                    }
                    Log.d(TAG, "onDataChange: "+user1.toString());
                    Map<String,Object> taskMap = new HashMap<String,Object>();
                    taskMap.put(Settings.Secure.getString(getContentResolver(),
                            Settings.Secure.ANDROID_ID), Settings.Secure.getString(getContentResolver(),
                            Settings.Secure.ANDROID_ID));
                    dataSnapshot.getRef().child("devices").updateChildren(taskMap);

                }
                MyApplication.addListener(uid);
                String newToken = FirebaseInstanceId.getInstance().getToken();
                if (newToken != null) {
                    Device device = new Device();
                    device.setPushToken(newToken);
                    device.setUid(uid);
                    String android_id = Settings.Secure.getString(getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                    FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/").child("Devices").child(android_id).setValue(device);
                }

                if(hasAddress) {
                    Intent i = new Intent(RegistrationConfirmationActivity.this, TabsActivity.class);
                    startActivity(i);
//                    finish();

                }else {
                    Intent intent = new Intent(RegistrationConfirmationActivity.this, PrivateRegisterLocation.class);
                    startActivity(intent);
//                    finish();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.EMAIL, Constants.RANDOM_STRING).equals(Constants.RANDOM_STRING))//if this is first time signing in with google
                                signInAndContinue(task);
                            else{
                                //if has already registered, can continue only if is same email
                                if (task.getResult().getUser().getEmail().equals(getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.EMAIL, Constants.RANDOM_STRING)))
                                    signInAndContinue(task);
                                else{//user is signning in with other email.
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegistrationConfirmationActivity.this);
                                    alertDialogBuilder
                                            .setMessage("You must sign in with the same email you signed in previously. ")
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
                        } else {
                            //
                            FirebaseDatabase.getInstance().getReference().child("Exceptions")
                                    .child("GoogleSignIn").child(Settings.Secure.getString(getContentResolver(),
                                    Settings.Secure.ANDROID_ID)).setValue("PersonalRegistrationActivity/340/108"+task.getException().getMessage().toString());
                        }

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null)
            mAuth.removeAuthStateListener(mAuthStateListener);
        mGoogleApiClient.disconnect();
    }
}

