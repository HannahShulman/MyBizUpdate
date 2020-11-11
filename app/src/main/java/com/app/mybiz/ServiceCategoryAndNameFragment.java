package com.app.mybiz;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.app.mybiz.Interface.RequiredFields;

import java.util.ArrayList;

/**
 * Created by hannashulmah on 28/12/2016.
 */

public class ServiceCategoryAndNameFragment extends Fragment implements RequiredFields, TextWatcher {

    Spinner categoroy_spinner;
    EditText serviceTitle;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Categories");
    String link = "";
    String category = ServiceRegistrationActivityForm.newService.getCategory();
    ArrayAdapter<String> adapter;
    TextView titleCounter;
    ArrayList <String> list;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =inflater.inflate(R.layout.service_registration_fragment_three, container, false);
        list = new ArrayList();
        list = UpdatesFromServer.categoryList;
        ServiceRegistrationFragmentContainer.allowToSaveInfo(false);

        if (list!=null && !list.contains(getResources().getString(R.string.choose_category))){
            list.add(0, getResources().getString(R.string.choose_category));
        }

        categoroy_spinner = (Spinner) rootView.findViewById(R.id.categoroy_spinner);
        if (ServiceRegistrationActivityForm.newService.getCategory().equals(""))
            categoroy_spinner.setSelection(0);
        serviceTitle = (EditText) rootView.findViewById(R.id.service_title);
        serviceTitle.addTextChangedListener(this);
        titleCounter = (TextView) rootView.findViewById(R.id.title_counter);

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, UpdatesFromServer.categoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoroy_spinner.setAdapter(adapter);

        for (int i = 0; i < list.size(); i++) {
            if (ServiceRegistrationActivityForm.newService.getCategory().equals(list.get(i))){
                categoroy_spinner.setSelection(i);
            }
        }

        categoroy_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = list.get(position);
                categoroy_spinner.setSelection(position);

                    ServiceRegistrationFragmentContainer.allowToSaveInfo(toSave());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                categoroy_spinner.setSelection(0);
                ServiceRegistrationFragmentContainer.allowToSaveInfo(toSave());

            }
        });


        serviceTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() > 1)
                    serviceTitle.setAlpha(.87f);
                else
                    serviceTitle.setAlpha(.54f);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {



            if(s.length()<=17)

            {
                titleCounter.setText(s.length() + "/17");
                if (s.length() > 1)
                    serviceTitle.setAlpha(.87f);

            }
        }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()>1)
                    serviceTitle.setAlpha(.87f);
                else
                    serviceTitle.setAlpha(.54f);

            }
        });

        if (!ServiceRegistrationActivityForm.newService.getTitle().equals("") && !ServiceRegistrationActivityForm.newService.getTitle().equals(Constants.DEFAULT_SERVICE_TITLE) ){
            serviceTitle.setText(ServiceRegistrationActivityForm.newService.getTitle());
        }
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (ServiceRegistrationFragmentContainer.saveVsBack==ServiceRegistrationFragmentContainer.SAVE) {
            ServiceRegistrationActivityForm.newService.setCategory(category);
            ServiceRegistrationActivityForm.newService.setTitle(serviceTitle.getText().toString());
        }
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public boolean isComplete() {
        if (serviceTitle.getText().toString().length()!=0 && categoroy_spinner.getSelectedItemPosition()!=0)
            return true;
        return false;
    }

    @Override
    public boolean toSave() {
        //category
        //service title
        if (ServiceRegistrationActivityForm.newService.getTitle().equals(serviceTitle.getText().toString())
                && ServiceRegistrationActivityForm.newService.getCategory().equals(list.get(categoroy_spinner.getSelectedItemPosition()))){
         return false;
        }

        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length()>0)
            ServiceRegistrationFragmentContainer.allowToSaveInfo(toSave());
        if (s.length()==0){
            //title
            if (ServiceRegistrationActivityForm.newService.getTitle().length()==0 && serviceTitle.getText().toString().length()==0){
                if (ServiceRegistrationActivityForm.newService.getCategory().equals(list.get(categoroy_spinner.getSelectedItemPosition()))){
                    ServiceRegistrationFragmentContainer.allowToSaveInfo(false);
                }else{
                    ServiceRegistrationFragmentContainer.allowToSaveInfo(true);
                }
            }

        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
