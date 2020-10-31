package com.mybiz.Fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mybiz.Adapters.SingleServiceListAdapter;
import com.mybiz.Objects.Service;
import com.mybiz.R;

import java.util.ArrayList;
import java.util.HashMap;

public class FavoriteServiceListFragment extends Fragment {

    String TAG = "favList";
    String myId;
    RecyclerView singleServices;
    SingleServiceListAdapter serviceListAdapter;
    ArrayList<Service> serviceArrayList;
    DatabaseReference ref;
    HashMap<String, Service> serviceList;
    LinearLayout empty_favorites_layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myId = getArguments().getString("myId");
        serviceList = new HashMap<>();
        View rootView = inflater.inflate(R.layout.fragment_single_service_list, container, false);
        singleServices = (RecyclerView) rootView.findViewById(R.id.single_services);
        empty_favorites_layout = (LinearLayout) rootView.findViewById(R.id.empty_favorites_layout);
        singleServices.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        serviceArrayList = new ArrayList<>();
        serviceListAdapter = new SingleServiceListAdapter(getActivity(), serviceArrayList);
        singleServices.setAdapter(serviceListAdapter);
        ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/AllUsers/PublicData/" + myId + "/favorites");


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        ref.addChildEventListener(myFavoritesListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        ref.removeEventListener(myFavoritesListener);
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    ChildEventListener myFavoritesListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Service favService = dataSnapshot.getValue(Service.class);
            Log.d(TAG, "onChildAdded: "+dataSnapshot.getValue());
            if (serviceList.get(favService.getKey())==null){
                serviceList.put(favService.getKey(), favService);
                serviceArrayList.add(favService);
                Log.d(TAG, "onChildAdded: "+serviceArrayList.size());
                serviceListAdapter.notifyDataSetChanged();
                if (serviceArrayList.size()==0){
                    //show layout
                    empty_favorites_layout.setVisibility(View.VISIBLE);
                    singleServices.setVisibility(View.GONE);
                }else{
                    //show recycler view
                    empty_favorites_layout.setVisibility(View.GONE);
                    singleServices.setVisibility(View.VISIBLE);
                }
            }

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            Log.d(TAG, "onChildRemoved: "+dataSnapshot);
            Service favService = dataSnapshot.getValue(Service.class);
            boolean found = false;
            for (int i = 0; i < serviceArrayList.size() && !found; i++) {
                if (serviceArrayList.get(i).getKey().equals(favService.getKey())) {
                    serviceArrayList.remove(i);
                    found=true;
            }

                if (serviceArrayList.size()==0){
                    //show layout
                    empty_favorites_layout.setVisibility(View.VISIBLE);
                    singleServices.setVisibility(View.GONE);
                }else{
                    //show recycler view
                    empty_favorites_layout.setVisibility(View.GONE);
                    singleServices.setVisibility(View.VISIBLE);
                }

//            serviceArrayList = new ArrayList<Service>();
//            for (Map.Entry<String, Service> entry : serviceList.entrySet()) {
//                String key = entry.getKey();
//                Log.d(TAG, "onChildRemoved1: "+key);
//                Service value = entry.getValue();
//                Log.d(TAG, "onChildRemoved2: "+value.toJson());
//                serviceArrayList.add(value);
            }

            serviceListAdapter.notifyDataSetChanged();
//            serviceListAdapter = new SingleServiceListAdapter(getContext(), serviceArrayList);
//            singleServices.setAdapter(serviceListAdapter);

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}

