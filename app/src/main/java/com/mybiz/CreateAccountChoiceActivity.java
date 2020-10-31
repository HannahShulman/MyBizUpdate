package com.mybiz;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.mybiz.Fragments.CreateAccountFragmentFive;
import com.mybiz.Fragments.CreateAccountFragmentFour;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class CreateAccountChoiceActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    Button privateRegistrationButton, serviceRegistrationButton, skipBtn;
    ViewPagerAdapter viewPagerAdapter;
    ViewPager viewpager;
    TextView loginAccount, skip;
    ImageView position0, position1, position2, position3, position4, privateUserHelp, serviceUserHelp;
    ProgressBar pb;
    String TAG = "createAccountChoice";
    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_choice);
        initViews();
    }

    private void initViews() {
        pb = (ProgressBar) findViewById(R.id.pb);
        loginAccount = (TextView) findViewById(R.id.login_account);
        loginAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateAccountChoiceActivity.this, MidLoginActivity.class);
                startActivity(intent);
            }
        });
        privateUserHelp = (ImageView) findViewById(R.id.private_user_help);
        skip = (TextView) findViewById(R.id.skip);
        privateRegistrationButton = (Button) findViewById(R.id.private_reg_btn);
        serviceRegistrationButton = (Button) findViewById(R.id.service_reg_btn);
        serviceUserHelp = (ImageView) findViewById(R.id.service_user_help);
        position0 = (CircleImageView) findViewById(R.id.position_zero);
        position1 = (CircleImageView) findViewById(R.id.position_one);
        position2 = (CircleImageView) findViewById(R.id.position_two);
        position3 = (CircleImageView) findViewById(R.id.position_three);
        position4 = (CircleImageView) findViewById(R.id.position_four);
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new CreateAccountFragmentFive());
        viewPagerAdapter.addFragment(new CreateAccountFragmentFour());
        viewPagerAdapter.addFragment(new CreateAccountFragmentThree());
        viewPagerAdapter.addFragment(new CreateAccountFragmentTwo());
        viewPagerAdapter.addFragment(new CreateAccountFragmentOne());
        viewpager.setAdapter(viewPagerAdapter);
        viewpager.setCurrentItem(4);
        addListeners();
    }

    private void addListeners() {
        privateRegistrationButton.setOnClickListener(this);
        serviceRegistrationButton.setOnClickListener(this);
        privateUserHelp.setOnClickListener(this);
        serviceUserHelp.setOnClickListener(this);
        viewpager.setOnPageChangeListener(this);
        skip.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.service_user_help:
                showInfoDialog(getResources().getString(R.string.service_user_help), getResources().getString(R.string.dialog_ok));
                break;
            case R.id.private_user_help:
               showInfoDialog(getResources().getString(R.string.private_user_help), getResources().getString(R.string.dialog_ok));
                break;
            case R.id.login_account:
                Intent intent = new Intent(CreateAccountChoiceActivity.this, MidLoginActivity.class);
                startActivity(intent);
                break;
            case R.id.private_reg_btn:
                //KKK
                Intent i = new Intent(CreateAccountChoiceActivity.this, PersonalRegistrationActivity.class);
                startActivity(i);
                break;
            case R.id.service_reg_btn:
                Intent j = new Intent(CreateAccountChoiceActivity.this, ServiceRegistrationActivityForm.class);
                startActivity(j);
                break;

            case R.id.skip:
                    getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).edit()
                            .putString(Constants.NAME, getResources().getString(R.string.guest_account))
                            .putBoolean(Constants.IS_ANONYMOUS, true)
                            .commit();
                    startActivity(new Intent(getBaseContext(), TabsActivity.class));
                    pb.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    private void showInfoDialog(String message, String posBtn) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreateAccountChoiceActivity.this);
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(posBtn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void updateIndicator(int position) {
        switch (position) {
            case 0:
                position0.setImageResource(R.color.white);
                position0.setAlpha(1.0f);
                position1.setImageResource(R.color.tw__composer_black);
                position1.setAlpha(.54f);
                position2.setImageResource(R.color.tw__composer_black);
                position2.setAlpha(.54f);
                position3.setImageResource(R.color.tw__composer_black);
                position3.setAlpha(.54f);
                position4.setImageResource(R.color.tw__composer_black);
                position4.setAlpha(.54f);
                break;
            case 1:
                position0.setImageResource(R.color.tw__composer_black);
                position0.setAlpha(.54f);
                position1.setImageResource(R.color.white);
                position1.setAlpha(1.0f);
                position2.setImageResource(R.color.tw__composer_black);
                position2.setAlpha(.54f);
                position3.setImageResource(R.color.tw__composer_black);
                position3.setAlpha(.54f);
                position4.setImageResource(R.color.tw__composer_black);
                position4.setAlpha(.54f);
                break;
            case 2:
                position0.setImageResource(R.color.tw__composer_black);
                position0.setAlpha(.54f);
                position1.setImageResource(R.color.tw__composer_black);
                position1.setAlpha(.54f);
                position2.setImageResource(R.color.white);
                position2.setAlpha(1.0f);
                position3.setImageResource(R.color.tw__composer_black);
                position3.setAlpha(.54f);
                position4.setImageResource(R.color.tw__composer_black);
                position4.setAlpha(.54f);
                break;
            case 3:
                position0.setImageResource(R.color.tw__composer_black);
                position0.setAlpha(.54f);
                position1.setImageResource(R.color.tw__composer_black);
                position1.setAlpha(.54f);
                position2.setImageResource(R.color.tw__composer_black);
                position2.setAlpha(.54f);
                position3.setImageResource(R.color.white);
                position3.setAlpha(1.0f);
                position4.setImageResource(R.color.tw__composer_black);
                position4.setAlpha(.54f);
                break;

            case 4:
                position0.setImageResource(R.color.tw__composer_black);
                position0.setAlpha(.54f);
                position1.setImageResource(R.color.tw__composer_black);
                position1.setAlpha(.54f);
                position2.setImageResource(R.color.tw__composer_black);
                position2.setAlpha(.54f);
                position3.setImageResource(R.color.tw__composer_black);
                position3.setAlpha(.54f);
                position4.setImageResource(R.color.white);
                position4.setAlpha(1.0f);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        updateIndicator(position);

    }

    @Override
    public void onPageSelected(int position) {
        updateIndicator(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }
    }

}
