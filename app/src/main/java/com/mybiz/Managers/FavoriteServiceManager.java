package com.mybiz.Managers;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;
import com.mybiz.Objects.Service;

/**
 * Created by hannashulmah on 19/03/2017.
 */

public class FavoriteServiceManager extends AppCompatActivity {
    private static FavoriteServiceManager instance;
    static Context context;

    private FavoriteServiceManager(Context context) {
        this.context = context;
    }

    public static synchronized FavoriteServiceManager getInstance(Context context) {
        if (instance == null)
            instance = new FavoriteServiceManager(context);
        return instance;
    }

    public static void addToFavoriteList(Service service, String serviceKey, String myID){
        FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PrivateData")
                .child(myID).child("favorites").child(serviceKey).setValue(service);

        FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PublicData")
                .child(myID).child("favorites").child(serviceKey).setValue(service);

        FirebaseDatabase.getInstance().getReference().child("Services").child("PrivateData")
                .child(serviceKey).child("followers").child(myID).setValue(myID);

        FirebaseDatabase.getInstance().getReference().child("Services").child("PublicData")
                .child(serviceKey).child("followers").child(myID).setValue(myID);
    }

    public static void removeToFavoriteList(String serviceKey, String myID){
        FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PrivateData")
                .child(myID).child("favorites").child(serviceKey).setValue(null);

        FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PublicData")
                .child(myID).child("favorites").child(serviceKey).setValue(null);

        FirebaseDatabase.getInstance().getReference().child("Services").child("PrivateData")
                .child(serviceKey).child("followers").child(myID).setValue(null);

        FirebaseDatabase.getInstance().getReference().child("Services").child("PublicData")
                .child(serviceKey).child("followers").child(myID).setValue(null);

    }
}
