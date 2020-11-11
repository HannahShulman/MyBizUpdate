package com.app.mybiz;

import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.app.mybiz.Activities.AllServiceInfo;
import com.app.mybiz.Objects.Service;

public class NoLinkedActivity extends AppCompatActivity {
String TAG = "noLinkedActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_linked);
        Uri uri = getIntent().getData();
        Log.d(TAG, "onCreate: "+uri.toString());
        if (uri != null) {
            Log.d(TAG, "onCreate: "+uri.getPath().toString());
            if (uri.getPath().contains("allInfo")) {
                String[] sb = uri.getPath().split("_");
                Log.d(TAG, "onCreate: "+sb[0]+"___"+sb[1]);
                if (sb[1] != null) {
                    String c = sb[1];
                    Log.d(TAG, "onCreate: "+c);
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/Services/PrivateData").child(c);
                    Log.d(TAG, "onCreate: "+ref);
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d(TAG, "onDataChange: "+dataSnapshot.toString());
                            Service service = dataSnapshot.getValue(Service.class);
                            Intent allInfo = new Intent(NoLinkedActivity.this, AllServiceInfo.class);
                            allInfo.putExtra("currentService", service);
                            startActivity(allInfo);
                            finish();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

        }
    }
}
