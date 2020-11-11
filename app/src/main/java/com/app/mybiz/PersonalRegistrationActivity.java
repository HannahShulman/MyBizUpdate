package com.app.mybiz;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.iid.FirebaseInstanceId;
import com.app.mybiz.activities.PrivateRegisterLocation;
import com.app.mybiz.activities.TermsAndConditionsActivity;
import com.app.mybiz.Objects.Service;
import com.app.mybiz.Objects.User;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class PersonalRegistrationActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {
    private static int RC_SIGN_IN = 0;
    private static int FB_SIGN_IN = 1;
    private static String TAG = "privateRegistration";
    DatabaseReference ref;
    SignInButton google_sign_in_btn;
    TextView termsAndConTextView;
    ProgressBar pb;
    ImageView backIcon;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    Button facebookCoverBtn;
    public GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private CallbackManager callbackManager;
    private TextView termsConditions, privacyPolicy;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_registration);
        pb = (ProgressBar) findViewById(R.id.pb);

//        termsAndConTextView = (TextView) findViewById(R.id.textView2);
        backIcon = (ImageView) findViewById(R.id.back_icon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("960396926680-7g45c3kdn4n0lf6gaoagdpffkl7ubifs.apps.googleusercontent.com")
                .requestId()
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        termsConditions = (TextView) findViewById(R.id.terms_n_conditions);
        termsConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PersonalRegistrationActivity.this, TermsAndConditionsActivity.class);
                intent.putExtra("title", getString(R.string.t_n_c));
                intent.putExtra("Content", R.string.terms_and_conditions);
                startActivity(intent);
            }
        });

        privacyPolicy = (TextView) findViewById(R.id.privacy_policy);
        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PersonalRegistrationActivity.this, TermsAndConditionsActivity.class);
                intent.putExtra("title", getString(R.string.priva_pol_title));
                intent.putExtra("Content", R.string.full_privacy_policy);
                startActivity(intent);
            }
        });
//        termsAndConTextView.setClickable(true);
//        termsAndConTextView.setMovementMethod(LinkMovementMethod.getInstance());
//        String termAndConditions = "<a href='http://www.google.com'> Terms & Conditions </a>";
//        String  privacyPolicy = "<a href='http://www.google.com'> Google </a>";
////        termsAndConTextView.setText("By continuing you indicate you  have read and agreed \n to the "+Html.fromHtml(termAndConditions)+" and"+Html.fromHtml(privacyPolicy));
//        termsAndConTextView.setText("אל דאגה, לא נפרסם שום דבר בשמך ללא אישור"+
//        "\n\n"+Html.fromHtml(termAndConditions) );

        ref = FirebaseDatabase.getInstance().getReferenceFromUrl(Constants.FIREBASE_URL);


        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null)
                    Log.d(TAG, "user logged in  " + user.getEmail());
                else
                    Log.d(TAG, "user logged out");
            }
        };


        //google sign in methods
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("960396926680-7g45c3kdn4n0lf6gaoagdpffkl7ubifs.apps.googleusercontent.com")
                .requestId()
                .requestEmail()
                .build();

        //removed  on rerfactor
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this, this)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
//
//                .build();

        google_sign_in_btn = (SignInButton) findViewById(R.id.google_sign_in_btn);
        google_sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                signIn();
            }
        });


        //facebook sign in methods
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        final LoginButton loginButton = (LoginButton) findViewById(R.id.facebook_sign_in_btn);
        facebookCoverBtn = (Button) findViewById(R.id.facebook_cover_btn);
        facebookCoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
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
                        Settings.Secure.ANDROID_ID)).setValue("PersonalRegistrationActivity/154:99" + "facebook canceled: ");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                FirebaseDatabase.getInstance().getReference().child("Exceptions")
                        .child("FacebookLoginSignIn").child(Settings.Secure.getString(getContentResolver(),
                        Settings.Secure.ANDROID_ID)).setValue("PersonalRegistrationActivity/159:99" + "facebook error: " + error);
            }
        });


    }

    //facebook methods
    private void signInWithFacebook(AccessToken token) {
        Log.d(TAG, "signInWithFacebook:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).edit().putString("facebookToken", token.getToken().toString()).commit();

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            signInAndContinue(task);
                        } else {
//                            mAuth.signOut();
                            Log.d(TAG, "onFailure: " + task.getException().getMessage());
                            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PersonalRegistrationActivity.this);
                            alertDialogBuilder
                                    .setMessage("this facebook's email has been used already by a google user. please sign in using google, or other facebook")
                                    .setCancelable(false)
                                    .setPositiveButton("google", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();

                                        }
                                    })
                                    .setNegativeButton("facebook", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                            mAuth = FirebaseAuth.getInstance();

                            FirebaseDatabase.getInstance().getReference().child("Exceptions")
                                    .child("FacebookLoginSignIn").child(Settings.Secure.getString(getContentResolver(),
                                    Settings.Secure.ANDROID_ID)).setValue("PersonalRegistrationActivity/184:99" + "facebook reg error: " + task.getException().getMessage());

                        }
                    }
                });
    }


    //google sign in methods.

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            //            if (task.isSuccess()) {
//            if (result.isSuccess()) {
//                GoogleSignInAccount account = result.getSignInAccount();
//                firebaseAuthWithGoogle(account);
//            } else {
//                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PersonalRegistrationActivity.this);
//                alertDialogBuilder.setTitle(getString(R.string.demo_tender));
//                alertDialogBuilder
//                        .setMessage("oops..., something went wrong, please try again..")
//                        .setCancelable(false)
//                        .setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                dialog.dismiss();
//                            }
//                        });
//                AlertDialog alertDialog = alertDialogBuilder.create();
//                alertDialog.show();
//                FirebaseDatabase.getInstance().getReference().child("Exceptions")
//                        .child("GoogleSignIn").child(Settings.Secure.getString(getContentResolver(),
//                        Settings.Secure.ANDROID_ID)).setValue("PersonalRegistrationActivity/209:97" + "__result: " +
//                        result + "__requestCode: " + requestCode + "__data: " + data);
//            }
//        } else {
//            callbackManager.onActivityResult(requestCode, resultCode, data);
            task.addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()) {
                    GoogleSignInAccount account = result.getSignInAccount();
                    firebaseAuthWithGoogle(account);
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PersonalRegistrationActivity.this);
                    alertDialogBuilder.setTitle(getString(R.string.demo_tender));
                    alertDialogBuilder
//                                .setMessage("oops..., something went wrong, please try again..")
                            .setMessage(task1.getException().getLocalizedMessage())
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    FirebaseDatabase.getInstance().getReference().child("Exceptions")
                            .child("GoogleSignIn").child(Settings.Secure.getString(getContentResolver(),
                            Settings.Secure.ANDROID_ID)).setValue("PersonalRegistrationActivity/209:97" + "__result: " +
                            result + "__requestCode: " + requestCode + "__data: " + data);
                }
            });
        }
    }


    private void signInAndContinue(Task<AuthResult> task) {
        Log.d(TAG, "signInAndContinue: " + task.getResult().getUser().toString());
        //basic info into
        final String uid = task.getResult().getUser().getUid();
        String name = task.getResult().getUser().getDisplayName();
        String email = task.getResult().getUser().getEmail();
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
                Log.d(TAG, "onDataChange: " + dataSnapshot.getValue());
                if (!dataSnapshot.exists() || !dataSnapshot.hasChild("mName")) {
                    Log.d(TAG, "onDataChange: " + user.toString());
                    mRootRef.child("AllUsers").child("PublicData").child(uid).setValue(user);
                    mRootRef.child("AllUsers").child("PrivateData").child(uid).setValue(user);
                } else {
                    User user1 = dataSnapshot.getValue(User.class);
                    hasAddress = true;
                    Log.d(TAG, "onDataChange: " + user1);
                    if (user1.getServices().size() != 0) {

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

                        if (service.l != null && service.l.size() > 1) {
                            LocationUtils.setLocationReg(service.l.get(0), service.l.get(1));
                        } else {
                            if (user1.getLocation() != null && user1.getLocation().getLatitude() != 0) {
                                LocationUtils.setLocationReg(user1.getLocation().getLatitude(), user1.getLocation().getLongitude());
                            }
                        }

                    } else {
                        if (user1.getLocation() != null && user1.getLocation().getLatitude() != 0) {
                            LocationUtils.setLocationReg(user1.getLocation().getLatitude(), user1.getLocation().getLongitude());
                        }
                    }
                    Log.d(TAG, "onDataChange: " + user1.toString());
                    Map<String, Object> taskMap = new HashMap<String, Object>();
                    taskMap.put(Settings.Secure.getString(getContentResolver(),
                            Settings.Secure.ANDROID_ID), Settings.Secure.getString(getContentResolver(),
                            Settings.Secure.ANDROID_ID));
                    dataSnapshot.getRef().child("devices").updateChildren(taskMap);

                }
                MyApplication.addListener(uid);
                String newToken = "";//FirebaseInstanceId.getInstance().getToken();
                if (newToken != null) {
                    Device device = new Device();
                    device.setPushToken(newToken);
                    device.setUid(uid);
                    String android_id = Settings.Secure.getString(getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                    FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/").child("Devices").child(android_id).setValue(device);
                }


                if (hasAddress) {
                    Intent i = new Intent(PersonalRegistrationActivity.this, TabsActivity.class);
                    startActivity(i);
//                    finish();

                } else {
                    Intent intent = new Intent(PersonalRegistrationActivity.this, PrivateRegisterLocation.class);
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
        Log.d(TAG, "credentials  addServiceToUser: " + account.getIdToken());

        getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).edit().putString("googleToken", account.getIdToken()).commit();
        Log.d(TAG, "firebaseAuthWithGoogle:  " + account.getIdToken() + "__");
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.EMAIL, Constants.RANDOM_STRING).equals(Constants.RANDOM_STRING))//if this is first time signing in with google
                                signInAndContinue(task);
                            else {
                                //if has already registered, can continue only if is same email
                                if (task.getResult().getUser().getEmail().equals(getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.EMAIL, Constants.RANDOM_STRING)))
                                    signInAndContinue(task);
                                else {//user is signning in with other email.
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PersonalRegistrationActivity.this);
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
                                    Settings.Secure.ANDROID_ID)).setValue("PersonalRegistrationActivity/340/108" + task.getException().getMessage().toString());
                        }

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
//        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null)
            mAuth.removeAuthStateListener(mAuthStateListener);
//        mGoogleApiClient.disconnect();
    }
}

