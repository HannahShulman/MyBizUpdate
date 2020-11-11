package com.app.mybiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mybiz.Activities.SingleServicesActivity;
import com.app.mybiz.Fragments.FavoriteServiceListFragment;
import com.app.mybiz.views.FooterBehavior;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by hannashulmah on 23/12/2016.
 */
public class ServicesFragments extends Fragment implements View.OnClickListener
//        implements View.OnClickListener {
{
    String TAG = "ServicesFragments";
    public static final int SERVICE_LIST = 1, BUSINESS_LIST = 2, FAVORITE_LIST = 3;
    public static int section = SERVICE_LIST;
    public static String myEmail, myId, myName;
    public static ListView single_services_list;
    public boolean isService = false;
    public ImageButton services, favourite, nearby, prof;
    public static RecyclerView recyclerView;
    static LinearLayout fragmentContainer, footerContainer;
    LinearLayout profLayout, serviceLayout, favoriteLayout;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    ImageView servcieBtn, favoriteBtn;
    ImageView profBtn;
    TextView profTxt, serviceTxt, favoriteTxt;
    public static ServiceListFragment serviceListFragment;
    public static ServiceListFragment profListFragment;
    public static FavoriteServiceListFragment myServices;

    public ServicesFragments() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.service_fragment_layout, container, false);
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentContainer = (LinearLayout) rootView.findViewById(R.id.fragment_container);
        footerContainer = (LinearLayout) rootView.findViewById(R.id.footer_container);
        profLayout = (LinearLayout) rootView.findViewById(R.id.prof_ll);
        serviceLayout = (LinearLayout) rootView.findViewById(R.id.service_ll);
        favoriteLayout = (LinearLayout) rootView.findViewById(R.id.favorite_ll);
        profBtn = (ImageView) rootView.findViewById(R.id.prof_btn);
        servcieBtn = (ImageView) rootView.findViewById(R.id.service_btn);
        favoriteBtn = (ImageView) rootView.findViewById(R.id.favorite_btn);
        profTxt = (TextView) rootView.findViewById(R.id.prof_txt);
        serviceTxt = (TextView) rootView.findViewById(R.id.service_txt);
        favoriteTxt = (TextView) rootView.findViewById(R.id.favorite_txt);
        profLayout.setOnClickListener(this);
        serviceLayout.setOnClickListener(this);
        favoriteLayout.setOnClickListener(this);

        profTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profLayout.performClick();
            }
        });
        serviceTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceLayout.performClick();
            }
        });
        favoriteTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoriteLayout.performClick();
            }
        });

        profBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profLayout.performClick();
            }
        });
        servcieBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceLayout.performClick();
            }
        });
        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoriteLayout.performClick();
            }
        });
        profLayout.performClick();
//        setChosen(footerContainer);
        showFragment(SERVICE_LIST);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CoordinatorLayout.LayoutParams lp = ((CoordinatorLayout.LayoutParams) ((View)getView().getParent()).getLayoutParams());
        FooterBehavior behavior = (FooterBehavior) lp.getBehavior();
        behavior.setLayout(getView(), getView().findViewById(R.id.footer_container));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.PREFERENCES, MODE_PRIVATE);
        myEmail = prefs.getString(Constants.EMAIL, Constants.RANDOM_STRING);
        myId = prefs.getString(Constants.APP_ID, Constants.RANDOM_STRING);
        myName = prefs.getString(Constants.NAME, Constants.RANDOM_STRING);
        serviceListFragment = new ServiceListFragment();
        Bundle businessBundle = new Bundle();
        businessBundle.putString("ref", "https://mybizz-3bbe5.firebaseio.com/Categories/Professional");
        serviceListFragment.setArguments(businessBundle);

        profListFragment = new ServiceListFragment();
        Bundle professionalBundle = new Bundle();
        professionalBundle.putString("ref", "https://mybizz-3bbe5.firebaseio.com/Categories/Business");
        profListFragment.setArguments(professionalBundle);

        myServices = new FavoriteServiceListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("myId", myId);
        myServices.setArguments(bundle);

    }


    @Override
    public void onClick(View v) {
        LinearLayout.LayoutParams  lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        lp.setMargins(0,6,0,0);

        LinearLayout.LayoutParams  lp1 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        lp1.setMargins(0,8,0,0);

        if (
               //v.getId()==R.id.prof_btn
//                ||
        v.getId()==R.id.prof_ll
//                || v.getId()== R.id.prof_txt
                )
         {
            profLayout.setSelected(true);
            serviceLayout.setSelected(false);
            favoriteLayout.setSelected(false);

            profLayout.setLayoutParams(lp);
            profLayout.setAlpha(1.0f);
            profTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

            serviceLayout.setLayoutParams(lp1);
            serviceLayout.setAlpha(.56f);
            serviceTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

            favoriteLayout.setLayoutParams(lp1);
            favoriteLayout.setAlpha(.56f);
            favoriteTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            showFragment(SERVICE_LIST);
        }

        if (
//                v.getId()==R.id.service_btn || v.getId()== R.id.service_txt
//                ||
                v.getId()==R.id.service_ll){
            profLayout.setSelected(false);
            serviceLayout.setSelected(true);
            favoriteLayout.setSelected(false);

            profLayout.setLayoutParams(lp1);
            profLayout.setAlpha(.56f);
            profTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

            serviceLayout.setLayoutParams(lp);
            serviceLayout.setAlpha(1.0f);
            serviceTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

            favoriteLayout.setLayoutParams(lp1);
            favoriteLayout.setAlpha(.56f);
            favoriteTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            showFragment(BUSINESS_LIST);
        }

        if (
//                v.getId()==R.id.favorite_btn || v.getId()== R.id.favourite_txt
//                ||
                v.getId()==R.id.favorite_ll
                ){
            profLayout.setSelected(false);
            serviceLayout.setSelected(false);
//            favoriteBtn.performClick();
            favoriteLayout.setSelected(true);

            profLayout.setLayoutParams(lp1);
            profLayout.setAlpha(.56f);
            profTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

            serviceLayout.setLayoutParams(lp1);
            serviceLayout.setAlpha(.56f);
            serviceTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

            favoriteLayout.setLayoutParams(lp);
            favoriteLayout.setAlpha(1.0f);
            favoriteTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

            showFragment(FAVORITE_LIST);
        }
    }

    public void showFragment(int fragment){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (fragment){
            case SERVICE_LIST:
                profBtn.setSelected(true);
                servcieBtn.setSelected(false);
                favoriteBtn.setSelected(false);
                footerContainer.setVisibility(View.VISIBLE);
                transaction.replace(R.id.fragment_container, serviceListFragment);
                if (profListFragment.isAdded())
                    transaction.remove(profListFragment);
                if (myServices.isAdded())
                    transaction.remove(profListFragment);
                transaction.commit();
                break;

            case BUSINESS_LIST:
                profBtn.setSelected(false);
                servcieBtn.setSelected(true);
                favoriteBtn.setSelected(false);
                footerContainer.setVisibility(View.VISIBLE);
                transaction.replace(R.id.fragment_container, profListFragment);
                transaction.remove(serviceListFragment);
                transaction.commit();
                break;

            case FAVORITE_LIST:
                profLayout.setSelected(false);
                serviceLayout.setSelected(false);
                favoriteLayout.setSelected(true);
                footerContainer.setVisibility(View.VISIBLE);
                transaction.replace(R.id.fragment_container, myServices);
                transaction.remove(serviceListFragment);
                if (profListFragment.isAdded())
                    transaction.remove(profListFragment);
                transaction.commit();
                break;

        }

    }

    public void showSingleServices(String category){
//        Log.d(TAG, "showSingleServices: "+category);
//        footerContainer.setVisibility(View.GONE);
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.addToBackStack(null);
//        SingleServiceListFragment singleServiceListFragment = new SingleServiceListFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString("category",category);
//        singleServiceListFragment.setArguments(bundle);
//        transaction.replace(R.id.fragment_container, singleServiceListFragment);
//        transaction.commit();
        Intent showServices = new Intent(getActivity(), SingleServicesActivity.class);
        showServices.putExtra("category", category);
        startActivity(showServices);
    }
}

