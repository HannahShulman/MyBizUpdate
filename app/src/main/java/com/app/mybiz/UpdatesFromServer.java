package com.app.mybiz;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.app.mybiz.objects.User;

import java.util.ArrayList;


public class UpdatesFromServer {
    private static final String TAG = "UpdatesFromServer";
    public static ArrayList categoryList = new ArrayList();


    public static void updateCategoriesFromServer() {
//        FirebaseDatabase.getInstance().getReference().child("Categories").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot snap : dataSnapshot.getChildren()) {//will happen twice, once for business once from professions
//                    for (DataSnapshot s : snap.getChildren()) {
//                        Category category = s.getValue(Category.class);
//                        if (!categoryList.contains(category.getTitle()))
//                            categoryList.add(category.getTitle());
//                    }
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

    }

    public static void accountUpdates(Context ctx) {
        String appID = ctx.getSharedPreferences(PreferenceKeys.PREFERENCES, Context.MODE_PRIVATE).getString(PreferenceKeys.APP_ID, PreferenceKeys.RANDOM_STRING);
        boolean amService = ctx.getSharedPreferences(PreferenceKeys.PREFERENCES, Context.MODE_PRIVATE).getBoolean(PreferenceKeys.IS_SERVICE, false);
        FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PrivateData").child(appID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.isContainsService()) {
                    //update service info
                } else {
                    //check for email, etc
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
