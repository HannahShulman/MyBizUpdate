package com.mybiz;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mybiz.Objects.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.mybiz.R.string.address;

/**
 * Created by itzikalgrisi on 23/02/2017.
 */

public class FakeService {
    private static final String TAG = "FakeService";
    static String[] cities = {"תל אביב","רמת גן","פתח תקווה","בני ברק","מודיעין","ראשון לציון","לוד","אשדוד","רחובות","ירושלים","הרצליה","רעננה","חדרה","בית שאן","עכו"
            ,"חיפה","כרמיאל","טבריה","צפת","דימונה","רכסים","נהריה","באר שבע","אילת"};
    public static void create(Context context){
        for (String city : cities) {
            final Service service = new Service();
            service.setEmail(city+"@"+city+".city");
            service.setTown(city);
            service.setAddress(city);
            service.setPhoneNumber("909090909");
            service.setPhoneNumber("878665757");
            service.setCategory("זגג");
            service.setSubcategory("זגג");
            service.setTitle(city);
            service.setUserUid(city);
            service.setKey(city);

            double lat = 0, lon = 0;
            Geocoder geocoder = new Geocoder(context);
            try {
                List<Address> list = geocoder.getFromLocationName(city, 2);
                if(list != null && list.size() > 0){
                    Address add = list.get(0);
                    Log.d(TAG, "getLocation:city: "+city+" lat: " + add.getLatitude());
                    Log.d(TAG, "getLocation: city: "+city+" long: " + add.getLongitude());
                    lat = add.getLatitude();
                    lon = add.getLongitude();
                }

                final  double lat1 = lat, lon1= lon;
                GeoFire geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference().getRoot().child("Services").child("Geo"));
                geoFire.setLocation(service.getUserUid(), new GeoLocation(lat1, lon1));


                FirebaseDatabase.getInstance().getReference().getRoot().child("Services").child("PublicData").child(service.getUserUid()).setValue(service, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    }
                });

                FirebaseDatabase.getInstance().getReference().getRoot().child("Services").child("PrivateData").child(service.getUserUid()).setValue(service, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    }
                });
                FirebaseDatabase.getInstance().getReference().getRoot().child("Services").child("Geo").child(service.getUserUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        MyGeoFire m = dataSnapshot.getValue(MyGeoFire.class);
//                        Log.d(TAG, "onDataChange: "+m);
//                        Log.d(TAG, "onDataChange: " + dataSnapshot.child("g").getValue());
                        Log.d(TAG, "onDataChange: " + dataSnapshot.child("l").getValue().getClass().getName());
//                        Log.d(TAG, "onDataChange: " + dataSnapshot.child("g").getValue());
                        HashMap<String, Object> map = new HashMap();
                        map.put("g", m.g);
                        map.put("l", m.l);
                        FirebaseDatabase.getInstance().getReference().getRoot().child("Services").child("PublicData").child(service.getUserUid()).updateChildren(map);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
