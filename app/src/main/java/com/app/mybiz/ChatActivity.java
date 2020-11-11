package com.app.mybiz;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import androidx.annotation.RequiresApi;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.app.mybiz.Activities.AllServiceInfo;
import com.app.mybiz.Managers.FavoriteServiceManager;
import com.app.mybiz.Objects.Message;
import com.app.mybiz.Objects.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import android.os.Handler;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatActivity extends AppCompatActivity implements View.OnClickListener {


    private static final int REQUEST_IMAGE_CAPTURE = 111;
    private static final int CHOOSE_IMAGE_GALLARY = 222;
    private static final String TAG = "ChatActivity";
    private HashMap<String, Message> messagesMap = new HashMap<>();
    Intent i;
    String myId, otherId, otherName, myName, imageUrl, key, profileUrl,state = "";
    RecyclerView msgList;
    ImageView sendIcon, takePic,shareService, callService;
    EditText newMsg;
    TextView contactName, lastSeenTv;
    Toolbar toolbar;
    DatabaseReference sub_ref, ref, dRef,allUsersPublicDataRef = FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PublicData");
    Service otherService;
    boolean isService = false;
    RelativeLayout profileBackLayout;
    ArrayList<String> sent = new ArrayList<>();
    RecyclerView.Adapter msgListAdapter;
    MybizzNotificationManager notificationsManager;
    float density;
    String fileName;
    File baseFile;
    boolean isAnonymous;
    boolean isFavorite;
    ChildEventListener currentChatListener;
    ValueEventListener OnlineListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() != null) {
                boolean online = dataSnapshot.getValue(boolean.class);
                if (online) {
                    state = "Online";
                    lastSeenTv.setText(state + "");
                    contactName.setGravity(Gravity.BOTTOM);
                    lastSeenTv.setVisibility(View.VISIBLE);

                }
                else {
                    state = "";
                    contactName.setGravity(Gravity.CENTER_VERTICAL);
                    lastSeenTv.setText(state + "");
                    lastSeenTv.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    ValueEventListener UnseenMessagesListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            //+ "/unseenMessages"
            for (DataSnapshot snap : dataSnapshot.getChildren()) {
                if (snap.getKey().equals("unseenMessages")) {
                    FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/AllUsers/PublicData/" + otherId + "/chats/" + myId + "/unseenMessageNumber").setValue(snap.getChildrenCount());
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    ValueEventListener IsOtherTyping = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.getValue() != null) {
                String isTyping = dataSnapshot.getValue(String.class);
                if (isTyping.equals("true"))
                    lastSeenTv.setText(getResources()
                    .getString(R.string.typing));
                else
                    lastSeenTv.setText(state);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_chat);
        density = getResources().getDisplayMetrics().density;
        isAnonymous = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getBoolean(Constants.IS_ANONYMOUS, false);
        i = getIntent();
        if (!isAnonymous)
            myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        otherId = i.getStringExtra("otherID");
        otherName = i.getStringExtra("otherName");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setHomeButtonEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
        }
        toolbar.setTitleTextAppearance(this, R.style.MyTitleTextApperance);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        notificationsManager = MybizzNotificationManager.getInstance(this.getApplicationContext());
        notificationsManager.removeSeenMessagesFile(otherId);
        profileBackLayout = (RelativeLayout) findViewById(R.id.layout);
        profileBackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //initialising components from xml
        msgList = (RecyclerView) findViewById(R.id.messaging_list);
        sendIcon = (ImageView) findViewById(R.id.send_icon);
        sendIcon.setColorFilter(ContextCompat.getColor(getBaseContext(), R.color.red_create_account));
        takePic = (ImageView) findViewById(R.id.take_pic_chat);
        newMsg = (EditText) findViewById(R.id.new_message);

            newMsg.addTextChangedListener(new TextWatcher() {
                boolean isTyping = false;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 0) {
                        //show camra
                        takePic.setVisibility(View.VISIBLE);
                        sendIcon.setVisibility(View.GONE);
                    } else {
                        //show send
                        takePic.setVisibility(View.GONE);
                        sendIcon.setVisibility(View.VISIBLE);
                    }

                    isTyping = true;
                    if (!isAnonymous) {
                    FirebaseDatabase.getInstance()
                            .getReferenceFromUrl
                                    ("https://mybizz-3bbe5.firebaseio.com/AllUsers/PublicData/" + otherId + "/chats/" + myId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/AllUsers/PublicData/" + otherId + "/chats/" + myId + "/isTyping").setValue("true");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
                }


                @Override
                public void afterTextChanged(Editable s) {
                    final long DELAY = 5000; // milliseconds
                    Handler h = new Handler();
                    Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            if (isTyping) {
                                isTyping = false;
                                FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/AllUsers/PublicData/" + otherId + "/chats/" + myId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/AllUsers/PublicData/" + otherId + "/chats/" + myId + "/isTyping").setValue("false");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
//                            if (!FirebaseAuth.getInstance().getCurrentUser().isAnonymous())
//                            FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/AllUsers/PublicData/" + otherId + "/chats/" + myId + "/isTyping").setValue("false");
                            }
                        }
                    };
                    h.postDelayed(r, DELAY);

                }
            });

        contactName = (TextView) findViewById(R.id.contactName);
        contactName.setText(otherName);
        lastSeenTv = (TextView) findViewById(R.id.last_seen);
        //listen to if typing
        if (!isAnonymous)
            FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/AllUsers/PublicData/" + myId + "/chats/" + otherId + "/isTyping").addValueEventListener(IsOtherTyping);
        shareService = (ImageView) findViewById(R.id.share_service);
        callService = (ImageView) findViewById(R.id.call_icon);
        isService = i.getBooleanExtra("isService", false);
        if (isService) {
            otherService = (Service) i.getSerializableExtra("currentService");
            invalidateOptionsMenu();
            shareService.setVisibility(View.VISIBLE);
            callService.setVisibility(View.VISIBLE);
        }else{
            invalidateOptionsMenu();
            shareService.setVisibility(View.GONE);
            callService.setVisibility(View.GONE);
        }

        if (!isAnonymous) {
            if (otherService != null) {
                FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PublicData")
                        .child(myId).child("favorites").child(otherService.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        isFavorite = dataSnapshot.exists();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }

        ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/AllUsers/PublicData/" + otherId + "/online");
        ref.addValueEventListener(OnlineListener);

        shareService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MyApplication.shareService(otherService.getKey());
                Intent intent1 = new Intent(Intent.ACTION_SEND);
                intent1.setType("text/plain");
                intent1.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.get_my_service)+"https://mybizz.application.to/allInfo_" + otherService.getKey()+"  "
                        +getResources().getString(R.string.download_app)+" https://play.google.com/store/apps/details?id=com.app.mybiz"
                );
                intent1.putExtra(Intent.EXTRA_SUBJECT, "Check out this site!");
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent1, "Share"));
            }
        });
        callService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + otherService.getPhoneNumber()));
                startActivity(callIntent);
            }

        });
        //profile image
        final CircleImageView profileImage = (CircleImageView) findViewById(R.id.profile);
        DatabaseReference ref11 = FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/AllUsers/PrivateData/" + otherId + "/profileUrl");
        ref11.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                profileUrl = dataSnapshot.getValue(String.class);
                Glide.with(getBaseContext()).load(profileUrl).into(profileImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        LinearLayout ll = (LinearLayout) findViewById(R.id.toolbar_layout);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isService) {
                    try {
                        Intent intent = new Intent();
                        intent.setClass(ChatActivity.this, AllServiceInfo.class);
                        intent.putExtra("currentService", otherService);
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.d("snappp", e.toString());
                    }
                }
            }
        });

        //setting data to views;
        toolbar.setTitle(otherName);
        //when send button is clicked the message is pushed into the ChatStream message array.
        myName = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.NAME, Constants.RANDOM_STRING);
        sendIcon.setOnClickListener(this);
        takePic.setOnClickListener(this);
        //here we retrieve all messages between the two pple comunicating
        if (!isAnonymous) {
            sub_ref = FirebaseDatabase.getInstance()
                    .getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/PrivateChat/" + myId + "__" + otherId + "/messages");
        }
        msgListAdapter = new ChatAdapter(messages = new ArrayList<>());
        msgList.setAdapter(msgListAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        msgList.setLayoutManager(linearLayoutManager);
        if (!isAnonymous) {
            sub_ref.addChildEventListener(currentChatListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG + "1", "onChildAdded: " + dataSnapshot);
                    Message message = dataSnapshot.getValue(Message.class);
                    Log.d(TAG + "13", "onChildAdded: " + message.toString());
                    if (message.getFromUid() == null || message.getFromUid().isEmpty())
                        return;
                    messages.add(message);
                    Log.d(TAG + "13", "onChildAdded: " + message.getKey());
                    messagesMap.put(message.getKey(), message);
//                msgListAdapter.notifyDataSetChanged();
                    msgListAdapter.notifyItemInserted(messages.size() - 1);
                    msgList.smoothScrollToPosition(messages.size() - 1);

                    if (!message.fromUid.equals(myId)) {
                        long nowTime = System.currentTimeMillis();
                        String msgId = message.getKey();
                        if (message.getDateVisible() == 0) {
//                    Log.d(TAG, "populateView: " + this.getRef(i));
                            sub_ref.child(message.getKey()).child("dateVisible").setValue(nowTime);
                            FirebaseDatabase.getInstance()
                                    .getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/PrivateChat/" + otherId + "__" + myId + "/messages").child(msgId).child("dateVisible").setValue(nowTime);
                            //4. update unseen messages.
                            allUsersPublicDataRef.child(myId).child("chats").child(otherId).child("unseenMessages").removeValue();
                            //set number of unseen messages
                            allUsersPublicDataRef.child(myId).child("chats").child(otherId).child("unseenMessageNumber").setValue(0);
                            allUsersPublicDataRef.child(otherId).child("chats").child(myId).child("unseenMessageNumber").setValue(0);
                        }
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    //deal with message's date thats
                    Log.d(TAG + "12", "onStartCommand: " + dataSnapshot);
                    Message message = messagesMap.get(dataSnapshot.getKey());
                    int mesIndex = messages.indexOf(message);
                    Log.d(TAG, "onChildChanged: " + mesIndex);
                    if (message != null && mesIndex != -1) {
                        Message newMessage = dataSnapshot.getValue(Message.class);
                        if (newMessage.getFromUid() == null && newMessage.getFromUid().isEmpty())
                            return;
                        messages.set(mesIndex, newMessage);
                        messagesMap.put(dataSnapshot.getKey(), newMessage);
                        msgListAdapter.notifyItemChanged(mesIndex);
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        dRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/AllUsers/PublicData/" + otherId + "/chats/" + myId);
        dRef.addValueEventListener(UnseenMessagesListener);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        dRef.addValueEventListener(UnseenMessagesListener);
        ref.addValueEventListener(OnlineListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        dRef.addValueEventListener(UnseenMessagesListener);
        ref.addValueEventListener(OnlineListener);

    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        dRef.removeEventListener(UnseenMessagesListener);
        ref.removeEventListener(OnlineListener);
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
        dRef.removeEventListener(UnseenMessagesListener);
        ref.removeEventListener(OnlineListener);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " + resultCode);
        Log.d(TAG, "onActivityResult: " + android.os.Build.BRAND + "/" + android.os.Build.PRODUCT + "/"
                + android.os.Build.DEVICE);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (fileName != null && new File(baseFile, fileName).exists()) {
                try {
                    data = new Intent();
                    data.setClass(ChatActivity.this, SendMessageService.class);
                    data.putExtra("filePath", new File(baseFile, fileName).getPath());
                    data.putExtra("isContactService", isService);
                    data.putExtra("isImage", true);
                    data.putExtra("imageUrl", imageUrl);
                    data.putExtra("myId", myId);
                    data.putExtra("otherId", otherId);
                    data.putExtra("myName", myName);
                    data.putExtra("otherName", otherName);
                    data.putExtra("currentService", otherService);
                    startService(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(TAG, "onActivityResult: " + resultCode);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            if (otherService != null) {
                getMenuInflater().inflate(R.menu.service_chat_activity_menu, menu);
                MenuItem item = menu.findItem(R.id.service_number);
                SpannableString s;
                if (!isFavorite)
                    s = new SpannableString(getResources().getString(R.string.add_to_favorites));
                else
                    s = new SpannableString("הסר ממועדפים");
                s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s.length(), 0);
                item.setTitle(s);
                item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (!isAnonymous) {
                            final String myID = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).getString(Constants.APP_ID, Constants.RANDOM_STRING);
                            //1.
                            FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PublicData")
                                    .child(myID).child("favorites").child(otherService.getKey()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        FavoriteServiceManager.removeToFavoriteList(otherService.getKey(), myID);
                                        isFavorite=false;
                                        Toast.makeText(getBaseContext(), getString(R.string.service_removed), Toast.LENGTH_SHORT).show();
                                        dataSnapshot.getRef().removeEventListener(this);
                                        invalidateOptionsMenu();
                                    } else {
                                        FavoriteServiceManager.addToFavoriteList(otherService, otherService.getKey(), myID);
                                        isFavorite = true;
                                        Toast.makeText(getBaseContext(), getString(R.string.service_added), Toast.LENGTH_SHORT).show();
                                        dataSnapshot.getRef().removeEventListener(this);
                                        invalidateOptionsMenu();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }else {
                            if (isAnonymous || getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).getString(Constants.APP_ID, Constants.RANDOM_STRING).equals(Constants.RANDOM_STRING)){
                                final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ChatActivity.this);
                                builder.setMessage(getString(R.string.register_for_favorites));
                                builder.setCancelable(true);
                                builder .setPositiveButton(getString(R.string.register), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent intent = new Intent(ChatActivity.this, CreateAccountChoiceActivity.class);
                                        startActivity(intent);

                                    }
                                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                builder.setCancelable(false);
                                builder.create().show();
                            }
                        }
                        return false;
                    }
                });
                return true;
            }
            return false;
    }


    //overridden functions
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.take_pic_chat:
                if (!isAnonymous && !myId.equals(Constants.RANDOM_STRING)) {
                    Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePic.resolveActivity(getPackageManager()) != null) {
                        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, 1121);
                            }
                        } else {
                            File f = baseFile = new File(getExternalFilesDir(null), "tmp");
                            if (!f.exists())
                                f.mkdir();
                            takePic.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(f, fileName = System.currentTimeMillis() + ".jpg")));
                            startActivityForResult(takePic, REQUEST_IMAGE_CAPTURE);
                        }
                    }
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                    builder.setCancelable(true);
                    builder.setMessage(getResources().getString(R.string.chat_must_reg))
                            .setPositiveButton(getString(R.string.register), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent intent = new Intent(ChatActivity.this, CreateAccountChoiceActivity.class);
                                    startActivity(intent);

                                }
                            }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.create().show();
                    builder.setCancelable(true);

                }
                break;

            case R.id.send_icon:
                if (!isAnonymous && !myId.equals(Constants.RANDOM_STRING)) {
                    Intent intent1 = new Intent(ChatActivity.this, SendMessageService.class);
                    //send info
                    intent1.putExtra("isContactService", otherService != null);
                    if (isService)
                        intent1.putExtra("currentService", otherService);
                    intent1.putExtra("msgContent", newMsg.getText().toString());
                    intent1.putExtra("myId", myId);
                    intent1.putExtra("otherId", otherId);
                    intent1.putExtra("myName", myName);
                    intent1.putExtra("otherName", otherName);
                    intent1.putExtra("isImage", false);
                    Log.d(TAG, "onClick: "+myName.toString());
                    startService(intent1);
                    newMsg.setText("");
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                    builder.setCancelable(true);
                    builder.setMessage(getResources().getString(R.string.chat_must_reg))
                            .setPositiveButton(getString(R.string.register), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent intent = new Intent(ChatActivity.this, CreateAccountChoiceActivity.class);
                                    startActivity(intent);

                                }
                            }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.create().show();
                    builder.setCancelable(true);

                }
                break;
            default:
                break;
        }
    }

    ArrayList<Message> messages;

    class ChatAdapter extends RecyclerView.Adapter<BaseViewHolder> {


        public ChatAdapter(ArrayList<Message> message) {
            super();
            messages = message;
        }

        @Override
        public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            BaseViewHolder baseViewHolder = null;
            View view;
            switch (viewType){
                case 0:
                    view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.single_message_send, parent, false);
                    baseViewHolder = new BaseViewHolder(view);
                    break;
                case 1:
                    view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.single_message_rceived, parent, false);
                    baseViewHolder = new BaseViewHolder(view);
                    break;
                case 2:
                    view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.single_image_send, parent, false);
                    baseViewHolder = new ImageViewHolder(view);
                    break;
                case 3:
                    view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.single_image_rceived, parent, false);
                    baseViewHolder = new ImageViewHolder(view);
                    break;
            }

            return baseViewHolder;
        }

        @Override
        public void onBindViewHolder(BaseViewHolder holder, int position) {
            holder.update(messages.get(position), position);
        }

        @Override
        public int getItemCount() {
            Log.d(TAG, "getItemCount: "+messages.size());
            return messages.size();
        }

        @Override
        public int getItemViewType(int position) {
            Log.d(TAG, "getItemViewType: ");
            Message message = messages.get(position);
            if (message.getPictureUrl() == null) {//txt message
                if (message.getFromUid().equals(myId))
                    return 0;
                else
                    return 1;
            } else { // picture message
                if (message.getFromUid().equals(myId))
                    return 2;
                else
                    return 3;
            }
        }
    }

    class BaseViewHolder extends RecyclerView.ViewHolder{

        View itemView;
        ImageView check;
        TextView messageTv;
        TextView dateView;
        TextView timeDate;
        View dateWrapper, bgContainer;

        public BaseViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            check = (ImageView) itemView.findViewById(R.id.check);
            messageTv = (TextView) itemView.findViewById(R.id.message_txt);
            dateView = (TextView) itemView.findViewById(R.id.date_view);
            bgContainer =  itemView.findViewById(R.id.bgContainer);
            timeDate = (TextView) itemView.findViewById(R.id.time);
            dateWrapper = itemView.findViewById(R.id.date_wrapper);
        }

        public void update(Message message, int pos){
            Log.d(TAG, "update: ");
            messageTv.setText(message.getContent());
            setPaddingBefore(message, pos);
            updateVisibleIndicator(message);
            setDate(message, pos);
            setMessageTime(message);
        }

        protected void setMessageTime(Message message) {
            int gmtOffset = TimeZone.getDefault().getOffset(System.currentTimeMillis());
            long msgTime = message.getDateCreated() + gmtOffset;
            SimpleDateFormat formatterTime = new SimpleDateFormat("HH:mm");
            formatterTime.setTimeZone(TimeZone.getTimeZone("UTC"));
            String timeString = formatterTime.format(new Date(msgTime));
            timeDate.setText(timeString + "");
        }

        public void setPaddingBefore(Message message, int i) {
            if (i > 0) {
                //set padding between messages
                if (messages.get(i - 1).getFromUid().equals(message.getFromUid())) {
                    itemView.setPadding(0, Math.round((float) 2 * density), 0, 0);
                    bgContainer.setSelected(true);
                } else {
                    itemView.setPadding(0, Math.round((float) 10 * density), 0, 0);
                    bgContainer.setSelected(false);
                }

            }else{
                itemView.setPadding(0, Math.round((float) 10 * density), 0, 0);
                bgContainer.setSelected(false);
            }
        }


        public void updateVisibleIndicator(Message message) {
            if (check == null)
                return;
            if (message.getDateVisible() == 0 && message.getDateSent() == 0) {
                check.setAlpha(.56f);
                check.setImageResource(R.drawable.ic_done_black_18dp);//
            } else if (message.getDateVisible() != 0) {
                check.setAlpha(1.0f);
                check.setImageResource(R.drawable.ic_done_all_red_18dp);//two red checks
            } else {
                check.setAlpha(.56f);
                check.setImageResource(R.drawable.ic_done_all_black_18dp);
            }
        }

        public void setDate(Message message, int i){
            int gmtOffset = TimeZone.getDefault().getOffset(System.currentTimeMillis());
            long msgTime1 = message.getDateCreated() + gmtOffset;
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat hFormat = new SimpleDateFormat("HH:mm");


            if(i == 0) {
                long firstMsgTime = message.getDateCreated();
                firstMsgTime += gmtOffset;
                String dateString = formatter.format(new Date(firstMsgTime));
                dateWrapper.setVisibility(View.VISIBLE);
                dateView.setText(dateString);
                return;
            }

            long lastMsgTime = messages.get(i - 1).getDateCreated();
            lastMsgTime += gmtOffset;
            String dateString = formatter.format(new Date(msgTime1));
            Log.d(TAG, "populateView: lmt" + dateString);
            String lastDateString = formatter.format(new Date(lastMsgTime));

            if (lastDateString.equals(dateString)) {
                dateWrapper.setVisibility(View.GONE);
            } else {
                dateWrapper.setVisibility(View.VISIBLE);
                dateView.setText(dateString);
            }

        }

    }
    class ImageViewHolder extends BaseViewHolder{

        com.app.mybiz.views.SquareImageView imageView;
        Message message;
        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = (com.app.mybiz.views.SquareImageView) itemView.findViewById(R.id.image);
            Log.d(TAG, "ImageViewHolder: "+imageView.getWidth());
            imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(message != null)
                            new ViewDialog().showDialog(ChatActivity.this, message.getPictureUrl());
                    }
            });
        }

        @Override
        public void update(Message message, int pos) {
            this.message = message;
            setPaddingBefore(message, pos);
            updateVisibleIndicator(message);
            setDate(message, pos);
            setMessageTime(message);
            setImage(message);
        }

        public void setImage(Message message){
            imageView.setVisibility(View.VISIBLE);
            Log.d(TAG, "setImage: "+imageView.getMeasuredWidth());
            Glide.with(getBaseContext()).load(message.getPictureUrl()).into(imageView);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    if (!isAnonymous)
        sub_ref.removeEventListener(currentChatListener);
    }
}