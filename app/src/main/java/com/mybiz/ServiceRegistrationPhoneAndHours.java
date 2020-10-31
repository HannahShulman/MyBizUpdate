package com.mybiz;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.mybiz.Interface.RequiredFields;
import com.mybiz.Objects.Service;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by hannashulmah on 28/12/2016.
 */

public class ServiceRegistrationPhoneAndHours extends Fragment implements RequiredFields{
    String TAG = "phoneAndHoursFragment";
    EditText phone_et, openning_et;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.service_registration_fragment_two, container, false);
        phone_et = (EditText) rootView.findViewById(R.id.phone_et);
        openning_et = (EditText) rootView.findViewById(R.id.openning_et);
        phone_et.setText(ServiceRegistrationActivityForm.newService.getPhoneNumber());
        openning_et.setText(ServiceRegistrationActivityForm.newService.getOpeningHours());
        phone_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ServiceRegistrationFragmentContainer.allowToSaveInfo(toSave());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        openning_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    ServiceRegistrationFragmentContainer.allowToSaveInfo(toSave());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (ServiceRegistrationFragmentContainer.saveVsBack== ServiceRegistrationFragmentContainer.SAVE){
            ServiceRegistrationActivityForm.newService.setPhoneNumber(phone_et.getText().toString().trim());
            ServiceRegistrationActivityForm.newService.setOpeningHours(openning_et.getText().toString().trim());
            ServiceRegistrationFragmentContainer.map.put("phoneNumber",phone_et.getText().toString().trim());
            ServiceRegistrationFragmentContainer.map.put("openingHours",openning_et.getText().toString().trim());

            if (ServiceRegistrationFragmentContainer.userState.equals("isEdit")){
                //update all fields in database
                String service = getActivity().getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).getString(Constants.MY_SERVICE, Constants.RANDOM_STRING);
                Gson gson = new Gson();
                try {
                    JSONObject obj = new JSONObject(service);
                    final Service s = gson.fromJson(obj.toString(), Service.class);
                    String serviceKey = s.getKey();
                    Log.d(TAG, "onPause: "+serviceKey);
                    Log.d(TAG, "onPause: "+ ServiceRegistrationFragmentContainer.map.size());
                    //all references to service
                    FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PublicData")
                            .child(s.getUserUid()).child("services").child(s.getKey()).updateChildren(ServiceRegistrationFragmentContainer.map);
                    FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PrivateData")
                            .child(s.getUserUid()).child("services").child(s.getKey()).updateChildren(ServiceRegistrationFragmentContainer.map);
                    FirebaseDatabase.getInstance().getReference().child("Services").child("PublicData")
                            .child(s.getKey()).child("followers").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String st) {
                            String userKey = dataSnapshot.getValue(String.class);
                            Log.d(TAG, "onChildAdded: "+userKey);
                            FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PrivateData").child(userKey)
                                    .child("favorites").child(s.getKey()).updateChildren(ServiceRegistrationFragmentContainer.map);

                            FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PublicData").child(userKey)
                                    .child("favorites").child(s.getKey()).updateChildren(ServiceRegistrationFragmentContainer.map);

                            dataSnapshot.getRef().removeEventListener(this);

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
                    FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PublicData").child(s.getUserUid())
                            .child("chats").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String st) {
                            String key = dataSnapshot.getKey();
                            FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PublicData")
                                    .child(key).child("chats").child(s.getUserUid()).child("otherContactService").updateChildren(ServiceRegistrationFragmentContainer.map);
                            dataSnapshot.getRef().removeEventListener(this);
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


                    FirebaseDatabase.getInstance().getReference().child("Services").child("PrivateData").child(serviceKey).updateChildren(ServiceRegistrationFragmentContainer.map);
                    FirebaseDatabase.getInstance().getReference().child("Services").child("PublicData").child(serviceKey).updateChildren(ServiceRegistrationFragmentContainer.map);
                    Log.d(TAG, "onPause: "+ServiceRegistrationActivityForm.newService.toString());
                    getActivity().getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).edit().putString(Constants.MY_SERVICE, ServiceRegistrationActivityForm.newService.toJson()).commit();
                    Log.d(TAG, "onPause: "+getActivity().getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).getString(Constants.MY_SERVICE, Constants.RANDOM_STRING));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
           }
        }
        ServiceRegistrationFragmentContainer.map = new HashMap<>();
    }

    @Override
    public boolean isComplete() {
        if (phone_et.getText().toString().length()!=0 && openning_et.getText().toString().length()!=0)
        return true;
        return false;
    }

    @Override
    public boolean toSave() {
    if (!ServiceRegistrationActivityForm.newService.getPhoneNumber().equals(phone_et.getText().toString())
                || !ServiceRegistrationActivityForm.newService.getOpeningHours().equals(openning_et.getText().toString()) && phone_et.getText().length()!=0 && openning_et.getText().length()!=0 )
        return true;
    return false;
    }
}

