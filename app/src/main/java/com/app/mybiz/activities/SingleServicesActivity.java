package com.app.mybiz.activities;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.app.mybiz.PreferenceKeys;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.app.mybiz.adapters.SingleServiceListAdapter;
import com.app.mybiz.objects.LatLng;
import com.app.mybiz.LocationUtils;
import com.app.mybiz.objects.Service;
import com.app.mybiz.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class SingleServicesActivity extends AppCompatActivity {

    String TAG = "SingleServiceFrag";
    String category;
    RecyclerView singleServices;
    SingleServiceListAdapter serviceListAdapter;
    DatabaseReference ref;
    ArrayList<Service> serviceList;
    HashMap<String, Service> serviceHashmap;
    Intent intent;
    Toolbar toolbar;

    private double lat = 32.162413, lon = 34.844675;
    private ChildEventListener childListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            final Service service = dataSnapshot.getValue(Service.class);

            Log.d(TAG, "onChildAdded1: "+dataSnapshot);
            Log.d(TAG, "onChildAdded2: "+dataSnapshot.getValue());

            String myId = getSharedPreferences(PreferenceKeys.PREFERENCES, MODE_PRIVATE).getString(PreferenceKeys.APP_ID, PreferenceKeys.RANDOM_STRING);
            String serId = service.getUserUid();
            Log.d(TAG, "onChildAdded3: "+myId);
            Log.d(TAG, "onChildAdded4:__"+serId);

//                if (!service.getUserUid().equals(myId)) {
//                if (serviceHashmap.get(service.getKey())==null) {
            serviceHashmap.put(service.getKey(), service);
            serviceList.add(service);
//                }
//                }
//                else
//                    serviceList.remove(service);

//                Log.d(TAG, "onChildAdded before: "+serviceList);
            Collections.sort(serviceList, new Comparator<Service>() {
                @Override
                public int compare(Service lhs, Service rhs) {
                    System.out.println(lhs);

                    if(lhs.l != null && lhs.l.size() == 0)
                        lhs.l = null;
                    if(rhs.l != null && rhs.l.size() == 0)
                        rhs.l = null;
                    if(lhs.l == null && rhs.l == null )
                        return 0;
                    else if(lhs.l == null && rhs.l != null)
                        return 1;
                    else if(rhs.l == null && lhs.l != null)
                        return -1;

                    Location loc = new Location("");
                    loc.setLatitude(lat);
                    loc.setLongitude(lon);

                    Location loc1 = new Location("");
                    loc1.setLatitude(lhs.l.get(0));
                    loc1.setLongitude(lhs.l.get(1));

                    Location loc2 = new Location("");
                    loc2.setLatitude(rhs.l.get(0));
                    loc2.setLongitude(rhs.l.get(1));

                    double dis1 = loc.distanceTo(loc1);//distance(lat, lhs.l.get(0), lon, lhs.l.get(1), 1, 1);
                    double dis2 = loc.distanceTo(loc2);//distance(lat, rhs.l.get(0), lon, rhs.l.get(1), 1, 1);

                    Log.d(TAG, "compare: "+lhs.getAddress()+" : "+dis1+", "+rhs.getAddress()+": "+dis2);
                    return dis1 == dis2 ? 0: dis1 > dis2?0:-1;
                }

            });
//                Log.d(TAG, "onChildAdded after: "+serviceList);

            serviceListAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            Log.d(TAG, "onChildRemoved111: "+dataSnapshot.getValue());
            Service service = dataSnapshot.getValue(Service.class);
            serviceHashmap.put(service.getKey(), service);
            for (int i = 0; i < serviceList.size(); i++) {
                if (service.getKey().equals(serviceList.get(i).getKey()))
                    serviceList.set(i, service);
            }
            serviceListAdapter.notifyDataSetChanged();

            Collections.sort(serviceList, new Comparator<Service>() {
                @Override
                public int compare(Service lhs, Service rhs) {
                    System.out.println(lhs);

                    if(lhs.l != null && lhs.l.size() == 0)
                        lhs.l = null;
                    if(rhs.l != null && rhs.l.size() == 0)
                        rhs.l = null;
                    if(lhs.l == null && rhs.l == null )
                        return 0;
                    else if(lhs.l == null && rhs.l != null)
                        return 1;
                    else if(rhs.l == null && lhs.l != null)
                        return -1;

                    Location loc = new Location("");
                    loc.setLatitude(lat);
                    loc.setLongitude(lon);

                    Location loc1 = new Location("");
                    loc1.setLatitude(lhs.l.get(0));
                    loc1.setLongitude(lhs.l.get(1));

                    Location loc2 = new Location("");
                    loc2.setLatitude(rhs.l.get(0));
                    loc2.setLongitude(rhs.l.get(1));

                    double dis1 = loc.distanceTo(loc1);//distance(lat, lhs.l.get(0), lon, lhs.l.get(1), 1, 1);
                    double dis2 = loc.distanceTo(loc2);//distance(lat, rhs.l.get(0), lon, rhs.l.get(1), 1, 1);

                    Log.d(TAG, "compare: "+lhs.getAddress()+" : "+dis1+", "+rhs.getAddress()+": "+dis2);
                    return dis1 == dis2 ? 0: dis1 > dis2?0:-1;
                }

            });
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            serviceListAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_services);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(R.drawable.right_arrow_w);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        intent = getIntent();
        getSupportActionBar().setTitle(intent.getStringExtra("category"));

        category = intent.getStringExtra("category");
//        toolbar.setTitle(category);
        serviceList = new ArrayList<>();
        serviceHashmap = new HashMap<>();
        Log.d("TAG", "onCreateView: " + category);
        singleServices = (RecyclerView) findViewById(R.id.single_services);
        singleServices.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        ref = FirebaseDatabase.getInstance().getReference().child("Services").child("PublicData");
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean st = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(! st){
            LatLng latLng = LocationUtils.getRegisterLocation();
            if(latLng != null){
                lat = latLng.lat;
                lon = latLng.lon;
            }
            return;
        }

        //LocationUtils.init(getContext());
        Location location = LocationUtils.getLastLocation();
        if(location != null){
            lat = location.getLatitude();
            lon = location.getLongitude();
        }else {
            LatLng latLng = LocationUtils.getRegisterLocation();
            if(latLng != null){
                lat = latLng.lat;
                lon = latLng.lon;
            }
        }
    }

    Query query;
    @Override
    public void onResume() {
        super.onResume();

        serviceList.clear();
        serviceListAdapter = new SingleServiceListAdapter(this, serviceList);
        singleServices.setAdapter(serviceListAdapter);


        query = ref.orderByChild("category").equalTo(category);

        query.addChildEventListener(childListener);

    }

    @Override
    public void onPause() {
        super.onPause();
        query.removeEventListener(childListener);
    }


    public  double distance(double lat1, double lat2, double lon1,
                            double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

}
