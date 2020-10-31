package com.mybiz.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RadioButton;
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
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.mybiz.Constants;
import com.mybiz.CreateSpecial;
import com.mybiz.Objects.Service;
import com.mybiz.Objects.Specials;
import com.mybiz.R;

import java.util.Arrays;
import java.util.List;

public class CreateOfferSecondFragment extends Fragment {

    String TAG = "offerScnd";
    RadioButton fbShareBtn, pp_payment;
    CallbackManager callbackManager = CallbackManager.Factory.create();
    ShareDialog d;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_create_offer_second_fragment, container, false);
        initViews(rootView);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        d = new ShareDialog(getActivity());
        return rootView;
    }


    public void initViews(View rootView) {
        fbShareBtn = (RadioButton) rootView.findViewById(R.id.fb_shareBtn);
        pp_payment = (RadioButton) rootView.findViewById(R.id.pp_payment);
    }

    public void publicizeOffer() {
        if (fbShareBtn.isChecked()) {
            Log.d(TAG, "publicizeOffer: ");
//            shareOnFacebook();
            ShareContent content = new ShareLinkContent.Builder()
                    .setContentTitle("Check out  " + getActivity().getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).getString(Constants.NAME, "") + " on this amazing app!!")
                    .setContentDescription("aaa")
                    .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.app.mybiz"))
                    .build();
            d.show(content);
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


            if (pp_payment.isChecked()) {
//            payWithPaypal();
            }
        }
    }


    public void shareOnFacebook() {
        Log.d(TAG, "shareOnFacebook: ");

        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());

        List<String> permissionNeeds = Arrays.asList("manage_pages", "publish_pages", "publish_actions");
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.logInWithPublishPermissions(getActivity(), Arrays.asList("publish_actions"));
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
//                if (loginResult.)
                Log.d(TAG, "onSuccess: " + loginResult.getRecentlyGrantedPermissions());
                Log.d(TAG, "onSuccess: " + CreateSpecial.offers.getImageUrl()); //registered

//                ShareLinkContent content = new ShareLinkContent.Builder()
//                        .setContentUrl(Uri.parse("www.google.com"))
//                        .build();
//


                ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
                        .putString("og:type", "apps.saves")
                        .putString("og:title", "Check out this app!")
                        .putString("og:url", "https://play.google.com/store/apps/details?id=com.app.mybiz")
                        .putString("books:isbn", "0-553-57340-3")
                        .build();
//

                ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                        .setActionType("books.reads")
                        .putObject("book", object)
                        .build();

                ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
                        .setPreviewPropertyName("book")
                        .setAction(action)
                        .build();
                ShareApi.share(content, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Log.d(TAG, "onSuccess1: " + result.getPostId());
                        Gson gson = new Gson();
                        String serviceJson = getActivity().getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).getString(Constants.MY_SERVICE, Constants.RANDOM_STRING);
                        if (!serviceJson.equals(Constants.RANDOM_STRING)) {
                            Log.d(TAG, "onSuccess: " + serviceJson.toString());
                            Service s = gson.fromJson(serviceJson, Service.class);
                            FirebaseDatabase.getInstance().getReference().child("Facebook exception 102:109").child(getActivity().getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).getString(Constants.APP_ID, Constants.RANDOM_STRING)).setValue(s.getTitle());
                            CreateSpecial.offers.setOfferCreator(s);
                            CreateSpecial.offers.setServiceKey(s.getKey());
                            String offerKey = FirebaseDatabase.getInstance().getReference().child("Offers").child(s.getCategory()).child(s.getSubcategory()).push().getKey();
                            CreateSpecial.offers.setOfferKey(offerKey);
                            CreateSpecial.offers.setServiceKey(s.getKey());
                            CreateSpecial.offers.setServiceTitle(s.getTitle());
                            CreateSpecial.offers.setServiceTown(s.getTown());
                            Log.d(TAG, "onSuccess: " + offerKey);
                            Log.d(TAG, "onSuccess: " + s.getUserUid());

                            //adding  to AllUsers-->PrivateData--> usrId-->services-->serviceKey-->Offers-->offerKey
                            FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PrivateData").child(s.getUserUid()).child("services")
                                    .child(s.getKey()).child("offers").child(offerKey).setValue(CreateSpecial.offers);

                            //adding  to AllUsers-->PublicData--> usrId-->services-->serviceKey-->Offers
                            FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PublicData").child(s.getUserUid()).child("services")
                                    .child(s.getKey()).child("offers").child(offerKey).setValue(CreateSpecial.offers);

                            //add to Services-->PrivateData-->ServiceKey-->Offers -->offerKey
                            FirebaseDatabase.getInstance().getReference().child("Services").child("PrivateData").child(s.getKey())
                                    .child("offers").child(offerKey).setValue(CreateSpecial.offers);


                            //add to Services-->PublicData-->ServiceKey-->Offers -->offerKey
                            FirebaseDatabase.getInstance().getReference().child("Services").child("PublicData").child(s.getKey())
                                    .child("offers").child(offerKey).setValue(CreateSpecial.offers);
                            //add to Offers

                            FirebaseDatabase.getInstance().getReference()
                                    .child("Offers").child(s.getCategory()).child(s.getSubcategory()).child(offerKey).setValue(CreateSpecial.offers);


                            CreateSpecial.offers = new Specials();
                            getActivity().finish();

                        } else {
                            Toast.makeText(getActivity(), "incorrect service", Toast.LENGTH_LONG).show();
                            FirebaseDatabase.getInstance().getReference().child("Facebook exception").child(getActivity().getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).getString(Constants.APP_ID, Constants.RANDOM_STRING)).setValue(serviceJson);
                        }
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "onCancel: ");
                        Toast.makeText(getActivity(), "Sharing has been canceled", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG, "onError: ");
                        Log.d(TAG, "onError1: " + error.getMessage().toString());
                        Toast.makeText(getActivity(), error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        FirebaseDatabase.getInstance().getReference().child("Facebook exception")
                                .child(getActivity().getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE)
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
                Log.d(TAG, "onError179:40: " + error.getMessage().toString());
                Toast.makeText(getActivity(), error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                FirebaseDatabase.getInstance().getReference().child("Facebook exception").child(getActivity().getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).getString(Constants.APP_ID, Constants.RANDOM_STRING)).setValue(error);


            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        callbackManager.onActivityResult(requestCode, responseCode, data);
        data.getCategories();
        Log.d(TAG, "onActivityResult: "+data.getBundleExtra("com.facebook.platform.protocol.RESULT_ARGS").getString("completionGesture", "mmm"));
        if (data.getBundleExtra("com.facebook.platform.protocol.RESULT_ARGS").getString("completionGesture", "mmm").equals("post")) {
            //add to firebase counter of sharing
            Gson gson = new Gson();
            String serviceJson = getActivity().getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).getString(Constants.MY_SERVICE, Constants.RANDOM_STRING);
            if (!serviceJson.equals(Constants.RANDOM_STRING)) {
                Log.d(TAG, "onSuccess: " + serviceJson.toString());
                Service s = gson.fromJson(serviceJson, Service.class);
                FirebaseDatabase.getInstance().getReference().child("Facebook exception 102:109").child(getActivity().getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).getString(Constants.APP_ID, Constants.RANDOM_STRING)).setValue(s.getTitle());
                CreateSpecial.offers.setOfferCreator(s);
                CreateSpecial.offers.setServiceKey(s.getKey());
                String offerKey = FirebaseDatabase.getInstance().getReference().child("Offers").child(s.getCategory()).child(s.getSubcategory()).push().getKey();
                CreateSpecial.offers.setOfferKey(offerKey);
                CreateSpecial.offers.setServiceKey(s.getKey());
                CreateSpecial.offers.setServiceTitle(s.getTitle());
                CreateSpecial.offers.setServiceTown(s.getTown());
                Log.d(TAG, "onSuccess: " + offerKey);
                Log.d(TAG, "onSuccess: " + s.getUserUid());

                //adding  to AllUsers-->PrivateData--> usrId-->services-->serviceKey-->Offers-->offerKey
                FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PrivateData").child(s.getUserUid()).child("services")
                        .child(s.getKey()).child("offers").child(offerKey).setValue(CreateSpecial.offers);

                //adding  to AllUsers-->PublicData--> usrId-->services-->serviceKey-->Offers
                FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PublicData").child(s.getUserUid()).child("services")
                        .child(s.getKey()).child("offers").child(offerKey).setValue(CreateSpecial.offers);

                //add to Services-->PrivateData-->ServiceKey-->Offers -->offerKey
                FirebaseDatabase.getInstance().getReference().child("Services").child("PrivateData").child(s.getKey())
                        .child("offers").child(offerKey).setValue(CreateSpecial.offers);


                //add to Services-->PublicData-->ServiceKey-->Offers -->offerKey
                FirebaseDatabase.getInstance().getReference().child("Services").child("PublicData").child(s.getKey())
                        .child("offers").child(offerKey).setValue(CreateSpecial.offers);
                //add to Offers

                FirebaseDatabase.getInstance().getReference()
                        .child("Offers").child(s.getCategory()).child(s.getSubcategory()).child(offerKey).setValue(CreateSpecial.offers);


                CreateSpecial.offers = new Specials();
                getActivity().finish();
            } else {
                if (data.getBundleExtra("com.facebook.platform.protocol.RESULT_ARGS").getString("completionGesture", "mmm").equals("cancel")) {

                }
            }
        }

    }
}
