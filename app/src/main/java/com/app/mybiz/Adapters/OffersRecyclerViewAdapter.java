package com.app.mybiz.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
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
import com.app.mybiz.Activities.AllServiceInfo;
import com.app.mybiz.ChatActivity;
import com.app.mybiz.Objects.Service;
import com.app.mybiz.Objects.Specials;
import com.app.mybiz.R;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by hannashulmah on 24/02/2017.
 */

public class OffersRecyclerViewAdapter extends RecyclerView.Adapter<OffersRecyclerViewAdapter.ViewHolder> {
    String TAG = "lll adapter";
    Context ctx;
    ArrayList<Specials> list;



    public OffersRecyclerViewAdapter() {
    }

    public OffersRecyclerViewAdapter(Context ctx, ArrayList<Specials> list) {
        this.ctx = ctx;
        this.list = list;

    };

    @Override
    public OffersRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        OffersRecyclerViewAdapter.ViewHolder vh = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType){
            case 0:
                View v1 = inflater.inflate(R.layout.single_service_offer_layout, parent, false);
                vh = new ViewHolder(v1);
                break;
            case 1:
                View v2 = inflater.inflate(R.layout.single_service_offer_layout, parent, false);
                vh = new ViewHolder(v2);
                break;
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(OffersRecyclerViewAdapter.ViewHolder holder, final int position) {
        final Specials t = list.get(position);
        final Service service = t.getOfferCreator();
        long timeLeft = t.getExpiryDate()-System.currentTimeMillis();
        int days = (int) (timeLeft / (1000*60*60*24));
        int hours = (int) TimeUnit.MILLISECONDS.toHours(timeLeft-(days*(1000*60*60*24)));

        String ex1 = "תוקף";
        String ex = "<b>"+ex1+"<b>";
        if (days>=1)
            holder.expiryDate.setText(Html.fromHtml(ex)+" "+days+"  "+ctx.getResources().getString(R.string.days)+"  " +hours+" "+ctx.getResources().getString(R.string.hours));
        else
            holder.expiryDate.setText(Html.fromHtml(ex)+"  "+hours+" "+ctx.getResources().getString(R.string.hours));


        if (t.getImageUrl().equals("https://firebasestorage.googleapis.com/v0/b/mybizz-3bbe5.appspot.com/o/default_images%2Fspecial_offer.png?alt=media&token=eb2824dd-20d2-4796-8eb7-502eee3faacd")){
            Glide.with(ctx).load(t.getOfferCreator().getProfileUrl()).into(holder.profileImage);
        }else{
            Glide.with(ctx).load(t.getImageUrl()).into(holder.profileImage);
        }

        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(t.getOfferCreator().getUserUid())){
            holder.chat_text.setText(ctx.getResources().getString(R.string.cancel));
            holder.chat_icon.setImageResource(R.drawable.ic_delete_black_24dp);
        }else{
            holder.chat_text.setText(ctx.getResources().getString(R.string.chats));
            holder.chat_icon.setImageResource(R.drawable.chat_icon);
        }



        holder.offer_title.setText(t.getOfferTitle());
        holder.offer_description.setText(t.getDescription());
        holder.chat_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: "+FirebaseAuth.getInstance().getCurrentUser().getUid()+"__"+t.getOfferCreator().getUserUid());
                if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(t.getOfferCreator().getUserUid())) {
                    Intent i = new Intent(ctx, ChatActivity.class);
                    i.putExtra("otherID", service.getUserUid());
                    i.putExtra("otherName", service.getTitle());
                    i.putExtra("isService", true);
                    i.putExtra("currentService", service);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(i);
                }else{
//                    Toast.makeText(ctx, "this nust be cancelled", Toast.LENGTH_SHORT).show();
                    deleteOffer(service,t.getOfferKey());
                }
            }
        });

        holder.additional_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent allInfo = new Intent(ctx, AllServiceInfo.class);
                allInfo.putExtra("currentService", service);
                allInfo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(allInfo);
            }
        });

        holder.share_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "https://mybizz.application.to/offer" + t.getOfferKey());
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out this site!");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(Intent.createChooser(intent, "Share"));
            }
        });




    }

    private void deleteOffer(final Service s, final String offerKey) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
        alertDialogBuilder.setTitle(ctx.getResources().getString(R.string.delete_confirmation));
        alertDialogBuilder
                .setMessage(ctx.getResources().getString(R.string.delete_offer_dialog_text))
                .setCancelable(false)
                .setPositiveButton("אישור",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {

                        FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PrivateData").child(s.getUserUid()).child("services")
                                .child(s.getKey()).child("offers").child(offerKey).removeValue();

                        //adding  to AllUsers-->PublicData--> usrId-->services-->serviceKey-->Offers
                        FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PublicData").child(s.getUserUid()).child("services")
                                .child(s.getKey()).child("offers").child(offerKey).removeValue();

                        //add to Services-->PrivateData-->ServiceKey-->Offers -->offerKey
                        FirebaseDatabase.getInstance().getReference().child("Services").child("PrivateData").child(s.getKey())
                                .child("offers").child(offerKey).removeValue();


                        //add to Services-->PublicData-->ServiceKey-->Offers -->offerKey
                        FirebaseDatabase.getInstance().getReference().child("Services").child("PublicData").child(s.getKey())
                                .child("offers").child(offerKey).removeValue();
                        //add to Offers

                        FirebaseDatabase.getInstance().getReference()
                                .child("Offers").child(s.getCategory()).child(s.getSubcategory()).child(offerKey).removeValue();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();








    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (list.get(position).getServiceUid() .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) ? 0 : 1;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage,favIcon, chat_icon;
        TextView offer_title, offer_description, expiryDate, chat_text;
        LinearLayout chat_layout, additional_info, share_layout;


        public ViewHolder(View convertView) {
            super(convertView);

            profileImage = (ImageView) convertView.findViewById(R.id.single_profile_image);
            favIcon = (ImageView) itemView.findViewById(R.id.favorite_icon);
            offer_title = (TextView) convertView.findViewById(R.id.offer_title);
            offer_description = (TextView) itemView.findViewById(R.id.offer_description);
            expiryDate = (TextView) itemView.findViewById(R.id.offer_expiry);
            chat_layout = (LinearLayout) itemView.findViewById(R.id.chat_layout);
            additional_info = (LinearLayout) itemView.findViewById(R.id.additional_info);
            share_layout = (LinearLayout) itemView.findViewById(R.id.share_layout);
            chat_icon = (ImageView) itemView.findViewById(R.id.chat_icon);
            chat_text = (TextView) itemView.findViewById(R.id.chat_text);
        }
    }
}
