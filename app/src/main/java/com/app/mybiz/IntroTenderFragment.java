package com.app.mybiz;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class IntroTenderFragment extends Fragment {

    ImageView backBtn;
    Drawable drawable;
    String title = "";
    String TAG = "tag";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        title = getArguments().getString("text");
        Log.d(TAG, "onCreateView: ");

        View rootView = inflater.inflate(R.layout.fragment_intro_tender, container, false);
        backBtn = (ImageView) rootView.findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        ImageView image = (ImageView) rootView.findViewById(R.id.fragment_image);
        TextView text = (TextView) rootView.findViewById(R.id.fragment_text);
        image.setImageDrawable(getResources().getDrawable(getArguments().getInt("drawable")));
        text.setText(title);

        return rootView;
    }



    @Override
    public void onDetach() {
        super.onDetach();
    }

}
