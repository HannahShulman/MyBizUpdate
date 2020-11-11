package com.app.mybiz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.mybiz.objects.ChatSummary;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.app.mybiz.objects.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by hannashulmah on 23/12/2016.
 */
public class RecentChatStreams extends Fragment {

    public RecyclerView recentMsg;
    String myId;
    String TAG = "RcntCtStrms";
    String category  = "פרטי";
    FirebaseRecyclerAdapter<ChatSummary, RecentChatViewHolder> usersListAdapter;
    LinearLayout empty_chats_layout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.activity_recent_chats, container, false);

        myId = getActivity().getSharedPreferences(PreferenceKeys.PREFERENCES, Context.MODE_PRIVATE).getString(PreferenceKeys.APP_ID, PreferenceKeys.RANDOM_STRING);
        recentMsg = (RecyclerView) rootview.findViewById(R.id.recent_msg_list);
        empty_chats_layout = (LinearLayout) rootview.findViewById(R.id.empty_chats_layout);
        RecyclerView.LayoutManager manager  = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recentMsg.setLayoutManager(manager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recentMsg.getContext(), LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.listview_divider));
        dividerItemDecoration.setOrientation(DividerItemDecoration.VERTICAL);
        recentMsg.addItemDecoration(dividerItemDecoration);


        DatabaseReference sub_ref = FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PublicData").child(myId).child("chats");
        sub_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()==0){
                    //show layout
                    empty_chats_layout.setVisibility(View.VISIBLE);
                    recentMsg.setVisibility(View.GONE);
                }else{
                    //show recycler
                    recentMsg.setVisibility(View.VISIBLE);
                    empty_chats_layout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Query query = sub_ref.orderByChild("invertLastMessage");
        usersListAdapter = new FirebaseRecyclerAdapter<ChatSummary, RecentChatViewHolder>(ChatSummary.class, R.layout.single_chat_summary, RecentChatViewHolder.class, query) {
            @Override
            protected void populateViewHolder(final RecentChatViewHolder viewHolder, final ChatSummary model, final int position) {
                viewHolder.chat_summary.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                ChatSummary cs = usersListAdapter.getItem(position);
                Intent i = new Intent(getActivity(), ChatActivity.class);

                if (cs.getOtherContactService()!=null){
                    i.putExtra("isService", true);
                    i.putExtra("currentService", cs.getOtherContactService());

                }
                i.putExtra("myID", myId);
                i.putExtra("otherID", model.getOtherId());
                Log.d(TAG, "onItemClick: "+model.getOtherId());
                i.putExtra("otherName", model.getMyName());
                startActivity(i);
                    }
                });

                Service s = model.getOtherContactService();
                if (s!=null)
                    category = s.getCategory();
                viewHolder.categoryType.setText(category);

                String lastSender = model.getLastSender();
                    if (lastSender!=null && !lastSender.equals(myId)) {
                        viewHolder.seenIcon.setVisibility(View.GONE);
                    } else {
                        //if message was not received to phone
                        //if message was received to phone but not seen
                        //if message was seen
                        viewHolder.seenIcon.setVisibility(View.VISIBLE);
                        if (model.getLastMessageSeen() != 0)
                            viewHolder.seenIcon.setColorFilter(Color.RED);
                        else
                            viewHolder.seenIcon.setColorFilter(Color.BLACK);
                    }


                int gmtOffset = TimeZone.getDefault().getOffset(System.currentTimeMillis());
                SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                String dateString = formatter.format(new Date(model.getLastMessageTime() + gmtOffset));
                viewHolder.lastMessageTime.setText(dateString);

                //profile image

                DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/AllUsers/PrivateData/" + model.getOtherId() + "/profileUrl");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onDataChange: "+dataSnapshot);
                        String profileUrl = dataSnapshot.getValue(String.class);
                        Glide.with(getContext()).load(profileUrl).into(viewHolder.profileImage);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                long timestampString = model.getLastMessageTime();
                Date date = new Date(timestampString * 1000);
                Calendar cal = Calendar.getInstance();
                TimeZone tz = cal.getTimeZone();
                SimpleDateFormat timeSdt = new SimpleDateFormat("HH:mm");
                timeSdt.setTimeZone(tz);
                String time = timeSdt.format(date);


                SimpleDateFormat dateSdt = new SimpleDateFormat("dd/MM/yyyy");
                dateSdt.setTimeZone(tz);
                String lastMsgDate = timeSdt.format(date);
                String nowDate = dateSdt.format(System.currentTimeMillis() * 1000);



                String seenContent;
                if (model.getIsTyping() != null && model.getIsTyping().equals("true")) {
                    viewHolder.txt_msg.setTextColor(getResources().getColor(R.color.main_color));
                    viewHolder.txt_msg.setText(getResources().getString(R.string.typing));
                } else {
                    seenContent = model.getLastMessageContent();
                    viewHolder.txt_msg.setTextColor(getResources().getColor(R.color.tw__composer_black));
                    if (model.getLastMessageContent()!=null && model.getLastMessageContent().length()>=21){
                         seenContent = model.getLastMessageContent().substring(0, 20)+"...";
                    }
                    else if (model.getLastMessageContent()==null)
                    {
                       seenContent =getString(R.string.image_recieved);
                    }
                    viewHolder.txt_msg.setText(seenContent);
                }

                if (model.getUnseenMessageNumber() != 0)
                    viewHolder.contactName.setText(model.getMyName() + " " + model.getUnseenMessageNumber());
                else
                    viewHolder.contactName.setText(model.getMyName());


            }
        };

        recentMsg.setAdapter(usersListAdapter);

//        recentMsg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ChatSummary cs = usersListAdapter.getItem(position);
//                Intent i = new Intent(getActivity(), ChatActivity.class);
//
//                if (cs.getOtherContactService()!=null){
//                    i.putExtra("isService", true);
//                    i.putExtra("currentService", cs.getOtherContactService());
//
//                }
//                i.putExtra("myID", myId);
//                i.putExtra("otherID", cs.getOtherId());
//                Log.l(TAG, "onItemClick: "+cs.getOtherId());
//                i.putExtra("otherName", cs.getMyName());
//                startActivity(i);
//            }
//        });

        return rootview;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
        SharedPreferences preferences = getActivity().getSharedPreferences(PreferenceKeys.PREFERENCES, MODE_PRIVATE);
        myId = preferences.getString(PreferenceKeys.APP_ID, PreferenceKeys.RANDOM_STRING);
    }

    public static class RecentChatViewHolder extends RecyclerView.ViewHolder{

        TextView categoryType, lastMessageTime, contactName, txt_msg;
        ImageView seenIcon;
        de.hdodenhof.circleimageview.CircleImageView profileImage;
        RelativeLayout chat_summary;

        public RecentChatViewHolder(View itemView) {
            super(itemView);
             categoryType = (TextView) itemView.findViewById(R.id.category);
            seenIcon = (ImageView) itemView.findViewById(R.id.seen_icon);
            lastMessageTime = (TextView) itemView.findViewById(R.id.msg_time);
            profileImage = (de.hdodenhof.circleimageview.CircleImageView) itemView.findViewById(R.id.profile);
            contactName = (TextView) itemView.findViewById(R.id.contact_name);
            txt_msg = (TextView) itemView.findViewById(R.id.txt_msg);
            chat_summary = (RelativeLayout) itemView.findViewById(R.id.chat_summary);

        }


    }

}
