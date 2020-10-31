package com.mybiz.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mybiz.R;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by itzikalgrisi on 01/03/2017.
 */

public class MyBizzRatingBar extends LinearLayout implements View.OnClickListener {


    public interface OnRatingBarChangeListener{
        void onRatingChanged(int rating, boolean fromUser);
    }

    private OnRatingBarChangeListener onRatingBarChangeListener;

    public void setOnRatingBarChangeListener(OnRatingBarChangeListener onRatingBarChangeListener) {
        this.onRatingBarChangeListener = onRatingBarChangeListener;
    }

    private int rating;

    public MyBizzRatingBar(Context context) {
        super(context);
        init();
    }

    public MyBizzRatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyBizzRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MyBizzRatingBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private ArrayList<ImageView>  stars = new ArrayList<>();
    private void init() {
        setOrientation(HORIZONTAL);

        inflate(getContext(), R.layout.rating_bar_view, this);
        stars.add((ImageView) findViewById(R.id.start1));
        stars.add((ImageView) findViewById(R.id.start2));
        stars.add((ImageView) findViewById(R.id.start3));
        stars.add((ImageView) findViewById(R.id.start4));
        stars.add((ImageView) findViewById(R.id.start5));

        for (ImageView star : stars) {
            star.setOnClickListener(this);
        }

//        Collections.reverse(stars);
    }

    @Override
    public void onClick(View v) {
        rating = stars.indexOf(v);

        updateRating();
        if(onRatingBarChangeListener != null)
            onRatingBarChangeListener.onRatingChanged(rating+1, true);
    }

    public void setRating(int rating) {
        this.rating = rating;
        updateRating();
        if(onRatingBarChangeListener != null)
            onRatingBarChangeListener.onRatingChanged(rating, false);

    }

    private void updateRating() {
        for (int i = 0; i < stars.size(); i++) {
            stars.get(i).setSelected(i <= rating);
        }
    }

    public int getRating() {
        return rating;
    }
}
