package com.mybiz;

import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by itzikalgrisi on 17/12/2016.
 */

public class InstanceIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        SharedPreferences sharedPreferences = getSharedPreferences("token", MODE_PRIVATE);
        String newToken = FirebaseInstanceId.getInstance().getToken();
        DatabaseReference sub_ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/");
        Device device = new Device();
        device.pushToken = newToken;
        String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);


        Map<String, Object> map = new HashMap<>();
        map.put("pushToken", android_id);
        sub_ref.updateChildren(map);
//        sub_ref.child("Devices").child(android_id).setValue(device);

        Log.d("token",  "serial: "+android_id+"\n token: "+newToken);

        sendRegistrationToServer(newToken);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }


}
