package com.app.mybiz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.app.mybiz.objects.Service;
import com.app.mybiz.activities.AllServiceInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NoLinkedActivity extends AppCompatActivity {
    String TAG = "noLinkedActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_linked);
        Uri uri = getIntent().getData();
        if (uri != null) {
            if (uri.getPath().contains("allInfo")) {
                String[] sb = uri.getPath().split("_");
                if (sb[1] != null) {
                    String c = sb[1];
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/Services/PrivateData").child(c);
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
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
