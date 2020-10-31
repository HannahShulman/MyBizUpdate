package com.mybiz.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.database.FirebaseDatabase;
import com.mybiz.Constants;
import com.mybiz.R;
import com.mybiz.TabsActivity;

import java.util.Arrays;
import java.util.List;

public class ShareMyService extends AppCompatActivity {

    String TAG = "ShareMyService";
    Toolbar toolbar;
    TextView skip;
    Button facebookCoverBtn;
    CallbackManager callbackManager = CallbackManager.Factory.create();
    ShareDialog d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_my_service);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueToNextActivity();
            }
        });

        skip = (TextView) findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueToNextActivity();
            }
        });

        facebookCoverBtn = (Button) findViewById(R.id.facebook_cover_btn);
        facebookCoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                shareOnFacebook();
                d = new ShareDialog(ShareMyService.this);
                ShareContent content = new ShareLinkContent.Builder()
                        .setContentTitle("Check out  "+getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.NAME, "")+" on this amazing app!!")
                        .setContentDescription("aaa")
                        .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.app.mybiz"))
                        .build();
                d.show(ShareMyService.this, content);
                d.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {

                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException error) {

                    }
                });

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (data.getBundleExtra("com.facebook.platform.protocol.RESULT_ARGS").getString("completionGesture", "mmm").equals("post")){
            //add to firebase counter of sharing
            continueToNextActivity();
        }else{
            if (data.getBundleExtra("com.facebook.platform.protocol.RESULT_ARGS").getString("completionGesture", "mmm").equals("cancel")) {

            }
        }
    }

    private void continueToNextActivity(){
        startActivity(new Intent(ShareMyService.this, TabsActivity.class));

    }


    public void shareOnFacebook(){

        FacebookSdk.sdkInitialize(getApplicationContext());

        List<String> permissionNeeds = Arrays.asList("manage_pages", "publish_pages", "publish_actions");
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.logInWithPublishPermissions(ShareMyService.this, permissionNeeds);
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "onSuccess: "+loginResult.getRecentlyGrantedPermissions());


                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse("www.google.com"))
                        .build();

                ShareApi.share(content, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Log.d(TAG, "onSuccess1: "+ result.getPostId());
//

                        continueToNextActivity();
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "onCancel: ");
                        Toast.makeText(getBaseContext(), "Sharing has been canceled", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG, "onError: ");
                        Log.d(TAG, "onError1: "+error.getMessage().toString());
                        Toast.makeText(getBaseContext(), error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        FirebaseDatabase.getInstance().getReference().child("Facebook exception")
                                .child(getSharedPreferences(Constants.PREFERENCES,Context.MODE_PRIVATE)
                                        .getString(Constants.APP_ID, Constants.RANDOM_STRING)).setValue(error);
                    }
                });




            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel 168:42: ");
//                Log.d(TAG, "onError1: "+error.getMessage().toString());
//                Toast.makeText(getActivity(), error.getMessage().toString(), Toast.LENGTH_SHORT).show();
//                FirebaseDatabase.getInstance().getReference().child("Facebook exception").child(getActivity().getSharedPreferences(Constants.PREFERENCES,Context.MODE_PRIVATE).getString(Constants.APP_ID, Constants.RANDOM_STRING)).setValue(error);


            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "onError: ");
                Log.d(TAG, "onError179:40: "+error.getMessage().toString());
                Toast.makeText(getBaseContext(), error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                FirebaseDatabase.getInstance().getReference().child("Facebook exception").child(getSharedPreferences(Constants.PREFERENCES,Context.MODE_PRIVATE).getString(Constants.APP_ID, Constants.RANDOM_STRING)).setValue(error);


            }
        });



    }
}
