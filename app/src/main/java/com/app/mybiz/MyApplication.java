package com.app.mybiz;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import com.app.mybiz.Managers.FavoriteServiceManager;
import com.app.mybiz.objects.Service;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by hannashulmah on 11/12/2016.
 */
public class MyApplication extends Application {
    private static FirebaseDatabase mDatabase;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    public static String android_id;
    public String TAG = "MyApp";
    static ArrayList<String> categoryList;
    private static Context mContext;


    public static Context getAppContext() {
        return mContext;
    }


    @Override
    public void onCreate() {
        super.onCreate();
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseAuth.getInstance();
        LocationUtils.init(this);
        MybizzNotificationManager.getInstance(this);
        FavoriteServiceManager.getInstance(this);
        UpdatesFromServer.updateCategoriesFromServer();
        mContext = this.getApplicationContext();

        MybizzNotificationManager.getInstance(mContext).manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        getSharedPreferences(PreferenceKeys.PREFERENCES, MODE_PRIVATE).edit().putString(PreferenceKeys.MY_NOTIFICATIONS, PreferenceKeys.DEFAULT_NOTIFICATION).commit();
        DatabaseReference sub_ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/");
        getSharedPreferences(PreferenceKeys.PREFERENCES, MODE_PRIVATE).edit().putString(PreferenceKeys.FIRST_TIME_DEVICE, "firstTime").commit();
        //remove device from other uids
        if (!getSharedPreferences(PreferenceKeys.PREFERENCES, MODE_PRIVATE).getString(PreferenceKeys.APP_ID, PreferenceKeys.RANDOM_STRING).equals(PreferenceKeys.RANDOM_STRING)) {

        } else {
            FirebaseDatabase.getInstance().getReference().child("Devices").child(Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Log.d(TAG, "onDataChange: " + dataSnapshot.getValue());
                                Device d = ((Device)dataSnapshot.getValue());
//                                Device d = dataSnapshot.getValue(Device.class);
                                String uid = d.getUid();
                                if (uid != null) {
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PublicData").child(uid).child("devices");
                                    Map<String, Object> map = new HashMap<String, Object>();
                                    map.put(Settings.Secure.getString(getContentResolver(),
                                            Settings.Secure.ANDROID_ID), null);
                                    ref.updateChildren(map);
                                }

                                FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/Devices/" + Settings.Secure.getString(getContentResolver(),
                                        Settings.Secure.ANDROID_ID) + "/uid").setValue(null);

                            } else {

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }

//            if (FirebaseAuth.getInstance().getCurrentUser() != null && FirebaseAuth.getInstance().getCurrentUser().getUid() != null) {
        String userUid = getSharedPreferences(PreferenceKeys.PREFERENCES, MODE_PRIVATE).getString(PreferenceKeys.APP_ID, PreferenceKeys.RANDOM_STRING);
        if (!userUid.equals(PreferenceKeys.RANDOM_STRING)) {
            sub_ref.child("AllUsers").child("PrivateData").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Device").setValue(android_id);
        }
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);


        if (FirebaseAuth.getInstance().getCurrentUser() != null && getSharedPreferences(PreferenceKeys.PREFERENCES, MODE_PRIVATE).getBoolean(PreferenceKeys.IS_SERVICE, false)) {
            //get my service
            try {
                JSONObject myService = new JSONObject(getSharedPreferences(PreferenceKeys.PREFERENCES, MODE_PRIVATE).getString(PreferenceKeys.MY_SERVICE, PreferenceKeys.RANDOM_STRING));
                FirebaseDatabase.getInstance().getReference().child("Services").child("PrivateData")
                        .child(myService.getString("key")).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Service service = dataSnapshot.getValue(Service.class);
                        getSharedPreferences(PreferenceKeys.PREFERENCES, MODE_PRIVATE).edit().putString(PreferenceKeys.MY_SERVICE, service.toString()).commit();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //get service and update info

        }

//        add categories
//        Category one = new Category("furniture", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/Category%20Profile%2F081416-furniture-thumbnail-bedroom.jpg?alt=media&token=e044b81f-1ee2-4cd3-90a8-6f5ace2702e5");
//        HashMap<String, Category> map = one.getSubCategoroies();
//        map.put(map.size()+"a", new Category("furniture", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/Category%20Profile%2F081416-furniture-thumbnail-bedroom.jpg?alt=media&token=e044b81f-1ee2-4cd3-90a8-6f5ace2702e5"));
//        map.put(map.size()+"a", new Category("furniture1", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/Category%20Profile%2F081416-furniture-thumbnail-bedroom.jpg?alt=media&token=e044b81f-1ee2-4cd3-90a8-6f5ace2702e5"));
//        map.put(map.size()+"a", new Category("furniture2", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/Category%20Profile%2F081416-furniture-thumbnail-bedroom.jpg?alt=media&token=e044b81f-1ee2-4cd3-90a8-6f5ace2702e5"));
//        map.put(map.size()+"a", new Category("furniture3", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/Category%20Profile%2F081416-furniture-thumbnail-bedroom.jpg?alt=media&token=e044b81f-1ee2-4cd3-90a8-6f5ace2702e5"));
//        one.setSubCategoroies(map);
//
//
//
//        DatabaseReference profRef =  FirebaseDatabase.getInstance().getReference().child("Categories").child("Professional");
//        Category books = new Category("ספרים חדש", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%97%D7%A0%D7%95%D7%99%D7%95%D7%AA%20%D7%A1%D7%A4%D7%A8%D7%99%D7%9D32.png?alt=media&token=702ceec7-027c-4a52-bf3b-37114c5831d9");
//        HashMap<String, Category> map3 = books.getSubCategoroies();
//        map3.put(map3.size()+"a", new Category("ספרים חדש", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%97%D7%A0%D7%95%D7%99%D7%95%D7%AA%20%D7%A1%D7%A4%D7%A8%D7%99%D7%9D32.png?alt=media&token=702ceec7-027c-4a52-bf3b-37114c5831d9"));
//        books.setSubCategoroies(map3);
//        profRef.child(books.getTitle()).setValue(books);
//
//        Category chesbon = new Category("רח חדש", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%A8%D7%90%D7%99%D7%99%D7%AA%20%D7%97%D7%A9%D7%91%D7%95%D7%9F32.png?alt=media&token=833beaf7-ae09-407c-8a95-8f9f0a4fe33c");
//        HashMap<String, Category> mapPainter = chesbon.getSubCategoroies();
//        mapPainter.put(mapPainter.size()+"a", new Category("רח חדש", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%A8%D7%90%D7%99%D7%99%D7%AA%20%D7%97%D7%A9%D7%91%D7%95%D7%9F32.png?alt=media&token=833beaf7-ae09-407c-8a95-8f9f0a4fe33c"));
//        chesbon.setSubCategoroies(mapPainter);
//        profRef.child(chesbon.getTitle()).setValue(chesbon);
////
//        Category pestController = new Category("מדביר", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%9E%D7%93%D7%91%D7%99%D7%A8.png?alt=media&token=ac56c5d5-9722-4656-8233-6036eb1fb497");
//        HashMap<String, Category> pestControllerMap = pestController.getSubCategoroies();
//        pestControllerMap.put(pestControllerMap.size()+"a", new Category("מדביר", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%9E%D7%93%D7%91%D7%99%D7%A8.png?alt=media&token=ac56c5d5-9722-4656-8233-6036eb1fb497"));
//        pestController.setSubCategoroies(pestControllerMap);
//        profRef.child(pestController.getTitle()).setValue(pestController);
//
//        Category pcTechnition = new Category("טכנאי מחשב", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%98%D7%9B%D7%A0%D7%90%D7%99%20%D7%9E%D7%97%D7%A9%D7%91%D7%99%D7%9D%202.png?alt=media&token=17953d42-e885-49ac-bfe6-180ef3306ccb");
//        HashMap<String, Category> pcTechnitionMap = pcTechnition.getSubCategoroies();
//        pcTechnitionMap.put(pcTechnitionMap.size()+"a", new Category("טכנאי מחשב", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%98%D7%9B%D7%A0%D7%90%D7%99%20%D7%9E%D7%97%D7%A9%D7%91%D7%99%D7%9D%202.png?alt=media&token=17953d42-e885-49ac-bfe6-180ef3306ccb"));
//        pcTechnition.setSubCategoroies(pcTechnitionMap);
//        profRef.child(pcTechnition.getTitle()).setValue(pcTechnition);
//
//        Category plumber = new Category("אינסטלטור", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%90%D7%99%D7%A0%D7%A1%D7%98%D7%9C%D7%98%D7%95%D7%A8.png?alt=media&token=ec6a727c-92bf-46fb-be81-e870e775d8d3");
//        HashMap<String, Category>plumberMap = plumber.getSubCategoroies();
//        plumberMap.put(plumberMap.size()+"a", new Category("אינסטלטור", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%90%D7%99%D7%A0%D7%A1%D7%98%D7%9C%D7%98%D7%95%D7%A8.png?alt=media&token=ec6a727c-92bf-46fb-be81-e870e775d8d3"));
//        plumber.setSubCategoroies(plumberMap);
//        profRef.child(plumber.getTitle()).setValue(plumber);
//
//        Category photographer = new Category("צלם", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%A6%D7%9C%D7%9D%202.png?alt=media&token=4b6c8af2-fa8d-415f-a5ee-93fdb798e60d");
//        HashMap<String, Category> photographerMap = photographer.getSubCategoroies();
//        photographerMap.put(photographerMap.size()+"a", new Category("צלם", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%A6%D7%9C%D7%9D%202.png?alt=media&token=4b6c8af2-fa8d-415f-a5ee-93fdb798e60d"));
//        photographer.setSubCategoroies(photographerMap);
//        profRef.child(photographer.getTitle()).setValue(photographer);
//
//        Category gasTechnition = new Category("טכנאי גז", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%98%D7%9B%D7%A0%D7%90%D7%99%20%D7%92%D7%96.png?alt=media&token=ac169423-ba39-4efa-be33-7abab722040a");
//        HashMap<String, Category> gasTechnitionMap = gasTechnition.getSubCategoroies();
//        gasTechnitionMap.put(gasTechnitionMap.size()+"a", new Category("טכנאי גז", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%98%D7%9B%D7%A0%D7%90%D7%99%20%D7%92%D7%96.png?alt=media&token=ac169423-ba39-4efa-be33-7abab722040a"));
//        gasTechnition.setSubCategoroies(gasTechnitionMap);
//        profRef.child(gasTechnition.getTitle()).setValue(gasTechnition);
//
//        Category vitrinar = new Category("וטרינר", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%95%D7%99%D7%98%D7%A8%D7%99%D7%A0%D7%A8.png?alt=media&token=17576da0-f4e1-41d5-95e1-53178994254c");
//        HashMap<String, Category> vitrinarMap = vitrinar.getSubCategoroies();
//        vitrinarMap.put(vitrinarMap.size()+"a", new Category("וטרינר", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%95%D7%99%D7%98%D7%A8%D7%99%D7%A0%D7%A8.png?alt=media&token=17576da0-f4e1-41d5-95e1-53178994254c"));
//        vitrinar.setSubCategoroies(vitrinarMap);
//        profRef.child(vitrinar.getTitle()).setValue(vitrinar);
//
//        Category glassCutter = new Category("זגג", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%96%D7%92%D7%92.png?alt=media&token=1c181263-7f59-418c-bb2b-7251da409302");
//        HashMap<String, Category> glassCutterMap = glassCutter.getSubCategoroies();
//        glassCutterMap.put(glassCutterMap.size()+"a", new Category("זגג", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%96%D7%92%D7%92.png?alt=media&token=1c181263-7f59-418c-bb2b-7251da409302"));
//        glassCutter.setSubCategoroies(glassCutterMap);
//        profRef.child(glassCutter.getTitle()).setValue(glassCutter);
//
//        Category security = new Category("אבטחה", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%90%D7%91%D7%98%D7%97%D7%94%202.png?alt=media&token=012e7fd4-d9b0-42b3-8399-df20816fb723");
//        HashMap<String, Category> securityMap = security.getSubCategoroies();
//        securityMap.put(securityMap.size()+"a", new Category("אבטחה", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%90%D7%91%D7%98%D7%97%D7%94%202.png?alt=media&token=012e7fd4-d9b0-42b3-8399-df20816fb723"));
//        security.setSubCategoroies(glassCutterMap);
//        profRef.child(security.getTitle()).setValue(security);
//
//
//        Category dj = new Category("תקליטן", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%AA%D7%A7%D7%9C%D7%99%D7%98%D7%9F.png?alt=media&token=a56c7f24-3495-4099-9662-d5ad516c1cba");
//        HashMap<String, Category> djMap = dj.getSubCategoroies();
//        djMap.put(djMap.size()+"a", new Category("תקליטן", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%AA%D7%A7%D7%9C%D7%99%D7%98%D7%9F.png?alt=media&token=a56c7f24-3495-4099-9662-d5ad516c1cba"));
//        dj.setSubCategoroies(djMap);
//        profRef.child(dj.getTitle()).setValue(dj);
//
//        Category contractor = new Category("קבלן", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%A7%D7%91%D7%9C%D7%9F.png?alt=media&token=59129c6b-5ea2-4e44-8b42-ac52e931c90a");
//        HashMap<String, Category> contractorMap = contractor.getSubCategoroies();
//        contractorMap.put(contractorMap.size()+"a", new Category("קבלן", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%A7%D7%91%D7%9C%D7%9F.png?alt=media&token=59129c6b-5ea2-4e44-8b42-ac52e931c90a"));
//        contractor.setSubCategoroies(contractorMap);
//        profRef.child(contractor.getTitle()).setValue(contractor);
//
//
//        Category lawyer = new Category("עורכי דין", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%A2%D7%A8%D7%99%D7%9B%D7%AA%20%D7%93%D7%99%D7%9F.png?alt=media&token=1ff8c4d5-54b3-4285-aa11-622990d08f60");
//        HashMap<String, Category> lawyerMap = lawyer.getSubCategoroies();
//        lawyerMap.put(lawyerMap.size()+"a", new Category("עורכי דין", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%A2%D7%A8%D7%99%D7%9B%D7%AA%20%D7%93%D7%99%D7%9F.png?alt=media&token=1ff8c4d5-54b3-4285-aa11-622990d08f60"));
//        lawyer.setSubCategoroies(lawyerMap);
//        profRef.child(lawyer.getTitle()).setValue(lawyer);
//
//
//        Category Handyman = new Category("שיפוצניק", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%A9%D7%99%D7%A4%D7%95%D7%A6%D7%95%D7%99%D7%9D.png?alt=media&token=7650ddc4-4232-4ba3-8707-f0e03eab15fc");
//        HashMap<String, Category> HandymanMap = Handyman.getSubCategoroies();
//        lawyerMap.put(HandymanMap.size()+"a", new Category("שיפוצניק", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%A9%D7%99%D7%A4%D7%95%D7%A6%D7%95%D7%99%D7%9D.png?alt=media&token=7650ddc4-4232-4ba3-8707-f0e03eab15fc"));
//        Handyman.setSubCategoroies(HandymanMap);
//        profRef.child(Handyman.getTitle()).setValue(Handyman);
//
//        Category drivingTeacher = new Category("לימוד נהיגה", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%9E%D7%95%D7%A8%D7%94%20%D7%A0%D7%94%D7%99%D7%92%D7%94.png?alt=media&token=5e529255-61f0-4855-a238-219cc75d8801");
//        HashMap<String, Category> drivingTeacherMap = drivingTeacher.getSubCategoroies();
//        drivingTeacherMap.put(drivingTeacherMap.size()+"a", new Category("לימוד נהיגה", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%9E%D7%95%D7%A8%D7%94%20%D7%A0%D7%94%D7%99%D7%92%D7%94.png?alt=media&token=5e529255-61f0-4855-a238-219cc75d8801"));
//        drivingTeacher.setSubCategoroies(drivingTeacherMap);
//        profRef.child(drivingTeacher.getTitle()).setValue(drivingTeacher);
//
//
//        Category cosmetics = new Category("קוסמטיקה", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%A7%D7%95%D7%A1%D7%9E%D7%98%D7%99%D7%A7%D7%94.png?alt=media&token=77b8db59-2e37-43cf-a3ef-945d7e64dc32");
//        HashMap<String, Category> cosmeticsMap = cosmetics.getSubCategoroies();
//        cosmeticsMap.put(cosmeticsMap.size()+"a", new Category("קוסמטיקה", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%A7%D7%95%D7%A1%D7%9E%D7%98%D7%99%D7%A7%D7%94.png?alt=media&token=77b8db59-2e37-43cf-a3ef-945d7e64dc32"));
//        cosmetics.setSubCategoroies(cosmeticsMap);
//        profRef.child(cosmetics.getTitle()).setValue(cosmetics);
//
//
//        DatabaseReference BusinessRef =  FirebaseDatabase.getInstance().getReference().child("Categories").child("Business");
//
//        Category yellowHasaot = new Category("הסעות", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%94%D7%A1%D7%A2%D7%95%D7%AA%20%D7%A6%D7%94%D7%95%D7%91.png?alt=media&token=cbbc8e64-e502-4149-ad6f-bdf0d54ed168");
//        HashMap<String, Category> yellowHasaotMap = yellowHasaot.getSubCategoroies();
//        yellowHasaotMap.put(yellowHasaotMap.size()+"a", new Category("הסעות", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%94%D7%A1%D7%A2%D7%95%D7%AA%20%D7%A6%D7%94%D7%95%D7%91.png?alt=media&token=cbbc8e64-e502-4149-ad6f-bdf0d54ed168"));
//        yellowHasaot.setSubCategoroies(yellowHasaotMap);
//        BusinessRef.child(yellowHasaot.getTitle()).setValue(yellowHasaot);
//
//
//        Category Trailers = new Category("גרר", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%92%D7%A8%D7%A8.png?alt=media&token=134b4e44-2698-4fc8-b5c8-9e3c6c0e664f");
//        HashMap<String, Category> TrailersMap = Trailers.getSubCategoroies();
//        TrailersMap.put(TrailersMap.size()+"a", new Category("גרר", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%92%D7%A8%D7%A8.png?alt=media&token=134b4e44-2698-4fc8-b5c8-9e3c6c0e664f"));
//        Trailers.setSubCategoroies(TrailersMap);
//        BusinessRef.child(Trailers.getTitle()).setValue(Trailers);
////   https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%94%D7%A1%D7%A2%D7%95%D7%AA.jpg?alt=media&token=8e676aea-bbba-4ebd-8219-4db453073631
//
//
//        Category hasaot = new Category("הסעות", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%94%D7%A1%D7%A2%D7%95%D7%AA.jpg?alt=media&token=8e676aea-bbba-4ebd-8219-4db453073631");
//        HashMap<String, Category> hasaotMap = hasaot.getSubCategoroies();
//        hasaotMap.put(hasaotMap.size()+"a", new Category("הסעות", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%94%D7%A1%D7%A2%D7%95%D7%AA.jpg?alt=media&token=8e676aea-bbba-4ebd-8219-4db453073631"));
//        hasaot.setSubCategoroies(hasaotMap);
//        BusinessRef.child(hasaot.getTitle()).setValue(hasaot);
//
//
//
//        Category hovalot = new Category("הובלות", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%94%D7%95%D7%91%D7%9C%D7%95%D7%AA.png?alt=media&token=3936371d-5e9c-435f-8c98-e9c05898db76");
//        HashMap<String, Category> hovalotMap = hovalot.getSubCategoroies();
//        hovalotMap.put(hovalotMap.size()+"a", new Category("הובלות", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%94%D7%95%D7%91%D7%9C%D7%95%D7%AA.png?alt=media&token=3936371d-5e9c-435f-8c98-e9c05898db76"));
//        hovalot.setSubCategoroies(hovalotMap);
//        BusinessRef.child(hovalot.getTitle()).setValue(hovalot);
//
//
//        Category garage = new Category("מוסך", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%9E%D7%95%D7%A1%D7%9A%201.png?alt=media&token=870ff82e-38ad-4986-bc26-e6dc27367866");
//        HashMap<String, Category> garageMap = garage.getSubCategoroies();
//        garageMap.put(garageMap.size()+"a", new Category("מוסך", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%9E%D7%95%D7%A1%D7%9A%201.png?alt=media&token=870ff82e-38ad-4986-bc26-e6dc27367866"));
//        garage.setSubCategoroies(garageMap);
//        BusinessRef.child(garage.getTitle()).setValue(garage);
//
//
//        Category babySitter1 = new Category("בייביסיטר", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%91%D7%99%D7%99%D7%91%D7%99%D7%A1%D7%99%D7%98%D7%A8%201.png?alt=media&token=69265e2d-c501-4e65-8c06-6fc72e529c0b");
//        HashMap<String, Category> babySitter1Map = babySitter1.getSubCategoroies();
//        babySitter1Map.put(babySitter1Map.size()+"a", new Category("בייביסיטר", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%91%D7%99%D7%99%D7%91%D7%99%D7%A1%D7%99%D7%98%D7%A8%201.png?alt=media&token=69265e2d-c501-4e65-8c06-6fc72e529c0b"));
//        babySitter1.setSubCategoroies(babySitter1Map);
//        profRef.child(babySitter1.getTitle()).setValue(babySitter1);
//
//        Category privateTeaching = new Category("הוראה פרטית", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%94%D7%95%D7%A8%D7%90%D7%94%20%D7%A4%D7%A8%D7%98%D7%99%D7%AA.png?alt=media&token=24a22a94-ed19-4853-ae07-ce638fda7aaf");
//        HashMap<String, Category> privateTeachingMap = privateTeaching.getSubCategoroies();
//        privateTeachingMap.put(privateTeachingMap.size()+"a", new Category("הוראה פרטית", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%94%D7%95%D7%A8%D7%90%D7%94%20%D7%A4%D7%A8%D7%98%D7%99%D7%AA.png?alt=media&token=24a22a94-ed19-4853-ae07-ce638fda7aaf"));
//        privateTeaching.setSubCategoroies(privateTeachingMap);
//        profRef.child(privateTeaching.getTitle()).setValue(privateTeaching);
//
////        Category books = new Category("ספרים", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%A1%D7%A4%D7%A8%D7%99%D7%9D.png?alt=media&token=14b84711-0af3-40c4-a9a8-131412173d13");
////        HashMap<String, Category> booksMap = books.getSubCategoroies();
////        booksMap.put(booksMap.size()+"a", new Category("ספרים", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%A1%D7%A4%D7%A8%D7%99%D7%9D.png?alt=media&token=14b84711-0af3-40c4-a9a8-131412173d13"));
////        books.setSubCategoroies(booksMap);
////        BusinessRef.child(books.getTitle()).setValue(books);
//
//        Category computers = new Category("מחשבים", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%9E%D7%97%D7%A9%D7%91%D7%99%D7%9D.png?alt=media&token=eae20afd-a954-4726-ae25-96a1795a6947");
//        HashMap<String, Category> computersMap = computers.getSubCategoroies();
//        computersMap.put(computersMap.size()+"a", new Category("מחשבים", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%9E%D7%97%D7%A9%D7%91%D7%99%D7%9D.png?alt=media&token=eae20afd-a954-4726-ae25-96a1795a6947"));
//        computers.setSubCategoroies(computersMap);
//        BusinessRef.child(computers.getTitle()).setValue(computers);
//
//        Category elect = new Category("מוצרי חשמל", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%9E%D7%95%D7%A6%D7%A8%D7%99%20%D7%97%D7%A9%D7%9E%D7%9C.png?alt=media&token=0d697974-dcbe-43de-90ac-4a6d1107883e");
//        HashMap<String, Category> electMap = elect.getSubCategoroies();
//        electMap.put(electMap.size()+"a", new Category("מוצרי חשמל", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%9E%D7%95%D7%A6%D7%A8%D7%99%20%D7%97%D7%A9%D7%9E%D7%9C.png?alt=media&token=0d697974-dcbe-43de-90ac-4a6d1107883e"));
//        elect.setSubCategoroies(electMap);
//        BusinessRef.child(elect.getTitle()).setValue(elect);
//
//        Category dress = new Category("הלבשה", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%94%D7%9C%D7%91%D7%A9%D7%94.png?alt=media&token=9d5619e3-ef0f-4511-812e-61b2985f022c");
//        HashMap<String, Category> dressMap = dress.getSubCategoroies();
//        dressMap.put(dressMap.size()+"a", new Category("הלבשה", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%94%D7%9C%D7%91%D7%A9%D7%94.png?alt=media&token=9d5619e3-ef0f-4511-812e-61b2985f022c"));
//        dress.setSubCategoroies(dressMap);
//        BusinessRef.child(dress.getTitle()).setValue(dress);
//
//        Category accounting = new Category("ראיית חשבון", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%A8%D7%90%D7%99%D7%99%D7%AA%20%D7%97%D7%A9%D7%91%D7%95%D7%9F.png?alt=media&token=8eed8b52-0040-42a4-b627-3a301bfddeb8");
//        HashMap<String, Category> accountingMap = accounting.getSubCategoroies();
//        accountingMap.put(accountingMap.size()+"a", new Category("ראיית חשבון", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%A8%D7%90%D7%99%D7%99%D7%AA%20%D7%97%D7%A9%D7%91%D7%95%D7%9F.png?alt=media&token=8eed8b52-0040-42a4-b627-3a301bfddeb8"));
//        accounting.setSubCategoroies(accountingMap);
//        BusinessRef.child(accounting.getTitle()).setValue(accounting);
//
//        Category furniture = new Category("רהיטים", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%A8%D7%94%D7%99%D7%98%D7%99%D7%9D.png?alt=media&token=ed42995e-672b-49aa-94d5-00b5dfdf93b0");
//        HashMap<String, Category> furnitureMap = furniture.getSubCategoroies();
//        furnitureMap.put(furnitureMap.size()+"a", new Category("רהיטים", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%A8%D7%94%D7%99%D7%98%D7%99%D7%9D.png?alt=media&token=ed42995e-672b-49aa-94d5-00b5dfdf93b0"));
//        furniture.setSubCategoroies(furnitureMap);
//        BusinessRef.child(furniture.getTitle()).setValue(furniture);
//
//        Category cleaners = new Category("ניקיון", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%A0%D7%99%D7%A7%D7%99%D7%95%D7%9F.png?alt=media&token=3c0cad8e-9bfa-4f6b-ad3d-eae907deb522");
//        HashMap<String, Category> cleanersMap = cleaners.getSubCategoroies();
//        cleanersMap.put(cleanersMap.size()+"a", new Category("ניקיון", "https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/CategoryImages%2F%D7%A0%D7%99%D7%A7%D7%99%D7%95%D7%9F.png?alt=media&token=3c0cad8e-9bfa-4f6b-ad3d-eae907deb522"));
//        cleaners.setSubCategoroies(cleanersMap);
//        profRef.child(cleaners.getTitle()).setValue(cleaners);


        //set user is online
        final String uid = getSharedPreferences(PreferenceKeys.PREFERENCES, MODE_PRIVATE).getString(PreferenceKeys.APP_ID, PreferenceKeys.RANDOM_STRING);
        if (!getSharedPreferences(PreferenceKeys.PREFERENCES, MODE_PRIVATE).getBoolean(PreferenceKeys.IS_ANONYMOUS, true) && !uid.equals(PreferenceKeys.RANDOM_STRING)) {
            addListener(uid);

        }
        FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PrivateData").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    addListener(uid);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


//        FakeService.create(this);

//        testGeoFire();

    }

    private void testGeoFire() {
        GeoFire geoFire = new GeoFire(FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/").getRoot().child("Services").child("PublicData"));
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(32.083585, 34.800792), 1);
        Log.d(TAG, "testGeoFire: start listen");
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.d(TAG, "onKeyEntered: " + key);
                Log.d(TAG, "onKeyEntered: " + location);
            }

            @Override
            public void onKeyExited(String key) {
                Log.d(TAG, "onKeyExited: " + key);
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                Log.d(TAG, "onGeoQueryReady: ");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.d(TAG, "onGeoQueryError: ");
            }
        });
    }

    public static void addListener(String uid) {//online offline
        DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/AllUsers/PublicData/" + uid + "/online");
        ref.setValue(true);
        //should go to database only when entering app dont think any need for more than this.
        UpdatesFromServer.updateCategoriesFromServer();
        ref.addValueEventListener(listener);
        ref.onDisconnect().setValue(false);
    }

    static ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public static void shareService(String serviceKey) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "https://mybizz.application.to/allInfo_" + serviceKey);
        intent.putExtra(Intent.EXTRA_TEXT, mContext.getResources().getString(R.string.get_my_service) + "https://mybizz.application.to/allInfo_" + serviceKey + "  "
                + mContext.getResources().getString(R.string.download_app) + " https://play.google.com/store/apps/details?id=com.app.mybiz"

        );
        intent.putExtra(Intent.EXTRA_SUBJECT, "Check out this site!");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(Intent.createChooser(intent, "Share"));
    }


}
