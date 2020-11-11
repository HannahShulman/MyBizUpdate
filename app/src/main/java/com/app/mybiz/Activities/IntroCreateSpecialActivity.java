package com.app.mybiz.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.app.mybiz.CreateSpecial;
import com.app.mybiz.IntroTenderFragment;
import com.app.mybiz.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class IntroCreateSpecialActivity extends AppCompatActivity implements View.OnClickListener {
//    ImageView helpDemo, helpCreateDemo;
    Button newSpecialBtn;
    ViewPager viewpager;
    ViewPagerAdapter viewPagerAdapter;
    de.hdodenhof.circleimageview.CircleImageView position0, position1, position2;
    TextView fragmentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_intro_create_special);
        position0 = (CircleImageView) findViewById(R.id.position_zero);
        position1 = (CircleImageView) findViewById(R.id.position_one);
        position2 = (CircleImageView) findViewById(R.id.position_two);
        position2.setVisibility(View.GONE);
//        position1.setVisibility(View.GONE);

//        helpDemo = (ImageView) findViewById(R.id.help_demo);
//        helpCreateDemo = (ImageView) findViewById(R.id.help_create_demo);
        newSpecialBtn = (Button) findViewById(R.id.new_special_btn);
        viewpager = (ViewPager) findViewById(R.id.viewpager);
//        fragmentPosition = (ImageView) findViewById(R.id.fragment_position);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        IntroTenderFragment introFragment1 = new IntroTenderFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putInt("drawable", R.drawable.offers_icon3);
        bundle1.putString("text",  getString(R.string.public_offer_reason));
        introFragment1.setArguments(bundle1);

        IntroTenderFragment introFragment2 = new IntroTenderFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putInt("drawable", R.drawable.offers_icon_2);
        bundle2.putString("text", getResources().getString(R.string.clients_notification));
        introFragment2.setArguments(bundle2);

        IntroTenderFragment introFragment3 = new IntroTenderFragment();
        Bundle bundle3 = new Bundle();
        bundle3.putInt("drawable", R.drawable.white);
        bundle3.putString("text", getResources().getString(R.string.third_tender_fragment_slogen));
        introFragment3.setArguments(bundle3);
        viewPagerAdapter.addFragment(introFragment2);
        viewPagerAdapter.addFragment(introFragment1);
//        viewPagerAdapter.addFragment(introFragment3);
        viewpager.setAdapter(viewPagerAdapter);
        viewpager.setCurrentItem(1);
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                fragmentPosition.setText(position+"");
                updateIndicator(position);
            }

            @Override
            public void onPageSelected(int position) {

                updateIndicator(position);
//                fragmentPosition.setText(position+"");

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
//        helpDemo.setOnClickListener(this);
//        helpCreateDemo.setOnClickListener(this);
        newSpecialBtn.setOnClickListener(this);
    }

    private void updateIndicator(int position) {
        switch(position){
            case 0:

                position0.setImageResource(R.color.white);
                position0.setAlpha(1.0f);
                position1.setImageResource(R.color.tw__composer_black);
                position1.setAlpha(.54f);
                position2.setImageResource(R.color.tw__composer_black);
                position2.setAlpha(.54f);
                break;
            case 1:
                position0.setImageResource(R.color.tw__composer_black);
                position0.setAlpha(.54f);
                position1.setImageResource(R.color.white);
                position1.setAlpha(1.0f);
                position2.setImageResource(R.color.tw__composer_black);
                position2.setAlpha(.54f);
                break;
            case 2:
                position0.setImageResource(R.color.tw__composer_black);
                position0.setAlpha(.54f);
                position1.setImageResource(R.color.tw__composer_black);
                position1.setAlpha(.54f);
                position2.setImageResource(R.color.white);
                position2.setAlpha(1.0f);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.help_demo:
                showExplanation("help demo tender");
                break;

            case R.id.help_create_demo:
                showExplanation("help create tender");
                break;

            case R.id.new_special_btn:
                startActivity(new Intent(IntroCreateSpecialActivity.this, CreateSpecial.class));
                finish();
                break;
        }
    }






    private void showExplanation(String text){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(IntroCreateSpecialActivity.this);
        alertDialogBuilder.setTitle(getString(R.string.demo_tender));
        alertDialogBuilder
                .setMessage(text)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.dialog_ok),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {

                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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
