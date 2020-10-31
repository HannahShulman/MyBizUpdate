package com.mybiz.Fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mybiz.R;

/**
 * Created by itzikalgrisi on 25/02/2017.
 */

public class EnableLocationFragment extends DialogFragment implements View.OnClickListener {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.enable_location, container, false);
        v.findViewById(R.id.useLast).setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
