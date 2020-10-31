package com.mybiz.Fragments;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mybiz.Interface.RequiredFields;
import com.mybiz.LocationUtils;
import com.mybiz.Objects.Location;
import com.mybiz.R;
//import com.mybiz.ServiceRegistrationActivity;
import com.mybiz.TabsActivity;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by hannashulmah on 28/12/2016.
 */

public class PrivateInsertAddressFragment extends Fragment implements
         RequiredFields, View.OnClickListener, View.OnFocusChangeListener {
    EditText  serviceHomeNumber;
    AutoCompleteTextView serviceTown, serviceAddress;
    TextView logIn;
    ProgressBar pb;
    LinearLayout buttonWrapper;

    ArrayList<String> cities;
    String TAG = "ServiceRegistration";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.private_insert_address, container, false);

        buttonWrapper = (LinearLayout) rootView.findViewById(R.id.button_wrapper);
        logIn = (TextView) rootView.findViewById(R.id.complete_address);
        logIn.setOnClickListener(this);
        serviceAddress = (AutoCompleteTextView) rootView.findViewById(R.id.address_et);
        serviceHomeNumber = (EditText) rootView.findViewById(R.id.home_number);
        serviceHomeNumber.setOnFocusChangeListener(this);
        serviceTown = (AutoCompleteTextView) rootView.findViewById(R.id.town_ed);
        serviceTown.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, cities = getCities()));
        serviceTown.setOnFocusChangeListener(this);
        serviceAddress.setOnFocusChangeListener(this);
        pb = (ProgressBar) rootView.findViewById(R.id.progress_bar);
//        serviceAddress.setText(ServiceRegistrationActivity.newService.getAddress());
//        serviceTown.setText(ServiceRegistrationActivity.newService.getTown());
        serviceTown.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        serviceAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    updateRegistrationButton();
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
                updateRegistrationButton();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return rootView;
    }

    private void updateRegistrationButton(){
        if (serviceTown.getText().toString().length()>0
                && serviceAddress.getText().toString().length()>0
               // && serviceHomeNumber.getText().toString().length()>0
                ){
            logIn.setEnabled(true);
            buttonWrapper.setAlpha(1.0f);
        }else{
            logIn.setEnabled(false);
            buttonWrapper.setAlpha(.56f);
        }
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
    public void onStop() {
        super.onStop();
            Log.d("chaniii", "onStop- stop");
//            ServiceRegistrationActivity.newService.setAddress(serviceAddress.getText().toString());
//            ServiceRegistrationActivity.newService.setTown(serviceTown.getText().toString());
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
    public  boolean isComplete(){
        if (

                        serviceAddress.getText().toString().length()!=0&&
                        serviceTown.getText().toString().length()!=0
                )
            return true;
        return false;
    }

    @Override
    public boolean toSave() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.complete_address:
                pb.setVisibility(View.VISIBLE);
                if(validateTown()){
                    if(! serviceAddress.getText().toString().isEmpty() && list != null && list.contains(serviceAddress.getText().toString().trim())){
                        getLocation("ישראל", serviceTown.getText().toString(), serviceAddress.getText().toString()+" "+serviceHomeNumber.getText().toString());
                        Intent intent = new Intent(getContext(), TabsActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                        pb.setVisibility(View.GONE);
                    }
                }
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()){
            case R.id.town_ed:
                if(! hasFocus)
                    validateText();
                break;
            case R.id.address_et:
                if(hasFocus) {
                    if(validateTown()){
                        Log.d(TAG, "onFocusChange: setadapter");
                        setStreetsAdapter();
                    }
                }
                break;
            case R.id.home_number:
                if(! hasFocus){
                }
        }
    }

    ArrayList<String> list;
    private void setStreetsAdapter() {
        list = getStreetsCity(serviceTown.getText().toString());
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
        if(cities.contains(town)) return true;
        serviceTown.setText("");
//        serviceAddress.clearFocus();
        serviceTown.post(new Runnable() {
            @Override
            public void run() {
                if(getActivity().getCurrentFocus() != null)
                    getActivity().getCurrentFocus().clearFocus();
                serviceTown.requestFocus();
            }
        });
//        Toast.makeText(getContext(), "No validation city name", Toast.LENGTH_SHORT).show();
        return false;
    }

    static double lat, lon;
    private void getLocation(String state, String city, String address){
        Geocoder geocoder = new Geocoder(getContext(), new Locale("iw"));
        try {
            List<Address> list = geocoder.getFromLocationName(state+", "+city+" "+address, 1);
            Location location = new Location();
            if(list != null && list.size() > 0){
                Address add = list.get(0);
                Log.d(TAG, "getLocation: lat: " + add.getLatitude());
                Log.d(TAG, "getLocation: long: " + add.getLongitude());
                lat = add.getLatitude();
                lon = add.getLongitude();
                location.setLongitude(lon);
                location.setLatitude(lat);

                LocationUtils.setLocationReg(lat, lon);

                location.setCountry("ישראל");
                location.setTown(city);
                location.setStreetName(serviceAddress.getText().toString());
                try {
                    location.setBuildingNumber(Integer.parseInt(serviceHomeNumber.getText().toString()));
                }catch (Exception e){

                }

                if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    FirebaseDatabase.getInstance().getReference().getRoot().child("AllUsers").child("PublicData").child(uid).child("location").setValue(location);
                    FirebaseDatabase.getInstance().getReference().getRoot().child("AllUsers").child("PrivateData").child(uid).child("location").setValue(location);
                    Log.d(TAG, "getLocation: " + add.getLocality());
                    Log.d(TAG, "getLocation: " + add);
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


