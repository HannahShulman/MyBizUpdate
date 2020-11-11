package com.app.mybiz.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.app.mybiz.Constants;
import com.app.mybiz.CreateAccountChoiceActivity;
import com.app.mybiz.LocationUtils;
import com.app.mybiz.Objects.Service;
import com.app.mybiz.R;
import com.app.mybiz.TabsActivity;

import java.util.HashMap;
import java.util.Map;

public class LogInAccount extends AppCompatActivity implements View.OnClickListener {
    String TAG = "loginaccount";
    EditText loginEmail, loginPassword;
    Button loginBtn;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logn_account);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(R.drawable.right_arrow_w);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.log_in_account);
//        getSupportActionBar().set
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        loginBtn = (Button) findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(this);
        loginEmail = (EditText) findViewById(R.id.login_email);
        loginPassword = (EditText) findViewById(R.id.login_password);
        loginEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()>0 && loginPassword.getText().toString().length()>8)
                    loginBtn.setAlpha(1.0f);
                else
                    loginBtn.setAlpha(.56f);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        loginPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length()>8 && loginEmail.getText().toString().contains("@"))
                        loginBtn.setAlpha(1.0f);
                    else
                        loginBtn.setAlpha(.56f);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onClick(View v) {

        String email = loginEmail.getText().toString();
        if (email!=null && loginPassword.getText().toString()!=null) {
            if (email.endsWith("@gmail.com")) ;
            email = email.replace("@gmail.com", "@mybizzgmail.com");
            signIn(email, loginPassword.getText().toString());
            ProgressDialog progress = new ProgressDialog(this);
            progress.setMessage("נרשם...:)");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.show();
        }else{
            return;
        }
    }

    private void signIn(final String email, String password){
//        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
               if (task.isSuccessful()){
                   Log.d(TAG, "signIn: "+task.getResult().getUser().getUid());
                   String uid = task.getResult().getUser().getUid();
                   Log.d(TAG, "onComplete: "+task.getResult().getUser().toString());
                   final DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PublicData").child(uid);
                   final DatabaseReference myServiceRef = mRootRef.child("services");
                   Query query = myServiceRef.orderByChild("email").equalTo(email);
                   query.addChildEventListener(new ChildEventListener() {
                       @Override
                       public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                           Service myService = dataSnapshot.getValue(Service.class);
                           Log.d(TAG, "onDataChange: "+myService.toJson());
                           getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).edit()
                                   .putString(Constants.APP_ID, myService.getUserUid())
                                   .putBoolean(Constants.IS_SERVICE, true)
                                   .putString(Constants.NAME, myService.getTitle())
                                   .putString(Constants.EMAIL, myService.getEmail())
                                   .putString(Constants.MY_CATEGORY, myService.getCategory())
                                   .putString(Constants.MY_SUB_CATEGORY, myService.getSubcategory())
                                   .putString(Constants.MY_SERVICE, myService.toJson())
                                   .commit();

                           if(myService.l != null && myService.l.size() > 1){
                               LocationUtils.setLocationReg(myService.l.get(0), myService.l.get(1));
                           }
                           //add device to user
                           Map<String,Object> taskMap = new HashMap<String,Object>();
                           taskMap.put(Settings.Secure.getString(getContentResolver(),
                                   Settings.Secure.ANDROID_ID), Settings.Secure.getString(getContentResolver(),
                                   Settings.Secure.ANDROID_ID));
                           mRootRef.child("devices").updateChildren(taskMap);


                           // move to mext activity
                           Intent intent = new Intent(LogInAccount.this, TabsActivity.class);
                           startActivity(intent);

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
               }else{
                   if (task.getException().getMessage().toString().contains("There is no user record corresponding to this identifier")){
                       AlertDialog.Builder builder = new AlertDialog.Builder(LogInAccount.this);
                       builder.setCancelable(true);
//                       builder.setMessage("no user found  "+task.getException().getMessage().toString())
                       builder.setMessage("המייל שהוזן, לא קיים במערכת, האם ברצונך להרשם?")
                               .setPositiveButton(getString(R.string.register), new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {

                                       Intent intent = new Intent(LogInAccount.this, CreateAccountChoiceActivity.class);
                                       startActivity(intent);

                                   }
                               }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {

                           }
                       });
                       builder.create().show();
                       builder.setCancelable(true);
                   }else{
                       AlertDialog.Builder builder = new AlertDialog.Builder(LogInAccount.this);
                       builder.setCancelable(true);
                       builder.setMessage(task.getException().getMessage().toString())
                               .setPositiveButton(getString(R.string.register), new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {

                                       Intent intent = new Intent(LogInAccount.this, CreateAccountChoiceActivity.class);
                                       startActivity(intent);

                                   }
                               }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {

                           }
                       });
                       builder.create().show();
                       builder.setCancelable(true);
                   }

               }
            }
        });
    }
}
