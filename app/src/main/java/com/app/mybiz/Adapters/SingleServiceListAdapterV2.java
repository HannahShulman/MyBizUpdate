package com.app.mybiz.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.app.mybiz.activities.AllServiceInfo;
import com.app.mybiz.activities.ServiceOffersList;
import com.app.mybiz.ChatActivity;
import com.app.mybiz.Constants;
import com.app.mybiz.CreateAccountChoiceActivity;
import com.app.mybiz.Managers.FavoriteServiceManager;
import com.app.mybiz.Objects.Service;
import com.app.mybiz.Objects.User;
import com.app.mybiz.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hannashulmah on 16/02/2017.
 */

public class SingleServiceListAdapterV2 extends RecyclerView.Adapter<SingleServiceListAdapterV2.ViewHolder> {
    String TAG = "SnglSrvcLstAdptr";
    Context ctx;
    ArrayList<Service> list;
    boolean isAnonymous;

    public SingleServiceListAdapterV2(Context ctx, ArrayList<Service> list) {
        this.ctx = ctx;
        this.list = list;
        isAnonymous = ctx.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).getBoolean(Constants.IS_ANONYMOUS, false);
    }

    @Override
    public SingleServiceListAdapterV2.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SingleServiceListAdapterV2.ViewHolder vh = null;
        vh = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.single_service_layout, parent, false));
        return vh;
    }


    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Service service = list.get(position);
        Log.d(TAG, "onBindViewHolder: "+service.toJson());
        Glide.with(ctx).load(service.getProfileUrl()).into(holder.image);
//        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(service.getUserUid())){
//            holder.chatLayout.setEnabled(false);
//        }
        holder.service_title.setText(service.getTitle());

        String homeNumber = service.getServiceHomeNumber();
        if (homeNumber==null){
            homeNumber = "";
        }
        String address = service.getAddress() + "  "+homeNumber+"   " + "<b>" + service.getTown() + "</b> ";
        holder.service_address.setText(Html.fromHtml(address));

        holder.service_description.setText(service.getShortDescription());


        if (service.getAverageRating() != 0)
            holder.num_reviews.setText("("+service.getNoReviewers() + ")");
        else
            holder.num_reviews.setVisibility(View.GONE);


        if (service.getAverageRating() != 0) {
            holder.avrg_reviewers.setVisibility(View.VISIBLE);
            holder.avrg_reviewers.setText(new DecimalFormat("##.#").format(service.getAverageRating()));
        } else {
            holder.avrg_reviewers.setVisibility(View.INVISIBLE);
        }
        if (service.getUserUid().equals(ctx.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).getString(Constants.APP_ID, Constants.RANDOM_STRING))) {
            holder.chatLayout.setAlpha(.12f);
            holder.chatLayout.setEnabled(false);
            holder.favoriteIcon.setVisibility(View.GONE);

        }else{
            holder.chatLayout.setAlpha(.80f);
            holder.chatLayout.setEnabled(true);
            holder.favoriteIcon.setVisibility(View.VISIBLE);
        }


        holder.chatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!service.getUserUid().equals(ctx.getSharedPreferences(Constants.PREFERENCES, ctx.MODE_PRIVATE).getString(Constants.APP_ID, Constants.RANDOM_STRING)))
                goToChat(service);
            }
        });


        holder.additional_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllInfo(service);
            }
        });

            if (service.getOffers().size() == 0) {
                holder.offersLayout.setEnabled(false);
                holder.offersLayout.setAlpha(.20f);
            } else {
                holder.sales.setColorFilter(Color.RED);
                holder.salesText.setTextColor(Color.RED);
                holder.offersLayout.setEnabled(true);
                holder.offersLayout.setAlpha(1.0f);
            }


        holder.offersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOffers(service);
            }
        });


        float averageRating = service.getAverageRating();
        holder.ratingBar.setStepSize(0.5f);
        holder.ratingBar.setRating(averageRating);



        if (!isAnonymous) {
            final DatabaseReference selfRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://mybizz-3bbe5.firebaseio.com/AllUsers/PublicData/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
            //decide which color to use for icon
            selfRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User u = dataSnapshot.getValue(User.class);
                    HashMap<String, Service> map = u.getFavorites();
                    if (map.get(service.getKey()) != null) {
                        holder.favoriteIcon.setSelected(true);
                        holder.favoriteIcon.setColorFilter(Color.RED);//
                    } else {
                        holder.favoriteIcon.setSelected(false);
                        holder.favoriteIcon.setColorFilter(Color.BLACK);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


            //adding to favorite list
            holder.favoriteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isAnonymous) {
                    final String myID = ctx.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).getString(Constants.APP_ID, Constants.RANDOM_STRING);

                    final String serviceKey = list.get(position).getKey();
                    //1.
                    FirebaseDatabase.getInstance().getReference().child("AllUsers").child("PublicData")
                            .child(myID).child("favorites").child(serviceKey).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                FavoriteServiceManager.removeToFavoriteList(serviceKey, myID);
                                dataSnapshot.getRef().removeEventListener(this);
                            } else {
                                Service service = list.get(position);
                                FavoriteServiceManager.addToFavoriteList(service, serviceKey, myID);
                                dataSnapshot.getRef().removeEventListener(this);

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else {
                        if (isAnonymous || ctx.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).getString(Constants.APP_ID, Constants.RANDOM_STRING).equals(Constants.RANDOM_STRING)){
                            final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ctx);
                            builder.setMessage(ctx.getString(R.string.register_for_favorites));
                            builder.setCancelable(true);
                            builder .setPositiveButton(ctx.getString(R.string.register), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(ctx, CreateAccountChoiceActivity.class);
                                            ctx.startActivity(intent);

                                        }
                                    }).setNegativeButton(ctx.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            builder.setCancelable(false);
                            builder.create().show();
                        }
                    }
                }
            });




        holder.shareService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");

                intent.putExtra(Intent.EXTRA_TEXT, ctx.getResources().getString(R.string.get_my_service)+"https://mybizz.application.to/allInfo_" + list.get(position).getKey()+"  "
                        +ctx.getResources().getString(R.string.download_app)+" https://play.google.com/store/apps/details?id=com.app.mybiz"

                );
//                intent.putExtra(Intent.EXTRA_TEXT, "https://mybizz.application.to/allInfo_" + list.get(position).getKey());
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out this site!");
                ctx.startActivity(Intent.createChooser(intent, "Share"));
//                MyApplication.shareService( list.get(position).getKey());
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void goToChat(Service service){
            Intent i = new Intent(ctx, ChatActivity.class);
            i.putExtra("otherID", service.getUserUid());
            i.putExtra("otherName", service.getTitle());
            i.putExtra("isService", true);
            i.putExtra("currentService", service);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
    }

    public void showAllInfo(Service service){
        Intent allInfo = new Intent(ctx, AllServiceInfo.class);
        allInfo.putExtra("currentService", service);
        allInfo.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(allInfo);
    }

    public  void showOffers(Service service){
        Intent i = new Intent(ctx, ServiceOffersList.class);
        i.putExtra("currentService", service);
        Log.d(TAG, service.getTitle());
        Log.d(TAG, service.getUserUid());
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(i);

    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image, favoriteIcon, shareService, sales;
        TextView service_title, service_address, service_description, num_reviews,avrg_reviewers, salesText;
        androidx.appcompat.widget.AppCompatRatingBar ratingBar;
        LinearLayout chatLayout, additional_info, offersLayout;
        View topLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            topLayout = itemView.findViewById(R.id.top_layout);
            image = (ImageView) itemView.findViewById(R.id.single_profile_image);
            service_title = (TextView) itemView.findViewById(R.id.service_title);
            service_address= (TextView) itemView.findViewById(R.id.service_address);
            service_description = (TextView) itemView.findViewById(R.id.service_description);
            ratingBar = (AppCompatRatingBar) itemView.findViewById(R.id.ratingBar);
            num_reviews = (TextView) itemView.findViewById(R.id.num_reviews);
            avrg_reviewers = (TextView) itemView.findViewById(R.id.avrg_reviewers);
            chatLayout = (LinearLayout) itemView.findViewById(R.id.chat_layout);
            additional_info = (LinearLayout) itemView.findViewById(R.id.additional_info);
            offersLayout = (LinearLayout) itemView.findViewById(R.id.offers_layout);
            favoriteIcon = (ImageView) itemView.findViewById(R.id.favorite_icon);
            shareService = (ImageView) itemView.findViewById(R.id.share);
            sales = (ImageView) itemView.findViewById(R.id.sales);
            salesText = (TextView) itemView.findViewById(R.id.sales_tv);
        }
    }
}

