package com.mybiz;

import android.content.Context;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mybiz.Interface.RequiredFields;
import com.mybiz.Objects.Service;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by hannashulmah on 01/03/2017.
 */

public class ServiceRegistrationLocationFragment extends Fragment  implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, RequiredFields, View.OnClickListener, View.OnFocusChangeListener {

    EditText serviceHomeNumber;
    AutoCompleteTextView serviceTown, serviceAddress;
    ArrayList<String> cities, list;
            TextView errorMsgTxt, errorMsgTxt1;
    String TAG = "ServiceRegistration";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.service_registration_location, container, false);
        ServiceRegistrationFragmentContainer.allowToSaveInfo(false);
        list = new ArrayList<>();
        errorMsgTxt = (TextView) rootView.findViewById(R.id.error_msg_txt);
        errorMsgTxt1 = (TextView) rootView.findViewById(R.id.error_msg_txt1);
//        errorMsgTxt2 = (TextView) rootView.findViewById(R.id.error_msg_txt2);
        errorMsgTxt.setVisibility(View.GONE);
        errorMsgTxt1.setVisibility(View.GONE);
//        errorMsgTxt2.setVisibility(View.GONE);
        serviceAddress = (AutoCompleteTextView) rootView.findViewById(R.id.address_et);
        serviceHomeNumber = (EditText) rootView.findViewById(R.id.home_number);
        serviceHomeNumber.setOnFocusChangeListener(this);
        serviceTown = (AutoCompleteTextView) rootView.findViewById(R.id.town_ed);
        new AsyncTask(){
            @Override
            protected Object doInBackground(Object[] objects) {
                cities = getCities();
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                serviceTown.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, cities));
            }
        }.execute();

        serviceTown.setOnFocusChangeListener(this);
        serviceAddress.setOnFocusChangeListener(this);
        serviceAddress.setText(ServiceRegistrationActivityForm.newService.getAddress());
        serviceTown.setText(ServiceRegistrationActivityForm.newService.getTown());
        serviceHomeNumber.setText(ServiceRegistrationActivityForm.newService.getServiceHomeNumber());
        serviceAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                ServiceRegistrationFragmentContainer.allowToSaveInfo(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ServiceRegistrationFragmentContainer.allowToSaveInfo(toSave());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        serviceHomeNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ServiceRegistrationFragmentContainer.allowToSaveInfo(toSave());
                serviceAddress.getBackground().clearColorFilter();
                serviceAddress.invalidate();
                errorMsgTxt1.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        serviceTown.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                ServiceRegistrationFragmentContainer.allowToSaveInfo(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                serviceAddress.setText("");
                ServiceRegistrationFragmentContainer.allowToSaveInfo(toSave());
                serviceTown.getBackground().clearColorFilter();
                serviceTown.invalidate();
                errorMsgTxt.setVisibility(View.GONE);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        serviceTown.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    if (!cities.contains(serviceTown.getText().toString())){
                        errorMsgTxt.setVisibility(View.VISIBLE);
                    }else{
                        errorMsgTxt.setVisibility(View.GONE);
                    }
                }
                else{
                    errorMsgTxt.setVisibility(View.GONE);
                }
            }
        });
        return rootView;
    }

    private ArrayList<String> getCities() {
        ArrayList<String> list = new ArrayList<>();
        try {
            JsonArray jsonArray = new JsonParser().parse(new InputStreamReader(getContext().getAssets().open("cs.txt"))).getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                Log.d("testcs", "getCities: "+jsonArray.get(i));
                list.add(jsonArray.get(i).getAsString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        if (ServiceRegistrationFragmentContainer.saveVsBack==ServiceRegistrationFragmentContainer.SAVE) {
            ServiceRegistrationActivityForm.newService.setAddress(serviceAddress.getText().toString());
            ServiceRegistrationActivityForm.newService.setTown(serviceTown.getText().toString());
            ServiceRegistrationActivityForm.newService.setServiceHomeNumber(serviceHomeNumber.getText().toString());
            new AsyncTask() {
                String town = serviceTown.getText().toString();
                String address = serviceAddress.getText().toString()+" "+serviceHomeNumber.getText().toString();
                @Override
                protected Object doInBackground(Object[] objects) {
                    getLocation("ישראל", town, address);
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    ServiceRegistrationFragmentContainer.map.put("address",serviceAddress.getText().toString());
                    ServiceRegistrationFragmentContainer.map.put("town",serviceTown.getText().toString());
                    ServiceRegistrationFragmentContainer.map.put("serviceHomeNumber", serviceHomeNumber.getText().toString());
                    Log.d(TAG, "1122onPause: "+ServiceRegistrationActivityForm.newService.getL());
                    ServiceRegistrationFragmentContainer.map.put("l", ServiceRegistrationActivityForm.newService.getL());

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
            }.execute();



            }
        Log.d(TAG, "onPause: "+ServiceRegistrationActivityForm.newService.toJson());
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("chaniii", "onStop");
    }

    @Override
    public void onResume() {
        super.onResume();
        //fil fields data from newUser object.
    }

            @Override
            public void onConnected(@Nullable Bundle bundle) {

            }

            @Override
            public void onConnectionSuspended(int i) {

            }

            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

            }

    @Override
    public boolean isComplete() {
        if (
            serviceAddress.getText().toString().length() != 0 &&
            serviceTown.getText().toString().length() != 0
            )
            return true;
        return false;
            }

    @Override
    public boolean toSave() {
        Log.d(TAG, "toSave: "+serviceTown.getListSelection());
        Log.d(TAG, "toSave1: "+ServiceRegistrationActivityForm.newService.getTown()+"__"+serviceTown.getEditableText().toString());
        Log.d(TAG, "toSave2: "+ServiceRegistrationActivityForm.newService.getAddress()+"__"+serviceAddress.getEditableText().toString());
        Log.d(TAG, "toSave3: "+ServiceRegistrationActivityForm.newService.getServiceHomeNumber()+"__"+serviceHomeNumber.getText().toString());
        if (ServiceRegistrationActivityForm.newService.getTown().equals(serviceTown.getText().toString())
                && ServiceRegistrationActivityForm.newService.getAddress().equals(serviceAddress.getText().toString())){
            return false;
        }
        if (isValid(cities,serviceTown.getText().toString()) && isValid(list, serviceAddress.getText().toString()))
            return true;

        return false;
    }

    private boolean isValid(ArrayList <String> list ,String location){
        if (list.contains(location))
            return true;
        return false;
    }

    @Override
            public void onClick(View v) {
                switch (v.getId()){

                }
            }

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                switch (v.getId()){
                    case R.id.town_ed:
                        Log.d(TAG, "onFocusChange: "+hasFocus+"__"+v.getId());
                        if(! hasFocus) {
                            validateText();
                        }else{
                            errorMsgTxt1.setVisibility(View.GONE);
                        }

                        break;
                    case R.id.address_et:
                        if(hasFocus) {
                            errorMsgTxt1.setVisibility(View.GONE);
                            if(validateTown()){
                                Log.d(TAG, "onFocusChange: setadapter");
                                setStreetsAdapter();
                            }
                        }else{
                            if (!hasFocus){
                                //check that street is ok
                                if (serviceHomeNumber.hasFocus()) {
                                    String street = serviceAddress.getText().toString();
                                    if (!list.contains(street)) {
                                        errorMsgTxt1.setVisibility(View.VISIBLE);
                                        serviceHomeNumber.requestFocus();
                                    }
                                    else
                                        errorMsgTxt1.setVisibility(View.GONE);
                                    if (street.isEmpty() || list == null)
                                        return;
                                    if (validateStreet())
                                        return;
                                }
                            }
                        }
                        break;
                    case R.id.home_number:
                        if(! hasFocus){
                            getLocation("ישראל", serviceTown.getText().toString(), serviceAddress.getText().toString()+" "+serviceHomeNumber.getText().toString());
                        }
                }
            }


            private void setStreetsAdapter() {
                 list= getStreetsCity(serviceTown.getText().toString());
                Log.d("Test city", "setStreetsAdapter: "+list);
                serviceAddress.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, list));
            }

            private ArrayList<String> getStreetsCity(String s) {
                ArrayList<String> list = new ArrayList<>();
                String firstC = s.substring(0, 1);
                if(firstC.isEmpty()) return list;
                try {
                    JsonObject jsonObject = new JsonParser().parse(new InputStreamReader(getContext().getAssets().open("cities/"+firstC))).getAsJsonObject();
                    if(jsonObject.has(s)){
                        JsonArray jsonArray = jsonObject.get(s).getAsJsonArray();
                        for (int i = 0; i < jsonArray.size(); i++) {
                            list.add(jsonArray.get(i).getAsString());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }
                return list;
            }

            private void validateText() {
                String town = serviceTown.getText().toString();
                if(town.isEmpty() || cities == null) return;
                if(validateTown())
                    return;

            }

            private boolean validateTown() {
                String town = serviceTown.getText().toString();
                if(cities.contains(town)) {
                    errorMsgTxt.setVisibility(View.GONE);
                    return true;
                }
                serviceTown.setText("");
                errorMsgTxt.setVisibility(View.GONE);
//        serviceAddress.clearFocus();
                serviceTown.post(new Runnable() {
                    @Override
                    public void run() {
                        if(getActivity()!=null && getActivity().getCurrentFocus() != null)
                            getActivity().getCurrentFocus().clearFocus();
//                        serviceTown.requestFocus();
                        serviceTown.getBackground().mutate().setColorFilter(getResources().getColor(R.color.red_create_account), PorterDuff.Mode.SRC_ATOP);
                    }
                });
                errorMsgTxt.setVisibility(View.VISIBLE);
                serviceTown.getBackground().mutate().setColorFilter(getResources().getColor(R.color.red_create_account), PorterDuff.Mode.SRC_ATOP);
                serviceTown.setActivated(true);
//                errorMsgTxt.setText(getString(R.string.));
//                Toast.makeText(getContext(), "No validation city name", Toast.LENGTH_SHORT).show();
                return false;
            }

    private boolean validateStreet() {
        String street = serviceAddress.getText().toString();
        if(list.contains(street)) {
            errorMsgTxt.setVisibility(View.GONE);
            return true;
        }
        serviceAddress.setText("");
        errorMsgTxt.setVisibility(View.GONE);
//        serviceAddress.clearFocus();
        serviceAddress.post(new Runnable() {
            @Override
            public void run() {
                if(getActivity()!=null && getActivity().getCurrentFocus() != null)
                    getActivity().getCurrentFocus().clearFocus();
//                        serviceTown.requestFocus();
                serviceAddress.getBackground().mutate().setColorFilter(getResources().getColor(R.color.red_create_account), PorterDuff.Mode.SRC_ATOP);
            }
        });
        errorMsgTxt1.setVisibility(View.VISIBLE);
//        serviceAddress.requestFocus();
        serviceAddress.getBackground().mutate().setColorFilter(getResources().getColor(R.color.red_create_account), PorterDuff.Mode.SRC_ATOP);
        serviceAddress.setActivated(true);
//                errorMsgTxt.setText(getString(R.string.));
//                Toast.makeText(getContext(), "No validation city name", Toast.LENGTH_SHORT).show();
        return false;
    }

            static double lat, lon;
            private void getLocation(String state, String city, String address){
                Geocoder geocoder = new Geocoder(getContext());
                try {
                    List<Address> list = geocoder.getFromLocationName(state+", "+city+" "+address, 1);
                    if(list != null && list.size() > 0){
                        Address add = list.get(0);
                        Log.d(TAG, "getLocation: lat: " + add.getLatitude());
                        Log.d(TAG, "getLocation: long: " + add.getLongitude());
                        lat = add.getLatitude();
                        lon = add.getLongitude();
                        Log.d(TAG, "getLocation: "+add.getLocality());
                        Log.d(TAG, "getLocation: "+add);
                        LocationUtils.setLocationReg(lat, lon);
                        if (!ServiceRegistrationFragmentContainer.userState.equals("isEdit")) {
                            ServiceRegistrationActivityForm.newService.l.add(lat);
                            ServiceRegistrationActivityForm.newService.l.add(lon);
                        }

                        if (ServiceRegistrationFragmentContainer.userState.equals("isEdit")) {
                        //update location in correct places.
                            if (ServiceRegistrationActivityForm.newService.l.size()>=1) {
                                ServiceRegistrationActivityForm.newService.l.set(0, lat);
                                ServiceRegistrationActivityForm.newService.l.set(1, lon);
                            }else{
                                ServiceRegistrationActivityForm.newService.l.add(lat);
                                ServiceRegistrationActivityForm.newService.l.add(lon);
                            }
                            ArrayList<Double> listAdd = new ArrayList();
                            listAdd.add(lat);
                            listAdd.add(lon);
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("l", listAdd);
                            FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PublicData")
                                    .child(ServiceRegistrationActivityForm.newService.getUserUid()).child("services").child(ServiceRegistrationActivityForm.newService.getKey()).updateChildren(map);
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }



}
