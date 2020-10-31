package com.mybiz.views;

import android.content.Context;
import com.google.android.material.appbar.AppBarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.mybiz.R;

/**
 * Created by itzikalgrisi on 10/02/2017.
 */

public class FooterBehavior extends AppBarLayout.ScrollingViewBehavior {
    private static final String TAG = "MyBehavior";
    private View layout;
    private View inner;

    public FooterBehavior() {
    }

    public FooterBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        boolean result = super.onDependentViewChanged(parent, child, dependency);

        if (layout != null) {

            WindowManager wm = (WindowManager) layout.getContext().getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();

            DisplayMetrics dip = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dip);
            int height = dip.heightPixels;  // deprecated

            Log.d(TAG, "onDependentViewChanged: height: "+height);
            int[] a = new int[2];
            layout.getLocationOnScreen(a);
            Log.d(TAG, "onDependentViewChanged: window " + a[1]);
            Log.d(TAG, "onDependentViewChanged: l height " + layout.getHeight());
            Log.d(TAG, "onDependentViewChanged: y: "+inner.getY());
            if(a[1]+layout.getHeight() != height){
                int sp = (a[1]+layout.getHeight())- height;
                inner.setY(layout.getHeight() - layout.getContext().getResources().getDimensionPixelOffset(R.dimen.footer_height) - sp);
            }else
                inner.setY(layout.getHeight()-layout.getContext().getResources().getDimensionPixelOffset(R.dimen.footer_height));
//            layout.setTranslationY(-900);
        }
        return result;
    }

    public void setLayout(View layout, View footer) {
        this.layout = layout;
        this.inner = footer;
    }

}
