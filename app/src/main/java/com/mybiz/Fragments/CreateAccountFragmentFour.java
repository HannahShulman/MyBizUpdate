package com.mybiz.Fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mybiz.R;

/**
 * Created by hannashulmah on 06/03/2017.
 */

public class CreateAccountFragmentFour extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.create_account_fragment_four, container, false);
        return rootView;
    }
}