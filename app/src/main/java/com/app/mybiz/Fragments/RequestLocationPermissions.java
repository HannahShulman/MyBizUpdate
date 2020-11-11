package com.app.mybiz.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.app.mybiz.LocationUtils;
import com.app.mybiz.R;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by itzikalgrisi on 24/02/2017.
 */

public class RequestLocationPermissions extends DialogFragment implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public  String TAG = "ReqLocPermissions";
    private Location mLastLocation;
    private boolean enable = false;
    private LocationRequest mLocationRequest;
    private Context mc;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mc = context.getApplicationContext();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //getLastLocation();
    }




    private com.app.mybiz.Objects.Location getLocation(double lat, double lon){
        Geocoder geocoder = new Geocoder(mc, new Locale("iw"));
        try {
            List<Address> list = geocoder.getFromLocation(lat, lon, 3);
            com.app.mybiz.Objects.Location location = new com.app.mybiz.Objects.Location();
            Log.d(TAG, "getLocation: "+list);
            if(list != null && list.size() > 0){
                Address add = list.get(0);
                Log.d(TAG, "getLocation: lat: " + add.getLatitude());
                Log.d(TAG, "getLocation: long: " + add.getLongitude());
                lat = add.getLatitude();
                lon = add.getLongitude();
                location.setLongitude(lon);
                location.setLatitude(lat);

                LocationUtils.setLocationReg(lat, lon);
                location.setCountry("ישראל");
                location.setTown(add.getLocality());
                location.setStreetName(add.getThoroughfare());

                if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    FirebaseDatabase.getInstance().getReference().getRoot().child("AllUsers").child("PublicData").child(uid).child("location").setValue(location);
                    FirebaseDatabase.getInstance().getReference().getRoot().child("AllUsers").child("PrivateData").child(uid).child("location").setValue(location);
                    Log.d(TAG, "getLocation: " + add.getLocality());
                    Log.d(TAG, "getLocation: " + add);
                }
            }

            return location;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getLastLocation(){
        Log.d(TAG, "getLastLocation: ");

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
        // 1 second, in milliseconds


        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        Log.d(TAG, "getLastLocation: " + mLastLocation);
        if (mLastLocation == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest, mLocationListener);
        } else {
            //If everything went fine lets get latitude and longitude
//            currentLatitude = location.getLatitude();
//            currentLongitude = location.getLongitude();
            if(listener != null)
                listener.onLocationUpdate(getLocation(mLastLocation.getLatitude(),
                        mLastLocation.getLongitude()), RequestLocationPermissions.this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public interface OnLocationOptionsListener{
        void setAddress(RequestLocationPermissions fragment);
        void tryEnableLocation(RequestLocationPermissions fragment);
        void requestPermission(boolean per, RequestLocationPermissions fragment);
        void onEnable(RequestLocationPermissions fragment);
        void onLocationUpdate(com.app.mybiz.Objects.Location location, RequestLocationPermissions fragment);
    }

    private OnLocationOptionsListener listener;

    public RequestLocationPermissions setListener(OnLocationOptionsListener listener) {
        this.listener = listener;
        return this;
    }

    public OnLocationOptionsListener getListener() {
        return listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.enable_location_fragment, container, false);

        TextView tv = (TextView) v.findViewById(R.id.askTV);
        tv.setText(getResources().getString(R.string.get_best_use));

        v.findViewById(R.id.enable).setOnClickListener(this);
        v.findViewById(R.id.insert).setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.enable:
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "enableLocation: if");
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1121);
                    return;
                }else
                    enableLocation();

                break;
            case R.id.insert:
                if(listener != null)
                    listener.setAddress(this);
                break;
        }
    }

    private void enableLocation() {
        Log.d(TAG, "enableLocation: else");
        LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE );
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Log.d(TAG, "enableLocation: status: "+statusOfGPS);
        enableGps();

    }

    @Override
    public void onStart() {
        super.onStart();
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        boolean st = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(st)
            enableGps();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: "+Arrays.toString(permissions));
        Log.d(TAG, "onRequestPermissionsResult: "+Arrays.toString(grantResults));
        if(requestCode == 1121) {
            if(mGoogleApiClient != null)
                mGoogleApiClient.reconnect();
            Log.d(TAG, "onRequestPermissionsResult: 1121");
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && enable)
                enableLocation();
            if(listener != null)
                listener.requestPermission(grantResults[0] == PackageManager.PERMISSION_GRANTED, this);

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    GoogleApiClient mGoogleApiClient = null;

    public void enableGps(){
        Log.d(TAG, "enableGps: enable");
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        //**************************
        builder.setAlwaysShow(true); //this is the key ingredient
        //**************************


        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.d(TAG, "onResult: success");
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        if(listener != null)
                            listener.onEnable(RequestLocationPermissions.this);
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        Log.d(TAG, "onResult: resilution_required");
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    getActivity(), 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                            Log.d(TAG, "onResult: excspetion");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        Log.d(TAG, "onResult: SETTINGS_CHANGE_UNAVAILABLE");
                        break;
                }
            }
        });

        mGoogleApiClient.connect();

        LocationManager mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

//        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1,
//                0, new android.location.LocationListener() {
//                    @Override
//                    public void onLocationChanged(Location location) {
//                        Log.d(TAG, "onLocationChanged: "+location);
//                        getLastLocation();
//                    }
//
//                    @Override
//                    public void onStatusChanged(String provider, int status, Bundle extras) {
//                        Log.d(TAG, "onStatusChanged: ");
//                    }
//
//                    @Override
//                    public void onProviderEnabled(String provider) {
//                        Log.d(TAG, "onProviderEnabled: ");
//                    }
//
//                    @Override
//                    public void onProviderDisabled(String provider) {
//                        Log.d(TAG, "onProviderDisabled: ");
//                    }
//                });

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        boolean st = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1000){
            Log.d(TAG, "onActivityResult: "+resultCode);
            Log.d(TAG, "onActivityResult: "+data);
            if(listener != null)
                listener.onEnable(this);
            mGoogleApiClient.reconnect();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {

            if (location != null) {
                if(listener != null)
                    listener.onLocationUpdate(getLocation(location.getLatitude(), location.getLongitude()), RequestLocationPermissions.this);
            }
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            Log.d(TAG, "onLocationChanged: "+location);
        }
    };
}
