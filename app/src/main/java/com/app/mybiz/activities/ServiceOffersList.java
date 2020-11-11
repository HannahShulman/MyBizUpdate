package com.app.mybiz.activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.app.mybiz.Adapters.OffersRecyclerViewAdapter;
import com.app.mybiz.Objects.Service;
import com.app.mybiz.Objects.Specials;
import com.app.mybiz.R;

import java.util.ArrayList;

public class ServiceOffersList extends AppCompatActivity {

    String TAG = "serOffers";
    RecyclerView allOffers;
    OffersRecyclerViewAdapter adapter;
    ArrayList<Specials> specialsList;
    Intent i;
    Service currentService;
    DatabaseReference ref;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servcice_offers_list);
        toolbar = (Toolbar) findViewById(R.id.service_title);
        i = getIntent();

        specialsList=new ArrayList<>();
        currentService = (Service) i.getSerializableExtra("currentService");
        if (currentService!=null){
            toolbar.setTitle(currentService.getTitle());
            Log.d(TAG, "onCreate: "+currentService.getTitle());
            Log.d(TAG, "onCreate: "+"__"+currentService.getUserUid());
            //reference to offers list
            ref = FirebaseDatabase.getInstance().getReference().child("Services")
                    .child("PublicData")
                    .child(currentService.getKey())
                    .child("offers");
            ref.addChildEventListener(offersListListener);
//            Query query = ref.orderByValue().endAt(System.currentTimeMillis()+1000000000);
//            query.addChildEventListener(offersListListener);
        }else{
            Log.d(TAG, "onCreate: service is null");
        }
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.right_arrow_w);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        allOffers = (RecyclerView) findViewById(R.id.all_offers);
        allOffers.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
//        ref.addChildEventListener(offersListListener);
        adapter = new OffersRecyclerViewAdapter(ServiceOffersList.this, specialsList);
        allOffers.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ref.removeEventListener(offersListListener);
    }

    ChildEventListener offersListListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Specials specials = dataSnapshot.getValue(Specials.class);
            if (specials.getExpiryDate()>=System.currentTimeMillis()+1800000)
                specialsList.add(specials);
//            specialsList.add(specials);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            Specials removedSpecials = dataSnapshot.getValue(Specials.class);
            specialsList.remove(removedSpecials);
            adapter.notifyDataSetChanged();

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}
