package com.app.mybiz.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.app.mybiz.FormItem;
import com.app.mybiz.R;
import com.app.mybiz.ServiceRegistrationFragmentContainer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by hannashulmah on 27/02/2017.
 */
public class ListViewFormAdapter extends BaseAdapter {
    String TAG = "listViewAdapter";

    ArrayList<FormItem> list;
    Context ctx;

    public ListViewFormAdapter(Context ctx, ArrayList<FormItem> list) {
        this.ctx = ctx;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }




    ViewHolder holder = null;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        holder = new ViewHolder();
        if (view==null){
            view = LayoutInflater.from(ctx).inflate(R.layout.first_form_item_layout, parent, false);
            holder.firstLayout = (FrameLayout) view.findViewById(R.id.first_layout);
            holder.profileImageService = (ImageView) view.findViewById(R.id.profile_image_service);
            holder.cb = (CheckBox) view.findViewById(R.id.is_complete_check);
            holder.cb.setChecked(list.get(position).isComplete());
            holder.cb1 = (CheckBox) view.findViewById(R.id.cb1);
            holder.titleText = (TextView) view.findViewById(R.id.title_text);
            holder.subtitleText = (TextView) view.findViewById(R.id.subtitle_text);
            holder.secondLayout = (RelativeLayout) view.findViewById(R.id.second_layout);
            holder.addImage = (TextView) view.findViewById(R.id.add_image);
            view.setTag(holder);

        }else{
            holder = (ViewHolder) view.getTag();
        }

        if (position!=0){
            holder.secondLayout.setVisibility(View.VISIBLE);
            holder.firstLayout.setVisibility(View.GONE);
            holder.cb.setChecked(list.get(position).isComplete());
            holder.titleText.setText(list.get(position).getText1());
            holder.subtitleText.setText(list.get(position).getSubText());


        }else{
            holder.firstLayout.setVisibility(View.VISIBLE);
            holder.cb1.setChecked(list.get(0).isComplete());
            holder.secondLayout.setVisibility(View.GONE);
            loadImageFromStorage(holder.profileImageService);
            holder.profileImageService.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ctx, ServiceRegistrationFragmentContainer.class);
                    intent.putExtra("fragmentNumber", 0);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(intent);
                }
            });


        }
        return view;
    }


    class ViewHolder{
        ImageView profileImageService;
        FrameLayout firstLayout;
        CheckBox cb, cb1;
        TextView titleText, subtitleText, addImage;
        RelativeLayout secondLayout;

    }

    private void loadImageFromStorage(ImageView imgView)
    {
        try {
            File f=new File(ctx.getFilesDir()+"/profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            imgView.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }
}
