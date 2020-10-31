package com.mybiz;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mybiz.Interface.RequiredFields;

/**
 * Created by hannashulmah on 28/12/2016.
 */


public class ServiceRegistrationEmailAndPassword extends Fragment implements RequiredFields, TextWatcher {
    EditText service_email_et, service_password_et;
    TextView emailWarningChars;
    public static LoginButton loginButton;
    private GoogleApiClient mGoogleApiClient;
    public static TextView googleVari;
    private static int RC_SIGN_IN = 0;
    private static int FB_SIGN_IN = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.service_registration_fragment_four, container, false);
        service_email_et = (EditText) rootView.findViewById(R.id.service_email_et);
        service_password_et = (EditText) rootView.findViewById(R.id.service_password_et);
        service_email_et.setText(ServiceRegistrationActivityForm.newService.getEmail());
        service_password_et.setText(ServiceRegistrationActivityForm.newService.getPassword());
        emailWarningChars = (TextView) rootView.findViewById(R.id.email_warning_chars);
        service_email_et.addTextChangedListener(this);
        service_email_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    emailWarningChars.setVisibility(View.GONE);
                }else{
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(service_email_et.getText().toString()).matches()) {
                        if (service_email_et.getText().toString().contains(" ")) {
                            emailWarningChars.setText("you have an extra space");
                        } else
                            emailWarningChars.setText("invalid email");
                        emailWarningChars.setVisibility(View.VISIBLE);
                    }
                    else
                        emailWarningChars.setVisibility(View.GONE);

                }
            }
        });
        service_password_et.addTextChangedListener(this);
        loginButton = (LoginButton) rootView.findViewById(R.id.fb_vari);
        googleVari = (TextView) rootView.findViewById(R.id.google_vari);
        googleVari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("960396926680-7g45c3kdn4n0lf6gaoagdpffkl7ubifs.apps.googleusercontent.com")
                .requestId()
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), null)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)

                .build();

        return  rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (ServiceRegistrationFragmentContainer.saveVsBack== ServiceRegistrationFragmentContainer.SAVE){
            ServiceRegistrationActivityForm.newService.setEmail(service_email_et.getText().toString());
            ServiceRegistrationActivityForm.newService.setPassword(service_password_et.getText().toString());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (ServiceRegistrationFragmentContainer.saveVsBack== ServiceRegistrationFragmentContainer.SAVE) {
            ServiceRegistrationActivityForm.newService.setEmail(service_email_et.getText().toString());
            ServiceRegistrationActivityForm.newService.setPassword(service_password_et.getText().toString());
        }
    }

    @Override
    public boolean isComplete() {
        if (service_email_et.getText().toString().length()>0
                //android.util.Patterns.EMAIL_ADDRESS.matcher(service_password_et.getText().toString()).matches()
                && service_password_et.getText().toString().length()!=0)
            return  true;
        return false;
    }

    @Override
    public boolean toSave() {
//            if (ServiceRegistrationActivityForm.newService.getEmail().equals(service_email_et.getText().toString())
//                    && ServiceRegistrationActivityForm.newService.getPassword().equals(service_password_et.getText().toString()))
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(service_email_et.getText().toString()).matches() || service_password_et.getText().toString().length()<9) {
//            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(service_email_et.getText().toString()).matches()){
//                emailWarningChars.setVisibility(View.VISIBLE);
//            }
//            if(service_password_et.getText().toString().length()<9){
//
//            }
            return false;
        }
        return true;
        }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length()>0)
            ServiceRegistrationFragmentContainer.allowToSaveInfo(toSave());
        if (s.length()==0){
            //email
            if (ServiceRegistrationActivityForm.newService.getEmail().length()==0 && service_email_et.getText().toString().length()==0){
                if (ServiceRegistrationActivityForm.newService.getPassword().equals(service_password_et.getText().toString())){
                    ServiceRegistrationFragmentContainer.allowToSaveInfo(false);
                }else{
                    ServiceRegistrationFragmentContainer.allowToSaveInfo(true);
                }
            }
            //password
            if (ServiceRegistrationActivityForm.newService.getPassword().length()==0 && service_password_et.getText().toString().length()==0){
                if (ServiceRegistrationActivityForm.newService.getEmail().equals(service_email_et.getText().toString())){
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



