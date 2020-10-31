package com.mybiz;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mybiz.Fragments.CreateOfferOneFragment;
import com.mybiz.Fragments.CreateOfferSecondFragment;
import com.mybiz.Objects.Specials;

public class CreateSpecial extends AppCompatActivity implements View.OnClickListener {
    String TAG = "createSpecial";
    public static Specials offers = new Specials();
    Toolbar toolbar;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    LinearLayout container;
    static CreateOfferOneFragment firstFragment;
    static CreateOfferSecondFragment secondFragment;
    Button nextFragment;
    int fragmentNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_special);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("פרסם מבצע");
        toolbar.setNavigationIcon(R.drawable.right_arrow_w);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        container = (LinearLayout) findViewById(R.id.container);
        nextFragment = (Button) findViewById(R.id.next_fragment);
        nextFragment.setOnClickListener(this);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        firstFragment = new CreateOfferOneFragment();
        secondFragment = new CreateOfferSecondFragment();
        fragmentTransaction.add(R.id.container, firstFragment);
        fragmentNumber = 1;
        fragmentTransaction.commit();


    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: "+fragmentNumber+"__");
        if (fragmentNumber==1) {
            if (offers.getImageUrl()!=null) {
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, secondFragment);
                fragmentNumber = 2;
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }else{

            }
        }else{
            ((CreateOfferSecondFragment)getSupportFragmentManager().findFragmentById(secondFragment.getId())).publicizeOffer();
        }

    }

    @Override
    public void onBackPressed() {
        if (fragmentNumber==2){
            fragmentNumber=1;
            super.onBackPressed();
        }else{
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreateSpecial.this);
            alertDialogBuilder
                    .setTitle("Are you sure you want to leave?")
                    .setMessage("Your offer info, will not be save.")
                    .setCancelable(false)
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                                finish();
                        }
                    })
            .setNegativeButton("no", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (secondFragment.isAdded())
            secondFragment.onActivityResult(requestCode, resultCode, data);

    }
}
