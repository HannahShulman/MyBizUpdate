package com.mybiz.Managers;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.mybiz.MyApplication;
import com.mybiz.R;

/**
 * Created by hannashulmah on 19/03/2017.
 */

public class ShareAndCallManager extends AppCompatActivity {
    private static ShareAndCallManager instance;
    private  Context context;

    private ShareAndCallManager(Context context) {
        this.context = context;
    }

    public static synchronized ShareAndCallManager getInstance(Context context) {
        if (instance == null)
            instance = new ShareAndCallManager(context);
        return instance;
    }

    public static void shareService(Context ctx, String serviceKey){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "https://mybizz.application.to/allInfo_" + serviceKey);
        intent.putExtra(Intent.EXTRA_TEXT, ctx.getResources().getString(R.string.get_my_service)+"https://mybizz.application.to/allInfo_" + serviceKey+"  "
                +ctx.getResources().getString(R.string.download_app)+" https://play.google.com/store/apps/details?id=com.app.mybiz"

        );
        intent.putExtra(Intent.EXTRA_SUBJECT, "Check out this site!");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.getAppContext().startActivity(Intent.createChooser(intent, "Share"));
    }
}
