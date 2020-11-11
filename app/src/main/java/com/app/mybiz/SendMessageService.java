package com.app.mybiz;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.app.mybiz.Objects.Message;
import com.app.mybiz.tests.FixOrientation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SendMessageService extends android.app.Service {

//ths service makes sure that every message thats sent is saved to server. i.e. sending the message to server is done in background

    DatabaseReference firebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference allUsersPublicDataRef = firebaseDatabaseReference.child("AllUsers").child("PublicData");
    ArrayList<String> sent = new ArrayList<>();
    String otherId;
    String myId;
    String otherName;
    String myName;
    String imageUrl;
    boolean isImage = false;
    Message message;
    String msgContent;
    DatabaseReference databaseReference;
    String TAG = "SendMessageService";
    com.app.mybiz.Objects.Service otherService;
    ValueEventListener AddMessageListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            final String key = dataSnapshot.getKey();
            final Message msg = dataSnapshot.getValue(Message.class);
            final long dateVisible = msg.getDateVisible();
            if (sent.contains(key)) {
                if (dateVisible != 0) {
                    sent.remove(key);
                    dataSnapshot.getRef().removeEventListener(this);
                }
            } else {
                dataSnapshot.getRef().removeEventListener(this);
            }
        }


        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public SendMessageService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isImage = intent.getBooleanExtra("isImage", false);
        msgContent = intent.getStringExtra("msgContent");
        myId = intent.getStringExtra("myId");
        otherId = intent.getStringExtra("otherId");
        otherService = (com.app.mybiz.Objects.Service) intent.getSerializableExtra("currentService");
        if (otherService != null)
            Log.d("idddddsss", otherService.getUserUid() + "__" + otherService.getTitle());
        else
            Log.d("idddddsss", "you are writing to a private user...");
        myName = intent.getStringExtra("myName");
        otherName = intent.getStringExtra("otherName");
        Log.d(TAG, "onStartCommand: "+otherName);
        message = new Message();
        if (getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getBoolean(Constants.IS_SERVICE, false)){
            message.setAmIService(true);
            Gson gson = new Gson();
            try {
                String s = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.MY_SERVICE, Constants.RANDOM_STRING);
                JSONObject obj = new JSONObject(s);
                com.app.mybiz.Objects.Service myService = gson.fromJson(obj.toString(), com.app.mybiz.Objects.Service.class);
                message.setSenderService(myService);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else{
            message.setAmIService(false);
            message.setSenderService(null);
        }
        message.setContent(msgContent);
        message.setFromUid(myId);
        message.setToUid(otherId);
        message.setSenderName(myName);
//        Log.d(TAG, "onStartCommand: "+message.toString());
        databaseReference = FirebaseDatabase.getInstance().getReference().child("PrivateChat").child(otherId + "__" + myId).child("messages").push();
        message.setKey(databaseReference.getKey());
        databaseReference.setValue(message);
        Log.d(TAG, "onStartCommand: "+message.toString());
        Log.d(TAG, "onStartCommand: "+message.getSenderName() + "__" + message.toString());

        if (isImage) {
            Log.d(TAG, "onStartCommand: "+isImage);
            Bundle extras = intent.getExtras();
//            Log.d(TAG, "onStartCommand: " + extras.getString("filePath"));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            Bitmap imageBitmap = null;
            BitmapFactory.decodeFile(extras.getString("filePath"), options);// (Bitmap) extras.get("data");//BitmapFactory.decodeFile(extras.getString("filePath"));

            int s = FixOrientation.calculateInSampleSize(options, 512, 330);

            options.inSampleSize = s;
            options.inJustDecodeBounds = false;
            imageBitmap = BitmapFactory.decodeFile(extras.getString("filePath"), options);
            if(imageBitmap == null)
                return START_NOT_STICKY;
            Log.d(TAG, "onStartCommand: "+s);
            Log.d(TAG, "onStartCommand: "+imageBitmap.getWidth());
            Log.d(TAG, "onStartCommand: "+imageBitmap.getHeight());


            ExifInterface exif = null;
            try {
                exif = new ExifInterface(extras.getString("filePath"));
                int orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);

                Log.d(TAG, "onStartCommandorie: "+orientation);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ExifInterface ei = null;
            try {
                ei = new ExifInterface(extras.getString("filePath"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            switch(orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    imageBitmap = rotateImage(imageBitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    imageBitmap =  rotateImage(imageBitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    imageBitmap = rotateImage(imageBitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:

                default:
                    break;
            }
            Log.d(TAG, "onStartCommand: bitmap");
            sendMessage();
            uploadFile(imageBitmap);
        } else {
            sendMessage();

        }
//        Toast.makeText(getApplicationContext(), msgContent, Toast.LENGTH_SHORT).show();
        return START_NOT_STICKY;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        firebaseDatabaseReference.child("PrivateChat").child(otherId + "__" + myId).child("messages").child(databaseReference.getKey()).removeEventListener(AddMessageListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void uploadFile(Bitmap bitmap) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://mybizz-3bbe5.appspot.com");
        StorageReference mountainImagesRef = storageRef.child("images2/" + System.currentTimeMillis() + ".png");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = mountainImagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d(TAG, "onFailure: "+exception.getMessage());

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri uri = taskSnapshot.getUploadSessionUri();//.getDownloadUrl();
                imageUrl = uri.toString();
                message.setPictureUrl(imageUrl);
                Log.d(TAG ,"downloadUrl" + imageUrl);
                sendMessage();
            }
        });
    }


    private void sendMessage() {
        Log.d(TAG, "sendMessage: ");
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PrivateData").child(myId).child("chats").child(otherId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: "+ dataSnapshot.exists());
                if (!dataSnapshot.exists()) {
                    ChatSummary chatSummary = new ChatSummary();
                    chatSummary.setMyName(myName);
                    chatSummary.setOtherName(otherName);
                    chatSummary.setMyId(myId);
                    chatSummary.setOtherId(otherId);
                    if (otherService != null) {
                        chatSummary.setContactService(true);
                        chatSummary.setType(otherService.getCategory());
                    } else {

                    }
                    ref.setValue(chatSummary);
                } else {
                    //updateChatStream();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        com.app.mybiz.Objects.Service myService = new com.app.mybiz.Objects.Service();
        final long time = System.currentTimeMillis();
        final boolean amService = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getBoolean(Constants.IS_SERVICE, false);
        if (amService) {
            String s = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.MY_SERVICE, Constants.RANDOM_STRING);
            if (!s.equals(Constants.RANDOM_STRING)) {
                Gson gson = new Gson();
                try {
                    JSONObject obj = new JSONObject(s);
                    myService= gson.fromJson(obj.toString(), com.app.mybiz.Objects.Service.class);
                    allUsersPublicDataRef.child(otherId).child("chats").child(myId).child("otherContactService").setValue(myService);
                } catch (IllegalStateException | JsonSyntaxException exception) {
                    Log.d(TAG, "sendMessage: " + exception.getMessage());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        DatabaseReference otherIdChatRef = allUsersPublicDataRef.child(myId).child("chats").child(otherId);
        Map<String, Object> map = new HashMap<>();
        map.put("lastMessageTime", time);
        map.put("otherId", otherId);
        map.put("invertLastMessage", -1 * time);
        map.put("lastMessageContent", message.getContent());
        map.put("lastMessageSenderId", message.getFromUid());
        map.put("lastMessageSeen", 0);
        map.put("myName", otherName);
        map.put("lastSender", myId);
        if (otherService!=null)
            map.put("otherContactService", otherService);
        otherIdChatRef.updateChildren(map);


        DatabaseReference myChatReference = allUsersPublicDataRef.child(otherId).child("chats").child(myId);
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("lastMessageTime", time);
        updateMap.put("otherId", myId);
        updateMap.put("invertLastMessage", -1 * time);
        updateMap.put("lastMessageContent", message.getContent());
        updateMap.put("lastMessageSenderId", message.getFromUid());
        updateMap.put("lastMessageSeen", 0);
        updateMap.put("myName", myName);
        updateMap.put("lastSender", myId);
        if (amService)
            updateMap.put("otherContactService", myService);
        myChatReference.updateChildren(updateMap);

        firebaseDatabaseReference.child("PrivateChat").child(myId + "__" + otherId).child("lastMessageTime").setValue(time);
        firebaseDatabaseReference.child("PrivateChat").child(otherId + "__" + myId).child("lastMessageTime").setValue(time);
        firebaseDatabaseReference.child("PrivateChat").child(myId + "__" + otherId).child("invertLastMessage").setValue(-1 * time);
        firebaseDatabaseReference.child("PrivateChat").child(otherId + "__" + myId).child("invertLastMessage").setValue(-1 * time);


        Log.d(TAG, "sendMessage: "+message.toString()+"_____");
        firebaseDatabaseReference.child("PrivateChat").child(myId + "__" + otherId).child("messages").child(databaseReference.getKey()).setValue(message);
        firebaseDatabaseReference.child("PrivateChat").child(otherId + "__" + myId).child("messages").child(databaseReference.getKey()).setValue(message);
        sent.add(databaseReference.getKey());
        firebaseDatabaseReference.child("PrivateChat").child(otherId + "__" + myId).child("messages").child(databaseReference.getKey()).addValueEventListener(AddMessageListener);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String id = databaseReference.getKey();
                if (sent.contains(id)) {
                    FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/AllUsers/PublicData/" + otherId + "/chats/" + myId + "/unseenMessages").child(id).setValue(id);
                    sent.remove(id);
                    firebaseDatabaseReference.child("UnsentMessages").child(databaseReference.getKey()).setValue(message);
                }
            }
        }, 3000);
    }
}
