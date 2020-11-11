package com.app.mybiz;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.app.mybiz.Activities.ShowSampleTender;
import com.app.mybiz.Objects.Tenders;
import com.app.mybiz.Objects.User;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CreateSampleTender extends AppCompatActivity implements View.OnClickListener {
//    Tenders sampleTenders;
    String TAG = "CreateTender";
    Spinner  subCategoryList;
    Button publishTender;
    EditText request;
    AutoCompleteTextView town;
    Spinner dateExpiry, categoryList;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Categories");
    ArrayAdapter adapter;
    ArrayList<String> subList;
    ArrayAdapter<String>categoryAdapter;
    Tenders t = new Tenders();
    Toolbar createTenderToolbar;
    TextView explanation, textCounter;
    static double lat, lon;
    int days = 0,  categoryChoice = 0;
    boolean isAnonymous;
    ProgressBar pb;

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_sample_tender);
        pb = (ProgressBar) findViewById(R.id.pb);
        isAnonymous = getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getBoolean(Constants.IS_ANONYMOUS, false);
        textCounter = (TextView) findViewById(R.id.text_counter);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        createTenderToolbar = (Toolbar) findViewById(R.id.create_tender_toolbar);
        createTenderToolbar.setNavigationIcon(R.drawable.right_arrow_w);
        createTenderToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        initViews();
        if (isAnonymous || getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.APP_ID, Constants.RANDOM_STRING).equals(Constants.RANDOM_STRING)){
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(CreateSampleTender.this);
            builder.setTitle(getString(R.string.tender_must_register))
                    .setPositiveButton(getString(R.string.register), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(CreateSampleTender.this, CreateAccountChoiceActivity.class);
                            startActivity(intent);

                        }
                    }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onBackPressed();
                    finish();
                }
            });
            builder.setCancelable(true);
            builder.create().show();

//            Toast.makeText(getBaseContext(), "you must sign up, in order to send messages", Toast.LENGTH_SHORT).show();
        }else{
            profileUrl();
            setListeners();
        }


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isAnonymous || getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.APP_ID, Constants.RANDOM_STRING).equals(Constants.RANDOM_STRING)) {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(CreateSampleTender.this);
            builder.setTitle("do you want to sign up?")
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(CreateSampleTender.this, CreateAccountChoiceActivity.class);
                            startActivity(intent);

                        }
                    }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onBackPressed();
                    finish();
                }
            });
            builder.setCancelable(true);
            builder.create().show();

//            Toast.makeText(getBaseContext(), "you must sign up, in order to send messages", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        explanation = (TextView) findViewById(R.id.explanation);
        explanation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreateSampleTender.this);
                alertDialogBuilder.setTitle("כאן יש כותרת");
                alertDialogBuilder
                        .setMessage("הסבר")
                        .setCancelable(false)
                        .setPositiveButton("אישור",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {

                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
        publishTender = (Button) findViewById(R.id.publish_tender);
        categoryList = (Spinner) findViewById(R.id.category_list);
        subCategoryList = (Spinner) findViewById(R.id.subcategory_list);
        dateExpiry = (Spinner) findViewById(R.id.days_to_end);
        town = (AutoCompleteTextView) findViewById(R.id.town);
        request = (EditText) findViewById(R.id.request);

        request.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()<=400) {
                    textCounter.setText(s.length() + "/" + 400);
                }
                else{
//                    Toast.makeText(getBaseContext(), "you have reached the maximum amount of characters", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1, getCities());
        town.setAdapter(adapter1);

        town.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updatePublishButton();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                updatePublishButton();
            }
        });

        town.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updatePublishButton();
            }
        });

        town.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                updatePublishButton();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                updatePublishButton();

            }

            @Override
            public void afterTextChanged(Editable s) {

                updatePublishButton();

            }
        });


        request.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                updatePublishButton();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePublishButton();
            }

            @Override
            public void afterTextChanged(Editable s) {
                updatePublishButton();
            }
        });


        String[] days = getResources().getStringArray(R.array.days);
        ArrayAdapter<String> daysAdapter = new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1,days);
        dateExpiry.setAdapter(daysAdapter);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1, UpdatesFromServer.categoryList);
        categoryList.setAdapter(categoryAdapter);

    }

    private void profileUrl() {
        FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/AllUsers/PublicData/"+getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.APP_ID, Constants.RANDOM_STRING))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        t.setProfileUrl(user.getProfileUrl());
                        t.setRequester(user.getmName());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    private void setListeners() {
        Log.d(TAG, "setListeners: ");
        publishTender.setOnClickListener(this);
        dateExpiry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                days = position;
                categoryChoice=position;
                updatePublishButton();
                t.setExpiryTime((position) * 86400000 + System.currentTimeMillis()
                );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        categoryList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected: categoryList");
                String category = (String) parent.getItemAtPosition(position);
                t.setCategory(category);
                t.setSubCategory(category);
                categoryChoice = position;
                updatePublishButton();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void updatePublishButton() {
        ArrayList<String> towns = getCities();
        boolean isCity = towns.contains(town.getText().toString());
        Log.d(TAG, "updatePublishButton: "+isCity+"__"+categoryChoice+"__"+request.getText()+"__"+days);
        if (categoryChoice!=0 && request.getText().toString().length()!=0
                && isCity
                &&days!=0){//if all complete
            publishTender.setAlpha(1.0f);
            publishTender.setEnabled(true);
        }else{
            publishTender.setAlpha(.54f);
            publishTender.setEnabled(false);
        }
    }


    private void uploadTenderToServer(){
        t.setRequest(request.getText().toString());
        t.setSubCategory(t.getCategory());
        t.setTown(town.getText().toString());
        t.setUid(getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.APP_ID, Constants.RANDOM_STRING));
        String tenderKey = FirebaseDatabase.getInstance().getReference().child("Tenders").child(t.getCategory()).child(t.getSubCategory()).push().getKey();
        t.setKey(tenderKey);
        FirebaseDatabase.getInstance().getReference().child("Tenders").child(t.getCategory()).child(t.getSubCategory()).child(tenderKey).setValue(t);
        FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PrivateData").child(t.getUid())
                .child("myTenders").child(tenderKey).setValue(t);
        FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PublicData").child(t.getUid())
                .child("myTenders").child(tenderKey).setValue(t);
//        Intent intent = new Intent(CreateTender.this, TabsActivity.class);
//        intent.putExtra("tabNumber", 0);
//        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.publish_tender:
                Log.d(TAG, "onClick: "+getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.APP_ID, Constants.RANDOM_STRING));
                pb.setVisibility(View.VISIBLE);
                t.setProfileUrl(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
                t.setRequester(getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE).getString(Constants.NAME, " "));
                t.setRequest(request.getText().toString());
                t.setSubCategory(t.getCategory());
                t.setTown(town.getText().toString());
                Intent toSample =new Intent(CreateSampleTender.this, ShowSampleTender.class);
                toSample.putExtra("sampleTender", t);
                startActivity(toSample);
                finish();
                break;
        }
    }

    private ArrayList<String> getCities() {
        ArrayList<String> list = new ArrayList<>();
        try {
            JsonArray jsonArray = new JsonParser().parse(new InputStreamReader(getApplicationContext().getAssets().open("cs.txt"))).getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                Log.d("testcs", "getCities: "+jsonArray.get(i));
                list.add(jsonArray.get(i).getAsString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private void getLocation(String state, String city){
        Geocoder geocoder = new Geocoder(getBaseContext());
        try {
            List<Address> list = geocoder.getFromLocationName(state+", "+city, 1);
            if(list != null && list.size() > 0){
                Address add = list.get(0);
                Log.d(TAG, "getLocation: lat: " + add.getLatitude());
                Log.d(TAG, "getLocation: long: " + add.getLongitude());
                lat = add.getLatitude();
                lon = add.getLongitude();
                Log.d(TAG, "getLocation: "+add.getLocality());
                Log.d(TAG, "getLocation: "+add);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
