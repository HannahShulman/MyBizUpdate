package com.app.mybiz;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.app.mybiz.Objects.LatLng;

/**
 * Created by itzikalgrisi on 25/02/2017.
 */

public class LocationUtils {
    private static GoogleApiClient mGoogleApiClient = null;
    private static String TAG = "LocationUtils";
    private static GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Log.d(TAG, "onConnected: ");
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            Log.d(TAG, "onConnected: "+mLastLocation);
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.d(TAG, "onConnectionSuspended: ");
        }

    };
    private static GoogleApiClient.OnConnectionFailedListener connectionFailedCallbacks = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.d(TAG, "onConnectionFailed: ");
        }
    };
    private static android.location.Location mLastLocation;
    private static Context context;

    public static void init(Context context){
        LocationUtils.context = context;
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(connectionCallbacks)
                    .addOnConnectionFailedListener(connectionFailedCallbacks)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();

    }

    public static android.location.Location getLastLocation() {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation == null) {
            return null;
        } else {
            //If everything went fine lets get latitude and longitude
//            currentLatitude = location.getLatitude();
//            currentLongitude = location.getLongitude();
            context.getSharedPreferences("locations", Context.MODE_PRIVATE).edit().
                    putString("lat", ""+mLastLocation.getLatitude())
                    .putString("lon", ""+mLastLocation.getLongitude()).commit();

                return mLastLocation;
        }

    }

    public static void setLocationReg(double lat, double lon){
        context.getSharedPreferences("locations", Context.MODE_PRIVATE).edit().
                putString("reglat", ""+lat)
                .putString("regLon", ""+lon).commit();

    }

    public static LatLng getLast(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("locations", Context.MODE_PRIVATE);
        String latStr = sharedPreferences.getString("lat", null);
        String lonStr = sharedPreferences.getString("lon", null);
        if(latStr != null && lonStr != null){
            return new LatLng(Double.parseDouble(latStr), Double.parseDouble(lonStr));
        }

        return null;
    }

    public static LatLng getRegisterLocation() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("locations", Context.MODE_PRIVATE);
        String latStr = sharedPreferences.getString("reglat", null);
        String lonStr = sharedPreferences.getString("regLon", null);
        Log.d(TAG, "getRegisterLocation: "+latStr+" "+lonStr);
        if(latStr != null && lonStr != null){
            return new LatLng(Double.parseDouble(latStr), Double.parseDouble(lonStr));
        }
        else
            return null;
    }
}
