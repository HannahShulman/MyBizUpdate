package com.app.mybiz;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.app.mybiz.Objects.Service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by itzikalgrisi on 10/01/2017.
 */


//singelton
public class MybizzNotificationManager extends AppCompatActivity {

    String TAG = "NotificationsManager";
    private static MybizzNotificationManager instance;
    String fileName = "notificationList.txt";
    NotificationManager manager;
    private Context context;
    HashMap<String, String> sendersUid = new HashMap<>();//stores ids of senders for indicating no of senders
    JsonObject unseenMessages = new JsonObject();
    JsonObject chatsUnseen = new JsonObject();
    String UNSEEN_F_N= "unseen.json";
    String UNSEEN_CHATS_F_N= "chats.json";
    File unseen, chats;

    private MybizzNotificationManager(Context context) {
        this.context = context;
        unseen = new File(context.getFilesDir(), UNSEEN_F_N);
        chats = new File(context.getFilesDir(), UNSEEN_CHATS_F_N);
    }

    public static synchronized MybizzNotificationManager getInstance(Context context) {
        if (instance == null)
            instance = new MybizzNotificationManager(context);
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public int getUnseenCount() throws JSONException {
        return getUnseenMessages().length();
    }


    //set notification builder style and info
    public Notification.Builder setNotificationBuilder() throws JSONException {
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification.Builder notifiBuilder = new Notification.Builder(context);
        notifiBuilder.setSmallIcon(R.drawable.splash_icon);
        notifiBuilder.setSound(notificationSound);
        Notification.InboxStyle style = new Notification.InboxStyle(notifiBuilder);
        style.setBigContentTitle("you have "+getUnseenCount()+" messages");
        style.setSummaryText("you have " + getUnseenCount() + " messages");
        boolean sameUser = true;

        //adds lines to notification, and checks if all messages are from same user.
        JSONArray allMyNotifications = new JSONArray(context.getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.MY_NOTIFICATIONS, Constants.DEFAULT_NOTIFICATION));
        Log.d(TAG, "setNotificationBuilder: "+allMyNotifications.toString());
        for (int i = 0; i < allMyNotifications.length(); i++) {
            JSONObject currentNotification = allMyNotifications.getJSONObject(i);
            sendersUid.put(currentNotification.getString("fromUid"), currentNotification.getString("fromUid"));
                if (i>0 && sameUser){
                    JSONObject recentNotification = allMyNotifications.getJSONObject(i-1);
                    if (!currentNotification.getString("fromUid").equals(recentNotification.getString("fromUid")))
                        sameUser=false;
                }
                if (i>allMyNotifications.length()-7) {
                    style.addLine(currentNotification.getString("senderName")+": "+currentNotification.getString("content"));
                }
        }

        //set the intents of notification (if from same user and if from multiple users
        //intent for single user


        final Intent singleIntent = new Intent(context, ChatActivity.class);
        //sending info for activity
        JSONObject object = allMyNotifications.getJSONObject(0);
        Log.d(TAG, "setNotificationBuilder: "+object.toString());
        Log.d(TAG, "setNotificationBuilder1: "+object.optString("amIService"));
        Log.d(TAG, "setNotificationBuilder12: "+object.optString("senderService"));
        singleIntent.putExtra("isService", object.optBoolean("amIService"));
//        if (object.optBoolean("amIService")){
            Log.d(TAG, "setNotificationBuilder: "+object.optString("senderService"));
            Gson gson = new Gson();
        if (!object.optString("senderService").isEmpty()) {
            JSONObject obj = new JSONObject(object.optString("senderService"));
            Service senderService = gson.fromJson(obj.toString(), Service.class);
            singleIntent.putExtra("currentService", senderService);
        }
//        }else{

//        }

        singleIntent.putExtra("otherID", object.getString("fromUid"));
        singleIntent.putExtra("otherName", object.getString("senderName"));
        singleIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent singleUserPendingIntent = PendingIntent.getActivity(context, 0, singleIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        //intent for multiple users
        Intent multipleUsersIntent = new Intent(context, TabsActivity.class);
        multipleUsersIntent.putExtra("tabNumber", 2);
        PendingIntent multipleUserPendingIntent = PendingIntent.getActivity(context, 0, multipleUsersIntent, PendingIntent.FLAG_UPDATE_CURRENT);


//        Notification.Builder notifiBuilder = new Notification.Builder(context);
//        notifiBuilder.setSmallIcon(R.drawable.splash_icon);
//        notifiBuilder.setContentTitle("aaaaaaaa");
//        notifiBuilder.setSound(notificationSound);
        if (sameUser) {//if all from one user

        //check if is service
//            Log.d(TAG, "setNotificationBuilder1: "+object.getString("senderService"));
            style.setBigContentTitle(object.getString("senderName"));
            notifiBuilder.setContentIntent(singleUserPendingIntent);
            notifiBuilder.setAutoCancel(true);
            notifiBuilder.setStyle(style);
            notifiBuilder.setContentText(getUnseenCount()+"  from "+object.getString("senderName"));
        } else {
            style.setBigContentTitle("you have " + getUnseenCount() + " messages");
            notifiBuilder.setContentText(getUnseenCount()+"  from "+sendersUid.size()+" senders");
            notifiBuilder.setContentIntent(multipleUserPendingIntent);
        }
        notifiBuilder.setContentTitle("MyBizz");

        notifiBuilder.setStyle(style);
        return notifiBuilder;
    }

    //update notification
    public void updateNotification() throws JSONException {
        //update notification
        Notification.Builder notify = setNotificationBuilder();
        manager.notify("tag", 234, notify.build());
    }


    //this method adds the received message to array in file containing other unseen messages
    public void setUnseenMessagesFile(String messageData) throws JSONException {
        JSONObject messageDetailsJsonObject = new JSONObject(messageData);
        Log.d(TAG, "onMessageReceived: "+messageData.toString());
        String myNotifications =  context.getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.MY_NOTIFICATIONS, Constants.DEFAULT_NOTIFICATION);
        JSONArray myNotificationArray = new JSONArray(myNotifications);
        myNotificationArray.put(messageDetailsJsonObject);
        Log.d(TAG, "onMessageReceived: "+myNotificationArray);
        context.getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).edit().putString(Constants.MY_NOTIFICATIONS, myNotificationArray.toString()).commit();
        updateNotification();
    }


    //this function is for removing a message from file

    public void removeSeenMessagesFile(String id) {
        sendersUid.remove(id);
        ArrayList<String> messages = new ArrayList<>();
        JSONArray notificationArray = null;
        try {
            notificationArray = getUnseenMessages();
            Log.d(TAG, "removeSeenMessagesFile1: "+notificationArray.toString()+"__"+notificationArray.length());
            for (int i = 0; i < notificationArray.length(); i++) {
                JSONObject currentNotification = notificationArray.getJSONObject(i);
                if (!currentNotification.getString("fromUid").equals(id)){
                    messages.add(currentNotification.toString());
                }
            }
            notificationArray = new JSONArray(messages);
            Log.d(TAG, "removeSeenMessagesFile2: "+messages.toString());
            Log.d(TAG, "removeSeenMessagesFile3: "+notificationArray.toString());
            context.getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).edit().putString(Constants.MY_NOTIFICATIONS, messages.toString()).commit();
            updateNotification();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //this method returns the content of file holding array, containing unseen messages
    public JSONArray getUnseenMessages() throws JSONException {
        String notificationFromPrefrences = context.getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.MY_NOTIFICATIONS, Constants.DEFAULT_NOTIFICATION);
        JSONArray notificationArray = new JSONArray(notificationFromPrefrences);
        return notificationArray;
    }


    private void saveInFiles(){

    }

    public void setUnseenMessagesFile(String messageDetails, String messageId) {
        try{
            JsonObject message = new JsonParser().parse(new StringReader(messageDetails)).getAsJsonObject();
            unseenMessages.add(messageId, message);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
