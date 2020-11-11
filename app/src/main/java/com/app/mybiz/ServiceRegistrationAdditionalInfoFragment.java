package com.app.mybiz;

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
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.app.mybiz.Interface.RequiredFields;
import com.app.mybiz.Objects.Service;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hannashulmah on 28/12/2016.
 */

public class ServiceRegistrationAdditionalInfoFragment extends Fragment implements View.OnClickListener, RequiredFields{
    EditText shortDescription, additionalAttributes;
    ArrayList<String> cities;
    String TAG = "ServiceRegistration";
    TextView text_counter1, text_counter2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.service_registration_fragment_one, container, false);
        shortDescription = (EditText) rootView.findViewById(R.id.short_description);
        additionalAttributes = (EditText) rootView.findViewById(R.id.additional_attributes);
        text_counter1 = (TextView) rootView.findViewById(R.id.text_counter1);
        text_counter2 = (TextView) rootView.findViewById(R.id.text_counter2);
        shortDescription.setText(ServiceRegistrationActivityForm.newService.getShortDescription());
        additionalAttributes.setText(ServiceRegistrationActivityForm.newService.getAdditionalInfo());
        shortDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()<=90)
                    text_counter1.setText(s.length()+"/90");
                ServiceRegistrationFragmentContainer.allowToSaveInfo(toSave());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        additionalAttributes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()<=600)
                    text_counter2.setText(s.length()+"/600");
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
        Log.d(TAG, "onPause: ");
        if (ServiceRegistrationFragmentContainer.saveVsBack== ServiceRegistrationFragmentContainer.SAVE) {
            ServiceRegistrationActivityForm.newService.setShortDescription(shortDescription.getText().toString());
            ServiceRegistrationActivityForm.newService.setAdditionalInfo(additionalAttributes.getText().toString());
            ServiceRegistrationFragmentContainer.map.put("shortDescription",shortDescription.getText().toString());
            ServiceRegistrationFragmentContainer.map.put("additionalInfo",additionalAttributes.getText().toString());

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
    }




    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public boolean toSave() {
    if (ServiceRegistrationActivityForm.newService.getShortDescription().equals(shortDescription.getText().toString())
                && ServiceRegistrationActivityForm.newService.getAdditionalInfo().equals(additionalAttributes.getText().toString()))
        return false;
    return true;
    }
}



