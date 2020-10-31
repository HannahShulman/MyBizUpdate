package com.mybiz.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.mybiz.ChatActivity;
import com.mybiz.Objects.Tenders;
import com.mybiz.R;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by hannashulmah on 11/02/2017.
 */

public class TenderRecyclerAdapter extends RecyclerView.Adapter<TenderRecyclerAdapter.ViewHolder> {
    String TAG = "lll adapter";
    Context ctx;
    ArrayList<Tenders> list;



    public TenderRecyclerAdapter() {
    }

    public TenderRecyclerAdapter(Context ctx, ArrayList<Tenders> list) {
        this.ctx = ctx;
        this.list = list;

    };

    @Override
    public TenderRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TenderRecyclerAdapter.ViewHolder vh = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType){
            case 0:
                View v1 = inflater.inflate(R.layout.user_tender_layout, parent, false);
                vh = new ViewHolder(v1);
                break;
            case 1:
                View v2 = inflater.inflate(R.layout.service_tender_layout, parent, false);
                vh = new ViewHolder(v2);
                break;
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(TenderRecyclerAdapter.ViewHolder holder, final int position) {
        Tenders t = list.get(position);
        long timeLeft = t.getExpiryTime()-System.currentTimeMillis();
        int days = (int) (timeLeft / (1000*60*60*24));
        int hours = (int) TimeUnit.MILLISECONDS.toHours(timeLeft-(days*(1000*60*60*24)));
        if (days>=1)
            holder.expiryDate.setText(days+" "+ctx.getResources().getString(R.string.days)+"  " +hours+" "+ctx.getResources().getString(R.string.hours));
        else
            holder.expiryDate.setText(hours+"  "+ctx.getResources().getString(R.string.hours));
//        holder.expiryDate.setText(android.text.format.DateFormat.format("dd/MM/yyyy hh:mm", t.getExpiryTime() ));
        Glide.with(ctx).load(t.getProfileUrl()).into(holder.profileImage);
        holder.name.setText(t.getRequester());
        holder.town.setText(t.getTown());
        holder.request.setText(t.getRequest());

        if (getItemViewType(position)==0){//for personal user
            holder.cancelLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //set dialog and remove from database
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
                    // set dialog message
                    alertDialogBuilder
                            .setMessage(ctx.getResources().getString(R.string.confirm_delete))
                            .setCancelable(false)
                            .setPositiveButton(ctx.getResources().getString(R.string.ishur),new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    // if this button is clicked, close
                                    //remove from data base
                                    Tenders t = list.get(position);
                                    FirebaseDatabase.getInstance().getReference().child("Tenders")
                                            .child(t.getCategory()).child(t.getSubCategory()).child(t.getKey()).removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PrivateData").child(t.getUid()).child("myTenders").child(t.getKey()).removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PublicData").child(t.getUid()).child("myTenders").child(t.getKey()).removeValue();
                                    list.remove(position);
                                    notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton(ctx.getString(R.string.no),new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    // if this button is clicked, just close
                                    // the dialog box and do nothing
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                }
            });

        }else   if (getItemViewType(position)==1){//for service

            holder.cancelLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ctx, ChatActivity.class);
                    intent.putExtra("otherID", list.get(position).getUid());
                    intent.putExtra("otherName", list.get(position).getRequester());
                    Log.d(TAG, "onClick: otherid"+list.get(position).getUid()+" otherName"+list.get(position).getRequester());
                    ctx.startActivity(intent);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (list.get(position).getUid() .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) ? 0 : 1;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage,shareIcon, cancelIcon;
        TextView name, town, expiryDate, request, shareTv, cancel;
        LinearLayout cancelLayout;

        public ViewHolder(View convertView) {
            super(convertView);

            profileImage = (ImageView) convertView.findViewById(R.id.profile);
            name = (TextView) convertView.findViewById(R.id.name);
            town = (TextView) convertView.findViewById(R.id.town);
            request = (TextView) convertView.findViewById(R.id.request_description);
            expiryDate = (TextView) convertView.findViewById(R.id.expiry_time_left);
            cancel = (TextView) convertView.findViewById(R.id.cancel_suggest_tv);
            cancelIcon = (ImageView) convertView.findViewById(R.id.cancel_suggest_icon);
            cancelLayout = (LinearLayout) convertView.findViewById(R.id.cancel_layout);
        }
    }
}
