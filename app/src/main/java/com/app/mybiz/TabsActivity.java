package com.app.mybiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.app.mybiz.activities.AboutMybizzActivity;
import com.app.mybiz.activities.AllServiceInfo;
import com.app.mybiz.activities.IntroCreateSpecialActivity;
import com.app.mybiz.activities.IntroTendersActivity;
import com.app.mybiz.objects.Service;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;


public class TabsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, ViewPager.OnPageChangeListener {

    String TAG = "TabsActivity";
    public static ViewPagerAdapter adapter;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    String profileUrl = "", uid;
    FloatingActionButton fab;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    SharedPreferences prefs;
    View navigationHeader;
    boolean isAnonymous;
    ValueEventListener profileUpdate;
    DatabaseReference ref1;
    AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);
        initInfo();
        initViews();
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (ServicesFragments.footerContainer.getVisibility() == View.GONE)
            ServicesFragments.footerContainer.setVisibility(View.VISIBLE);
        if (isAnonymous && uid.equals(PreferenceKeys.RANDOM_STRING)){
            Log.d(TAG, "onBackPressed: unknown user");
            super.onBackPressed();

        }
        else {
            moveTaskToBack(true);
            Log.d(TAG, "onBackPressed: registered user");
        }
    }


    private void updateNavigation() {
        TextView userNameView = (TextView) navigationHeader.findViewById(R.id.user_name);
        TextView userEmailView = (TextView) navigationHeader.findViewById(R.id.user_email);
        TextView accountTypeView = (TextView) navigationHeader.findViewById(R.id.account_type);
        final CircleImageView profileImage = (CircleImageView) navigationHeader.findViewById(R.id.profile_image);
        boolean isService = getSharedPreferences(PreferenceKeys.PREFERENCES, MODE_PRIVATE).getBoolean(PreferenceKeys.IS_SERVICE, false);
        if (!isService) {//private user
            if (isAnonymous || uid.equals(PreferenceKeys.RANDOM_STRING)){
                Log.d(TAG, "onCreate: i am a guest");
                navigationView.inflateMenu(R.menu.guest_user_navigation_drawer);
                accountTypeView.setText(getString(R.string.guest_account));
                userEmailView.setVisibility(View.GONE);
                profileImage.setImageResource(R.drawable.guest_profile);
                Glide.with(getBaseContext()).load(PreferenceKeys.PERSONAL_DEFAULT_PROFILE_URL).into(profileImage);
            }else {//registered private user
                navigationView.inflateMenu(R.menu.private_user_navigation_drawer);
                accountTypeView.setText(getString(R.string.personal_account));
            }
        } else {//service
            navigationView.inflateMenu(R.menu.service_navigation_menu);
            accountTypeView.setText(getString(R.string.service_account));
        }
        String email = getSharedPreferences(PreferenceKeys.PREFERENCES, MODE_PRIVATE).getString(PreferenceKeys.EMAIL, PreferenceKeys.RANDOM_STRING);
        if(email.endsWith("@mybizzgmail.com")) {
            email = email.replace("@mybizzgmail.com","@gmail.com");
        }
        userEmailView.setText(email);
        userNameView.setText(getSharedPreferences(PreferenceKeys.PREFERENCES, MODE_PRIVATE).getString(PreferenceKeys.NAME, PreferenceKeys.RANDOM_STRING));
        if (!isAnonymous || !uid.equals(PreferenceKeys.RANDOM_STRING)) {
            ref1 = FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/AllUsers/PublicData/" + uid + "/profileUrl");
            ref1.addValueEventListener(profileUpdate = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    profileUrl = dataSnapshot.getValue(String.class);
                    Glide.with(getBaseContext()).load(profileUrl).into(profileImage);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        navigationView.setNavigationItemSelectedListener(this);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewDialog.showDialog(TabsActivity.this, profileUrl);
            }
        });
    }

    private void initViews() {
        fab = (FloatingActionButton) findViewById(R.id.add_tender_fab);
        fab.setOnClickListener(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitleTextAppearance(this, R.style.MyTitleTextApperance);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.right_arrow_w);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationHeader = navigationView.getHeaderView(0);
        updateNavigation();


//        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            StateListAnimator stateListAnimator = new StateListAnimator();
//            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(appBarLayout, "elevation", 4));
//            appBarLayout.setStateListAnimator(stateListAnimator);
//            appBarLayout.setElevation(4);
//        }
//
//


        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        viewPager.setCurrentItem(getIntent().getIntExtra("tabNumber", 1));

        tabLayout = (TabLayout) findViewById(R.id.tabs);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            StateListAnimator stateListAnimator = new StateListAnimator();
//            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(tabLayout, "elevation", 4));
//            tabLayout.setStateListAnimator(stateListAnimator);
//            tabLayout.setElevation(4);
//        }
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFF"));
        tabLayout.setSelectedTabIndicatorHeight((int) (2 * getResources().getDisplayMetrics().density));
        tabLayout.setTabTextColors(Color.parseColor("#54cdcccc"), Color.parseColor("#ffffff"));
    }

    private void initInfo() {
        prefs = getSharedPreferences(PreferenceKeys.PREFERENCES, MODE_PRIVATE);
        uid = prefs.getString(PreferenceKeys.APP_ID, PreferenceKeys.RANDOM_STRING);
        isAnonymous = prefs.getBoolean(PreferenceKeys.IS_ANONYMOUS, false);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TendersFragment(), getResources().getString(R.string.tenders));
        adapter.addFragment(new ServicesFragments(), getResources().getString(R.string.service_tab));
        adapter.addFragment(new RecentChatStreams(), getResources().getString(R.string.chat));
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        drawerLayout.closeDrawers();
        //if private user
        switch (item.getItemId()) {
            case R.id.invite_friend:
                //send link  of app to friend
                final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.app.mybiz");
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out this site!");
                startActivity(Intent.createChooser(intent, "Share"));
                break;

            case R.id.login_account:
                startActivity(new Intent(TabsActivity.this, CreateAccountChoiceActivity.class));
                finish();
                break;

            case R.id.favorite_service:
                viewPager.setCurrentItem(1);
                ((ServicesFragments) getSupportFragmentManager().findFragmentByTag(adapter.getItem(1).getTag())).favoriteLayout.performClick();
//
                break;

            case R.id.business_account:
                startActivity(new Intent(TabsActivity.this, ServiceRegistrationActivityForm.class));
                break;



            case R.id.add_offer:
//                startActivity(new Intent(TabsActivity.this, CreateSpecial.class));
                startActivity(new Intent(TabsActivity.this, IntroCreateSpecialActivity.class));

                break;

            case R.id.drawer_service_profile:
                final boolean amService = getSharedPreferences(PreferenceKeys.PREFERENCES, MODE_PRIVATE).getBoolean(PreferenceKeys.IS_SERVICE, false);
                if (amService) {
                    String s = getSharedPreferences(PreferenceKeys.PREFERENCES, MODE_PRIVATE).getString(PreferenceKeys.MY_SERVICE, PreferenceKeys.RANDOM_STRING);
                    Log.d(TAG, "onNavigationItemSelected: "+s);
                    if (!s.equals(PreferenceKeys.RANDOM_STRING)) {
                        Gson gson = new Gson();
                        try {
                            JSONObject obj = new JSONObject(s);
                            Service myService = gson.fromJson(obj.toString(), Service.class);
                            Log.d(TAG, "onNavigationItemSelected: "+myService.toString());
                            Log.d(TAG, "onNavigationItemSelected: "+myService.getTitle());
                            Intent toAllInfo = new Intent(TabsActivity.this, AllServiceInfo.class);
                                toAllInfo.putExtra("currentService", myService);
                                startActivity(toAllInfo);
                        } catch (IllegalStateException | JsonSyntaxException exception) {
                            Log.d(TAG, "onNavigationItemSelected1: "+exception.getMessage());
//                            {"additionalInfo":"", "title":"bgxgx", "userUid":"5PEUCz70ryU5bV887dFxhcqFOPC2", "address": "רמב"ם", "town":"פתח תקווה", "serviceHomeNumber":"147852", "phoneNumber":"0587015118", "openingHours":"גסכהענינעהכב", "email": "ddd@ddd.ddd", "password":"aaabbbccc", "category":"מוצרי חשמל", "subcategory":"מוצרי חשמל", "shortDescription":"וכבני,יכ", "isApproved":"false", "noReviewers":"0", "averageRating":"0.0", "avarageQualityService":"0.0", "avarageReliability":"0.0", "avaragePunctuality":"0.0", "devices":{}, "profileUrl":"https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/images%2F1490364297780.png?alt=media&token=a42ae9aa-92d1-459e-8ddd-e4cbee801280", "key":"-Kg-cbGl8On3UjiI8hzt", "service":"false","l":[]}
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "onNavigationItemSelected2: "+e.getMessage());
                        }
                    }else {
//                        Toast.makeText(getBaseContext(), "i am not service  "+s, Toast.LENGTH_SHORT).show();
                    }

                }else{
//                    Toast.makeText(getBaseContext(), "i am not service", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.about_mybizz:
                //new Activity about mybizz
                startActivity(new Intent(TabsActivity.this, AboutMybizzActivity.class));
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                break;

            case R.id.share_w_friend:
                String s = getSharedPreferences(PreferenceKeys.PREFERENCES, MODE_PRIVATE).getString(PreferenceKeys.MY_SERVICE, PreferenceKeys.RANDOM_STRING);
                if (!s.equals(PreferenceKeys.RANDOM_STRING)) {
                    Gson gson = new Gson();

                    try {
                        JSONObject obj = new JSONObject(s);
                        Service myService = gson.fromJson(obj.toString(), Service.class);
//                        MyApplication.shareService(myService.getKey());
                        Intent intent1 = new Intent(Intent.ACTION_SEND);
                        intent1.setType("text/plain");
                        intent1.putExtra(
                                Intent.EXTRA_TEXT,
                                getResources().getString(R.string.download_app)+
                                        "https://play.google.com/store/apps/details?id=com.app.mybiz"
                                +getResources().getString(R.string.get_my_service)+"https://mybizz.application.to/allInfo_" + myService.getKey()+"  "
                        );
                        intent1.putExtra(Intent.EXTRA_SUBJECT, "Check out this site!");
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(Intent.createChooser(intent1, "Share"));

                    } catch (IllegalStateException | JsonSyntaxException exception) {
                        Log.d(TAG, "onNavigationItemSelected2: "+exception);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG, "onNavigationItemSelected3: "+e);
                    }
                }else {
//                        Toast.makeText(getBaseContext(), "i am not service  "+s, Toast.LENGTH_SHORT).show();
                }






                break;
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_tender_fab:
                Log.d(TAG, "onClick: tender try out  ");
                Intent intent = new Intent(TabsActivity.this, IntroTendersActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ref1!=null)
            ref1.removeEventListener(profileUpdate);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        Log.d("TAG", "onPageScrollStateChanged: "+state);
        if (state!=0){
            fab.setVisibility(View.GONE);
        }else{
            if (viewPager.getCurrentItem()==0){
                fab.setVisibility(View.VISIBLE);
            }
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

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

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}