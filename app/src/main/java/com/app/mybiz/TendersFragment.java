package com.app.mybiz;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.app.mybiz.activities.IntroTendersActivity;
import com.app.mybiz.adapters.TenderRecyclerAdapter;
import com.app.mybiz.objects.Tenders;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hannashulmah on 23/12/2016.
 */
public class TendersFragment extends Fragment implements View.OnClickListener {

    String TAG = "tend_fragment";
    FloatingActionButton fab;
    RecyclerView tenderList;
    ArrayList<Tenders> tendersList;
    TenderRecyclerAdapter tendersAdapter;
    HashMap<String, Tenders> myTenderList;
    RelativeLayout fragmentCover;
    String town = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        tendersList = new ArrayList<>();
        myTenderList = new HashMap<>();
        Log.d(TAG, "onCreateView: "+tendersList.size()+"_"+myTenderList.size());
        View rootView = inflater.inflate(R.layout.activity_tenders, container, false);
        initViews(rootView);
        setListeners();
        getList();
        Log.d(TAG, "onCreateView: "+tendersList.size());
        tenderList.setAdapter(tendersAdapter);
        return rootView;
    }



    private void initViews(View v) {
        fab = (FloatingActionButton) v.findViewById(R.id.add_tender_fab);
        tenderList = (RecyclerView) v.findViewById(R.id.tender_list);
        tenderList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        tendersAdapter = new TenderRecyclerAdapter(getContext(), tendersList);

        fragmentCover = (RelativeLayout) v.findViewById(R.id.fragment_cover);
    }

    private void setListeners() {
        fab.setOnClickListener(this);
    }

    private boolean isService(){
        if (getActivity().getSharedPreferences(PreferenceKeys.PREFERENCES, Context.MODE_PRIVATE).getBoolean(PreferenceKeys.IS_SERVICE, false)){
            String str = getActivity().getSharedPreferences(PreferenceKeys.PREFERENCES, Context.MODE_PRIVATE).getString(PreferenceKeys.MY_SERVICE, "");
            Log.d(TAG, "isService: str: "+str);
            if(str != null && ! str.isEmpty()){
                try{
                    JsonObject jsonObject = new JsonParser().parse(new StringReader(str)).getAsJsonObject();
                    town = jsonObject.get("town").getAsString();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            return true;
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), IntroTendersActivity.class);
        getActivity().startActivity(intent);
    }

    public void getList() {

        final String myId = getActivity().getSharedPreferences(PreferenceKeys.PREFERENCES, Context.MODE_PRIVATE).getString(PreferenceKeys.APP_ID, PreferenceKeys.RANDOM_STRING);
        final String myCategory = getActivity().getSharedPreferences(PreferenceKeys.PREFERENCES, Context.MODE_PRIVATE).getString(PreferenceKeys.MY_CATEGORY, PreferenceKeys.RANDOM_STRING);
        final String mySubCategory = getActivity().getSharedPreferences(PreferenceKeys.PREFERENCES, Context.MODE_PRIVATE).getString(PreferenceKeys.MY_SUB_CATEGORY, PreferenceKeys.RANDOM_STRING);
        //1. first my tenders from AllUsers-->privateData-->myId-->myTenders
        //2. if i am a service get relevant tenders from Services-->PublicData-->MyCategory -->mySubCategory... query if town matches and tenders key is not in hash map

        Log.d(TAG, "getList: "+myCategory+"__"+mySubCategory);

        //tenders reference


        if (isService() && !myCategory.equals(PreferenceKeys.RANDOM_STRING) && !mySubCategory.equals(PreferenceKeys.RANDOM_STRING)){
            DatabaseReference tendersRef = FirebaseDatabase.getInstance().getReference().child("Tenders").child(myCategory).child(mySubCategory);
            Log.d(TAG, "getList: "+ tendersRef.toString());
            Log.d(TAG, "getList: "+town);
//            Query query = tendersRef.orderByChild("town").equalTo(town);

            tendersRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Tenders tenders = dataSnapshot.getValue(Tenders.class);
                    Log.d(TAG, "onChildAddedservice: "+dataSnapshot);
                    if (myTenderList.get(tenders.getKey())==null && !tenders.getUid().equals(myId) && (tenders.getExpiryTime()-System.currentTimeMillis()>0)) {
                        myTenderList.put(tenders.getKey(), tenders);
                        tendersList.add(tenders);
                        tendersAdapter.notifyDataSetChanged();
                    }

                    if (tendersList.size() == 0) {
                        tenderList.setVisibility(View.GONE);
                        fragmentCover.setVisibility(View.VISIBLE);
                    } else {
                        tenderList.setVisibility(View.VISIBLE);
                        fragmentCover.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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

        FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PrivateData").child(myId).child("myTenders").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded: my tender  "+dataSnapshot.getValue());
                Tenders t = dataSnapshot.getValue(Tenders.class);
                if (myTenderList.get(t.getKey())==null) {
                    myTenderList.put(t.getKey(), t);
                    tendersList.add(t);
                    tendersAdapter.notifyDataSetChanged();
                }

                if (tendersList.size() == 0) {
                    tenderList.setVisibility(View.GONE);
                    fragmentCover.setVisibility(View.VISIBLE);
                } else {
                    tenderList.setVisibility(View.VISIBLE);
                    fragmentCover.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Tenders t = dataSnapshot.getValue(Tenders.class);
                myTenderList.remove(t.getKey());
                tendersList.remove(t);
                tendersAdapter.notifyDataSetChanged();


                if (tendersList.size() == 0) {
                    tenderList.setVisibility(View.GONE);
                    fragmentCover.setVisibility(View.VISIBLE);
                } else {
                    tenderList.setVisibility(View.VISIBLE);
                    fragmentCover.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
