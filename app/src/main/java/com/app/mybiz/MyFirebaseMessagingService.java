package com.app.mybiz;

import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by itzikalgrisi on 17/12/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    String TAG = "FirebaseMsg";

    MybizzNotificationManager notificationsManager;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationsManager = MybizzNotificationManager.getInstance(this.getApplicationContext());
    }



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData().size() > 0) {
            JSONObject ob = new JSONObject(remoteMessage.getData());
            Log.d(TAG, "onMessageReceived: "+ob.toString());
            try {
                String messageDetails = ob.getString("message");
                notificationsManager.setUnseenMessagesFile(messageDetails);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            long time = System.currentTimeMillis();
            String msgKey = null;
            try {

                String object = ob.getString("message");
                JSONObject messageDetails = new JSONObject(object);
                msgKey = messageDetails.getString("key");
                Log.d(TAG, "onMessageReceived: "+msgKey);
                String senderUid = messageDetails.getString("fromUid");
                String receiverUid = messageDetails.getString("toUid");

                HashMap<String, Object> map = new HashMap<>();
                map.put("dateSent", time);

//                FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/PrivateChat/"+receiverUid+"__"+senderUid+"/messages/"+msgKey).updateChildren(map);
                FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/PrivateChat/"+senderUid+"__"+receiverUid+"/messages/"+msgKey).updateChildren(map);

            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(TAG, "onMessageReceived: "+e.getMessage());
            }


        }

        if (remoteMessage.getNotification() != null) {
//            try {
//                notificationsManager.updateNotification();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        } else {
        }
    }
}
