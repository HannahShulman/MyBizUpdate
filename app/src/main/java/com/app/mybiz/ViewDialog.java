package com.app.mybiz;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ViewDialog {

    com.app.mybiz.Objects.TouchImageView a;

    //dialog for info
    public ViewDialog showDialog( Context ctx, String uri) {
        final Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog);
        a = (com.app.mybiz.Objects.TouchImageView) dialog.findViewById(R.id.a);
        a.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Glide.with(ctx).load(uri).into(a);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(true);
        dialog.show();
        return null;
    }


}